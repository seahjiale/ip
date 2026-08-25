/** Ends Bobby's session and shows the farewell message. */
public class ExitCommand extends Command {

    /** Shows Bobby's farewell message. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** Returns that this command ends the command loop. */
    @Override
    public boolean isExit() {
        return true;
    }
}
