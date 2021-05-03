package list;

import collaboration.Collaboration;
import exceptions.CollaborationException;
import exceptions.ParticipantException;
import userAccount.tasks.Task;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class ListFinishedTask {

    public static void listFinishedTasks(PrintWriter out,
                                         Map<String, Collaboration> collaborations,
                                         String collaborationName, String username) throws CollaborationException, ParticipantException {

        if (!collaborations.containsKey(collaborationName)) {
            throw new CollaborationException("Collaboration " + collaborationName + " doesn't exist!");
        }

        boolean isParticipant = collaborations.get(collaborationName).getParticipants().contains(username);
        boolean isCreator = collaborations.get(collaborationName).getCreator().equals(username);
        if (!isCreator && !isParticipant) {
            throw new ParticipantException(username + " is not in " + collaborationName);
        }

        String message = null;
        for (Map.Entry<String, List<Task>> entry : collaborations.get(collaborationName).getFinishedTasks().entrySet()) {
            for (Task finishedTask : entry.getValue()) {
                message = "Task " + finishedTask.name() + " finished by " + entry.getKey() + " on " + finishedTask.date();
                out.println(message);
                out.flush();
            }
        }

        if (message == null) {
            out.println("No finished tasks yet in " + collaborationName);
            out.flush();
        }
    }
}
