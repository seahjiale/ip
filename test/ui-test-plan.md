# UI test plan

Run this plan with:

```text
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

Each expected-output block contains the complete console output for its input.

## Test Case 1: Mark and unmark a to-do task

### Aim

Verify that a new to-do task is shown as incomplete, can be marked done, and
can be changed back to incomplete.

### Input

```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected Output

```text
____________________________________________________________
██████╗  ██████╗ ██████╗ ██████╗ ██╗   ██╗
██╔══██╗██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
██████╔╝██║   ██║██████╔╝██████╔╝ ╚████╔╝
██╔══██╗██║   ██║██╔══██╗██╔══██╗  ╚██╔╝
██████╔╝╚██████╔╝██████╔╝██████╔╝   ██║
╚═════╝  ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝
Hello! I'm Bobby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 2: Add and list a to-do task

### Aim

Verify that the `todo` command creates a task without a date or time and that
the task is displayed with the `[T]` type marker.

### Input

```text
todo borrow book
list
bye
```

### Expected Output

```text
____________________________________________________________
██████╗  ██████╗ ██████╗ ██████╗ ██╗   ██╗
██╔══██╗██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
██████╔╝██║   ██║██████╔╝██████╔╝ ╚████╔╝
██╔══██╗██║   ██║██╔══██╗██╔══██╗  ╚██╔╝
██████╔╝╚██████╔╝██████╔╝██████╔╝   ██║
╚═════╝  ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝
Hello! I'm Bobby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 3: Add and list a deadline task

### Aim

Verify that the `deadline` command creates a task with its deadline text and
displays it with the `[D]` type marker.

### Input

```text
deadline return book /by Sunday
list
bye
```

### Expected Output

```text
____________________________________________________________
██████╗  ██████╗ ██████╗ ██████╗ ██╗   ██╗
██╔══██╗██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
██████╔╝██║   ██║██████╔╝██████╔╝ ╚████╔╝
██╔══██╗██║   ██║██╔══██╗██╔══██╗  ╚██╔╝
██████╔╝╚██████╔╝██████╔╝██████╔╝   ██║
╚═════╝  ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝
Hello! I'm Bobby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 4: Add and list an event task

### Aim

Verify that the `event` command creates a task with its start and end text and
displays it with the `[E]` type marker.

### Input

```text
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected Output

```text
____________________________________________________________
██████╗  ██████╗ ██████╗ ██████╗ ██╗   ██╗
██╔══██╗██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
██████╔╝██║   ██║██████╔╝██████╔╝ ╚████╔╝
██╔══██╗██║   ██║██╔══██╗██╔══██╗  ╚██╔╝
██████╔╝╚██████╔╝██████╔╝██████╔╝   ██║
╚═════╝  ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝
Hello! I'm Bobby.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 5: Reject an unknown command

### Aim

Verify that input that does not begin with a supported command is rejected
with a helpful error message and is not added as a task.

### Input

```text
blah
bye
```

### Expected Output

```text
____________________________________________________________
██████╗  ██████╗ ██████╗ ██████╗ ██╗   ██╗
██╔══██╗██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
██████╔╝██║   ██║██████╔╝██████╔╝ ╚████╔╝
██╔══██╗██║   ██║██╔══██╗██╔══██╗  ╚██╔╝
██████╔╝╚██████╔╝██████╔╝██████╔╝   ██║
╚═════╝  ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝
Hello! I'm Bobby.
What can I do for you?
____________________________________________________________
____________________________________________________________
No such task type available. Try again!
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 6: Reject a to-do without a description

### Aim

Verify that the `todo` task type is accepted but an empty description is
rejected with a specific error message.

### Input

```text
todo
bye
```

### Expected Output

```text
____________________________________________________________
██████╗  ██████╗ ██████╗ ██████╗ ██╗   ██╗
██╔══██╗██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
██████╔╝██║   ██║██████╔╝██████╔╝ ╚████╔╝
██╔══██╗██║   ██║██╔══██╗██╔══██╗  ╚██╔╝
██████╔╝╚██████╔╝██████╔╝██████╔╝   ██║
╚═════╝  ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝
Hello! I'm Bobby.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 7: Reject a deadline without a description

### Aim

Verify that a deadline with no description is rejected without causing the
program to crash.

### Input

```text
deadline /by Sunday
bye
```

### Expected Output

```text
____________________________________________________________
██████╗  ██████╗ ██████╗ ██████╗ ██╗   ██╗
██╔══██╗██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
██████╔╝██║   ██║██████╔╝██████╔╝ ╚████╔╝
██╔══██╗██║   ██║██╔══██╗██╔══██╗  ╚██╔╝
██████╔╝╚██████╔╝██████╔╝██████╔╝   ██║
╚═════╝  ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝
Hello! I'm Bobby.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 8: Reject a deadline without a date

### Aim

Verify that a deadline with no date after `/by` is rejected without causing
the program to crash.

### Input

```text
deadline return book /by
bye
```

### Expected Output

```text
____________________________________________________________
██████╗  ██████╗ ██████╗ ██████╗ ██╗   ██╗
██╔══██╗██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
██████╔╝██║   ██║██████╔╝██████╔╝ ╚████╔╝
██╔══██╗██║   ██║██╔══██╗██╔══██╗  ╚██╔╝
██████╔╝╚██████╔╝██████╔╝██████╔╝   ██║
╚═════╝  ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝
Hello! I'm Bobby.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! The date of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
