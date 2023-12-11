package com.vukhoa23.app.server;

import com.vukhoa23.app.client.ClientUI.ClientFrame;
import com.vukhoa23.app.client.entity.*;
import com.vukhoa23.utils.DbUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                                } else {
                                    if (messageInfo.getIsGroupChat() == 0 && messageInfo.getIsFile() == 0) {
                                        try {
                                            // connect to db and save the message
                                            Connection connection = DbUtils.getConnection();
                                            PreparedStatement stmt = connection.prepareStatement(
                                                    "INSERT INTO chat_history(sender, receiver, content, createdDate, is_groupChat, is_file) values(?, ?, ?, ?, ?, 0)"
                                            );
                                            stmt.setString(1, messageInfo.getUsername());
                                            stmt.setString(2, messageInfo.getReceiver());
                                            stmt.setString(3, messageInfo.getMessage());
                                            stmt.setString(4, messageInfo.getCreatedDate().toString());
                                            stmt.setInt(5, 0);
                                            stmt.executeUpdate();
                                            connection.close();
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else if (messageInfo.getIsGroupChat() == 1 && messageInfo.getIsFile() == 0) {
                                        try {
                                            Connection connection = DbUtils.getConnection();
                                            PreparedStatement stmt = connection.prepareStatement(
                                                    "INSERT INTO chat_history(sender, content, createdDate, is_groupChat, group_id, is_file) VALUES(?, ?, ?, ?, ?, 0)"
                                            );
                                            stmt.setString(1, messageInfo.getUsername());
                                            stmt.setString(2, messageInfo.getMessage());
                                            stmt.setString(3, messageInfo.getCreatedDate());
                                            stmt.setInt(4, messageInfo.isGroupChat());
                                            stmt.setInt(5, messageInfo.getGroupId());
                                            stmt.executeUpdate();
                                            connection.close();
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else if (messageInfo.getIsGroupChat() == 0 && messageInfo.getIsFile() == 1) {
                                        try {
                                            Connection connection = DbUtils.getConnection();
                                            PreparedStatement stmt = connection.prepareStatement(
                                                    "INSERT INTO chat_history(sender, receiver, createdDate, is_groupChat, is_file, original_file_name, generated_file_name, file_size) values(?, ?, ?, 0, 1, ?, ?, ?)"
                                            );
                                            stmt.setString(1, messageInfo.getUsername());
                                            stmt.setString(2, messageInfo.getReceiver());
                                            stmt.setString(3, new Date().toString());
                                            stmt.setString(4, messageInfo.getOriginalFileName());
                                            stmt.setString(5, messageInfo.getGeneratedFileName());
                                            stmt.setFloat(6, messageInfo.getFileSize());
                                            stmt.executeUpdate();
                                            connection.close();
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        try {
                                            Connection connection = DbUtils.getConnection();
                                            PreparedStatement stmt = connection.prepareStatement(
                                                    "INSERT INTO chat_history(sender, createdDate, is_groupChat, group_id ,is_file, original_file_name, generated_file_name, file_size) values(?, ?, 1, ? ,1, ?, ?, ?)"
                                            );

                                            stmt.setString(1, messageInfo.getUsername());
                                            stmt.setString(2, new Date().toString());
                                            stmt.setInt(3, messageInfo.getGroupId());
                                            stmt.setString(4, messageInfo.getOriginalFileName());
                                            stmt.setString(5, messageInfo.getGeneratedFileName());
                                            stmt.setFloat(6, messageInfo.getFileSize());
                                            stmt.executeUpdate();
                                            connection.close();
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }

                                    }
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
                        // save user upload file
                        else if (option == 3) {
                            FileSend fileSend = (FileSend) objectInputStream.readObject();
                            System.out.println(fileSend);
                            File dir = new File("./files");
                            dir.mkdirs();
                            FileOutputStream fileOutputStream = new FileOutputStream("./files/" + fileSend.getGeneratedName());

                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, bytesRead);
                            }
                            fileOutputStream.close();
                        }
                        // send groups that user belongs to
                        else if (option == 4) {
                            try {
                                String query = "SELECT group_id, group_chat.name\n" +
                                        "FROM users_groups\n" +
                                        "JOIN group_chat\n" +
                                        "ON group_chat.id = users_groups.group_id\n" +
                                        "WHERE users_groups.username =?";
                                String username = (String) objectInputStream.readObject();
                                Connection connection = DbUtils.getConnection();
                                PreparedStatement stmt = connection.prepareStatement(query);
                                stmt.setString(1, username);
                                ResultSet rs = stmt.executeQuery();
                                ArrayList<GroupQueryResult> groups = new ArrayList<>();
                                while(rs.next()){
                                    groups.add(new GroupQueryResult(rs.getInt(1), rs.getString(2)));
                                }
                                OutputStream outputStream = socket.getOutputStream();
                                // create a data output stream from the output stream so we can send data through it
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                objectOutputStream.writeObject(groups);
                                socket.close();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
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
                    } else if (object instanceof FileSend) {
                        FileSend fileSend = (FileSend) object;
                        System.out.println("User want to download: " + fileSend.getGeneratedName());
                        File fileToSend = new File("./files/" + fileSend.getGeneratedName());

                        FileInputStream fileInputStream = new FileInputStream(fileToSend);
                        OutputStream fileOutputStream = socket.getOutputStream();

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                        socket.close();
                        fileOutputStream.close();
                        fileInputStream.close();
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
