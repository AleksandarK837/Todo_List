package delete;

import collaboration.Collaboration;
import exceptions.CollaborationException;

import java.io.PrintWriter;
import java.util.Map;

public class DeleteCollaboration {

    public static void deleteCollaboration(String username,
                                           Map<String, Collaboration> collaborations,
                                           String collaborationName, PrintWriter out) throws CollaborationException {

        if (!collaborations.containsKey(collaborationName)) {
            throw new CollaborationException(collaborationName + " doesn't exist!");
        }

        if (!collaborations.get(collaborationName).getCreator().equals(username)) {
            throw new CollaborationException("Can't delete " + collaborationName + "! The creator is: "
                    + collaborations.get(collaborationName).getCreator());
        }

        collaborations.remove(collaborationName);
        out.println("Collaboration " + collaborationName + " deleted!");
    }
}
