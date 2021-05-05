import add.AddCollaboration;
import add.AddUser;
import assign.AssignTask;
import collaboration.Collaboration;
import delete.DeleteCollaboration;
import exceptions.CollaborationException;
import exceptions.ParticipantException;
import exceptions.TaskException;
import exceptions.UserException;
import finish.FinishTaskCollaboration;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import server.ClientRequestHandler;
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

    static ClientRequestHandler userPesho;
    static ClientRequestHandler userGosho;
    static ClientRequestHandler userIcko;
    static ClientRequestHandler userAleks;

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

            userPesho = new ClientRequestHandler(socket, registeredUsers, collaborations);
            userGosho = new ClientRequestHandler(socket, registeredUsers, collaborations);
            userIcko = new ClientRequestHandler(socket, registeredUsers, collaborations);
            userAleks = new ClientRequestHandler(socket, registeredUsers, collaborations);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddDateTask() {

        userPesho.checkCommand("add-task lesson 2020-05-30 2020-06-30 math");
        assertEquals(userPesho.getUserTasks().getDateTasks().get(new Task("lesson", "2020-05-30")),
                                                                 new TaskAttributes("2020-06-30", "math"));
    }

    @Test
    public void testAddInboxTask() {

        userPesho.checkCommand("add-task lesson math");
        assertEquals(userPesho.getUserTasks().getInboxTasks().get("lesson"),
                new TaskAttributes("", "math"));
    }

    @Test (expected = TaskException.class)
    public void testAddSameTaskName() throws TaskException {
        UserTasks tasks = userPesho.getUserTasks();
        tasks.addTask("Dance", "", "", "math");
        tasks.addTask("Dance", "", "", "math");
    }

    @Test
    public void testFinishInboxTask() {
        userPesho.checkCommand("add-task math-lesson math");
        userPesho.checkCommand("finish-task math-lesson");

        Assert.assertFalse(userPesho.getUserTasks().getInboxTasks().containsKey("math-lesson"));
        Assert.assertTrue(userPesho.getUserTasks().getFinishedTasks().containsKey(new Task("math-lesson", LocalDate.now().toString())));
    }

    @Test
    public void testFinishDateTask() {
        userPesho.checkCommand("add-task java-lesson 2020-03-03 math");
        userPesho.checkCommand("finish-task java-lesson");

        Assert.assertFalse(userPesho.getUserTasks().getDateTasks().containsKey(new Task("java-lesson", "2020-03-03")));
        Assert.assertTrue(userPesho.getUserTasks().getFinishedTasks().containsKey(new Task("java-lesson", LocalDate.now().toString())));
    }

    @Test
    public void testDeleteTask() {
        userGosho.checkCommand("add-task lesson math");
        userGosho.checkCommand("add-task jogging 2020-05-30");

        userGosho.checkCommand("delete-task lesson");
        userGosho.checkCommand("delete-task jogging 2020-05-30");

        Assert.assertFalse(userGosho.getUserTasks().getInboxTasks().containsKey("lesson"));
        Assert.assertFalse(userGosho.getUserTasks().getDateTasks().containsKey(new Task("jogging", "2020-05-30")));
    }

    @Test
    public void testUpdateTask() {
        userAleks.checkCommand("add-task jogging 2020-05-30");
        userAleks.checkCommand("update-task jogging 2021-05-30 2021-06-30 park");

        Assert.assertFalse(userAleks.getUserTasks().getDateTasks().containsKey(new Task("jogging", "2020-05-30")));
        Assert.assertTrue(userAleks.getUserTasks().getDateTasks().containsKey(new Task("jogging", "2021-05-30")));
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
