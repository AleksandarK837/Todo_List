package finish;

import exceptions.TaskException;
import userAccount.UserTasks;
import java.io.PrintWriter;

public class FinishTask {

    public static void finish(UserTasks userTasks, String taskName, PrintWriter out) throws TaskException {

        userTasks.finishTask(taskName, out);

    }
}
