package com.vukhoa23.app.server;

import com.vukhoa23.app.client.entity.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static ArrayList<SocketInfo> connectedSocket = new ArrayList<>();

    public static ArrayList<OnlineUserInfo> onlineUserInfos = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket ss = new ServerSocket(7777);
        while (true) {
            Socket socket = ss.accept(); // blocking call, this will wait until a connection is attempted on this port.
            System.out.println("Connection from " + socket + "!");

            Thread receiveThread = new Thread(() -> {
                try {
                    InputStream inputStream = socket.getInputStream();
                    // create a DataInputStream so we can read data from it.
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    Object object = (Object) objectInputStream.readObject();
                    if (object instanceof String) {
                        String username = (String) object;
                        SocketInfo socketInfo = new SocketInfo(socket, username);
                        connectedSocket.add(socketInfo);
                        OnlineUserInfo userInfo = new OnlineUserInfo(socket.getPort(), username);
                        onlineUserInfos.add(userInfo);
                        // send data of connected users
                        connectedSocket.forEach((connected) -> {
                            try {
                                OutputStream outputStream = connected.getSocket().getOutputStream();
                                // create a data output stream from the output stream so we can send data through it
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                objectOutputStream.writeObject(onlineUserInfos);
                            } catch (IOException err) {
                                err.printStackTrace();
                                throw new RuntimeException("Error when send messages to connected clients");
                            }
                        });

                        while (true) {
                            // get the input stream from the connected socket
                            // read the message from the socket
                            Object receivedObject = objectInputStream.readObject();
                            if (receivedObject instanceof MessageInfo) {
                                MessageInfo messageInfo = (MessageInfo) receivedObject;
                                System.out.println(messageInfo);
                                if (messageInfo.getMessage().equals("quit")) {
                                    System.out.println(messageInfo.getUsername() + " disconnected");
                                    connectedSocket.remove(socketInfo);
                                    onlineUserInfos.remove(userInfo);
                                    socket.close();
                                    connectedSocket.forEach((connected) -> {
                                        try {
                                            OutputStream outputStream = connected.getSocket().getOutputStream();
                                            // create a data output stream from the output stream so we can send data through it
                                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                            objectOutputStream.writeObject(onlineUserInfos);
                                        } catch (IOException err) {
                                            err.printStackTrace();
                                            throw new RuntimeException("Error when send messages to connected clients");
                                        }
                                    });
                                    break;
                                }
                                connectedSocket.forEach((connected) -> {
                                    try {
                                        OutputStream outputStream = connected.getSocket().getOutputStream();
                                        // create a data output stream from the output stream so we can send data through it
                                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                        objectOutputStream.writeObject(messageInfo);
                                    } catch (IOException err) {
                                        throw new RuntimeException("Error when send messages to connected clients");
                                    }
                                });
                                System.out.println(connectedSocket);
                            }
                        }
                    } else if (object instanceof Integer) {
                        Integer option = (Integer) object;
                        // return list of online users, prevent same user log in at a time
                        if (option == 1) {
                            OutputStream outputStream = socket.getOutputStream();
                            // create a data output stream from the output stream so we can send data through it
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                            objectOutputStream.writeObject(onlineUserInfos);

                            objectOutputStream.close();
                            objectInputStream.close();
                            socket.close();
                        }
                        // when new user create account, populate that user to all user tab
                        else if (option == 2) {
                            connectedSocket.forEach((connected) -> {
                                try {
                                    OutputStream outputStream = connected.getSocket().getOutputStream();
                                    // create a data output stream from the output stream so we can send data through it
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                    objectOutputStream.writeObject(onlineUserInfos);
                                } catch (IOException err) {
                                    err.printStackTrace();
                                    throw new RuntimeException("Error when send messages to connected clients");
                                }
                            });
                        }
                        // save user file
                        else if (option == 3){
                            FileSend fileSend = (FileSend) objectInputStream.readObject();
                            System.out.println(fileSend);
                            File dir = new File("./files");
                            dir.mkdirs();
                            FileOutputStream fileOutputStream = new FileOutputStream("./files/"+fileSend.getGeneratedName());

                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, bytesRead);
                            }

                        }
                    } else if (object instanceof GroupCreated) {
                        GroupCreated groupCreated = (GroupCreated) object;
                        for (SocketInfo connected : connectedSocket) {
                            if (groupCreated.getUsersInGroup().contains(connected.getUsername())) {
                                OutputStream outputStream = connected.getSocket().getOutputStream();
                                // create a data output stream from the output stream so we can send data through it
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                objectOutputStream.writeObject(groupCreated);
                            }
                        }
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
