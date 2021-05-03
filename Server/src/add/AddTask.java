package add;

import exceptions.TaskException;
import userAccount.UserTasks;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AddTask {

    public static boolean isDate(String date) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static void addTask(UserTasks userTasks, String attributes) throws TaskException {

        String []split = attributes.split(" ", 4);
        String name = split[0];
        String date = "";
        String dueDate = "";
        String description = "";

        if (split.length == 4) {
            date = split[1];
            dueDate = split[2];
            description = split[3];

            if (!isDate(date) || !isDate(dueDate)) {
                throw new TaskException(date + " or " + dueDate + " is not valid date!");
            }
        }

        if (split.length == 3) {
            date = split[1];
            if (!isDate(date)) {
                throw new TaskException(date + " is not valid");
            }

            if (isDate(split[2])) {
                dueDate = split[2];
            } else {
                description = split[2];
            }
        }

        if (split.length == 2) {
            if (isDate(split[1])) {
                date = split[1];
            } else {
                description = split[1];
            }
        }

        userTasks.addTask(name, date, dueDate, description);
    }
}
