/* CS4850 Socket API Project -- Client
 * Author:      Sean Brennan
 * PawPrint:    slbvp6
 * Student #:   14264291
 * Description: Computer Networks class project using the Socket API in Java
 */
package Client;

import java.io.*;
import java.net.Socket; //https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/Socket.html
import java.util.Scanner; //https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Scanner.html
import java.util.StringTokenizer; //https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/StringTokenizer.html

public class Client extends Thread{
    private Socket socket;
    private DataInputStream input; //For both user input and server input
    private DataOutputStream output;
    //private boolean loggedIn = false; //Use user string instead so we can tell the server which User to log out...
    private String loggedIn = ""; //User string set when logged in
    private boolean kill = false;
    private String address = "";
    private int port;

    public Client(String address, int port){
        System.out.println("My chat room client. Version One.\n");

        this.address = address;
        this.port = port;
        //this.start();
    }

    public void run(){
        try{
            //socket = new Socket(address, port);
            while(!kill) {
                socket = new Socket(address, port);
                input = new DataInputStream(System.in);
                output = new DataOutputStream(socket.getOutputStream());
                String command = input.readLine(); //deprecated method, consider changing implementation
                parseInput(command);
            }
        }
        catch(Exception e){ //Expand on catches if I have time
            System.out.println("test");
            System.out.println(e);
            System.out.println("Couldn't connect to server; ensure ChatRoom Server is running & try again!");
            kill = true;
            //System.exit(-1);
        }
    }

    private boolean userPassRequirements(String userID, String pass){
        return ((userID.length() >= 3 && userID.length() <= 32) && (pass.length() >= 4 && pass.length() <= 8));
    }
    private boolean messageRequirements(String message){
        return (message.length() <= 256 && message.length() >= 1);
    }
    private void parseInput(String command){ //Returns <= 0 for invalid user inputs
        //Need to make sure we're passing valid input to the server first
        StringTokenizer tokenizer = new StringTokenizer(command);
        String function = ""; //Get the primary function user is trying to call
        if(tokenizer.hasMoreTokens()){
             function = tokenizer.nextToken();
        }
        switch(function){ //Enhanced Switch: https://www.geeksforgeeks.org/enhancements-for-switch-statement-in-java-13/
            case "login" ->{
                String userID = tokenizer.nextToken();
                String pass = tokenizer.nextToken();
                login(userID, pass);
//                return userPassRequirements(userID, pass) ? 1 : -1; //Return positive if meets requirements
            }
            case "newuser" ->{
                String userID = tokenizer.nextToken();
                String pass = tokenizer.nextToken();
                newUser(userID, pass);
//                return userPassRequirements(userID, pass) ? 2 : -2;
            }
            case "send" ->{
                if(tokenizer.hasMoreTokens()){ //Need to avoid error
                    String message = tokenizer.nextToken("");
                    send(message);
                }
                else{
                    send(""); //better than sending NULL
                }
            }
            case "logout" ->{
                logout();
            }
            default ->{
                System.out.println("Not a valid command; 1. login, 2. newuser, 3. send, 4. logout");
            }
        }
    }
    private boolean logout(){//Log out of user client, logout, then close
        if(loggedIn.equals("")){
            System.out.println("You're not even logged in bro...");
            return false;
        }
        try{
            /*
            System.out.println("Disconnecting from server...");
            input.close();
            output.close();
            socket.close();
             */
            output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF("logout " + loggedIn);
            input = new DataInputStream(socket.getInputStream());
            System.out.println(input.readUTF());
            loggedIn = "";
            return true;
        }
        catch(IOException io){
            System.out.println(io);
            return false;
        }
    }
    private boolean login(String userID, String pass){
        if(!loggedIn.equals("")){
            System.out.println("Already logged in.");
            return false;
        }
        if(!userPassRequirements(userID, pass)){ //Invalid input, don't bog server down with an unnecessary request
            System.out.println("Username & password length requirements not correct, cannot login");
            return false;
        }
        //Send input to server to log user in, if server responds w/ true then set loggedIn variable to true
        try{
            output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF("login " + userID + " " + pass + "\n");
            input = new DataInputStream(socket.getInputStream());
            String response = input.readUTF();
            System.out.println(response);
            if(response.equals("Denied. Username or password is incorrect.")){
                return false;
            }
            else{
                loggedIn = userID;
                return true;
            }
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }
    private boolean send(String message){
        //Error checking
        if(loggedIn.equals("")){
            System.out.println("Denied. Please log in first.");
            try{
                output = new DataOutputStream(socket.getOutputStream()); //I still need to assign a new output for some reason?
                output.writeUTF("send \n");
                input = new DataInputStream(socket.getInputStream());
            }
            catch(Exception e){
                System.out.println("Weird stuff");
                System.out.println(e);
            }
            return false;
        }
        if(message.length() > 256 || message.length() < 1){
            System.out.println("Sorry, message needs to be between 1 & 256 characters.");
            return false;
        }

        try{
            //Send user input through the socket
            output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF("send " + message);
            input = new DataInputStream(socket.getInputStream());
            String response = input.readUTF();
            System.out.println(response);
            return true;
        }
        catch(IOException io){
            System.out.println(io);
            return false;
        }
    }
    private boolean newUser(String userID, String pass) { //Check correct usage of command then send to server
        if(!loggedIn.equals("")){
            System.out.println("Can't make new user while logged in.");
            return false;
        }
        if(userPassRequirements(userID, pass)){ //If input is good then send it off to server
            try{
                //Send data from client over socket
                //System.out.println("Trying to send " + userID + pass + " to the server...");
                output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF("newuser " + userID + " " + pass + "\n");
                //Listen for server response over socket
                input = new DataInputStream(socket.getInputStream()); //Need BufferedInputStream again?
                String response = input.readUTF();
                System.out.println(response);
                if(response.equals("New user account created. Please login.")){
                    return true;
                }
                else{
                    return false;
                }
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        return false;
    }
}
