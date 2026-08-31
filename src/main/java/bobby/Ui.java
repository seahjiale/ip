package bobby;

import java.io.PrintStream;
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

    /** Output destination for messages shown by Bobby. */
    private final PrintStream output;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        this(System.out);
    }

    /** Creates a user interface that writes messages to the given destination. */
    Ui(PrintStream output) {
        scanner = new Scanner(System.in);
        this.output = output;
    }

    /** Shows Bobby's greeting and command prompt. */
    public void showWelcome() {
        showSeparator();
        output.print(BANNER);
        output.println("Hello! I'm Bobby.");
        output.println("What can I do for you?");
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
        output.println(SEPARATOR);
    }

    /**
     * Shows an error message to the user.
     *
     * @param message error text to display
     */
    public void showError(String message) {
        output.println(message);
    }

    /**
     * Shows all tasks in their current order.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(TaskList tasks) {
        output.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Shows tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks tasks found by the search, in their original order
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        output.println("Here are the matching tasks in your list:");
        if (matchingTasks.isEmpty()) {
            output.println("No matching tasks found.");
            return;
        }
        for (int i = 0; i < matchingTasks.size(); i++) {
            output.println((i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task task that was added
     * @param taskCount number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("Got it. I've added this task:");
        output.println(task);
        output.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task task that was marked done
     */
    public void showTaskMarkedDone(Task task) {
        output.println("Nice! I've marked this task as done:");
        output.println("  " + task);
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task task that was marked incomplete
     */
    public void showTaskMarkedNotDone(Task task) {
        output.println("OK, I've marked this task as not done yet:");
        output.println("  " + task);
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task task that was removed
     * @param taskCount number of tasks after the removal
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println("Noted. I've removed this task:");
        output.println(task);
        output.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Shows Bobby's farewell message. */
    public void showGoodbye() {
        output.println("Bye. Hope to see you again soon!");
        showSeparator();
    }
}
