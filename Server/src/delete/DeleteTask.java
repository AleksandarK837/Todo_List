package delete;

import exceptions.TaskException;
import userAccount.UserTasks;

public class DeleteTask {

    public static void deleteTask(UserTasks userTasks, String attributes) throws TaskException {

        String []split = attributes.split(" ", 2);
        String name = split[0];
        String date = null;

        if (split.length == 2) {
            date = split[1];
        }

        userTasks.deleteTask(name, date);
    }
}
