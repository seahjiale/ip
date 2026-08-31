package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests typed event dates and their display and storage representations. */
public class EventTest {

    /** Verifies that event input is parsed into LocalDate values. */
    @Test
    public void fromInput_isoDates_localDatesReturned() {
        Event event = Event.fromInput("project meeting", "2026-08-25", "2026-08-26");

        assertEquals(LocalDate.of(2026, 8, 25), event.getFrom());
        assertEquals(LocalDate.of(2026, 8, 26), event.getTo());
    }

    /** Verifies that event dates are formatted consistently for users and storage. */
    @Test
    public void representations_typedDates_formattedConsistently() {
        Event event = new Event("project meeting",
                LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 26));

        assertEquals("[E][ ] project meeting (from: Aug 25 2026 to: Aug 26 2026)",
                event.toString());
        assertEquals("E | 0 | project meeting | 2026-08-25 | 2026-08-26",
                event.toStorageString());
    }
}
