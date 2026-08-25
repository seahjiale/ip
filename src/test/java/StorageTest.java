import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests persistence, reconstruction, and validation of stored tasks. */
public class StorageTest {

    private Path temporaryDirectory;

    /** Creates a test directory inside the writable project build directory. */
    @BeforeEach
    public void setUp() throws Exception {
        Path testRoot = Path.of("build", "test-data");
        Files.createDirectories(testRoot);
        temporaryDirectory = Files.createDirectory(
                testRoot.resolve("storage-" + UUID.randomUUID()));
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

    /** Verifies that all supported task types and completion states survive a round trip. */
    @Test
    public void saveAndLoad_mixedTaskList_allTasksAndStatesPreserved() throws Exception {
        Path taskFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(taskFile.toString());
        TaskList tasks = new TaskList();
        Task todo = new ToDo("read book");
        Task deadline = Deadline.fromInput("return book", "25/8/2026 0930");
        Task event = new Event("project meeting", "Monday", "Tuesday");
        deadline.markAsDone();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.save(tasks);

        assertEquals(List.of(
                "T | 0 | read book",
                "D | 1 | return book | 25/8/2026 0930",
                "E | 0 | project meeting | Monday | Tuesday"),
                Files.readAllLines(taskFile));

        TaskList loadedTasks = storage.load();
        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
        assertEquals("[D][X] return book (by: Aug 25 2026 9:30 AM)",
                loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Monday to: Tuesday)",
                loadedTasks.get(2).toString());
    }

    /** Verifies that saving an empty list truncates any previous task data. */
    @Test
    public void save_emptyTaskList_existingFileIsCleared() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(taskFile.toString());
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("temporary task"));
        storage.save(tasks);

        storage.save(new TaskList());

        assertFalse(Files.readAllLines(taskFile).iterator().hasNext());
    }

    /** Verifies that loading a missing file starts with an empty task list. */
    @Test
    public void load_missingFile_emptyTaskListReturned() throws BobbyException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertEquals(0, storage.load().size());
    }

    /** Verifies that malformed stored lines are rejected instead of being partially loaded. */
    @Test
    public void load_malformedTaskLines_exceptionThrown() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(taskFile.toString());
        List<String> malformedLines = List.of(
                "T | 0",
                "X | 0 | unknown task",
                "T | 0 | task | extra field",
                "T | 2 | task",
                "D | 0 | task | not-a-date",
                "E | 0 | task | only one detail");

        for (String malformedLine : malformedLines) {
            Files.writeString(taskFile, malformedLine);

            BobbyException exception = assertThrows(BobbyException.class, storage::load);

            assertEquals("Error! Could not load tasks from disk.", exception.getMessage());
        }
    }
}
