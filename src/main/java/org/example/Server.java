package org.example;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket ss = new ServerSocket(7777);
        while(true) {
            System.out.println("ServerSocket awaiting connections...");
            Socket socket = ss.accept(); // blocking call, this will wait until a connection is attempted on this port.
            System.out.println("Connection from " + socket + "!");

            Thread receiveThread = new Thread(() -> {
                String connected = socket.toString();
                try {
                    while (true) {
                        // get the input stream from the connected socket
                        InputStream inputStream = socket.getInputStream();
                        // create a DataInputStream so we can read data from it.
                        DataInputStream dataInputStream = new DataInputStream(inputStream);

                        // read the message from the socket
                        String message = dataInputStream.readUTF();
                        System.out.println(connected + ": " + message);
                        if(message.equals("quit")){
                            System.out.println(connected + " disconnected");
                            socket.close();
                            break;
                        }
                    }
                } catch (IOException err) {
                    System.out.println(err);
                }
            });
            receiveThread.start();
        }
    }
}
