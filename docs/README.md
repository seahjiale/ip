# Bobby User Guide

Bobby manages todos, deadlines, and events through text commands. Tasks are
numbered in the order in which they were added.

## Prioritizing tasks

Use the `priority` command to assign or clear the priority of an existing task:

```text
priority TASK_NUMBER LEVEL
```

The supported levels and numeric aliases are:

| Priority | Numeric alias |
| --- | --- |
| `high` | `1` |
| `medium` | `2` |
| `low` | `3` |
| `none` | None |

Priority names are case-insensitive. The `priority` command name must remain
lowercase, like Bobby's other command names.

For example, this command assigns high priority to task 2:

```text
priority 2 high
```

Bobby responds with:

```text
Beep boop! I've updated this task's priority:
  [T][ ][P: HIGH] read book
```

The equivalent numeric command is:

```text
priority 2 1
```

Clear a task's priority by assigning `none`:

```text
priority 2 none
```

An unprioritized task does not display a priority badge:

```text
[T][ ] read book
```

Priorities are supported by todos, deadlines, and events. Marking or
unmarking a task does not change its priority. Assigning a priority also does
not reorder the task list, so task numbers remain stable.

Tasks created without a priority use `none`. Existing Bobby data files that
do not contain priority fields remain supported and their tasks load with
`none`. Bobby saves priority values automatically after a successful update.

Invalid task numbers and unsupported levels are rejected without changing
the task or its saved data. Valid levels are `high`, `medium`, `low`, `none`,
`1`, `2`, and `3`.
