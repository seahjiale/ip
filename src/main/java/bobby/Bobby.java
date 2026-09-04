package bobby;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * A minimal chatbot that echoes commands until the user says goodbye.
 */
public class Bobby {
    /** Storage used by the GUI-backed chatbot instance. */
    private final Storage storage;

    /** Parser used by the GUI-backed chatbot instance. */
    private final Parser parser;

    /** Tasks used by the GUI-backed chatbot instance. */
    private TaskList tasks;

    /** Simple class name of the most recently executed command. */
    private String commandType;

    /** Creates a Bobby application instance. */
    public Bobby() {
        this("data/duke.txt");
    }

    /** Creates a Bobby chatbot instance backed by the given task file. */
    Bobby(String filePath) {
        storage = new Storage(filePath);
        parser = new Parser();
        try {
            tasks = storage.load();
        } catch (BobbyException exception) {
            tasks = new TaskList();
        }
    }

    /**
     * Processes one input using Bobby's existing parser, commands, task list, and storage.
     *
     * @param input user input to process
     * @return Bobby's response text
     */
    public String getResponse(String input) {
        ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
        PrintStream responseStream = new PrintStream(responseOutput, true, StandardCharsets.UTF_8);
        Ui responseUi = new Ui(responseStream);
        commandType = null;
        try {
            if (input.isEmpty()) {
                throw new BobbyException("Error! The command cannot be empty!");
            }
            Command command = parser.parse(input);
            assert command != null : "Parser must return a command for valid input";
            command.execute(tasks, responseUi, storage);
            commandType = command.getClass().getSimpleName();
        } catch (BobbyException exception) {
            responseUi.showError(exception.getMessage());
        }
        responseStream.close();
        return responseOutput.toString(StandardCharsets.UTF_8)
                .replace("____________________________________________________________" + System.lineSeparator(), "");
    }

    /**
     * Returns the type of the most recently executed command.
     *
     * @return the command's simple class name, or {@code null} if execution failed
     */
    public String getCommandType() {
        return commandType;
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
