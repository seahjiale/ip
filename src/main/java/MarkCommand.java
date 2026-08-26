/** Marks a selected task as done and persists the updated task list. */
public class MarkCommand extends Command {
    /** Original user input, including the command and task number. */
    private final String command;

    /**
     * Creates a mark command containing the user's original input.
     *
     * @param command original user input
     */
    public MarkCommand(String command) {
        this.command = command;
    }

    /**
     * Marks the selected task, rolling back if saving fails.
     *
     * @param tasks current task list to update
     * @param ui interface used to show the confirmation
     * @param storage persistence service used to save the updated list
     * @throws BobbyException if the task number is invalid or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobbyException {
        Parser parser = new Parser();
        int taskIndex = parser.parseTaskIndex(command, "mark", tasks.size());
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            storage.save(tasks);
        } catch (BobbyException exception) {
            restoreTaskStatus(task, wasDone);
            throw exception;
        }
        ui.showTaskMarkedDone(task);
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
