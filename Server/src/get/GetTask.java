package get;

import exceptions.TaskException;
import userAccount.UserTasks;

import java.io.PrintWriter;

public class GetTask {

    public static void getTask(UserTasks userTasks, PrintWriter out, String attributes) throws TaskException {

        String []split = attributes.split(" ", 2);
        String name = split[0];
        String date = null;

        if (split.length == 2) {
            date = split[1];
        }

        userTasks.getTask(name, date, out);
    }
}
