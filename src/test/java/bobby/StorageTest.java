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

        assertEquals("Error! Could not load tasks because line 1 contains invalid data.",
                exception.getMessage());
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

    /** Verifies that an empty description is rejected with its storage line number. */
    @Test
    public void load_emptyDescription_exceptionIdentifiesLine() throws IOException {
        Path taskFile = temporaryDirectory.resolve("empty-description.txt");
        Files.write(taskFile, List.of(
                "T | 0 | valid task",
                "T | 0 |   | NONE"), StandardCharsets.UTF_8);

        BobbyException exception = assertThrows(BobbyException.class, () ->
                new Storage(taskFile.toString()).load());

        assertEquals("Error! Could not load tasks because line 2 contains invalid data.",
                exception.getMessage());
    }

    /** Verifies that duplicate stored task details are rejected. */
    @Test
    public void load_duplicateTasks_exceptionIdentifiesDuplicateLine() throws IOException {
        Path taskFile = temporaryDirectory.resolve("duplicates.txt");
        Files.write(taskFile, List.of(
                "T | 0 | Read Book | NONE",
                "T | 1 | read book | HIGH"), StandardCharsets.UTF_8);

        BobbyException exception = assertThrows(BobbyException.class, () ->
                new Storage(taskFile.toString()).load());

        assertEquals("Error! Could not load tasks because line 2 duplicates an earlier task.",
                exception.getMessage());
    }

    /** Verifies that a failed load prevents later commands from overwriting the data file. */
    @Test
    public void save_afterFailedLoad_originalFilePreserved() throws IOException {
        Path taskFile = temporaryDirectory.resolve("protected.txt");
        String malformedData = "T | invalid | read book | NONE";
        Files.writeString(taskFile, malformedData, StandardCharsets.UTF_8);
        Storage storage = new Storage(taskFile.toString());
        assertThrows(BobbyException.class, storage::load);

        BobbyException exception = assertThrows(BobbyException.class, () ->
                storage.save(new TaskList(List.of(new ToDo("replacement")))));

        assertEquals("Error! Bobby will not overwrite task data that failed to load. "
                + "Fix or move the data file, then restart Bobby.", exception.getMessage());
        assertEquals(malformedData, Files.readString(taskFile, StandardCharsets.UTF_8));
    }

    /** Verifies that a directory used as the data file produces a controlled load error. */
    @Test
    public void load_dataPathIsDirectory_controlledExceptionThrown() {
        BobbyException exception = assertThrows(BobbyException.class, () ->
                new Storage(temporaryDirectory.toString()).load());

        assertEquals("Error! Could not load tasks from disk. "
                + "Check that the file exists and is readable.", exception.getMessage());
    }

    /** Verifies that a stored event with an invalid date range is rejected. */
    @Test
    public void load_eventWithNonIncreasingDates_exceptionThrown() throws IOException {
        Path taskFile = temporaryDirectory.resolve("invalid-event.txt");
        Files.writeString(taskFile,
                "E | 0 | meeting | 2026-10-02 | 2026-10-01 | NONE",
                StandardCharsets.UTF_8);

        BobbyException exception = assertThrows(BobbyException.class, () ->
                new Storage(taskFile.toString()).load());

        assertEquals("Error! Could not load tasks because line 1 contains invalid data.",
                exception.getMessage());
    }

    /** Verifies that atomic saving also supports filenames shorter than three characters. */
    @Test
    public void save_shortFilename_tasksSaved() throws BobbyException, IOException {
        Path taskFile = temporaryDirectory.resolve("db");
        Storage storage = new Storage(taskFile.toString());

        storage.save(new TaskList(List.of(new ToDo("read book"))));

        assertEquals("T | 0 | read book | NONE" + System.lineSeparator(),
                Files.readString(taskFile, StandardCharsets.UTF_8));
    }
}
