package list;

import exceptions.TaskException;
import userAccount.UserTasks;

import java.io.PrintWriter;

public class ListTasks {

    public static void listTasks(UserTasks userTasks, String attributes, PrintWriter out) throws TaskException {

        boolean flag = false;
        String date = null;

        if (attributes != null) {
            String[] split = attributes.split(" ", 2);
            if (split.length == 1) {
                if (split[0].equals("true")) {
                    flag = true;
                } else {
                    date = split[0];
                }
            }
            else {
                date = split[0];
                if (split[1].equals("true")) {
                    flag = true;
                }
            }
        }

        userTasks.listTasks(date, flag, out);
    }
}
