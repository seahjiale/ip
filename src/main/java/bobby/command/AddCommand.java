package bobby.command;

import bobby.exception.BobbyException;
import bobby.model.Task;
import bobby.model.TaskList;
import bobby.storage.Storage;
import bobby.ui.Ui;

/** Adds a parsed task to the task list and persists the change. */
public class AddCommand extends Command {
    private final Task task;

    /** Creates an add command for the given task. */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** Adds the task, saving it and rolling back the addition if saving fails. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobbyException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (BobbyException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
