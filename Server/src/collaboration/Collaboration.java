package collaboration;

import exceptions.TaskException;
import exceptions.UserException;
import userAccount.tasks.Task;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Collaboration implements Serializable {

    private final String creator;
    private List<String> participants;

    Map<String, List<String>> assignTasks; // <key = assignTo, value = list of tasks>
    Map<String, List<Task>> finishedTasks;

    public Collaboration(String creator) {
        this.creator = creator;
        participants = new ArrayList<>();
        assignTasks = new ConcurrentHashMap<>();
        finishedTasks = new ConcurrentHashMap<>();
    }

    public void assignTask(String task, String participant) throws TaskException {

        if (!assignTasks.containsKey(participant)) {
            assignTasks.put(participant, new ArrayList<>());
        }

        if (assignTasks.get(participant).contains(task)) {
            throw new TaskException("User " + participant + " has already task: " + task);
        }

        assignTasks.get(participant).add(task);
    }


    public String getCreator() {
        return creator;
    }

    public List<String> getParticipants() {
        return new ArrayList<>(participants);
    }

    public Map<String, List<String>> getTasks() {
        return new ConcurrentHashMap<>(assignTasks);
    }

    public Map<String, List<Task>> getFinishedTasks() {
        return new ConcurrentHashMap<>(finishedTasks);
    }

    public void addParticipant(String participant) {
        participants.add(participant);
    }

    public void finishTask(String participant, String task) throws UserException, TaskException {

        if (!assignTasks.containsKey(participant)) {
            throw new UserException(participant + " has not task " + task);
        }

        if (!assignTasks.get(participant).contains(task)) {
            throw new TaskException(task + " is not assign to " + participant);
        }

        if (!finishedTasks.containsKey(participant)) {
            finishedTasks.put(participant, new ArrayList<>());
        }
        finishedTasks.get(participant).add(new Task(task, LocalDate.now().toString()));
        assignTasks.get(participant).remove(task);

        if (assignTasks.get(participant).size() == 0) {
            assignTasks.remove(participant);
        }
    }
}
