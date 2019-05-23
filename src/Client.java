import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static Scanner scanner;
    private static Client client;

    /**
     * Initiate the client
     * @param ip IP to connect to
     * @param port port to use with IP
     */
    public void init(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Failed to connect to server.");
        }

    }

    /**
     * Send message to the server to be broadcasted to all users
     * @param message Message to send
     */
    public void send(String message) {
        try {
            out.println(message);
        } catch (Exception e) {
        }
    }


    public static void main(String[] args) {
        // Initiate the client
        client = new Client();
        // Initiate a global scanner for messages and other inputs
        scanner = new Scanner(System.in);
        // Ask for IP to connect two
        System.out.println("Please enter an ip (default localhost):");
        String ip = scanner.nextLine();
        if(ip.equals("")) ip = "localhost";
        System.out.println("Please enter a username (default Unknown):");
        String username = scanner.nextLine();
        if(username.equals("")) username = "Unknown";
        // Connect to teh server
        client.init(ip, 1010);
        // Send away the username to set it
        client.send("!username "+username); // Set username
        client.send("!joined"); // Officially join
        // Create the handler, listening for messages from the server
        new Handler();
        // Listen for input from the user via the terminal and then send a message.
        awaitMsg();
    }


    public static class Handler extends Thread {
        public Handler(){
            this.start();
        }

        public void run(){
            String input;
            try{
                // Listen for inputs from the server
                while ((input = in.readLine()) != null) {
                    System.out.println(input);
                }
            } catch(IOException e){
            }
        }
    }

    /**
     * Infinite loop of scanning the console for new messages to send and to
     * keep the process alive.
     */
    public static void awaitMsg(){
         String msg = scanner.nextLine();
         client.send(msg);
         awaitMsg();
    }
}
