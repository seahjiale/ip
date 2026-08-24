import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }

            try {
                if (command.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + getTaskDisplay(tasks.get(i)));
                    }
                } else if (command.startsWith("mark ")) {
                    int taskIndex = Integer.parseInt(command.substring(5)) - 1;
                    tasks.get(taskIndex).markAsDone();
                    saveTasks(tasks);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + getTaskDisplay(tasks.get(taskIndex)));
                } else if (command.startsWith("unmark ")) {
                    int taskIndex = Integer.parseInt(command.substring(7)) - 1;
                    tasks.get(taskIndex).unmarkAsDone();
                    saveTasks(tasks);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + getTaskDisplay(tasks.get(taskIndex)));
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    if (tasks.isEmpty()) {
                        throw new BobbyException("No tasks available to delete.");
                    }

                    String taskNumber = command.length() > 7 ? command.substring(7).trim() : "";
                    if (taskNumber.isEmpty()) {
                        throw new BobbyException("Error! The task number cannot be empty!");
                    }

                    int taskIndex;
                    try {
                        taskIndex = Integer.parseInt(taskNumber) - 1;
                    } catch (NumberFormatException exception) {
                        throw new BobbyException("Error! The task number must be a valid integer.");
                    }
                    if (taskIndex < 0 || taskIndex >= tasks.size()) {
                        throw new BobbyException("Error! The task number must be between 1 and "
                                + tasks.size() + ".");
                    }

                    Task deletedTask = tasks.remove(taskIndex);
                    saveTasks(tasks);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println(getTaskDisplay(deletedTask));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = command.length() > 5 ? command.substring(5) : "";
                    if (description.trim().isEmpty()) {
                        throw new BobbyException("Error! The description of a todo cannot be empty!");
                    } else {
                        Task task = new ToDo(description);
                        tasks.add(task);
                        saveTasks(tasks);
                        addTaskMessage(task, tasks.size());
                    }
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    String deadlineDetails = command.length() > 9 ? command.substring(9) : "";
                    String[] deadlineParts = deadlineDetails.split(" /by ", 2);
                    boolean hasNoDescription = deadlineDetails.trim().isEmpty()
                            || deadlineDetails.trim().startsWith("/by")
                            || (deadlineParts.length > 1 && deadlineParts[0].trim().isEmpty());
                    if (hasNoDescription) {
                        throw new BobbyException("Error! The description of a deadline cannot be empty!");
                    } else if (deadlineParts.length < 2 || deadlineParts[1].trim().isEmpty()) {
                        throw new BobbyException("Error! The date of a deadline cannot be empty!");
                    } else {
                        Task task = new Deadline(deadlineParts[0], deadlineParts[1]);
                        tasks.add(task);
                        saveTasks(tasks);
                        addTaskMessage(task, tasks.size());
                    }
                } else if (command.equals("event") || command.startsWith("event ")) {
                    String eventDetails = command.length() > 6 ? command.substring(6).trim() : "";
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
                        Task task = new Event(description, from, to);
                        tasks.add(task);
                        saveTasks(tasks);
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
            task = new Deadline(description, parts[3].trim());
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
            Files.write(TASK_FILE, taskLines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new BobbyException("Error! Could not save tasks to disk.");
        }
    }

    /** Returns the display text for one supported task type. */
    private static String getTaskDisplay(Task task) {
        return task.toString();
    }
}
