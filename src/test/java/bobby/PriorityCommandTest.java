package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests priority updates and persistence rollback. */
public class PriorityCommandTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that a valid command updates the selected task without reordering the list. */
    @Test
    public void execute_validPriority_taskUpdatedWithoutReordering() throws BobbyException {
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        TaskList tasks = new TaskList();
        tasks.add(firstTask);
        tasks.add(secondTask);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(output, true, StandardCharsets.UTF_8));
        Storage storage = new Storage(temporaryDirectory.resolve("duke.txt").toString());

        new PriorityCommand("priority 2 HIGH").execute(tasks, ui, storage);

        assertEquals(firstTask, tasks.get(0));
        assertEquals(secondTask, tasks.get(1));
        assertEquals(Priority.HIGH, secondTask.getPriority());
        assertEquals("Beep boop! I've updated this task's priority:" + System.lineSeparator()
                + "  [T][ ][P: HIGH] second task" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    /** Verifies that the old priority is restored when persistence fails. */
    @Test
    public void execute_saveFails_previousPriorityRestored() {
        Task task = new ToDo("read book");
        task.setPriority(Priority.LOW);
        TaskList tasks = new TaskList();
        tasks.add(task);
        Ui ui = new Ui(new PrintStream(new ByteArrayOutputStream()));
        Storage storage = new FailingStorage(temporaryDirectory.resolve("duke.txt").toString());

        assertThrows(BobbyException.class, () ->
                new PriorityCommand("priority 1 high").execute(tasks, ui, storage));

        assertEquals(Priority.LOW, task.getPriority());
    }

    /** Verifies empty-list validation and that assigning the current priority succeeds. */
    @Test
    public void execute_emptyListAndSamePriority_expectedValidationAndSuccess()
            throws BobbyException, IOException {
        Path taskFile = temporaryDirectory.resolve("duke.txt");
        Storage storage = new Storage(taskFile.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(output, true, StandardCharsets.UTF_8));

        BobbyException exception = assertThrows(BobbyException.class, () ->
                new PriorityCommand("priority 1 high").execute(new TaskList(), ui, storage));
        assertEquals("No tasks available to prioritize.", exception.getMessage());

        Task task = new ToDo("read book");
        task.setPriority(Priority.HIGH);
        TaskList tasks = new TaskList(List.of(task));

        new PriorityCommand("priority 1 high").execute(tasks, ui, storage);

        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(List.of("T | 0 | read book | HIGH"),
                Files.readAllLines(taskFile, StandardCharsets.UTF_8));
    }

    /** Storage implementation that always fails when asked to save. */
    private static class FailingStorage extends Storage {
        FailingStorage(String filePath) {
            super(filePath);
        }

        @Override
        public void save(TaskList tasks) throws BobbyException {
            throw new BobbyException("Error! Could not save tasks to disk.");
        }
    }
}
