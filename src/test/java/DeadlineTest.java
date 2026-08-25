import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests deadline parsing, typed date values, and display/storage formats. */
public class DeadlineTest {

    /** Verifies parsing and formatting of a date-only deadline. */
    @Test
    public void fromInput_dateOnly_deadlineCreatedWithStartOfDay() {
        Deadline deadline = Deadline.fromInput("submit report", "2026-08-25");

        assertEquals(LocalDateTime.of(2026, 8, 25, 0, 0), deadline.getBy());
        assertEquals("[D][ ] submit report (by: Aug 25 2026)", deadline.toString());
        assertEquals("D | 0 | submit report | 2026-08-25", deadline.toStorageString());
    }

    /** Verifies parsing and formatting of a deadline that includes a time. */
    @Test
    public void fromInput_dateAndTime_deadlinePreservesTime() {
        Deadline deadline = Deadline.fromInput("call client", "25/8/2026 0930");

        assertEquals(LocalDateTime.of(2026, 8, 25, 9, 30), deadline.getBy());
        assertEquals("[D][ ] call client (by: Aug 25 2026 9:30 AM)", deadline.toString());
        assertEquals("D | 0 | call client | 25/8/2026 0930", deadline.toStorageString());
    }

    /** Verifies that strict parsing rejects impossible dates and times. */
    @Test
    public void fromInput_invalidDateOrTime_dateTimeParseExceptionThrown() {
        assertThrows(DateTimeParseException.class,
                () -> Deadline.fromInput("invalid date", "2026-02-30"));
        assertThrows(DateTimeParseException.class,
                () -> Deadline.fromInput("invalid time", "25/8/2026 2500"));
    }

    /** Verifies that a completed deadline uses the completed state in both formats. */
    @Test
    public void deadline_markAsDone_completedStateStoredAndDisplayed() {
        Deadline deadline = Deadline.fromInput("submit report", "2026-08-25");

        deadline.markAsDone();

        assertEquals("[D][X] submit report (by: Aug 25 2026)", deadline.toString());
        assertEquals("D | 1 | submit report | 2026-08-25", deadline.toStorageString());
    }
}
