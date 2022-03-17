/* CS4850 Socket API Project -- Server
 * Author:      Sean Brennan
 * PawPrint:    slbvp6
 * Student #:   14264291
 * Description: Computer Networks class project using the Socket API in Java
 */
package Server;

import java.io.*;
import java.net.ServerSocket; //https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/ServerSocket.html
import java.net.Socket; //https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/Socket.html
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List; //https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/List.html
import java.util.ArrayList; //https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ArrayList.html
import java.util.Scanner; //https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Scanner.html
import java.util.StringTokenizer; //https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/StringTokenizer.html

public class Server {
    //src:https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SocketChannel.html
    private ServerSocket serverSocket; //Server.Server needs special server socket
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output; //To send messages back to the client
    //Global loggedIn state? Not scalable for V2
    //private boolean loggedIn = false;
    private List<String> loggedInList = new ArrayList<String>();
    private boolean kill = false;

    public Server(int port) {
        System.out.println("My chat room server. Version One.\n");
        try {
            serverSocket = new ServerSocket(port);
            //Waiting for client to connect over socket...
            while(!kill){
                socket = serverSocket.accept();
                System.out.println("Client connected!");
                //BufferedInputStream to avoid EOFException src: https://stackoverflow.com/questions/17972172/eofexception-in-readutf
                input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                processInput();
            }
            close();
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
        return true;
//        return loggedInList.contains(userID) && loggedInList.remove(userID);
    }

    private boolean send(String message) {
        System.out.println(message);
        return true;
    }

    public boolean close() {
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
            File file = new File("../users.txt"); //src: https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){ //O(n) for n user entries
                String user = scanner.nextLine();
                if(user.equals(clientInput)){ //Almost forgot .equals() method from 3330
                    //Accept login & send confirmation message to client
                    System.out.println("Logging in...\n");
                    //Implement real "login" here
                    //loggedIn = true;
                    loggedInList.add(username);
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
    public boolean newUser(String userID, String pass) {
        //Open userlist
        //Check if username is already taken, else make user and append to userlist.
        try {
            File file = new File("../users.txt"); //src: https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) { //O(n) for n user entries
                String user = scanner.nextLine();
                if (user.equals(userID)) { //Almost forgot .equals() method from 3330
                    System.out.println(user + " already taken, please try again with another username.\n");
                    return false;
                }
            }
            System.out.println("user " + userID + " not found. Creating user!\n");
            String entry = "\n(" + userID + ", " + pass + ")";
            try{
                //src: https://stackoverflow.com/questions/1625234/how-to-append-text-to-an-existing-file-in-java
                Files.write(Paths.get("../users.txt"), entry.getBytes(), StandardOpenOption.APPEND);
                //Send message to client that user was created
            }
            catch(Exception e){
                System.out.println(e);
            }
            return true;
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf);
            return false;
        }
    }
}