package server;

import collaboration.Collaboration;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static final int SERVER_PORT = 7777;

    private Map<String, String> registeredUsers;
    private Map<String, Collaboration> collaborations;

    public Server() {
        registeredUsers = new ConcurrentHashMap<>();
        collaborations = new ConcurrentHashMap<>();
    }

    private void loadRegisteredUsers() {

        final String fileName = "registeredUsers.txt";

        try (var file = new BufferedReader(new FileReader(fileName))) {

            String line;
            while ((line = file.readLine()) != null) {
                String []info = line.split(" ", 2);
                registeredUsers.put(info[0], info[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCollaborations() {

        final String fileName = "collaborationsData.bin";

        try (var file = new ObjectInputStream(new FileInputStream(fileName))) {
            collaborations = (ConcurrentHashMap) file.readObject();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void runServer() {

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            loadRegisteredUsers();
            loadCollaborations();

            System.out.println("Server started and listening for connect requests");
            Socket clientSocket;

            while (true) {
                clientSocket = serverSocket.accept();

                System.out.println("Accepted connection request from " + clientSocket.getInetAddress());

                ClientRequestHandler client = new ClientRequestHandler(clientSocket, registeredUsers, collaborations);
                new Thread(client).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server().runServer();
    }
}
