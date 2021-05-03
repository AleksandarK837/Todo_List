package list;

import collaboration.Collaboration;
import exceptions.CollaborationException;
import exceptions.ParticipantException;

import java.io.PrintWriter;
import java.util.Map;

public class ListUsersCollaboration {

    public static void listUsers(PrintWriter out, String username, Map<String,
            Collaboration> collaborations, String collaborationName) throws CollaborationException, ParticipantException {

        if (!collaborations.containsKey(collaborationName)) {
            throw new CollaborationException(collaborationName + " doesn't exist!");
        }

        boolean isParticipant = collaborations.get(collaborationName).getParticipants().contains(username);
        boolean isCreator = collaborations.get(collaborationName).getCreator().equals(username);
        if (!isParticipant && !isCreator) {
            throw new ParticipantException(username + " is not in " + collaborationName);
        }

        String creator = collaborations.get(collaborationName).getCreator();
        out.println(creator);
        for (String participant : collaborations.get(collaborationName).getParticipants()) {
            out.println(participant);
            out.flush();
        }
    }
}
