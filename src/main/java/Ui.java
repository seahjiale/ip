import java.util.List;
import java.util.Scanner;

/** Handles all console input and output for Bobby. */
public class Ui {
    /** Separator printed between chatbot interactions. */
    private static final String SEPARATOR = "____________________________________________________________";
    /** ASCII-art banner printed at startup. */
    private static final String BANNER = " ____   ____  ____  ____  __   __\n"
            + "| __ ) / __ \\| __ )| __ ) \\ \\ / /\n"
            + "|  _ \\| |  | |  _ \\|  _ \\  \\ V /\n"
            + "| |_) | |__| | |_) | |_) |   | |\n"
            + "|____/ \\____/|____/|____/    |_|\n";

    /** Input source for user commands. */
    private final Scanner scanner;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Shows Bobby's greeting and command prompt. */
    public void showWelcome() {
        showSeparator();
        System.out.print(BANNER);
        System.out.println("Hello! I'm Bobby.");
        System.out.println("What can I do for you?");
        showSeparator();
    }

    /**
     * Reads and trims the next command, or returns {@code null} when input ends.
     *
     * @return the next trimmed command, or {@code null} when input ends
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    /** Shows the visual separator used between interactions. */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Shows an error message to the user.
     *
     * @param message error text to display
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Shows all tasks in their current order.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Shows tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks tasks found by the search, in their original order
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        System.out.println("Here are the matching tasks in your list:");
        if (matchingTasks.isEmpty()) {
            System.out.println("No matching tasks found.");
            return;
        }
        for (int i = 0; i < matchingTasks.size(); i++) {
            System.out.println((i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task task that was added
     * @param taskCount number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println(task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task task that was marked done
     */
    public void showTaskMarkedDone(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task task that was marked incomplete
     */
    public void showTaskMarkedNotDone(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task task that was removed
     * @param taskCount number of tasks after the removal
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println(task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Shows Bobby's farewell message. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        showSeparator();
    }
}
