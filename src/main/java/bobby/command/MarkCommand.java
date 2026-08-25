package bobby.command;

import bobby.exception.BobbyException;
import bobby.model.Task;
import bobby.model.TaskList;
import bobby.parser.Parser;
import bobby.storage.Storage;
import bobby.ui.Ui;

/** Marks a selected task as done and persists the updated task list. */
public class MarkCommand extends Command {
    private final String command;

    /** Creates a mark command containing the user's original input. */
    public MarkCommand(String command) {
        this.command = command;
    }

    /** Marks the selected task, rolling back if saving fails. */
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

    /** Restores the task's previous completion state after a failed save. */
    private void restoreTaskStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.unmarkAsDone();
        }
    }
}
