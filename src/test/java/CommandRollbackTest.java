import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests command rollback when saving a task-list change fails. */
public class CommandRollbackTest {

    private Path temporaryDirectory;

    /** Creates a test directory inside the writable project build directory. */
    @BeforeEach
    public void setUp() throws Exception {
        Path testRoot = Path.of("build", "test-data");
        Files.createDirectories(testRoot);
        temporaryDirectory = Files.createDirectory(
                testRoot.resolve("rollback-" + UUID.randomUUID()));
    }

    /** Removes files created by the test. */
    @AfterEach
    public void tearDown() throws Exception {
        try (Stream<Path> paths = Files.walk(temporaryDirectory)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (java.io.IOException exception) {
                    throw new UncheckedIOException(exception);
                }
            });
        } catch (UncheckedIOException exception) {
            throw exception.getCause();
        }
    }

    /** Verifies that a failed add does not leave the new task in the list. */
    @Test
    public void addCommand_saveFails_taskAdditionRolledBack() throws Exception {
        TaskList tasks = new TaskList();
        Storage failingStorage = createFailingStorage();

        assertThrows(BobbyException.class,
                () -> new AddCommand(new ToDo("new task")).execute(tasks, new Ui(), failingStorage));

        assertEquals(0, tasks.size());
    }

    /** Verifies that a failed mark restores an originally incomplete task. */
    @Test
    public void markCommand_saveFails_originalIncompleteStateRestored() throws Exception {
        Task task = new ToDo("task");
        TaskList tasks = new TaskList();
        tasks.add(task);

        assertThrows(BobbyException.class,
                () -> new MarkCommand("mark 1").execute(tasks, new Ui(), createFailingStorage()));

        assertFalse(task.isDone());
    }

    /** Verifies that a failed unmark restores an originally completed task. */
    @Test
    public void unmarkCommand_saveFails_originalCompletedStateRestored() throws Exception {
        Task task = new ToDo("task");
        task.markAsDone();
        TaskList tasks = new TaskList();
        tasks.add(task);

        assertThrows(BobbyException.class,
                () -> new UnmarkCommand("unmark 1")
                        .execute(tasks, new Ui(), createFailingStorage()));

        assertTrue(task.isDone());
    }

    /** Verifies that a failed delete restores the task at its original position. */
    @Test
    public void deleteCommand_saveFails_deletedTaskRestoredAtOriginalIndex() throws Exception {
        Task firstTask = new ToDo("first");
        Task deletedTask = new ToDo("deleted");
        Task lastTask = new ToDo("last");
        TaskList tasks = new TaskList();
        tasks.add(firstTask);
        tasks.add(deletedTask);
        tasks.add(lastTask);

        assertThrows(BobbyException.class,
                () -> new DeleteCommand("delete 2")
                        .execute(tasks, new Ui(), createFailingStorage()));

        assertEquals(3, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(deletedTask, tasks.get(1));
        assertSame(lastTask, tasks.get(2));
    }

    /** Creates storage whose parent path is a file, forcing save to fail predictably. */
    private Storage createFailingStorage() throws Exception {
        Path parentFile = Files.createTempFile(temporaryDirectory, "not-a-directory", ".tmp");
        return new Storage(parentFile.resolve("tasks.txt").toString());
    }
}
