/**
 * A minimal chatbot that echoes commands until the user says goodbye.
 */
public class Bobby {
    /**
     * Prints Bobby's welcome message, stores tasks, changes task completion states, deletes tasks,
     * lists tasks, and exits on {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data/duke.txt");
        Parser parser = new Parser();
        ui.showWelcome();

        TaskList tasks;
        try {
            tasks = storage.load();
        } catch (BobbyException exception) {
            tasks = new TaskList();
            ui.showError(exception.getMessage());
        }
        while (true) {
            String command = ui.readCommand();
            if (command == null) {
                ui.showSeparator();
                ui.showGoodbye();
                break;
            }

            ui.showSeparator();
            if (command.equalsIgnoreCase("bye")) {
                ui.showGoodbye();
                break;
            }

            try {
                if (command.isEmpty()) {
                    throw new BobbyException("Error! The command cannot be empty!");
                } else if (command.equals("list")) {
                    ui.showTaskList(tasks);
                } else if (parser.isCommand(command, "mark")) {
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
                } else if (parser.isCommand(command, "unmark")) {
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
                } else if (parser.isCommand(command, "delete")) {
                    int taskIndex = parser.parseTaskIndex(command, "delete", tasks.size());
                    Task deletedTask = tasks.remove(taskIndex);
                    try {
                        storage.save(tasks);
                    } catch (BobbyException exception) {
                        tasks.add(taskIndex, deletedTask);
                        throw exception;
                    }
                    ui.showTaskDeleted(deletedTask, tasks.size());
                } else if (parser.isTaskCreationCommand(command)) {
                    parser.parse(command).execute(tasks, ui, storage);
                } else {
                    throw new BobbyException("No such task type available. Try again!");
                }
            } catch (BobbyException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showSeparator();
        }
    }

    /** Restores a task's completion state after a failed save. */
    private static void restoreTaskStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.unmarkAsDone();
        }
    }

}
