package add;

import collaboration.Collaboration;
import exceptions.CollaborationException;

import java.io.PrintWriter;
import java.util.Map;

public class AddCollaboration {

    public static void addCollaboration(String creator, Map<String,
            Collaboration> collaborations, String collaborationName, PrintWriter out) throws CollaborationException {

        if (collaborations.containsKey(collaborationName)) {
            throw new CollaborationException("Collaboration name " + collaborationName + " already exists!");
        }

        collaborations.put(collaborationName, new Collaboration(creator));
        out.println("Created new collaboration " + collaborationName + " by " + creator);
        out.flush();
    }
}
