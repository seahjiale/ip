/** Represents a task with a deadline. */
public class Deadline extends Task {
    private final String by;

    /** Creates an incomplete deadline task. */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /** Returns this deadline task in the format used by the command line interface. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }

    /** Returns this deadline in the format used when saving tasks to disk. */
    @Override
    public String toStorageString() {
        return "D | " + getStorageStatus() + " | " + getDescription() + " | " + by;
    }
}
