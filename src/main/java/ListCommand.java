/** Displays all tasks currently stored by Bobby. */
public class ListCommand extends Command {

    /** Shows the current task list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
