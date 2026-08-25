import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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

        List<Task> tasks;
        try {
            tasks = storage.load();
        } catch (BobbyException exception) {
            tasks = new ArrayList<>();
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
                } else if (parser.isCommand(command, "todo")) {
                    String description = parser.getCommandArgument(command, "todo");
                    if (description.trim().isEmpty()) {
                        throw new BobbyException("Error! The description of a todo cannot be empty!");
                    } else {
                        validateStorageField(description);
                        Task task = new ToDo(description.trim());
                        tasks.add(task);
                        try {
                            storage.save(tasks);
                        } catch (BobbyException exception) {
                            tasks.remove(tasks.size() - 1);
                            throw exception;
                        }
                        ui.showTaskAdded(task, tasks.size());
                    }
                } else if (parser.isCommand(command, "deadline")) {
                    String deadlineDetails = parser.getCommandArgument(command, "deadline");
                    String[] deadlineParts = deadlineDetails.split(" /by ", 2);
                    boolean hasNoDescription = deadlineDetails.trim().isEmpty()
                            || deadlineDetails.trim().startsWith("/by")
                            || (deadlineParts.length > 1 && deadlineParts[0].trim().isEmpty());
                    if (hasNoDescription) {
                        throw new BobbyException("Error! The description of a deadline cannot be empty!");
                    } else if (deadlineParts.length < 2 || deadlineParts[1].trim().isEmpty()) {
                        throw new BobbyException("Error! The date of a deadline cannot be empty!");
                    } else {
                        validateStorageField(deadlineParts[0]);
                        validateStorageField(deadlineParts[1]);
                        Task task;
                        try {
                            task = Deadline.fromInput(deadlineParts[0].trim(), deadlineParts[1].trim());
                        } catch (DateTimeParseException exception) {
                            throw new BobbyException("Error! The deadline must be a valid date. "
                                    + "Use yyyy-MM-dd or d/M/yyyy HHmm.");
                        }
                        tasks.add(task);
                        try {
                            storage.save(tasks);
                        } catch (BobbyException exception) {
                            tasks.remove(tasks.size() - 1);
                            throw exception;
                        }
                        ui.showTaskAdded(task, tasks.size());
                    }
                } else if (parser.isCommand(command, "event")) {
                    String eventDetails = parser.getCommandArgument(command, "event");
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
                        throw new BobbyException("Error! Start time of an event cannot be empty. Try again!");
                    } else if (to.isEmpty()) {
                        throw new BobbyException("Error! End time of an event cannot be empty. Try again!");
                    } else {
                        validateStorageField(description);
                        validateStorageField(from);
                        validateStorageField(to);
                        Task task = new Event(description, from, to);
                        tasks.add(task);
                        try {
                            storage.save(tasks);
                        } catch (BobbyException exception) {
                            tasks.remove(tasks.size() - 1);
                            throw exception;
                        }
                        ui.showTaskAdded(task, tasks.size());
                    }
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

    /** Rejects storage separator characters that would make a saved line ambiguous. */
    private static void validateStorageField(String field) throws BobbyException {
        if (field.contains("|")) {
            throw new BobbyException("Error! Task details cannot contain the '|' character!");
        }
    }

}
