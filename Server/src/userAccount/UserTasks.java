package userAccount;

import exceptions.TaskException;
import userAccount.tasks.Task;
import userAccount.tasks.TaskAttributes;

import java.io.PrintWriter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UserTasks implements Serializable {

    private final Map<Task, TaskAttributes> dateTasks;
    private final Map<String, TaskAttributes> inboxTasks;
    private final Map<Task, TaskAttributes> finishedTasks;

    public UserTasks() {

        dateTasks = new ConcurrentHashMap<>();
        inboxTasks = new ConcurrentHashMap<>();
        finishedTasks = new ConcurrentHashMap<>();
    }

    public Map<Task, TaskAttributes> getDateTasks() {
        return new ConcurrentHashMap<>(dateTasks);
    }

    public Map<String, TaskAttributes> getInboxTasks() {
        return new ConcurrentHashMap<>(inboxTasks);
    }

    public Map<Task, TaskAttributes> getFinishedTasks() {
        return new ConcurrentHashMap<>(finishedTasks);
    }

    public void addTask(String name, String date, String dueDate, String description) throws TaskException {

        if (date.equals("")) {
            if (inboxTasks.containsKey(name)) {
                throw new TaskException("Task " + name + " already exists in inbox!");
            }
            inboxTasks.put(name, new TaskAttributes(dueDate, description));
            return;
        }

        if (dateTasks.containsKey(new Task(name, date))) {
            throw new TaskException("Task " + name + " with date " + date + " already exists in date-tasks!");
        }
        dateTasks.put(new Task(name, date), new TaskAttributes(dueDate, description));
    }

    public void deleteTask(String name, String date) throws TaskException {

        if (date == null && !inboxTasks.containsKey(name)) {
            throw new TaskException("Inbox Task " + name + " doesn't exist!");
        } else if (date == null) {
            inboxTasks.remove(name);
            return;
        }

        Task task = new Task(name, date);
        if (!dateTasks.containsKey(task)) {
            throw new TaskException("Task " + name + " with date " + date + " doesn't exist!");
        }
        dateTasks.remove(task);
    }

    public void getTask(String name, String date, PrintWriter out) throws TaskException {

        if (date == null && !inboxTasks.containsKey(name)) {
            throw new TaskException("Inbox Task " + name + " doesn't exist!");
        } else if (date == null) {
            final TaskAttributes taskAttributes = inboxTasks.get(name);
            out.println(name + " " + taskAttributes.dueDate() + " " + taskAttributes.description());
            out.flush();
            return;
        }

        Task task = new Task(name, date);
        if (!dateTasks.containsKey(task)) {
            throw new TaskException("Task " + name + " with date " + date + " doesn't exist!");
        }
        final TaskAttributes taskAttributes = dateTasks.get(task);
        out.println(task.name() + " " + task.date() + " " + taskAttributes.dueDate() + " " + taskAttributes.description());
        out.flush();
    }

    public void listTasks(String date, boolean flag, PrintWriter out) throws TaskException {

        if (date == null && !flag) {
            if (inboxTasks.isEmpty()) {
                out.println("There is no tasks in inbox!");
                out.flush();
                return;
            }

            for (Map.Entry<String, TaskAttributes> entry : inboxTasks.entrySet()) {
                String message = entry.getKey() + " " + entry.getValue().dueDate() + " " + entry.getValue().description();
                out.println(message);
                out.flush();
            }
        }
        else if (date != null && !flag) {
            if (dateTasks.isEmpty()) {
                out.println("There is no date tasks!");
                out.flush();
                return;
            }

            String message = null;
            for (Map.Entry<Task, TaskAttributes> entry : dateTasks.entrySet()) {

                if (entry.getKey().date().equals(date)) {
                    message = entry.getKey().name() + " " + entry.getKey().date() + " " +
                              entry.getValue().dueDate() + " " + entry.getValue().description();
                    out.println(message);
                    out.flush();
                }
            }

            if (message == null) {
                throw new TaskException("Date " + date + " doesn't exist!");
            }
        }
        else if (date != null) {
            if (finishedTasks.isEmpty()) {
                out.println("There's no finished tasks yet!");
                out.flush();
                return;
            }

            String message = null;
            for (Map.Entry<Task, TaskAttributes> entry : finishedTasks.entrySet()) {

                if (entry.getKey().date().equals(date)) {
                    message = entry.getKey().name() + " " + entry.getKey().date() + " " +
                            entry.getValue().dueDate() + " " + entry.getValue().description();
                    out.println(message);
                    out.flush();
                }
            }

            if (message == null) {
                throw new TaskException("Date " + date + " doesn't exist!");
            }
        }
        else {

            if (finishedTasks.isEmpty()) {
                out.println("There's no finished tasks yet!");
                out.flush();
                return;
            }

            for (Map.Entry<Task, TaskAttributes> entry : finishedTasks.entrySet()) {
                String message = entry.getKey().name() + " " + entry.getKey().date() + " " +
                        entry.getValue().dueDate() + " " + entry.getValue().description();
                out.println(message);
                out.flush();
            }
        }

    }

    public void listDashBoard(PrintWriter out) {

        String message = null;
        for (Map.Entry<Task, TaskAttributes> entry : dateTasks.entrySet()) {
            if (entry.getKey().date().equals(LocalDate.now().toString())) {
                message = entry.getKey().name() + " " + entry.getKey().date() + " " +
                          entry.getValue().dueDate() + " " + entry.getValue().description();
                out.println(message);
                out.flush();
            }
        }

        if (message == null) {
            out.println("There's no tasks for today!");
            out.flush();
        }
    }

    public void finishTask(String taskName, PrintWriter out) throws TaskException {

        if (inboxTasks.containsKey(taskName)) {
            TaskAttributes attributes = inboxTasks.get(taskName);
            finishedTasks.put(new Task(taskName, LocalDate.now().toString()), attributes);
            inboxTasks.remove(taskName);
            out.println(taskName + " finished!");
            out.flush();
            return;
        }
        else {
            for (Map.Entry<Task, TaskAttributes> entry : dateTasks.entrySet()) {
                if (entry.getKey().name().equals(taskName)) {
                    finishedTasks.put(new Task(entry.getKey().name(), LocalDate.now().toString()), entry.getValue());
                    dateTasks.remove(entry.getKey());
                    out.println(taskName + " finished!");
                    out.flush();
                    return;
                }
            }
        }

        throw new TaskException("Task " + taskName + " doesn't exist!");
    }

    public void updateTask(String name, String date, String dueDate, String description) throws TaskException {

        if (inboxTasks.containsKey(name)) {
            String newDate = dueDate.equals("") ? inboxTasks.get(name).dueDate() : dueDate;
            String newDescription = description.equals("") ? inboxTasks.get(name).description() : description;

            if (date == null) {
                inboxTasks.put(name, new TaskAttributes(newDate, newDescription));
            }
            else {
                inboxTasks.remove(name);
                dateTasks.put(new Task(name, date), new TaskAttributes(newDate, newDescription));
            }
            return;
        }

        for (Map.Entry<Task, TaskAttributes> entry : dateTasks.entrySet()) {
            if (entry.getKey().name().equals(name)) {
                String newDate = date.equals("") ? entry.getKey().date() : date;
                String newDueDate = dueDate.equals("") ? entry.getValue().dueDate() : dueDate;
                String newDescription = description.equals("") ? entry.getValue().description() : description;

                dateTasks.remove(entry.getKey());
                dateTasks.put(new Task(name, newDate), new TaskAttributes(newDueDate, newDescription));
                return;
            }
        }
        throw new TaskException("Task " + name + " doesn't exist!");
    }
}
