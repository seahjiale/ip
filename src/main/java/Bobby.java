import java.util.Scanner;

/**
 * A minimal chatbot that echoes commands until the user says goodbye.
 */
public class Bobby {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    /**
     * Prints Bobby's welcome message, stores tasks, changes task completion states, lists tasks, and exits on {@code bye}.
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

        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }

            if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + getTaskDisplay(tasks[i]));
                }
            } else if (command.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(command.substring(5)) - 1;
                tasks[taskIndex].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + getTaskDisplay(tasks[taskIndex]));
            } else if (command.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(command.substring(7)) - 1;
                tasks[taskIndex].unmarkAsDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + getTaskDisplay(tasks[taskIndex]));
            } else if (command.equals("todo") || command.startsWith("todo ")) {
                String description = command.length() > 5 ? command.substring(5) : "";
                if (description.trim().isEmpty()) {
                    printEmptyDescriptionMessage();
                } else if (taskCount < MAX_TASKS) {
                    tasks[taskCount] = new ToDo(description);
                    addTaskMessage(tasks[taskCount], ++taskCount);
                }
            } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                String deadlineDetails = command.length() > 9 ? command.substring(9) : "";
                String[] deadlineParts = deadlineDetails.split(" /by ", 2);
                boolean hasNoDescription = deadlineDetails.trim().isEmpty()
                        || deadlineDetails.trim().startsWith("/by")
                        || (deadlineParts.length > 1 && deadlineParts[0].trim().isEmpty());
                if (hasNoDescription) {
                    printEmptyDeadlineDescriptionMessage();
                } else if (deadlineParts.length < 2 || deadlineParts[1].trim().isEmpty()) {
                    printEmptyDeadlineDateMessage();
                } else if (taskCount < MAX_TASKS) {
                    tasks[taskCount] = new Deadline(deadlineParts[0], deadlineParts[1]);
                    addTaskMessage(tasks[taskCount], ++taskCount);
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
                    printEmptyEventDescriptionMessage();
                } else if (from.isEmpty()) {
                    printEmptyEventStartTimeMessage();
                } else if (to.isEmpty()) {
                    printEmptyEventEndTimeMessage();
                } else if (taskCount < MAX_TASKS) {
                    tasks[taskCount] = new Event(description, from, to);
                    addTaskMessage(tasks[taskCount], ++taskCount);
                }
            } else {
                printUnknownCommandMessage();
            }
            System.out.println(SEPARATOR);
        }
    }

    /** Prints the error shown when the input does not use a supported command. */
    private static void printUnknownCommandMessage() {
        System.out.println("No such task type available. Try again!");
    }

    /** Prints the error shown when a to-do has no description. */
    private static void printEmptyDescriptionMessage() {
        System.out.println("Error! The description of a todo cannot be empty!");
    }

    /** Prints the error shown when a deadline has no description. */
    private static void printEmptyDeadlineDescriptionMessage() {
        System.out.println("Error! The description of a deadline cannot be empty!");
    }

    /** Prints the error shown when a deadline has no date. */
    private static void printEmptyDeadlineDateMessage() {
        System.out.println("Error! The date of a deadline cannot be empty!");
    }

    /** Prints the error shown when an event has no description. */
    private static void printEmptyEventDescriptionMessage() {
        System.out.println("Error! The description of an event cannot be empty!");
    }

    /** Prints the error shown when an event has no start time. */
    private static void printEmptyEventStartTimeMessage() {
        System.out.println("Error! Start time of an event cannot be empty. Try again!");
    }

    /** Prints the error shown when an event has no end time. */
    private static void printEmptyEventEndTimeMessage() {
        System.out.println("Error! End time of an event cannot be empty. Try again!");
    }

    /** Prints the confirmation shown after adding a typed task. */
    private static void addTaskMessage(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println(getTaskDisplay(task));
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Returns the display text for one supported task type. */
    private static String getTaskDisplay(Task task) {
        return task.toString();
    }
}
