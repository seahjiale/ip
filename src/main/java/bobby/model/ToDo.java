package bobby.model;

/** Represents a task without a date or time. */
public class ToDo extends Task {

    /** Creates an incomplete to-do with the given description. */
    public ToDo(String description) {
        super(description);
    }

    /** Returns this to-do in the format used by the command line interface. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
