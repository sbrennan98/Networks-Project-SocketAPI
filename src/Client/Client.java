package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public Client(String address, int port){
        System.out.println("My chat room client. Version One.\n");

        try{
            socket = new Socket(address, port);
            input = new DataInputStream(System.in);
            output = new DataOutputStream(socket.getOutputStream());
        }
        catch(Exception e){
            System.out.println(e);
            System.out.println("Could not connect to server; please ensure the ChatRoom Server is running & try again!");
            System.exit(-1);
        }

        //src: https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Scanner.html
        Scanner userInput = new Scanner(System.in); //src: https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html
        String userID = null;
        String pass = null;
        String message = null;
        boolean bool = true;
        while(bool){
            try{
                //Enhanced Switch src: https://www.geeksforgeeks.org/enhancements-for-switch-statement-in-java-13/
                switch (userInput.next()) {
                    case "login" -> { //grab & verify UserID & Password
                        if (userInput.hasNext()) {
                            userID = userInput.next(); //grab next token which will be the userID
                            if (userInput.hasNext()) {
                                pass = userInput.next(); //grab the next token which will be the password
                                if(userPassRequirements(userID, pass)){ //Check input requirements in login too
                                    //to save extra time complexity of scanning list of users we know won't be there
                                    connect(address, port);
                                    bool = false; //successfully retrieved UserID & Pass from user input, break loop
                                }
                                else{
                                    System.out.println("Input invalid, ensure username is between 3 & 32 characters & password is between 4 & 8 characters.");
                                    userInput.reset();
                                }
                                //Prompt server to login client here
                            }
                        }
                    }
                    case "newuser" -> {
                        if (userInput.hasNext()) {
                            userID = userInput.next();
                            if (userInput.hasNext()) {
                                pass = userInput.next();
                                if(userPassRequirements(userID, pass)){ //Double check length requirements
                                    bool = false; //successfully retrieved UserID & Pass from user input, break loop
                                    newUser(userID, pass); //Prompt server to create newuser here
                                }
                                else{
                                    System.out.println("Input invalid, ensure username is between 3 & 32 characters & password is between 4 & 8 characters.");
                                    userInput.reset();
                                }
                            }
                        }
                    }
                    case "send" -> {
                        System.out.println("send command called");
                        if(userInput.hasNext()){
                            message = userInput.nextLine();
                            while(message.equals("")){ //This was causing issues with multi line command inputs
                                message = userInput.nextLine();
                            }
                            //System.out.println("Message:\n" + message); //message retrieved successfully!
                            if(message.length() >= 1 && message.length() <=256){
                                bool = false;
                                //send message to server here
                            }
                            else{
                                System.out.println("Message too long, must be under 256 characters.");
                                userInput.reset();

                            }
                        }
                    }
                    case "logout" -> {
                        System.out.println("logout command called");
                        //If currently logged in then bool = false
                        //bool = false;
                    }
                    default -> {
                        System.out.println("Not a valid command; 1. login, 2. newuser, 3. send, 4. logout");
                        //userInput = new Scanner(System.in);
                        userInput.reset();
                    }
                }
            }
            catch(Exception e){
                System.out.println("Exception Thrown: " + e);
            }
        }
//        disconnect();
    }

    private boolean userPassRequirements(String userID, String pass){
        return ((userID.length() >= 3 && userID.length() <= 32) && (pass.length() >= 4 && pass.length() <= 8));
    }

    private boolean connect(String address, int port) {
        //Establish Connection
        try {
            socket = new Socket(address, port);
            System.out.println("Connected Successfully!\n");
            return true;
        } catch (IOException io) {
            System.out.println(io);
            return false;
        }
    }
    private boolean disconnect(){
        //Disconnect
        try{
            System.out.println("Disconnecting from server...\n");
            input.close();
            output.close();
            socket.close();
            return true;
        }
        catch(IOException io){
            System.out.println(io);
            return false;
        }
    }
    private boolean logout(){
        //Log out of user client, logout, then close
        return disconnect();

    }
    private boolean login(String username, String password){
        try{
            File file = new File("../../users.txt"); //src: https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){ //O(n) for n user entries
                String user = scanner.nextLine();
                if(user.equals(username)){ //Almost forgot .equals() method from 3330
                    System.out.println("Logging in...\n");
                    return true;
                }
            }
            System.out.println("user " + username + " not found.\n");
            return false;
        }
        catch(FileNotFoundException fnf){
            System.out.println(fnf);
            return false;
        }
    }
    private boolean send(String message){
        //Error checking
        if(message.length() > 256 || message.length() < 1){
            System.out.println("Sorry, message needs to be between 1 & 256 characters.\n");
            return false;
        }

        try{
            //Get input from the user
            input = new DataInputStream(System.in);
            //Send user input through the socket
            output = new DataOutputStream(socket.getOutputStream());
            return true;
        }
        catch(IOException io){
            System.out.println(io);
            return false;
        }
    }
    private boolean newUser(String username, String password) { //Client.Client checks correct usage of command then sends to the server
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
