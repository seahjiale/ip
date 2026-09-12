package bobby;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/** Handles loading tasks from and saving tasks to Bobby's data file. */
public class Storage {
    /** File containing one serialized task per line. */
    private final Path taskFile;
    /** Whether saving is safe after the most recent load attempt. */
    private boolean canSave = true;

    /**
     * Creates storage backed by the file at the given path.
     *
     * @param filePath path to the task data file
     */
    public Storage(String filePath) {
        taskFile = Paths.get(filePath);
    }

    /**
     * Loads all saved tasks, or returns an empty task list when the file does not exist.
     *
     * @return tasks reconstructed from the storage file
     * @throws BobbyException if the file cannot be read or contains invalid task data
     */
    public TaskList load() throws BobbyException {
        canSave = true;
        try {
            if (Files.notExists(taskFile)) {
                return new TaskList();
            }
            List<String> taskLines = Files.readAllLines(taskFile, StandardCharsets.UTF_8);
            TaskList tasks = new TaskList();
            for (int i = 0; i < taskLines.size(); i++) {
                String taskLine = taskLines.get(i);
                if (!taskLine.trim().isEmpty()) {
                    Task task;
                    try {
                        task = parseTask(taskLine);
                    } catch (BobbyException exception) {
                        canSave = false;
                        throw invalidDataException(i + 1);
                    }
                    if (tasks.containsTaskWithSameDetails(task)) {
                        canSave = false;
                        throw new BobbyException("Error! Could not load tasks because line "
                                + (i + 1) + " duplicates an earlier task.");
                    }
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (IOException | SecurityException exception) {
            canSave = false;
            throw new BobbyException("Error! Could not load tasks from disk. "
                    + "Check that the file exists and is readable.");
        }
    }

    /**
     * Saves all tasks using Bobby's stable, line-based storage format.
     *
     * @param tasks tasks to serialize
     * @throws BobbyException if the file or its parent directory cannot be written
     */
    public void save(TaskList tasks) throws BobbyException {
        assert tasks != null : "Storage must save a non-null task list";
        if (!canSave) {
            throw new BobbyException("Error! Bobby will not overwrite task data that failed to load. "
                    + "Fix or move the data file, then restart Bobby.");
        }

        Path absoluteTaskFile = taskFile.toAbsolutePath().normalize();
        Path parentDirectory = absoluteTaskFile.getParent();
        if (parentDirectory == null) {
            throw new BobbyException("Error! The task data file path is invalid.");
        }
        Path temporaryFile = null;
        try {
            Files.createDirectories(parentDirectory);
            List<String> taskLines = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                taskLines.add(tasks.get(i).toStorageString());
            }
            String temporaryFilePrefix = absoluteTaskFile.getFileName().toString();
            if (temporaryFilePrefix.length() < 3) {
                temporaryFilePrefix = "bobby-" + temporaryFilePrefix;
            }
            temporaryFile = Files.createTempFile(parentDirectory, temporaryFilePrefix, ".tmp");
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            moveIntoPlace(temporaryFile, absoluteTaskFile);
            temporaryFile = null;
        } catch (IOException | SecurityException exception) {
            throw new BobbyException("Error! Could not save tasks to disk. "
                    + "Check that the folder is writable and has enough space.");
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /** Moves a complete temporary data file into place, using an atomic move when supported. */
    private void moveIntoPlace(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Best-effort cleanup for a temporary file left behind by a failed save. */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException exception) {
            // The original save error is more useful to the user than a cleanup error.
        }
    }

    /** Creates a user-facing error identifying the malformed storage line. */
    private BobbyException invalidDataException(int lineNumber) {
        return new BobbyException("Error! Could not load tasks because line " + lineNumber
                + " contains invalid data.");
    }

    /**
     * Recreates one task from a line in the task storage format.
     *
     * @param taskLine serialized task line
     * @return task represented by {@code taskLine}
     * @throws BobbyException if the line does not follow the storage format
     */
    private Task parseTask(String taskLine) throws BobbyException {
        String[] parts = taskLine.split("\\s*\\|\\s*", -1);
        if (parts.length < 3) {
            throw new BobbyException("Error! Could not load tasks from disk.");
        }

        String taskType = parts[0].trim();
        String status = parts[1].trim();
        String description = parts[2].trim();
        if (description.isEmpty() || containsControlCharacter(description)) {
            throw new BobbyException("Error! Could not load tasks from disk.");
        }
        description = description.replaceAll("\\s+", " ");
        Task task;
        boolean hasStoredPriority;
        if (taskType.equals("T") && (parts.length == 3 || parts.length == 4)) {
            task = new ToDo(description);
            hasStoredPriority = parts.length == 4;
        } else if (taskType.equals("D") && (parts.length == 4 || parts.length == 5)) {
            try {
                task = Deadline.fromInput(description, parts[3].trim());
            } catch (IllegalArgumentException exception) {
                throw new BobbyException("Error! Could not load tasks from disk.");
            }
            hasStoredPriority = parts.length == 5;
        } else if (taskType.equals("E") && (parts.length == 5 || parts.length == 6)) {
            try {
                task = Event.fromInput(description, parts[3].trim(), parts[4].trim());
            } catch (IllegalArgumentException exception) {
                throw new BobbyException("Error! Could not load tasks from disk.");
            }
            hasStoredPriority = parts.length == 6;
        } else {
            throw new BobbyException("Error! Could not load tasks from disk.");
        }

        if (status.equals("1")) {
            task.markAsDone();
        } else if (!status.equals("0")) {
            throw new BobbyException("Error! Could not load tasks from disk.");
        }

        if (hasStoredPriority) {
            try {
                task.setPriority(Priority.valueOf(parts[parts.length - 1].trim()));
            } catch (IllegalArgumentException exception) {
                throw new BobbyException("Error! Could not load tasks from disk.");
            }
        }
        return task;
    }

    /** Returns whether a stored description contains an unsafe control character. */
    private boolean containsControlCharacter(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (Character.isISOControl(input.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
