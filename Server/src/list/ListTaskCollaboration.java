package list;

import collaboration.Collaboration;
import exceptions.CollaborationException;
import exceptions.ParticipantException;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class ListTaskCollaboration {

    public static void listTasks(PrintWriter out, Map<String, Collaboration> collaborations,
                                 String collaborationName, String user) throws CollaborationException, ParticipantException {

        if (!collaborations.containsKey(collaborationName)) {
            throw new CollaborationException(collaborationName + " doesn't exist!");
        }

        boolean isParticipant = collaborations.get(collaborationName).getParticipants().contains(user);
        boolean isCreator = collaborations.get(collaborationName).getCreator().equals(user);
        if (!isCreator && !isParticipant) {
            throw new ParticipantException(user + " is not in " + collaborationName);
        }

        String message = null;
        Collaboration collaboration = collaborations.get(collaborationName);
        for (Map.Entry<String, List<String>> userTasks : collaboration.getTasks().entrySet()) {
            for (String task : userTasks.getValue()) {
                message = "Task " + task + " assign to " + userTasks.getKey();
                out.println(message);
                out.flush();
            }
        }

        if (message == null) {
            out.println("There's no tasks in Collaboration - " + collaborationName);
            out.flush();
        }
    }
}
