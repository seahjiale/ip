/** Marks a selected task as not done and persists the updated task list. */
public class UnmarkCommand extends Command {
    /** Original user input, including the command and task number. */
    private final String command;

    /**
     * Creates an unmark command containing the user's original input.
     *
     * @param command original user input
     */
    public UnmarkCommand(String command) {
        this.command = command;
    }

    /**
     * Marks the selected task as not done, rolling back if saving fails.
     *
     * @param tasks current task list to update
     * @param ui interface used to show the confirmation
     * @param storage persistence service used to save the updated list
     * @throws BobbyException if the task number is invalid or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobbyException {
        Parser parser = new Parser();
        int taskIndex = parser.parseTaskIndex(command, "unmark", tasks.size());
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        task.unmarkAsDone();
        try {
            storage.save(tasks);
        } catch (BobbyException exception) {
            restoreTaskStatus(task, wasDone);
            throw exception;
        }
        ui.showTaskMarkedNotDone(task);
    }

    /**
     * Restores the task's previous completion state after a failed save.
     *
     * @param task task whose state should be restored
     * @param wasDone whether the task was complete before the attempted update
     */
    private void restoreTaskStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.unmarkAsDone();
        }
    }
}
