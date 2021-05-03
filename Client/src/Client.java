import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client implements Runnable {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7777;
    private static boolean isClosed = false;
    private static Socket clientSocket;

    @Override
    public void run() {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String response;
            while (true) {
                response = reader.readLine();
                System.out.println(response);
                if (response.equals("Disconnected from server.")) {
                    isClosed = true;
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            clientSocket = socket;
            new Thread(new Client()).start();

            System.out.println("Enter command message: <login/register> <username> <password>");
            String line = scanner.nextLine().trim();

            out.println(line);
            out.flush();

            while (!isClosed) {
                String command = scanner.nextLine().trim();
                out.println(command);
            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("File doesn't exist!");
            e.printStackTrace();
        }
    }
}
