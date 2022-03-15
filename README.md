# CS4850: Computer Networks Project

## Description

This is the Mizzou CS4850 2022SP class project implemented in Java using the Sockets API. This project was made with OpenJDK `17.0.1`, but the Sockets API is available on many versions. Below you can find the helpful documentation used to make this project.

[Sockets API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/Socket.html)

[ServerSocket](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/ServerSocket.html)

[Scanner](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Scanner.html)

[Files](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/file/Files.html)

## How to run

### Install Java 17

This project was made with OpenJDK `17.0.1` so ensure it is installed. To check your version of java and see if it was installed correctly, run `java --version` in the terminal.

### Compile Client & Server from root directory

From the root folder of the project, compile the files with `javac src/Client/Main.java && javac src/Server/Main.java`. This will compile both the Client & Server. Then to start the Server type `java src/Server/Main`. To run the Client type `java src/Client/Main`, but note that the Server must already be running or else it won't be able to connect through the Socket API and will close out of the program.