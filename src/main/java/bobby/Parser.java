package bobby;

import java.time.format.DateTimeParseException;

/** Parses command names, task commands, arguments, and task numbers from user input. */
public class Parser {

    /** Creates a parser for Bobby commands. */
    public Parser() {
    }

    /**
     * Returns whether an input is a command or has whitespace-separated arguments.
     *
     * @param input complete user input to inspect
     * @param commandName command name to match
     * @return {@code true} when the input is exactly the command or starts with it
     */
    public boolean isCommand(String input, String commandName) {
        return input.equals(commandName)
                || (input.length() > commandName.length()
                && input.startsWith(commandName)
                && Character.isWhitespace(input.charAt(commandName.length())));
    }

    /**
     * Returns the argument text after a command name.
     *
     * @param command complete user input
     * @param commandName command whose argument should be extracted
     * @return trimmed text following the command name, or an empty string
     */
    public String getCommandArgument(String command, String commandName) {
        if (command.length() <= commandName.length()) {
            return "";
        }
        return command.substring(commandName.length()).trim();
    }

    /**
     * Returns whether the input requests that Bobby exits.
     *
     * @param command complete user input
     * @return {@code true} when the input is {@code bye}, ignoring case
     */
    public boolean isExitCommand(String command) {
        return command.equalsIgnoreCase("bye");
    }

    /**
     * Returns whether the command creates a to-do, deadline, or event task.
     *
     * @param command complete user input
     * @return {@code true} for a supported task-creation command
     */
    public boolean isTaskCreationCommand(String command) {
        return isCommand(command, "todo")
                || isCommand(command, "deadline")
                || isCommand(command, "event");
    }

    /**
     * Parses a supported task command into an executable command.
     *
     * @param command complete user input
     * @return command object that performs the requested action
     * @throws BobbyException if the input does not begin with a supported command
     */
    public Command parse(String command) throws BobbyException {
        if (isExitCommand(command)) {
            return new ExitCommand();
        } else if (command.equals("list")) {
            return new ListCommand();
        } else if (isCommand(command, "find")) {
            String keyword = getCommandArgument(command, "find");
            if (keyword.isEmpty()) {
                throw new BobbyException("Error! The search keyword cannot be empty!");
            }
            return new FindCommand(keyword);
        } else if (isCommand(command, "mark")) {
            return new MarkCommand(command);
        } else if (isCommand(command, "unmark")) {
            return new UnmarkCommand(command);
        } else if (isCommand(command, "delete")) {
            return new DeleteCommand(command);
        } else if (isCommand(command, "todo")) {
            return new AddCommand(parseTodo(command));
        } else if (isCommand(command, "deadline")) {
            return new AddCommand(parseDeadline(command));
        } else if (isCommand(command, "event")) {
            return new AddCommand(parseEvent(command));
        }
        throw new BobbyException("No such task type available. Try again!");
    }

    /**
     * Parses and validates a one-based task number from a task command.
     *
     * @param command complete user input containing the task number
     * @param commandName command whose task number is being parsed
     * @param taskCount number of tasks currently available
     * @return zero-based index corresponding to the requested task
     * @throws BobbyException if no tasks exist or the task number is invalid
     */
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

    /**
     * Parses a to-do command and validates its description.
     *
     * @param command complete to-do command
     * @return parsed to-do task
     * @throws BobbyException if the description is empty or unsafe for storage
     */
    private Task parseTodo(String command) throws BobbyException {
        String description = getCommandArgument(command, "todo");
        if (description.trim().isEmpty()) {
            throw new BobbyException("Error! The description of a todo cannot be empty!");
        }
        validateStorageField(description);
        return new ToDo(description.trim());
    }

    /**
     * Parses a deadline command and validates its description and date.
     *
     * @param command complete deadline command
     * @return parsed deadline task
     * @throws BobbyException if the description or date is invalid
     */
    private Task parseDeadline(String command) throws BobbyException {
        String deadlineDetails = getCommandArgument(command, "deadline");
        String[] deadlineParts = deadlineDetails.split(" /by ", 2);
        boolean hasNoDescription = deadlineDetails.trim().isEmpty()
                || deadlineDetails.trim().startsWith("/by")
                || (deadlineParts.length > 1 && deadlineParts[0].trim().isEmpty());
        if (hasNoDescription) {
            throw new BobbyException("Error! The description of a deadline cannot be empty!");
        } else if (deadlineParts.length < 2 || deadlineParts[1].trim().isEmpty()) {
            throw new BobbyException("Error! The date of a deadline cannot be empty!");
        }

        validateStorageField(deadlineParts[0]);
        validateStorageField(deadlineParts[1]);
        try {
            return Deadline.fromInput(deadlineParts[0].trim(), deadlineParts[1].trim());
        } catch (DateTimeParseException exception) {
            throw new BobbyException("Error! The deadline must be a valid date. "
                    + "Use yyyy-MM-dd or d/M/yyyy HHmm.");
        }
    }

    /**
     * Parses an event command and validates its description, start, and end dates.
     *
     * @param command complete event command
     * @return parsed event task
     * @throws BobbyException if any event field is empty, invalid, or unsafe for storage
     */
    private Task parseEvent(String command) throws BobbyException {
        String eventDetails = getCommandArgument(command, "event");
        int fromMarkerIndex = eventDetails.indexOf("/from");
        int toMarkerIndex = eventDetails.indexOf("/to");

        String description;
        String from = "";
        String to = "";
        if (fromMarkerIndex >= 0) {
            description = eventDetails.substring(0, fromMarkerIndex).trim();
            if (toMarkerIndex > fromMarkerIndex) {
                from = eventDetails.substring(fromMarkerIndex + 5, toMarkerIndex).trim();
            } else {
                from = eventDetails.substring(fromMarkerIndex + 5).trim();
            }
        } else if (toMarkerIndex >= 0) {
            description = eventDetails.substring(0, toMarkerIndex).trim();
        } else {
            description = eventDetails;
        }
        if (toMarkerIndex >= 0) {
            to = eventDetails.substring(toMarkerIndex + 3).trim();
        }

        if (description.isEmpty()) {
            throw new BobbyException("Error! The description of an event cannot be empty!");
        } else if (from.isEmpty()) {
            throw new BobbyException("Error! Start date of an event cannot be empty. Try again!");
        } else if (to.isEmpty()) {
            throw new BobbyException("Error! End date of an event cannot be empty. Try again!");
        }

        validateStorageField(description);
        validateStorageField(from);
        validateStorageField(to);
        try {
            return Event.fromInput(description, from, to);
        } catch (DateTimeParseException exception) {
            throw new BobbyException("Error! Event dates must be valid dates. Use yyyy-MM-dd.");
        }
    }

    /**
     * Rejects storage separator characters that would make a saved line ambiguous.
     *
     * @param field task field to validate
     * @throws BobbyException if {@code field} contains the storage separator
     */
    private void validateStorageField(String field) throws BobbyException {
        if (field.contains("|")) {
            throw new BobbyException("Error! Task details cannot contain the '|' character!");
        }
    }
}
