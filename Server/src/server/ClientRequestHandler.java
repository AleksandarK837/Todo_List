package server;

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
import get.GetTask;
import list.*;
import update.UpdateTask;
import userAccount.UserTasks;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ClientRequestHandler implements Runnable {

    private String username;
    private String password;
    private UserTasks userTasks;

    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private final Map<String, String> registeredUsers;
    private final Map<String, Collaboration> collaborations;

    public ClientRequestHandler(Socket socket, Map<String, String> registeredUsers,
                                Map<String, Collaboration> collaborations) {
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.registeredUsers = registeredUsers;
        this.collaborations = collaborations;
        userTasks = new UserTasks();
    }

    public void checkCommand(String input) throws IllegalArgumentException {

        try {

            String[] lines = input.split(" ", 2);
            String command = lines[0];
            String attributes;
            if (lines.length <= 1) {
                attributes = null;
            } else {
                attributes = lines[1];
            }

            switch (command) {
                case "add-task":
                    AddTask.addTask(userTasks, attributes);
                    break;
                case "update-task":
                    UpdateTask.updateTask(userTasks, attributes, out);
                    break;
                case "delete-task":
                    DeleteTask.deleteTask(userTasks, attributes);
                    break;
                case "get-task":
                    GetTask.getTask(userTasks, out, attributes);
                    break;
                case "list-tasks":
                    ListTasks.listTasks(userTasks, attributes, out);
                    break;
                case "list-dashboard":
                    ListDashboard.listDashboard(userTasks, out);
                    break;
                case "finish-task":
                    FinishTask.finish(userTasks, attributes, username, out);
                    break;
                case "add-collaboration":
                    AddCollaboration.addCollaboration(username, collaborations, attributes, out);
                    break;
                case "delete-collaboration":
                    DeleteCollaboration.deleteCollaboration(username, collaborations, attributes, out);
                    break;
                case "list-collaborations":
                    ListCollaborations.listCollaborations(out, collaborations, username);
                    break;
                case "add-user": {
                    String[] splitAttributes = attributes.split(" ", 2);
                    String collaborationName = splitAttributes[0];
                    String newParticipant = splitAttributes[1];
                    AddUser.add(registeredUsers, username, collaborations, collaborationName, newParticipant, out);
                }
                break;
                case "assign-task": {
                    String[] splitAttributes = attributes.split(" ", 3);
                    String collaborationName = splitAttributes[0];
                    String newParticipant = splitAttributes[1];
                    String taskName = splitAttributes[2];
                    AssignTask.assign(registeredUsers, username, collaborationName, collaborations, taskName, newParticipant, out);
                }
                break;
                case "list-tasks-collaboration":
                    ListTaskCollaboration.listTasks(out, collaborations, attributes, username);
                    break;
                case "list-finished-tasks":
                    ListFinishedTask.listFinishedTasks(out, collaborations, attributes, username);
                    break;
                case "list-users":
                    ListUsersCollaboration.listUsers(out, username, collaborations, attributes);
                    break;
                case "finish-task-collaboration": {
                    String[] splitAttributes = attributes.split(" ", 2);
                    String collaborationName = splitAttributes[0];
                    String task = splitAttributes[1];
                    FinishTaskCollaboration.finish(task, collaborationName, collaborations, username, out);
                }
                break;
                default:
                    out.println("Unknown command!");
                    out.flush();
            }
        } catch (TaskException | CollaborationException | UserException | ParticipantException e) {
            out.println(e.toString());
            out.flush();
        }
    }

    private void validateUser() throws IOException, UserException {

        String line = in.readLine();
        String []split = line.split(" ", 3);

        String command = split[0];
        this.username = split[1];
        this.password = split[2];

        String response;
        if (command.equals("login")) {
            login(username, password);
            response = username + " logged successfully!";
        }
        else if (command.equals("register")) {
            register(username, password);
            response = username + " registered successfully!";
        }
        else {
            throw new UserException("Enter valid command: register or login");
        }
        out.println(response);
        out.flush();
    }

    private synchronized void login(String username, String password) throws UserException {
        if (!registeredUsers.containsKey(username)) {
            throw new UserException("Username " + username + " doesn't exist!");
        }

        if (!registeredUsers.get(username).equals(password)) {
            throw new UserException("Password " + password + " is wrong, please try again!");
        }
    }

    private synchronized void register(String username, String password) throws UserException{
        if (registeredUsers.containsKey(username)) {
            throw new UserException("Username " + username + " already exist! Please enter another one!");
        }

        writeUserToLoginFile();
        registeredUsers.put(username, password);
    }

    private void saveData() {

        final String userFile = username + "Data.bin";
        final String collaborationFile = "collaborationsData.bin";

        try (var userFileData = new ObjectOutputStream(new FileOutputStream(userFile));
             var collaborationsFileData = new ObjectOutputStream(new FileOutputStream(collaborationFile))) {

            userFileData.writeObject(userTasks);

            synchronized (collaborations) {
                collaborationsFileData.writeObject(collaborations);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserData() {

        String userFile = username + "Data.bin";
        try (var file = new ObjectInputStream(new FileInputStream(userFile))) {
            userTasks = (UserTasks) file.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeUserToLoginFile() {

        try (var file = new BufferedWriter(new FileWriter("registeredUsers.txt", true))) {

            file.write(username + " " + password);
            file.newLine();
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Collaboration> getCollaborations() {
        return collaborations;
    }

    public UserTasks getUserTasks() {
        return userTasks;
    }

    @Override
    public void run() {

        try {
            validateUser();
            loadUserData();

            String input;
            while ((input = in.readLine()) != null) {
                System.out.println("Received command-message: " + input);
                if (input.equals("quit")) {
                    saveData();
                    out.println("Disconnected from server.");
                    out.flush();
                    break;
                }
                checkCommand(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UserException e) {
            e.printStackTrace();
            out.println(e.toString());
            out.flush();
        }
        finally
        {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
