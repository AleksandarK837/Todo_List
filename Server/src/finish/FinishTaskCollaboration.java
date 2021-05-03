package finish;

import collaboration.Collaboration;
import exceptions.CollaborationException;
import exceptions.ParticipantException;
import exceptions.TaskException;
import exceptions.UserException;

import java.io.PrintWriter;
import java.util.Map;

public class FinishTaskCollaboration {

    public static void finish(String taskName, String collaborationName,
                              Map<String, Collaboration> collaborations,
                              String username, PrintWriter out) throws CollaborationException, ParticipantException, TaskException, UserException {
        if (!collaborations.containsKey(collaborationName)) {
            throw new CollaborationException("Collaboration " + collaborationName + " doesn't exist!");
        }


        boolean isParticipant = collaborations.get(collaborationName).getParticipants().contains(username);
        boolean isCreator = collaborations.get(collaborationName).getCreator().equals(username);
        if (!isCreator && !isParticipant) {
            throw new ParticipantException(username + " is not in " + collaborationName);
        }

        collaborations.get(collaborationName).finishTask(username, taskName);
        out.println("Task " + taskName + " finished by " + username + " in group " + collaborationName);
        out.flush();
    }
}
