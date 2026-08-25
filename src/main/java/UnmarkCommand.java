/** Marks a selected task as not done and persists the updated task list. */
public class UnmarkCommand extends Command {
    private final String command;

    /** Creates an unmark command containing the user's original input. */
    public UnmarkCommand(String command) {
        this.command = command;
    }

    /** Marks the selected task as not done, rolling back if saving fails. */
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

    /** Restores the task's previous completion state after a failed save. */
    private void restoreTaskStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.unmarkAsDone();
        }
    }
}
