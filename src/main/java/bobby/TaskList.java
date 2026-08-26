package bobby;

import java.util.ArrayList;
import java.util.List;

/** Owns Bobby's ordered collection of tasks and its basic list operations. */
public class TaskList {
    /** Ordered tasks currently managed by this list. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the given tasks.
     *
     * @param tasks tasks to copy into the new list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of stored tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based position of the task
     * @return the task at {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is outside the list
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to append
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at the given zero-based index.
     *
     * @param index zero-based position at which to insert the task
     * @param task task to insert
     * @throws IndexOutOfBoundsException if {@code index} is outside the valid insertion range
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based position of the task to remove
     * @return the removed task
     * @throws IndexOutOfBoundsException if {@code index} is outside the list
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }
}
