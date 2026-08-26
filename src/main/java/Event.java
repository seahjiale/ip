/** Represents a task with a start and end time. */
public class Event extends Task {
    /** Text describing when the event starts. */
    private final String from;
    /** Text describing when the event ends. */
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description text describing the event
     * @param from text describing when the event starts
     * @param to text describing when the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event task in the format used by the command line interface.
     *
     * @return the event's display text
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns this event in the format used when saving tasks to disk.
     *
     * @return the event's storage line
     */
    @Override
    public String toStorageString() {
        return "E | " + getStorageStatus() + " | " + getDescription() + " | " + from + " | " + to;
    }
}
