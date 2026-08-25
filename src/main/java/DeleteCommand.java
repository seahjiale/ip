/** Deletes a selected task and persists the updated task list. */
public class DeleteCommand extends Command {
    private final String command;

    /** Creates a delete command containing the user's original input. */
    public DeleteCommand(String command) {
        this.command = command;
    }

    /** Deletes the selected task, restoring it if saving fails. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobbyException {
        Parser parser = new Parser();
        int taskIndex = parser.parseTaskIndex(command, "delete", tasks.size());
        Task deletedTask = tasks.remove(taskIndex);
        try {
            storage.save(tasks);
        } catch (BobbyException exception) {
            tasks.add(taskIndex, deletedTask);
            throw exception;
        }
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
