# Manual GUI and compatibility test plan

These checks cover JavaFX behavior and environment combinations that are not
reliable in headless JUnit tests. Record the application version, tester,
date, environment, and result when each case is run. An unchecked case is
planned but has not been claimed as completed.

## Test Case 1: JavaFX conversation flow

- [ ] Start Bobby with `./gradlew run` on Java 25.
- [ ] Confirm the welcome message and both avatars appear without distortion.
- [ ] Enter `todo read book`, `mark 1`, `list`, and `bye`.
- [ ] Confirm user messages are right-aligned and Bobby messages are left-aligned.
- [ ] Confirm successful responses use their command-specific styling.
- [ ] Confirm the conversation automatically scrolls to the latest message.

## Test Case 2: Invalid input feedback

- [ ] Enter `deadline return book /by`.
- [ ] Confirm the error response includes the deadline format and examples.
- [ ] Confirm the input field uses the error style, retains focus, and selects
  the invalid text so it can be replaced immediately.
- [ ] Enter a valid deadline and confirm the input error style clears.

## Test Case 3: Window size and display scaling

- [ ] Repeat Test Cases 1 and 2 at 1366 by 768 with 100% scaling.
- [ ] Repeat Test Cases 1 and 2 at 1920 by 1080 with 125% scaling.
- [ ] Resize the window to its minimum supported size.
- [ ] Confirm text, avatars, the input field, and the send button remain visible
  without overlap or clipping in every configuration.

## Test Case 4: Operating-system compatibility

- [ ] Run Test Case 1 on Windows 11.
- [ ] Run Test Case 1 on macOS using Java 25.
- [ ] Run Test Case 1 on a desktop Linux distribution using Java 25.
- [ ] Confirm task data saved on one launch reloads correctly on the next launch
  for each operating system.

## Test Case 5: English and Chinese operating-system settings

- [ ] Run Test Case 1 with the operating-system display language and locale set
  to English.
- [ ] Run Test Case 1 with the operating-system display language and locale set
  to Chinese.
- [ ] In both environments, confirm Bobby's English labels remain readable,
  dates retain Bobby's documented English format, and saved tasks reload without
  character corruption.
- [ ] Add and find a task containing Chinese characters to verify UTF-8 input,
  display, persistence, and search behavior.
