/** Represents one executable command entered by a user. */
public abstract class Command {

    /** Executes this command using the current task list, UI, and storage. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws BobbyException;

    /** Returns whether this command should end Bobby's command loop. */
    public boolean isExit() {
        return false;
    }
}
