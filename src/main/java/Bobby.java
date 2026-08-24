import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A minimal chatbot that echoes commands until the user says goodbye.
 */
public class Bobby {
    private static final Path TASK_FILE = Paths.get("data", "duke.txt");

    /**
     * Prints Bobby's welcome message, stores tasks, changes task completion states, deletes tasks,
     * lists tasks, and exits on {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        List<Task> tasks;
        try {
            tasks = loadTasks();
        } catch (BobbyException exception) {
            tasks = new ArrayList<>();
            ui.showError(exception.getMessage());
        }
        while (true) {
            String command = ui.readCommand();
            if (command == null) {
                ui.showSeparator();
                ui.showGoodbye();
                break;
            }

            ui.showSeparator();
            if (command.equalsIgnoreCase("bye")) {
                ui.showGoodbye();
                break;
            }

            try {
                if (command.isEmpty()) {
                    throw new BobbyException("Error! The command cannot be empty!");
                } else if (command.equals("list")) {
                    ui.showTaskList(tasks);
                } else if (isCommand(command, "mark")) {
                    int taskIndex = parseTaskIndex(command, "mark", tasks.size());
                    Task task = tasks.get(taskIndex);
                    boolean wasDone = task.isDone();
                    task.markAsDone();
                    try {
                        saveTasks(tasks);
                    } catch (BobbyException exception) {
                        restoreTaskStatus(task, wasDone);
                        throw exception;
                    }
                    ui.showTaskMarkedDone(task);
                } else if (isCommand(command, "unmark")) {
                    int taskIndex = parseTaskIndex(command, "unmark", tasks.size());
                    Task task = tasks.get(taskIndex);
                    boolean wasDone = task.isDone();
                    task.unmarkAsDone();
                    try {
                        saveTasks(tasks);
                    } catch (BobbyException exception) {
                        restoreTaskStatus(task, wasDone);
                        throw exception;
                    }
                    ui.showTaskMarkedNotDone(task);
                } else if (isCommand(command, "delete")) {
                    int taskIndex = parseTaskIndex(command, "delete", tasks.size());
                    Task deletedTask = tasks.remove(taskIndex);
                    try {
                        saveTasks(tasks);
                    } catch (BobbyException exception) {
                        tasks.add(taskIndex, deletedTask);
                        throw exception;
                    }
                    ui.showTaskDeleted(deletedTask, tasks.size());
                } else if (isCommand(command, "todo")) {
                    String description = getCommandArgument(command, "todo");
                    if (description.trim().isEmpty()) {
                        throw new BobbyException("Error! The description of a todo cannot be empty!");
                    } else {
                        validateStorageField(description);
                        Task task = new ToDo(description.trim());
                        tasks.add(task);
                        try {
                            saveTasks(tasks);
                        } catch (BobbyException exception) {
                            tasks.remove(tasks.size() - 1);
                            throw exception;
                        }
                        ui.showTaskAdded(task, tasks.size());
                    }
                } else if (isCommand(command, "deadline")) {
                    String deadlineDetails = getCommandArgument(command, "deadline");
                    String[] deadlineParts = deadlineDetails.split(" /by ", 2);
                    boolean hasNoDescription = deadlineDetails.trim().isEmpty()
                            || deadlineDetails.trim().startsWith("/by")
                            || (deadlineParts.length > 1 && deadlineParts[0].trim().isEmpty());
                    if (hasNoDescription) {
                        throw new BobbyException("Error! The description of a deadline cannot be empty!");
                    } else if (deadlineParts.length < 2 || deadlineParts[1].trim().isEmpty()) {
                        throw new BobbyException("Error! The date of a deadline cannot be empty!");
                    } else {
                        validateStorageField(deadlineParts[0]);
                        validateStorageField(deadlineParts[1]);
                        Task task;
                        try {
                            task = Deadline.fromInput(deadlineParts[0].trim(), deadlineParts[1].trim());
                        } catch (DateTimeParseException exception) {
                            throw new BobbyException("Error! The deadline must be a valid date. "
                                    + "Use yyyy-MM-dd or d/M/yyyy HHmm.");
                        }
                        tasks.add(task);
                        try {
                            saveTasks(tasks);
                        } catch (BobbyException exception) {
                            tasks.remove(tasks.size() - 1);
                            throw exception;
                        }
                        ui.showTaskAdded(task, tasks.size());
                    }
                } else if (isCommand(command, "event")) {
                    String eventDetails = getCommandArgument(command, "event");
                    int fromMarkerIndex = eventDetails.indexOf("/from");
                    int toMarkerIndex = eventDetails.indexOf("/to");

                    String description;
                    String from = "";
                    String to = "";
                    if (fromMarkerIndex >= 0) {
                        description = eventDetails.substring(0, fromMarkerIndex).trim();
                        if (toMarkerIndex > fromMarkerIndex) {
                            from = eventDetails.substring(fromMarkerIndex + 5, toMarkerIndex).trim();
                        } else {
                            from = eventDetails.substring(fromMarkerIndex + 5).trim();
                        }
                    } else if (toMarkerIndex >= 0) {
                        description = eventDetails.substring(0, toMarkerIndex).trim();
                    } else {
                        description = eventDetails;
                    }
                    if (toMarkerIndex >= 0) {
                        to = eventDetails.substring(toMarkerIndex + 3).trim();
                    }

                    if (description.isEmpty()) {
                        throw new BobbyException("Error! The description of an event cannot be empty!");
                    } else if (from.isEmpty()) {
                        throw new BobbyException("Error! Start time of an event cannot be empty. Try again!");
                    } else if (to.isEmpty()) {
                        throw new BobbyException("Error! End time of an event cannot be empty. Try again!");
                    } else {
                        validateStorageField(description);
                        validateStorageField(from);
                        validateStorageField(to);
                        Task task = new Event(description, from, to);
                        tasks.add(task);
                        try {
                            saveTasks(tasks);
                        } catch (BobbyException exception) {
                            tasks.remove(tasks.size() - 1);
                            throw exception;
                        }
                        ui.showTaskAdded(task, tasks.size());
                    }
                } else {
                    throw new BobbyException("No such task type available. Try again!");
                }
            } catch (BobbyException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showSeparator();
        }
    }

    /** Returns whether an input is a command or has whitespace-separated arguments. */
    private static boolean isCommand(String input, String commandName) {
        return input.equals(commandName)
                || (input.length() > commandName.length()
                && input.startsWith(commandName)
                && Character.isWhitespace(input.charAt(commandName.length())));
    }

