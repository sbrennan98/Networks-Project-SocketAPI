package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

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
            socket = serverSocket.accept();
            System.out.println("Client connected!");
            input = new DataInputStream(socket.getInputStream());
            //closeConnection();
        } catch (IOException io) {
            System.out.println(io);
        }
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
    public boolean newUser(String username, String password){
        //Open userlist
        //Check if username is already taken, else make user and append to userlist.
        try{
            File file = new File("../../users.txt"); //src: https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){ //O(n) for n user entries
                String user = scanner.nextLine();
                if(user.equals(username)){ //Almost forgot .equals() method from 3330
                    System.out.println(user + " already taken, please try again with another username.\n");
                    return false;
                }
            }
            System.out.println("user " + username + " not found. Creating user!\n");

            return true;
        }
        catch(FileNotFoundException fnf){
            System.out.println(fnf);
            return false;
        }

    }
}
