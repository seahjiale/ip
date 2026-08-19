/** Represents a task with a start and end time, without inheriting from other task types. */
public class Event {
    private final String description;
    private final String from;
    private final String to;
    private boolean isDone;

    /** Creates an incomplete event task. */
    public Event(String description, String from, String to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    /** Marks this event task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this event task as incomplete. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /** Returns this event task in the format used by the command line interface. */
    @Override
    public String toString() {
        return "[E][" + (isDone ? "X" : " ") + "] " + description
                + " (from: " + from + " to: " + to + ")";
    }
}
