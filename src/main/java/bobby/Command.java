package bobby;

/** Represents one executable command entered by a user. */
public abstract class Command {

    /** Creates a command. */
    public Command() {
    }

    /**
     * Executes this command using the current task list, UI, and storage.
     *
     * @param tasks current task list
     * @param ui interface used to show results
     * @param storage persistence service for saved tasks
     * @throws BobbyException if the command cannot complete or be persisted
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws BobbyException;

    /**
     * Returns whether this command should end Bobby's command loop.
     *
     * @return {@code true} when executing this command should end the session
     */
    public boolean isExit() {
        return false;
    }
}
