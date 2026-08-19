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

        Object[] tasks = new Object[MAX_TASKS];
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
                markTaskAsDone(tasks[taskIndex]);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + getTaskDisplay(tasks[taskIndex]));
            } else if (command.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(command.substring(7)) - 1;
                unmarkTaskAsDone(tasks[taskIndex]);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + getTaskDisplay(tasks[taskIndex]));
            } else if (command.startsWith("todo ") && taskCount < MAX_TASKS) {
                tasks[taskCount] = new ToDo(command.substring(5));
                addTaskMessage(tasks[taskCount], ++taskCount);
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println("added: " + command);
            }
            System.out.println(SEPARATOR);
        }
    }

    /** Prints the confirmation shown after adding a typed task. */
    private static void addTaskMessage(Object task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println(getTaskDisplay(task));
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Returns the display text for one supported task type. */
    private static String getTaskDisplay(Object task) {
        if (task instanceof ToDo) {
            return ((ToDo) task).toString();
        }
        Task basicTask = (Task) task;
        return "[" + basicTask.getStatusIcon() + "] " + basicTask.getDescription();
    }

    /** Marks one supported task type as complete. */
    private static void markTaskAsDone(Object task) {
        if (task instanceof ToDo) {
            ((ToDo) task).markAsDone();
        } else {
            ((Task) task).markAsDone();
        }
    }

    /** Marks one supported task type as incomplete. */
    private static void unmarkTaskAsDone(Object task) {
        if (task instanceof ToDo) {
            ((ToDo) task).unmarkAsDone();
        } else {
            ((Task) task).unmarkAsDone();
        }
    }
}
