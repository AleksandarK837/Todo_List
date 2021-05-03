package add;

import collaboration.Collaboration;
import exceptions.CollaborationException;
import exceptions.ParticipantException;
import exceptions.UserException;

import java.io.PrintWriter;
import java.util.Map;

public class AddUser {

    public static void add(Map<String, String> registeredUsers,
                           String username,
                           Map<String, Collaboration> collaborations,
                           String collaborationName,
                           String newParticipant, PrintWriter out) throws UserException, CollaborationException, ParticipantException {

        if (!registeredUsers.containsKey(newParticipant)) {
            throw new UserException(newParticipant + " doesn't exist!");
        }

        if (!collaborations.containsKey(collaborationName)) {
            throw new CollaborationException("Collaboration " + collaborationName + " doesn't exist!");
        }

        boolean isParticipant = collaborations.get(collaborationName).getParticipants().contains(username);
        boolean isCreator = collaborations.get(collaborationName).getCreator().equals(username);

        if (!isCreator && !isParticipant) {
            throw new ParticipantException(username + " is not participant in " + collaborationName);
        }

        collaborations.get(collaborationName).addParticipant(newParticipant);
        out.println("Participant " + newParticipant + " added!");
        out.flush();
    }
}
