package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("[T][ ] learn Java"));
        assertEquals("AddCommand", bobby.getCommandType());

        bobby.getResponse("mark 1");
        assertEquals("MarkCommand", bobby.getCommandType());

        bobby.getResponse("delete 1");
        assertEquals("DeleteCommand", bobby.getCommandType());

        bobby.getResponse("bye");
        assertEquals("ExitCommand", bobby.getCommandType());
    }
}
