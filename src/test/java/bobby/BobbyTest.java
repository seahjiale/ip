package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Bobby's response method used by the JavaFX controller. */
public class BobbyTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that a GUI response uses the existing add-command behavior. */
    @Test
    public void getResponse_addCommand_returnsExistingCommandResponse() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("duke.txt").toString());

        String response = bobby.getResponse("todo learn Java");

        assertTrue(response.contains("Boop! I've tucked this task into the list:"));
        assertTrue(response.contains("[T][ ] learn Java"));
        assertEquals("AddCommand", bobby.getCommandType());
        assertFalse(bobby.wasLastResponseError());

        bobby.getResponse("mark 1");
        assertEquals("MarkCommand", bobby.getCommandType());

        bobby.getResponse("unmark 1");
        assertEquals("UnmarkCommand", bobby.getCommandType());

        bobby.getResponse("delete 1");
        assertEquals("DeleteCommand", bobby.getCommandType());

        bobby.getResponse("bye");
        assertEquals("ExitCommand", bobby.getCommandType());
    }

    /** Verifies priority aliases, exact output, clearing, and stable task order. */
    @Test
    public void getResponse_priorityCommands_updatePriorityWithoutReordering() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("priority.txt").toString());
        bobby.getResponse("todo first task");
        bobby.getResponse("todo second task");

        String updateResponse = bobby.getResponse("priority 2 1");

        assertEquals("Beep boop! I've updated this task's priority:" + System.lineSeparator()
                + "  [T][ ][P: HIGH] second task" + System.lineSeparator(), updateResponse);
        assertEquals("PriorityCommand", bobby.getCommandType());
        String listResponse = bobby.getResponse("list");
        assertTrue(listResponse.contains("1.[T][ ] first task"));
        assertTrue(listResponse.contains("2.[T][ ][P: HIGH] second task"));

        String clearResponse = bobby.getResponse("priority 2 none");

        assertTrue(clearResponse.contains("[T][ ] second task"));
    }

    /** Verifies that invalid priority input does not change the task. */
    @Test
    public void getResponse_invalidPriority_errorReturnedAndTaskUnchanged() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("invalid-priority.txt").toString());
        bobby.getResponse("todo read book");

        String response = bobby.getResponse("priority 1 urgent");

        assertEquals("Error! The priority level must be high, medium, low, none, 1, 2, or 3."
                + System.lineSeparator() + System.lineSeparator()
                + "Format: priority TASK_NUMBER LEVEL" + System.lineSeparator()
                + "Example: priority 1 high" + System.lineSeparator()
                + "Levels: high, medium, low, none, 1, 2, or 3" + System.lineSeparator(), response);
        assertNull(bobby.getCommandType());
        assertTrue(bobby.wasLastResponseError());

        assertTrue(bobby.getResponse("list").contains("1.[T][ ] read book"));
        assertFalse(bobby.wasLastResponseError());
    }

    /** Verifies that unknown GUI commands return a concise list of valid commands. */
    @Test
    public void getResponse_unknownCommand_availableCommandsReturned() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("unknown-command.txt").toString());

        String response = bobby.getResponse("blah");

        assertTrue(response.contains("Available commands: todo, deadline, event, list, mark, unmark,"));
        assertTrue(response.contains("delete, find, priority, bye"));
        assertTrue(bobby.wasLastResponseError());
    }

    /** Verifies that invalid deadlines return concrete date and date-time examples. */
    @Test
    public void getResponse_invalidDeadline_concreteExamplesReturned() {
        Bobby bobby = new Bobby(temporaryDirectory.resolve("invalid-deadline.txt").toString());

        String response = bobby.getResponse("deadline return book /by");

        assertTrue(response.contains("Example: deadline return book /by 2026-09-20"));
        assertTrue(response.contains("With time: deadline call client /by 2026-09-20 1800"));
        assertTrue(bobby.wasLastResponseError());
    }
}
