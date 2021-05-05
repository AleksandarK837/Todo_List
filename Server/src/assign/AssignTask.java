package assign;

import collaboration.Collaboration;
import exceptions.CollaborationException;
import exceptions.ParticipantException;
import exceptions.TaskException;
import exceptions.UserException;

import java.io.PrintWriter;
import java.util.Map;

public class AssignTask {

    public static void assign(Map<String, String> registeredUsers,
                              String username, String collaborationName,
                              Map<String, Collaboration> collaborations,
                              String taskName,
                              String participant, PrintWriter out) throws UserException, CollaborationException, ParticipantException, TaskException {

        if (!registeredUsers.containsKey(participant)) {
            throw new UserException(participant + " doesn't exist!");
        }

        if (!collaborations.containsKey(collaborationName)) {
            throw new CollaborationException("Collaboration + " + collaborationName + " doesn't exist!");
        }

        boolean usernameInGroup = collaborations.get(collaborationName).getParticipants().contains(username);
        boolean participantInGroup = collaborations.get(collaborationName).getParticipants().contains(participant);
        if (!usernameInGroup && !participantInGroup) {
            throw new ParticipantException(username + " or " + participant + " are not in " + collaborationName);
        }

        collaborations.get(collaborationName).assignTask(taskName, participant);
        out.println("Task " + taskName + " assign to " + participant);
    }
}
