import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    private static ArrayList<ClientHandler> users = new ArrayList<ClientHandler>();

    /**
     * Initiate the server.
     * @param port Port to start the server on
     */
    public void init(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (true){
                ClientHandler socket = new ClientHandler(serverSocket.accept());
                socket.start();
                users.add(socket);
                System.out.println(users.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        public PrintWriter out;
        private BufferedReader in;
        public String username;
        private boolean joined;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        /**
         * Runnable thread listens for any new messages from users
         * and then broadcasts them to all other users.
         */
        public void run() {
            try {
                // Create PrinterWriter for sending messages to the clients
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                // Create a reader for listening for inputs from the users
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String input; // Input from the user
                while ((input = in.readLine()) != null) { // Upon the input being something, broadcast it
                    if(input.equals("")) continue;
                    // Look for commands, they start with "!"
                    if (input.substring(0, 1).equals("!")) {
                        // If it is a command, look up what (e.g !username Test, the username would be set to 'Test')
                        String command = input.substring(1, input.indexOf(' ') != -1 ? input.indexOf(' ') : input.length());
                        String value = "";
                        // Make sure the value is long enough to be a command value.
                        // It needs to have a space and one or more characters after the space.
                        // Some commands like, joined and roll don't need a value so it can be left blank.
                        if(input.indexOf(" ") != -1 && input.indexOf(" ") != input.length()-1){
                            value = input.substring(input.indexOf(' ')+1);
                        }

                        if(command.equals("username")){
                            // If the command is to change username, change the username.
                            this.username = value;
                        }

                        // Join command, automatically is sent upon joining and can only run once per user.
                        if(command.equals("joined") && !this.joined){
                            this.joined = true;
                            broadcast(this.username + " has joined!");
                        }

                        System.out.println(command);
                        if(command.equals("roll")){
                            // TODO: Rolls options
                            broadcast(this.username + " rolls " + Math.round((Math.floor(Math.random()*100))+1) + " (1-100)");
                        }

                    } else {
                        // If not a command, it's a message. Send the message to all users.
                        send("[" + this.username + "] " + input);
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * Broadcast message to all users
     * @param message Message to send
     */
    public static void send(String message){
        // Loop through all users and send them the message
        for(int i = 0; i < users.size(); i++){
            users.get(i).out.println(message);
        }
    }

    /**
     * Send message from the server
     * @param message Message to send
     */
    public static void broadcast(String message){
        send("[Server] " + message);
    }

    public static void main(String[] args) {
        int port = 1010; // Default port for this chat-app
        Server server = new Server(); // Create new instance of the server
        System.out.println("Started server on port " + port);
        server.init(port); // Start the server with the default port.
    }
}
