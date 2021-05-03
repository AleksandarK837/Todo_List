package list;

import collaboration.Collaboration;

import java.io.PrintWriter;
import java.util.Map;

public class ListCollaborations {

    public static void listCollaborations(PrintWriter out, Map<String, Collaboration> collaborations, String username) {

        String message = null;
        for (Map.Entry<String, Collaboration> entry : collaborations.entrySet()) {
            if (entry.getValue().getCreator().equals(username)) {
                message = "Creator of Collaboration: " + entry.getKey();
                out.println(message);
                out.flush();
            }

            if (entry.getValue().getParticipants().contains(username)) {
                message = "Participant in Collaboration: " + entry.getKey();
                out.println(message);
                out.flush();
            }
        }

        if (message == null) {
            out.println("You are not a participant in any Collaboration!");
            out.flush();
        }
    }
}
