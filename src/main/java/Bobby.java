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
            } else if (command.startsWith("todo ") && taskCount < MAX_TASKS) {
                tasks[taskCount] = new ToDo(command.substring(5));
                addTaskMessage(tasks[taskCount], ++taskCount);
            } else if (command.startsWith("deadline ") && taskCount < MAX_TASKS) {
                String[] deadlineParts = command.substring(9).split(" /by ", 2);
                tasks[taskCount] = new Deadline(deadlineParts[0], deadlineParts[1]);
                addTaskMessage(tasks[taskCount], ++taskCount);
            } else if (command.startsWith("event ") && taskCount < MAX_TASKS) {
                String[] eventParts = command.substring(6).split(" /from | /to ", 3);
                tasks[taskCount] = new Event(eventParts[0], eventParts[1], eventParts[2]);
                addTaskMessage(tasks[taskCount], ++taskCount);
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
