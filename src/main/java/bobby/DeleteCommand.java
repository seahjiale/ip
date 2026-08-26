package bobby;

/** Deletes a selected task and persists the updated task list. */
public class DeleteCommand extends Command {
    /** Original user input, including the command and task number. */
    private final String command;

    /**
     * Creates a delete command containing the user's original input.
     *
     * @param command original user input
     */
    public DeleteCommand(String command) {
        this.command = command;
    }

    /**
     * Deletes the selected task, restoring it if saving fails.
     *
     * @param tasks current task list to update
     * @param ui interface used to show the confirmation
     * @param storage persistence service used to save the updated list
     * @throws BobbyException if the task number is invalid or saving fails
     */
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
