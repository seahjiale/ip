/** Represents a task with a deadline, without inheriting from other task types. */
public class Deadline {
    private final String description;
    private final String by;
    private boolean isDone;

    /** Creates an incomplete deadline task. */
    public Deadline(String description, String by) {
        this.description = description;
        this.by = by;
    }

    /** Marks this deadline task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this deadline task as incomplete. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /** Returns this deadline task in the format used by the command line interface. */
    @Override
    public String toString() {
        return "[D][" + (isDone ? "X" : " ") + "] " + description + " (by: " + by + ")";
    }
}
