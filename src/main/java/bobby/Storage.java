package bobby;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Handles loading tasks from and saving tasks to Bobby's data file. */
public class Storage {
    /** File containing one serialized task per line. */
    private final Path taskFile;

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
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(taskFile)) {
            return new TaskList();
        }

        try {
            List<String> taskLines = Files.readAllLines(taskFile, StandardCharsets.UTF_8);
            for (String taskLine : taskLines) {
                if (!taskLine.trim().isEmpty()) {
                    tasks.add(parseTask(taskLine));
                }
            }
            return new TaskList(tasks);
        } catch (IOException exception) {
            throw new BobbyException("Error! Could not load tasks from disk.");
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
        try {
            Files.createDirectories(taskFile.getParent());
            List<String> taskLines = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                taskLines.add(tasks.get(i).toStorageString());
            }
            Files.write(taskFile, taskLines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException exception) {
            throw new BobbyException("Error! Could not save tasks to disk.");
        }
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
        Task task;
        if (taskType.equals("T") && parts.length == 3) {
            task = new ToDo(description);
        } else if (taskType.equals("D") && parts.length == 4) {
            try {
                task = Deadline.fromInput(description, parts[3].trim());
            } catch (DateTimeParseException exception) {
                throw new BobbyException("Error! Could not load tasks from disk.");
            }
        } else if (taskType.equals("E") && parts.length == 5) {
            try {
                task = Event.fromInput(description, parts[3].trim(), parts[4].trim());
            } catch (DateTimeParseException exception) {
                throw new BobbyException("Error! Could not load tasks from disk.");
            }
        } else {
            throw new BobbyException("Error! Could not load tasks from disk.");
        }

        if (status.equals("1")) {
            task.markAsDone();
        } else if (!status.equals("0")) {
            throw new BobbyException("Error! Could not load tasks from disk.");
        }
        return task;
    }
}
