import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests task state transitions and the common task representations. */
public class TaskTest {

    /** Verifies the default state and representations of a newly created task. */
    @Test
    public void task_newTask_incompleteAndCorrectlyFormatted() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("0", task.getStorageStatus());
        assertEquals("[ ] read book", task.toString());
        assertEquals("T | 0 | read book", task.toStorageString());
    }

    /** Verifies that marking and unmarking update all completion representations. */
    @Test
    public void task_markAndUnmark_stateAndRepresentationsUpdated() {
        Task task = new Task("read book");

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("1", task.getStorageStatus());
        assertEquals("[X] read book", task.toString());
        assertEquals("T | 1 | read book", task.toStorageString());

        task.unmarkAsDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("0", task.getStorageStatus());
    }

    /** Verifies the task-specific display and storage formats for to-dos and events. */
    @Test
    public void task_subclasses_displayAndStorageFormatsCorrectly() {
        ToDo toDo = new ToDo("buy milk");
        Event event = new Event("team meeting", "Monday", "Tuesday");

        assertEquals("[T][ ] buy milk", toDo.toString());
        assertEquals("T | 0 | buy milk", toDo.toStorageString());
        assertEquals("[E][ ] team meeting (from: Monday to: Tuesday)", event.toString());
        assertEquals("E | 0 | team meeting | Monday | Tuesday", event.toStorageString());

        event.markAsDone();
        assertEquals("[E][X] team meeting (from: Monday to: Tuesday)", event.toString());
        assertEquals("E | 1 | team meeting | Monday | Tuesday", event.toStorageString());
    }
}
