package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Server {
    //src:https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SocketChannel.html
    private ServerSocket serverSocket; //Server.Server needs special server socket
    private Socket socket;
    private DataInputStream input;

    public Server(int port) {
        System.out.println("My chat room server. Version One.\n");
        //Initialize the server
        //May not be very scalable to have server load all users & passwords into memory; scan file directly instead?
        try {
            serverSocket = new ServerSocket(port);
            //Waiting for client to send its output over socket...
            socket = serverSocket.accept();
            System.out.println("Client connected!");
            //BufferedInputStream to avoid EOFException src: https://stackoverflow.com/questions/17972172/eofexception-in-readutf
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            processInput();
            //closeConnection();
        } catch (IOException io) {
            System.out.println(io);
        }
    }

    private int processInput() {
        String command = "";
        try {
            command = input.readUTF();
            System.out.println(command);
        } catch (Exception e) {
            System.out.println("Couldn't Process Input...");
            System.out.println(e);
            return -1;
        }

        StringTokenizer tokenizer = new StringTokenizer(command);
        String function = tokenizer.nextToken();
        switch (function) {
            case "login" -> login(tokenizer.nextToken(), tokenizer.nextToken());
            case "newuser" -> newUser(tokenizer.nextToken(), tokenizer.nextToken());
            case "send" -> send(tokenizer.nextToken());
            case "logout" -> logout();
            default -> {
                System.out.println("Client sent wrong user input somehow...");
                return -1;
            }
        }
        return 1;
    }

    private boolean logout() {
        System.out.println("Logout");
        return true;
    }

    private boolean send(String message) {
        System.out.println(message);
        return true;
    }

    public boolean closeConnection() {
        try {
            System.out.println("Closing server...\n");
            socket.close();
            input.close();
            return true;
        } catch (IOException io) {
            System.out.println(io);
            return false;
        }
    }
    private boolean login(String username, String password){
        String clientInput = "(" + username + ", " + password + ")"; //Form tuple-like format from input like given .txt
        System.out.println("Looking for " + clientInput);
        try{
            File file = new File("../../users.txt"); //src: https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){ //O(n) for n user entries
                String user = scanner.nextLine();
                if(user.equals(clientInput)){ //Almost forgot .equals() method from 3330
                    //Accept login & send confirmation message to client
                    System.out.println("Logging in...\n");
                    //Implement real "login" here
                    return true;
                }
            }
            //Decline login & send error message to client
            System.out.println("Username or password is incorrect...");
            return false;
        }
        catch(FileNotFoundException fnf){
            System.out.println(fnf);
            return false;
        }
    }
    public boolean newUser(String username, String password) {
        //Open userlist
        //Check if username is already taken, else make user and append to userlist.
        try {
            File file = new File("../../users.txt"); //src: https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) { //O(n) for n user entries
                String user = scanner.nextLine();
                if (user.equals(username)) { //Almost forgot .equals() method from 3330
                    System.out.println(user + " already taken, please try again with another username.\n");
                    return false;
                }
            }
            System.out.println("user " + username + " not found. Creating user!\n");

            return true;
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf);
            return false;
        }
    }
}