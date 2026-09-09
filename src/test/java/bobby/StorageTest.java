package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests legacy and priority-aware task storage. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that legacy records load with no priority. */
    @Test
    public void load_legacyRecords_nonePriorityAssigned() throws IOException, BobbyException {
        Path taskFile = temporaryDirectory.resolve("duke.txt");
        Files.write(taskFile, List.of(
                "T | 0 | read book",
                "D | 1 | return book | 2026-10-01",
                "E | 0 | meeting | 2026-10-01 | 2026-10-02"), StandardCharsets.UTF_8);

        TaskList tasks = new Storage(taskFile.toString()).load();

        assertEquals(3, tasks.size());
        assertEquals(Priority.NONE, tasks.get(0).getPriority());
        assertEquals(Priority.NONE, tasks.get(1).getPriority());
        assertEquals(Priority.NONE, tasks.get(2).getPriority());
        assertTrue(tasks.get(1).isDone());
    }

    /** Verifies that all task types save and reload canonical priority values. */
    @Test
    public void saveAndLoad_prioritizedTasks_prioritiesRoundTrip()
            throws IOException, BobbyException {
        Path taskFile = temporaryDirectory.resolve("duke.txt");
        Task todo = new ToDo("read book");
        todo.setPriority(Priority.HIGH);
        Task deadline = new Deadline("return book", LocalDate.of(2026, 10, 1));
        deadline.setPriority(Priority.MEDIUM);
        Task event = new Event("meeting", LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 2));
        event.setPriority(Priority.LOW);
        TaskList tasks = new TaskList(List.of(todo, deadline, event));
        Storage storage = new Storage(taskFile.toString());

        storage.save(tasks);

        assertEquals(List.of(
                "T | 0 | read book | HIGH",
                "D | 0 | return book | 2026-10-01 | MEDIUM",
                "E | 0 | meeting | 2026-10-01 | 2026-10-02 | LOW"),
                Files.readAllLines(taskFile, StandardCharsets.UTF_8));
        TaskList loadedTasks = storage.load();
        assertEquals(Priority.HIGH, loadedTasks.get(0).getPriority());
        assertEquals(Priority.MEDIUM, loadedTasks.get(1).getPriority());
        assertEquals(Priority.LOW, loadedTasks.get(2).getPriority());
    }

    /** Verifies that an invalid stored priority causes the complete load to fail. */
    @Test
    public void load_invalidStoredPriority_exceptionThrown() throws IOException {
        Path taskFile = temporaryDirectory.resolve("duke.txt");
        Files.writeString(taskFile, "T | 0 | read book | URGENT", StandardCharsets.UTF_8);

        BobbyException exception = assertThrows(BobbyException.class, () ->
                new Storage(taskFile.toString()).load());

        assertEquals("Error! Could not load tasks from disk.", exception.getMessage());
    }

    /** Verifies that legacy and priority-aware records can coexist in one file. */
    @Test
    public void load_mixedLegacyAndNewRecords_prioritiesLoadedCorrectly()
            throws IOException, BobbyException {
        Path taskFile = temporaryDirectory.resolve("duke.txt");
        Files.write(taskFile, List.of(
                "T | 0 | legacy task",
                "D | 0 | explicit none | 2026-10-01 | NONE",
                "E | 1 | prioritized event | 2026-10-01 | 2026-10-02 | HIGH"),
                StandardCharsets.UTF_8);

        TaskList tasks = new Storage(taskFile.toString()).load();

        assertEquals(3, tasks.size());
        assertEquals(Priority.NONE, tasks.get(0).getPriority());
        assertEquals(Priority.NONE, tasks.get(1).getPriority());
        assertEquals(Priority.HIGH, tasks.get(2).getPriority());
        assertTrue(tasks.get(2).isDone());
    }
}
