package update;

import add.AddTask;
import exceptions.TaskException;
import userAccount.UserTasks;

import java.io.PrintWriter;

public class UpdateTask {

    public static void updateTask(UserTasks userTasks, String attributes, PrintWriter out) throws TaskException {

        String []split = attributes.split(" ", 4);
        String name = split[0];
        String date = "";
        String dueDate = "";
        String description = "";

        if (split.length == 4) {
            date = split[1];
            dueDate = split[2];
            description = split[3];

            if (!AddTask.isDate(date) || !AddTask.isDate(dueDate)) {
                throw new TaskException(date + " or " + dueDate + " is not valid!");
            }
        }

        if (split.length == 3) {
            date = split[1];
            if (!AddTask.isDate(date)) {
                throw new TaskException(date + " is not valid");
            }

            if (AddTask.isDate(split[2])) {
                dueDate = split[2];
            } else {
                description = split[2];
            }
        }

        if (split.length == 2) {
            if (AddTask.isDate(split[1])) {
                date = split[1];
            } else {
                description = split[1];
            }
        }

        userTasks.updateTask(name, date, dueDate, description);
        out.println("Task " + name + " updated successfully");
        out.flush();
    }
}
