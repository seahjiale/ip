/**
 * A minimal chatbot that echoes commands until the user says goodbye.
 */
public class Bobby {
    /** Creates a Bobby application instance. */
    public Bobby() {
    }

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
                if (command.isEmpty()) {
                    throw new BobbyException("Error! The command cannot be empty!");
                } else {
                    Command parsedCommand = parser.parse(command);
                    parsedCommand.execute(tasks, ui, storage);
                    if (parsedCommand.isExit()) {
                        break;
                    }
                }
            } catch (BobbyException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showSeparator();
        }
    }

}
