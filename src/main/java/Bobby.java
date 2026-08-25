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
                ExitCommand exitCommand = new ExitCommand();
                exitCommand.execute(tasks, ui, storage);
                break;
            }

            ui.showSeparator();
            try {
                if (parser.isExitCommand(command)) {
                    Command exitCommand = parser.parse(command);
                    exitCommand.execute(tasks, ui, storage);
                    if (exitCommand.isExit()) {
                        break;
                    }
                } else if (command.isEmpty()) {
                    throw new BobbyException("Error! The command cannot be empty!");
                } else if (command.equals("list")) {
                    parser.parse(command).execute(tasks, ui, storage);
                } else if (parser.isCommand(command, "mark")
                        || parser.isCommand(command, "unmark")) {
                    parser.parse(command).execute(tasks, ui, storage);
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

}
