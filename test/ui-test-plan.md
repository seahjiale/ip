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
Error! The description of a todo cannot be empty!
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
Error! The description of a deadline cannot be empty!
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
Error! The date of a deadline cannot be empty!
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 9: Reject an event without a description

### Aim

Verify that an event with no description is rejected without causing the
program to crash.

### Input

```text
event /from Mon 2pm /to 4pm
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
Error! The description of an event cannot be empty!
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 10: Reject an event without a start time

### Aim

Verify that an event with no `/from` start time is rejected without causing
the program to crash.

### Input

```text
event project meeting /from /to 4pm
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
Error! Start time of an event cannot be empty. Try again!
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 11: Reject an event without an end time

### Aim

Verify that an event with no `/to` end time is rejected without causing the
program to crash.

### Input

```text
event project meeting /from Mon 2pm /to
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
Error! End time of an event cannot be empty. Try again!
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 12: Preserve state after an invalid to-do

### Aim

Verify that an invalid empty `todo` does not add a task or change the task
count, and that a later valid task is stored correctly.

### Input

```text
todo write report
todo
list
todo submit report
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
[T][ ] write report
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Error! The description of a todo cannot be empty!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] write report
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] submit report
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] write report
2.[T][ ] submit report
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 13: Preserve state after an invalid deadline

### Aim

Verify that a deadline with no date does not add a task or change the task
count, and that a later valid deadline is stored correctly.

### Input

```text
deadline submit report /by Friday
deadline submit slides /by
list
deadline submit slides /by Monday
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
[D][ ] submit report (by: Friday)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Error! The date of a deadline cannot be empty!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] submit report (by: Friday)
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] submit slides (by: Monday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] submit report (by: Friday)
2.[D][ ] submit slides (by: Monday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 14: Preserve state after an invalid event

### Aim

Verify that an event with no end time does not add a task or change the task
count, and that a later valid event is stored correctly.

### Input

```text
event team meeting /from Mon /to Tue
event planning /from Mon /to
list
event planning /from Tue /to Wed
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
[E][ ] team meeting (from: Mon to: Tue)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Error! End time of an event cannot be empty. Try again!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] team meeting (from: Mon to: Tue)
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] planning (from: Tue to: Wed)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] team meeting (from: Mon to: Tue)
2.[E][ ] planning (from: Tue to: Wed)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 15: Delete a task from the list

### Aim

Verify that the `delete` command removes the selected task, displays the
removed task, updates the task count, and renumbers the remaining tasks.

### Input

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
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
Got it. I've added this task:
[D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: June 6th)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: June 6th)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: June 6th)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 16: Reject delete without a task number

### Aim

Verify that `delete` without a task number shows an error and leaves the task
list unchanged.

### Input

```text
todo read book
delete
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
Error! The task number cannot be empty!
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 17: Reject invalid delete task numbers

### Aim

Verify that non-numeric and out-of-range task numbers show errors without
changing the task list.

### Input

```text
todo read book
delete 2
delete 0
delete abc
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
Error! The task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Error! The task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Error! The task number must be a valid integer.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 18: Reject delete when no tasks are available

### Aim

Verify that attempting to delete from an empty list shows that no tasks are
available to delete.

### Input

```text
delete 1
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
No tasks available to delete.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
