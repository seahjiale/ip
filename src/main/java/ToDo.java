/**
 * Represents a task without a date or time.
 * This class deliberately does not inherit from the other task types.
 */
public class ToDo {
    private final String description;
    private boolean isDone;

    /** Creates an incomplete to-do with the given description. */
    public ToDo(String description) {
        this.description = description;
    }

    /** Marks this to-do as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this to-do as incomplete. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /** Returns this to-do in the format used by the command line interface. */
    @Override
    public String toString() {
        return "[T][" + (isDone ? "X" : " ") + "] " + description;
    }
}
