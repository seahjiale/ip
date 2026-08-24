import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Represents a task with a typed date and optional time deadline. */
public class Deadline extends Task {
    private static final DateTimeFormatter DATE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private final LocalDateTime by;
    private final boolean includesTime;

    /** Creates an incomplete deadline task for a date without a time. */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by.atStartOfDay();
        this.includesTime = false;
    }

    /** Creates an incomplete deadline task for a date and time. */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
        this.includesTime = true;
    }

    /** Creates a deadline by parsing one of Bobby's supported input formats. */
    public static Deadline fromInput(String description, String input)
            throws DateTimeParseException {
        if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return new Deadline(description, LocalDate.parse(input, DATE_INPUT_FORMAT));
        }
        return new Deadline(description, LocalDateTime.parse(input, DATE_TIME_INPUT_FORMAT));
    }

    /** Returns the deadline as a typed date and time. */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns the date in the display format, including a time when one was supplied. */
    private String getDisplayDate() {
        String date = by.format(DATE_DISPLAY_FORMAT);
        return includesTime ? date + " " + by.format(TIME_DISPLAY_FORMAT) : date;
    }

    /** Returns the date in the canonical format used when saving tasks to disk. */
    private String getStorageDate() {
        return includesTime
                ? by.format(DATE_TIME_INPUT_FORMAT)
                : by.toLocalDate().format(DATE_INPUT_FORMAT);
    }

    /** Returns this deadline task in the format used by the command line interface. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + getDisplayDate() + ")";
    }

    /** Returns this deadline in the format used when saving tasks to disk. */
    @Override
    public String toStorageString() {
        return "D | " + getStorageStatus() + " | " + getDescription() + " | " + getStorageDate();
    }
}
