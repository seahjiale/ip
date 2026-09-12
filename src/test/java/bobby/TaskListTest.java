package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list search behavior. */
public class TaskListTest {

    /** Verifies that search is case-insensitive, substring-based, and ordered. */
    @Test
    public void findByDescription_keywordMatchesIgnoringCaseInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return BOOK", java.time.LocalDate.of(2026, 6, 6)));
        tasks.add(new ToDo("go jogging"));

        List<Task> matchingTasks = tasks.findByDescription("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("return BOOK", matchingTasks.get(1).getDescription());
    }

    /** Verifies that a keyword with no matches returns an empty result. */
    @Test
    public void findByDescription_noMatch_emptyListReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        assertEquals(0, tasks.findByDescription("movie").size());
    }

    /** Verifies that duplicate checks compare type, normalized description, and date details. */
    @Test
    public void containsTaskWithSameDetails_variedCandidates_expectedResultReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Read Book"));
        tasks.add(new Deadline("return book", java.time.LocalDate.of(2026, 6, 6)));

        assertTrue(tasks.containsTaskWithSameDetails(new ToDo("  read   book ")));
        assertTrue(tasks.containsTaskWithSameDetails(
                new Deadline("RETURN BOOK", java.time.LocalDate.of(2026, 6, 6))));
        assertFalse(tasks.containsTaskWithSameDetails(
                new Deadline("return book", java.time.LocalDate.of(2026, 6, 7))));
        assertFalse(tasks.containsTaskWithSameDetails(new ToDo("return book")));
    }
}
