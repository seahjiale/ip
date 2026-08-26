import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
