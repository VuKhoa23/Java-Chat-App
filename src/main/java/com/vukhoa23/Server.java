package com.vukhoa23;

import com.vukhoa23.app.entity.MessageInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<Socket> connectedSocket = new ArrayList<>();
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket ss = new ServerSocket(7777);
        while (true) {
            Socket socket = ss.accept(); // blocking call, this will wait until a connection is attempted on this port.
            System.out.println("Connection from " + socket + "!");
            connectedSocket.add(socket);
            Thread receiveThread = new Thread(() -> {
                try {
                    InputStream inputStream = socket.getInputStream();
                    // create a DataInputStream so we can read data from it.
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    while (true) {
                        // get the input stream from the connected socket

                        // read the message from the socket
                        MessageInfo messageInfo = (MessageInfo) objectInputStream.readObject();
                        System.out.println(messageInfo);
                        if (messageInfo.getMessage().equals("quit")) {
                            System.out.println(messageInfo.getUsername() + " disconnected");
                            connectedSocket.remove(socket);
                            socket.close();
                            break;
                        }
                        connectedSocket.forEach((connected)->{
                            try {
                                OutputStream outputStream = connected.getOutputStream();
                                // create a data output stream from the output stream so we can send data through it
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                objectOutputStream.writeObject(messageInfo);
                            }
                            catch(IOException err){
                                throw new RuntimeException("Error when send messages to connected clients");
                            }
                        });
                    }
                } catch (IOException err) {
                    System.out.println(err);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            receiveThread.start();
        }
    }
}
