/** Parses command names, command arguments, and task numbers from user input. */
public class Parser {

    /** Returns whether an input is a command or has whitespace-separated arguments. */
    public boolean isCommand(String input, String commandName) {
        return input.equals(commandName)
                || (input.length() > commandName.length()
                && input.startsWith(commandName)
                && Character.isWhitespace(input.charAt(commandName.length())));
    }

    /** Returns the argument text after a command name. */
    public String getCommandArgument(String command, String commandName) {
        if (command.length() <= commandName.length()) {
            return "";
        }
        return command.substring(commandName.length()).trim();
    }

    /** Parses and validates a one-based task number from a task command. */
    public int parseTaskIndex(String command, String commandName, int taskCount)
            throws BobbyException {
        if (taskCount == 0) {
            throw new BobbyException("No tasks available to " + commandName + ".");
        }

        String taskNumber = getCommandArgument(command, commandName);
        if (taskNumber.isEmpty()) {
            throw new BobbyException("Error! The task number cannot be empty!");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new BobbyException("Error! The task number must be a valid integer.");
        }
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new BobbyException("Error! The task number must be between 1 and "
                    + taskCount + ".");
        }
        return taskIndex;
    }
}
