/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** Text describing the task. */
    private final String description;
    /** Whether the task has been marked complete. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the symbol used to display this task's completion state.
     *
     * @return {@code "X"} if the task is done, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's completion state in the storage format.
     *
     * @return {@code "1"} if the task is done, otherwise {@code "0"}
     */
    public String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns whether this task is complete.
     *
     * @return {@code true} when the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns this task in the format used when saving tasks to disk.
     *
     * @return the line representing this task in storage
     */
    public String toStorageString() {
        return "T | " + getStorageStatus() + " | " + description;
    }

    /**
     * Returns this task's completion marker and description.
     * Subclasses extend this representation with their task type and details.
     *
     * @return the task display text
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
