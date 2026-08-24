import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A minimal chatbot that echoes commands until the user says goodbye.
 */
public class Bobby {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final Path TASK_FILE = Paths.get("data", "duke.txt");

    /**
     * Prints Bobby's welcome message, stores tasks, changes task completion states, deletes tasks,
     * lists tasks, and exits on {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner = "██████╗  ██████╗ ██████╗ ██████╗ ██╗   ██╗\n"
                + "██╔══██╗██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝\n"
                + "██████╔╝██║   ██║██████╔╝██████╔╝ ╚████╔╝\n"
                + "██╔══██╗██║   ██║██╔══██╗██╔══██╗  ╚██╔╝\n"
                + "██████╔╝╚██████╔╝██████╔╝██████╔╝   ██║\n"
                + "╚═════╝  ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝\n";

        System.out.println(SEPARATOR);
        System.out.print(banner);
        System.out.println("Hello! I'm Bobby.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);

        List<Task> tasks;
        try {
            tasks = loadTasks();
        } catch (BobbyException exception) {
            tasks = new ArrayList<>();
            System.out.println(exception.getMessage());
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (!scanner.hasNextLine()) {
                System.out.println(SEPARATOR);
                printGoodbyeMessage();
                break;
            }

            String command = scanner.nextLine().trim();
            System.out.println(SEPARATOR);
            if (command.equalsIgnoreCase("bye")) {
                printGoodbyeMessage();
                break;
            }

            try {
                if (command.isEmpty()) {
                    throw new BobbyException("Error! The command cannot be empty!");
                } else if (command.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + getTaskDisplay(tasks.get(i)));
                    }
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
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + getTaskDisplay(task));
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
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + getTaskDisplay(task));
                } else if (isCommand(command, "delete")) {
                    int taskIndex = parseTaskIndex(command, "delete", tasks.size());
                    Task deletedTask = tasks.remove(taskIndex);
                    try {
                        saveTasks(tasks);
                    } catch (BobbyException exception) {
                        tasks.add(taskIndex, deletedTask);
                        throw exception;
                    }
                    System.out.println("Noted. I've removed this task:");
                    System.out.println(getTaskDisplay(deletedTask));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
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
                        addTaskMessage(task, tasks.size());
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
                        addTaskMessage(task, tasks.size());
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
                        addTaskMessage(task, tasks.size());
                    }
                } else {
                    throw new BobbyException("No such task type available. Try again!");
                }
            } catch (BobbyException exception) {
                System.out.println(exception.getMessage());
            }
            System.out.println(SEPARATOR);
        }
    }

    /** Prints the confirmation shown after adding a typed task. */
    private static void addTaskMessage(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println(getTaskDisplay(task));
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Prints the message used when Bobby exits normally or reaches end of input. */
    private static void printGoodbyeMessage() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(SEPARATOR);
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

    /** Returns the display text for one supported task type. */
    private static String getTaskDisplay(Task task) {
        return task.toString();
    }
}
