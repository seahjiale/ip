# UI test plan

Run this plan with:

```text
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

Each expected-output block contains the complete console output for its input.

## Test Case 1: Mark and unmark a task

### Aim

Verify that a new task is shown as incomplete, can be marked done, and can be
changed back to incomplete.

### Input

```text
read book
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
added: read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] read book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[ ] read book
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
