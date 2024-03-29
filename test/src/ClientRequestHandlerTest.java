import add.AddCollaboration;
import add.AddTask;
import add.AddUser;
import assign.AssignTask;
import collaboration.Collaboration;
import delete.DeleteCollaboration;
import delete.DeleteTask;
import exceptions.CollaborationException;
import exceptions.ParticipantException;
import exceptions.TaskException;
import exceptions.UserException;
import finish.FinishTask;
import finish.FinishTaskCollaboration;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import server.ClientRequestHandler;
import update.UpdateTask;
import userAccount.UserTasks;
import userAccount.tasks.Task;
import userAccount.tasks.TaskAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ClientRequestHandlerTest {

    static final String pesho = "pesho";
    static final String gosho = "gosho";
    static final String icko = "icko";
    static final String aleks = "aleks";

    static final String password = "12345";

    static final String HOST = "localhost";
    static final int PORT = 4040;

    static final Map<String, String> registeredUsers = new ConcurrentHashMap<>();
    static final Map<String, Collaboration> collaborations = new ConcurrentHashMap<>();
    static ServerSocket serverSocket;
    static Socket socket;
    static PrintWriter out;

    @BeforeClass
    public static void setUP() {
        try {
            serverSocket = new ServerSocket(PORT);
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream());

            registeredUsers.put(pesho, password);
            registeredUsers.put(gosho, password);
            registeredUsers.put(icko, password);
            registeredUsers.put(aleks, password);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddDateTask() throws TaskException {
        final UserTasks userTasks = new UserTasks();
        String dateTaskAttributes = "lesson 2020-05-30 2020-06-30 math";

        AddTask.addTask(userTasks, dateTaskAttributes);
        assertEquals(userTasks.getDateTasks().get(new Task("lesson", "2020-05-30")),
                                                                 new TaskAttributes("2020-06-30", "math"));
    }

    @Test
    public void testAddInboxTask() throws TaskException {
        final UserTasks userTasks = new UserTasks();
        String inboxTaskAttributes = "lesson math";

        AddTask.addTask(userTasks, inboxTaskAttributes);
        assertEquals(userTasks.getInboxTasks().get("lesson"),
                new TaskAttributes("", "math"));
    }

    @Test (expected = TaskException.class)
    public void testAddSameTaskName() throws TaskException {
        final UserTasks userTasks = new UserTasks();
        String taskAttributes = "dance music";

        AddTask.addTask(userTasks, taskAttributes);
        AddTask.addTask(userTasks, taskAttributes);
    }

    @Test
    public void testFinishInboxTask() throws TaskException {
        final UserTasks userTasks = new UserTasks();
        String addTaskAttributes = "math-lesson math";
        String finishTaskAttributes = "math-lesson";

        AddTask.addTask(userTasks, addTaskAttributes);
        Assert.assertTrue(userTasks.getInboxTasks().containsKey("math-lesson"));

        FinishTask.finish(userTasks, finishTaskAttributes, out);
        Assert.assertFalse(userTasks.getInboxTasks().containsKey("math-lesson"));
        Assert.assertTrue(userTasks.getFinishedTasks().containsKey(new Task("math-lesson", LocalDate.now().toString())));
    }

    @Test
    public void testFinishDateTask() throws TaskException {
        final UserTasks userTasks = new UserTasks();
        String addTaskAttributes = "java-lesson 2020-03-03 java";
        String finishTaskAttributes = "java-lesson";

        AddTask.addTask(userTasks, addTaskAttributes);
        Assert.assertTrue(userTasks.getDateTasks().containsKey(new Task("java-lesson", "2020-03-03")));

        FinishTask.finish(userTasks, finishTaskAttributes, out);
        Assert.assertFalse(userTasks.getDateTasks().containsKey(new Task("java-lesson", "2020-03-03")));
        Assert.assertTrue(userTasks.getFinishedTasks().containsKey(new Task("java-lesson", LocalDate.now().toString())));
    }

    @Test
    public void testDeleteTask() throws TaskException {
        final UserTasks userTasks = new UserTasks();

        String addInboxTaskAttributes = "lesson math";
        AddTask.addTask(userTasks, addInboxTaskAttributes);
        Assert.assertTrue(userTasks.getInboxTasks().containsKey("lesson"));

        String dateTaskAttributes = "jogging 2020-05-30";
        AddTask.addTask(userTasks, dateTaskAttributes);
        Assert.assertTrue(userTasks.getDateTasks().containsKey(new Task("jogging", "2020-05-30")));

        String deleteInboxTaskAttributes = "lesson";
        DeleteTask.deleteTask(userTasks, deleteInboxTaskAttributes);
        Assert.assertFalse(userTasks.getInboxTasks().containsKey("lesson"));

        String deleteDateTaskAttributes = "jogging 2020-05-30";
        DeleteTask.deleteTask(userTasks, deleteDateTaskAttributes);
        Assert.assertFalse(userTasks.getDateTasks().containsKey(new Task("jogging", "2020-05-30")));
    }

    @Test
    public void testUpdateTask() throws TaskException {
        String addTaskAttributes = "jogging 2020-05-30";
        String updateTaskAttributes = "jogging 2021-05-30 2021-06-30 park";

        final UserTasks userTasks = new UserTasks();
        AddTask.addTask(userTasks, addTaskAttributes);
        Assert.assertTrue(userTasks.getDateTasks().containsKey(new Task("jogging", "2020-05-30")));

        UpdateTask.updateTask(userTasks, updateTaskAttributes, out);
        Assert.assertTrue(userTasks.getDateTasks().containsKey(new Task("jogging", "2021-05-30")));
        Assert.assertFalse(userTasks.getDateTasks().containsKey(new Task("jogging", "2020-05-30")));
    }

    @Test (expected = CollaborationException.class)
    public void testAddCollaborationTwice() throws CollaborationException {
        String group = "University group";
        AddCollaboration.addCollaboration(pesho, collaborations, group, out);
        AddCollaboration.addCollaboration(gosho, collaborations, group, out);
    }

    @Test
    public void addCollaborationParticipants() {
        String group = "Online lessons";
        collaborations.put(group, new Collaboration(pesho));
        collaborations.get(group).addParticipant(icko);
        collaborations.get(group).addParticipant(gosho);

        Assert.assertTrue(collaborations.get(group).getParticipants().contains(icko));
        Assert.assertTrue(collaborations.get(group).getParticipants().contains(gosho));
    }

    @Test (expected = CollaborationException.class)
    public void testDeleteGroupIfNotCreator() throws CollaborationException {
        String group = "Online Yoga";
        collaborations.put(group, new Collaboration(pesho));
        collaborations.get(group).addParticipant(icko);
        collaborations.get(group).addParticipant(gosho);

        DeleteCollaboration.deleteCollaboration(gosho, collaborations, group, out);
    }

    @Test (expected = ParticipantException.class)
    public void testAddUserIfNotParticipant() throws CollaborationException, ParticipantException, UserException {
        String group = "Online Meeting";
        collaborations.put(group, new Collaboration(pesho));
        AddUser.add(registeredUsers, aleks, collaborations, group, icko, out);
    }

    @Test
    public void testDeleteCollaboration() throws CollaborationException {
        String group = "Surf group";
        collaborations.put(group, new Collaboration(pesho));
        collaborations.get(group).addParticipant(icko);
        collaborations.get(group).addParticipant(gosho);

        Assert.assertTrue(collaborations.containsKey(group));
        DeleteCollaboration.deleteCollaboration(pesho, collaborations, group, out);
        Assert.assertFalse(collaborations.containsKey(group));
    }

    @Test
    public void testAssignTaskToParticipant() throws CollaborationException, ParticipantException, UserException, TaskException {
        String group = "fun group";
        String task = "funny task";

        collaborations.put(group, new Collaboration(pesho));
        collaborations.get(group).addParticipant(icko);

        AssignTask.assign(registeredUsers, icko, group, collaborations, task, pesho, out);
        Assert.assertTrue(collaborations.get(group).getTasks().get(pesho).contains(task));
    }

    @Test (expected = ParticipantException.class)
    public void testAssignTaskIfNotParticipant() throws CollaborationException, ParticipantException, UserException, TaskException {

        String group = "bad group";
        String task = "bad task";

        collaborations.put(group, new Collaboration(pesho));
        collaborations.get(group).addParticipant(icko);

        AssignTask.assign(registeredUsers, aleks, group, collaborations, task, pesho, out);
    }

    @Test (expected = TaskException.class)
    public void testAssignSameTask() throws CollaborationException, ParticipantException, UserException, TaskException {

        String group = "Programming group";
        String task = "Hello World";

        collaborations.put(group, new Collaboration(pesho));
        collaborations.get(group).addParticipant(icko);
        collaborations.get(group).addParticipant(gosho);

        AssignTask.assign(registeredUsers, gosho, group, collaborations, task, pesho, out);
        AssignTask.assign(registeredUsers, icko, group, collaborations, task, pesho, out);
    }

    @Test
    public void testFinishCollaborationTask() throws ParticipantException, CollaborationException, UserException, TaskException {
        String group = "Group for Jogging";
        String task = "jogging";

        collaborations.put(group, new Collaboration(pesho));
        collaborations.get(group).addParticipant(icko);

        AssignTask.assign(registeredUsers, pesho, group, collaborations, task, icko, out);
        Assert.assertTrue(collaborations.get(group).getTasks().get(icko).contains(task));

        FinishTaskCollaboration.finish(task, group, collaborations, icko, out);
        Assert.assertFalse(collaborations.get(group).getTasks().containsKey(icko));

        Assert.assertTrue(collaborations.get(group).getFinishedTasks().get(icko).contains(new Task(task, LocalDate.now().toString())));
    }

    @AfterClass
    public static void closeUp() {

        try {
            serverSocket.close();
            socket.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