    /** Returns the argument text after a command name. */
    private static String getCommandArgument(String command, String commandName) {
        if (command.length() <= commandName.length()) {
            return "";
        }
        return command.substring(commandName.length()).trim();
    }

    /** Parses and validates a one-based task number from a task command. */
    private static int parseTaskIndex(String command, String commandName, int taskCount)
            throws BobbyException {
        if (taskCount == 0) {
            throw new BobbyException("No tasks available to " + commandName + ".");
        }

        String taskNumber = getCommandArgument(command, commandName);
        if (taskNumber.isEmpty()) {
            throw new BobbyException("Error! The task number cannot be empty!");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new BobbyException("Error! The task number must be a valid integer.");
        }
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new BobbyException("Error! The task number must be between 1 and "
                    + taskCount + ".");
        }
        return taskIndex;
    }

    /** Restores a task's completion state after a failed save. */
    private static void restoreTaskStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.unmarkAsDone();
        }
    }

    /** Rejects storage separator characters that would make a saved line ambiguous. */
    private static void validateStorageField(String field) throws BobbyException {
        if (field.contains("|")) {
            throw new BobbyException("Error! Task details cannot contain the '|' character!");
        }
    }

    /** Loads the saved task list, or returns an empty list if no save file exists. */
    private static List<Task> loadTasks() throws BobbyException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(TASK_FILE)) {
            return tasks;
        }

        try {
            List<String> taskLines = Files.readAllLines(TASK_FILE, StandardCharsets.UTF_8);
            for (String taskLine : taskLines) {
                if (!taskLine.trim().isEmpty()) {
                    tasks.add(parseTask(taskLine));
                }
            }
            return tasks;
        } catch (IOException exception) {
            throw new BobbyException("Error! Could not load tasks from disk.");
        }
    }

    /** Recreates one task from a line in the task storage format. */
    private static Task parseTask(String taskLine) throws BobbyException {
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
            task = new Event(description, parts[3].trim(), parts[4].trim());
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

    /** Writes the current task list to disk in a format that can be loaded later. */
    private static void saveTasks(List<Task> tasks) throws BobbyException {
        try {
            Files.createDirectories(TASK_FILE.getParent());
            List<String> taskLines = new ArrayList<>();
            for (Task task : tasks) {
                taskLines.add(task.toStorageString());
            }
            Files.write(TASK_FILE, taskLines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException exception) {
            throw new BobbyException("Error! Could not save tasks to disk.");
        }
    }

}
