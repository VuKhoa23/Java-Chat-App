package com.vukhoa23.app.client.ClientUI;

import com.vukhoa23.app.client.entity.FileSend;
import com.vukhoa23.app.client.entity.GroupCreated;
import com.vukhoa23.app.client.entity.MessageInfo;
import com.vukhoa23.app.client.entity.OnlineUserInfo;
import com.vukhoa23.utils.DbUtils;
import com.vukhoa23.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomePage extends JPanel {
    JPanel messagesContainer = new JPanel();
    JScrollPane messagesContainerScroll = new JScrollPane(messagesContainer);
    JPanel receiverBox = new JPanel();


    HomePage(String theUsername) throws IOException {
        // connect to server
        Socket socket = new Socket("localhost", 7777);
        System.out.println("Connected!");
        // get the output stream from the socket.
        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        //DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(theUsername);

        this.setLayout(null);
        this.setBounds(0, 0, 1000, 750);
        this.setBackground(Color.gray);

        // current receiver
        receiverBox.setBounds(205, 560, 590, 20);
        receiverBox.setBackground(Color.gray);
        receiverBox.setLayout(new FlowLayout());
        this.add(receiverBox);


        // button to redirect user to create page
        JButton createGroupBtn = new JButton("Create group chat");
        createGroupBtn.setBounds(820, 600, 150, 50);
        createGroupBtn.addActionListener(e -> {
            try {
                ClientFrame.homeToCreateGroup();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        this.add(createGroupBtn);

        JTextArea messageInp = new JTextArea();
        JButton sendBtn = new JButton("Send");
        messageInp.setColumns(30);
        messageInp.setRows(5);
        messageInp.setLineWrap(true);
        messageInp.setWrapStyleWord(true);

        JScrollPane messageInpScroll = new JScrollPane(messageInp);

        JPanel messageInpContainer = new JPanel();
        messageInpContainer.setLayout(new FlowLayout());
        messageInpContainer.setBounds(200, 600, 600, 90);
        messageInpContainer.add(messageInpScroll);
        messageInpContainer.add(sendBtn);

        this.add(messageInpContainer);

        JPanel onlineUsersContainer = new JPanel();

        onlineUsersContainer.setBackground(Color.lightGray);
        onlineUsersContainer.setLayout(new BoxLayout(onlineUsersContainer, BoxLayout.Y_AXIS));
        JScrollPane onlineUsersContainerScroll = new JScrollPane(onlineUsersContainer);
        onlineUsersContainerScroll.setBounds(0, 50, 200, 500);
        this.add(onlineUsersContainerScroll);

        JPanel groupChatContainer = new JPanel();
        groupChatContainer.setBackground(Color.lightGray);
        groupChatContainer.setLayout(new BoxLayout(groupChatContainer, BoxLayout.Y_AXIS));
        JScrollPane groupChatContainerScroll = new JScrollPane(groupChatContainer);
        groupChatContainerScroll.setBounds(800, 50, 200, 500);
        this.add(groupChatContainerScroll);


        messagesContainer.setBackground(Color.darkGray);
        messagesContainer.setLayout(new GridLayout(0, 1));
        messagesContainerScroll.setBounds(200, 50, 600, 500);
        this.add(messagesContainerScroll);

        //create thread that receive message from server
        Thread receiveThread = new Thread(() -> {
            try {
                populateGroupChat(groupChatContainer);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                while (true) {
                    // create a DataInputStream so we can read data from it.
                    InputStream inputStream = socket.getInputStream();
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    Object object = (Object) objectInputStream.readObject();
                    if (object instanceof MessageInfo) {
                        MessageInfo messageInfo = (MessageInfo) object;
                        if (messageInfo.getMessage().equals("quit")) {
                            break;
                        }
                        if (ClientFrame.currentReceiver != null && messageInfo.getReceiver() != null && (messageInfo.getReceiver().equals(ClientFrame.username)
                                && ClientFrame.currentReceiver.equals(messageInfo.getUsername()))) {
                            populateMessageToContainer(messageInfo.getUsername(), messageInfo.getReceiver());
                        } else if (ClientFrame.isGroupChat != null && ClientFrame.isGroupChat && (messageInfo.getGroupId() == ClientFrame.groupId)) {
                            populateGroupChatToContainer(messageInfo.getUsername(), messageInfo.getGroupId());
                        } else if (ClientFrame.currentReceiver != null && messageInfo.getReceiver() != null && (messageInfo.getReceiver().equals(ClientFrame.currentReceiver)) && (ClientFrame.username.equals(messageInfo.getUsername()))) {
                            System.out.println("here");
                            populateMessageToContainer(messageInfo.getUsername(), messageInfo.getReceiver());
                        }
                    } else if (object instanceof List) {
                        ArrayList<OnlineUserInfo> connectedUsers = (ArrayList<OnlineUserInfo>) object;
                        populateOnlineUsers(connectedUsers, onlineUsersContainer);
                    } else if (object instanceof GroupCreated) {
                        populateGroupChat(groupChatContainer);
                    }
                }
            } catch (IOException | SQLException err) {
                System.out.println("Error when receive message from client");
                System.exit(0);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        receiveThread.start();

        // send file button
        JButton selectFileBtn = new JButton("Send a file");
        selectFileBtn.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    Socket fileSocket = new Socket("localhost", 7777);
                    OutputStream socketOutputStream = fileSocket.getOutputStream();
                    // create a data output stream from the output stream so we can send data through it
                    ObjectOutputStream socketObjectOutputStream = new ObjectOutputStream(socketOutputStream);
                    Integer option = 3;
                    socketObjectOutputStream.writeObject(option);
                    File selectedFile = fileChooser.getSelectedFile();
                    float tempFileSize = selectedFile.length() / 1000000.0f;
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    float fileSize = Float.valueOf(decimalFormat.format(tempFileSize));
                    FileSend fileSend = new FileSend(selectedFile.getName(),
                            new Date().getTime() + FileUtils.getFileExtension(selectedFile));
                    socketObjectOutputStream.writeObject(fileSend);

                    if (ClientFrame.currentReceiver == null && ClientFrame.isGroupChat == null) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Select a user to chat with",
                                "Alert",
                                JOptionPane.ERROR_MESSAGE);
                        fileSocket.close();
                        socketObjectOutputStream.close();
                        // sending file to a user
                    } else if (ClientFrame.currentReceiver != null && (ClientFrame.isGroupChat == null || !ClientFrame.isGroupChat)) {
                        JFrame uploadFrame = new JFrame();
                        uploadFrame.setSize(new Dimension(300, 200));
                        JLabel label = new JLabel();
                        uploadFrame.setLayout(new FlowLayout());
                        uploadFrame.add(label);
                        uploadFrame.setVisible(true);

                        FileInputStream fileInputStream = new FileInputStream(selectedFile);
                        OutputStream fileOutputStream = fileSocket.getOutputStream();

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                        fileOutputStream.close();
                        label.setText("File sent");

                        fileSocket.close();
                        fileOutputStream.close();
                        fileOutputStream.close();

                        MessageInfo messageInfo = new MessageInfo(ClientFrame.username, ClientFrame.currentReceiver,
                                "", new Date().toString(), 1, fileSend.getOriginalName(), fileSend.getGeneratedName());
                        messageInfo.setIsGroupChat(0);
                        messageInfo.setQuery("INSERT INTO chat_history(sender, receiver, createdDate, is_groupChat, is_file, original_file_name, generated_file_name, file_size) values(?, ?, ?, 0, 1, ?, ?, ?)");
                        messageInfo.setFileSize(fileSize);
                        objectOutputStream.writeObject(messageInfo);
                    } else {
                        FileInputStream fileInputStream = new FileInputStream(selectedFile);
                        OutputStream fileOutputStream = fileSocket.getOutputStream();

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                        fileSocket.close();
                        fileOutputStream.close();

                        MessageInfo messageInfo = new MessageInfo(ClientFrame.username, "",
                                new Date().toString(), 1, ClientFrame.groupId, 1,
                                fileSend.getOriginalName(), fileSend.getGeneratedName());
                        messageInfo.setFileSize(fileSize);
                        messageInfo.setQuery("INSERT INTO chat_history(sender, createdDate, is_groupChat, group_id ,is_file, original_file_name, generated_file_name, file_size) values(?, ?, 1, ? ,1, ?, ?, ?)");

                        fileInputStream.close();
                        fileOutputStream.close();
                        objectOutputStream.writeObject(messageInfo);
                    }
                }
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
        messageInpContainer.add(selectFileBtn);

        sendBtn.addActionListener((e) -> {
            try {
                if (ClientFrame.currentReceiver == null && ClientFrame.isGroupChat == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Select a user to chat with",
                            "Alert",
                            JOptionPane.ERROR_MESSAGE);
                } else if (!ClientFrame.isGroupChat) {
                    String theString = messageInp.getText();
                    MessageInfo messageInfo = new MessageInfo(
                            ClientFrame.username,
                            ClientFrame.currentReceiver,
                            theString, new Date().toString());
                    messageInfo.setIsGroupChat(0);
                    messageInfo.setIsFile(0);
                    messageInfo.setQuery("INSERT INTO chat_history(sender, receiver, content, createdDate, is_groupChat, is_file) values(?, ?, ?, ?, ?, 0)");

                    // write the message we want to send
                    objectOutputStream.writeObject(messageInfo);
                } else {
                    String theString = messageInp.getText();
                    MessageInfo messageInfo = new MessageInfo(
                            ClientFrame.username,
                            theString,
                            new Date().toString(),
                            1,
                            ClientFrame.groupId
                    );
                    messageInfo.setIsFile(0);
                    messageInfo.setQuery("INSERT INTO chat_history(sender, content, createdDate, is_groupChat, group_id, is_file) VALUES(?, ?, ?, ?, ?, 0)");
                    // write the message we want to send
                    objectOutputStream.writeObject(messageInfo);
                    // populate messages
                    //populateGroupChatToContainer(ClientFrame.username, ClientFrame.groupId);
                    //objectOutputStream.flush();
                }
            } catch (IOException err) {
                throw new RuntimeException("Error when sending message from client");
            }
        });
    }


    public void populateGroupChatToContainer(String theUsername, int groupId) throws SQLException {
        messagesContainer.removeAll();
        Connection connection = DbUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM chat_history WHERE group_id=?"
        );
        preparedStatement.setInt(1, groupId);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            MessageInfo messageInfo = new MessageInfo(
                    rs.getString("sender"),
                    rs.getString("content"),
                    rs.getString("createdDate"),
                    rs.getInt("is_groupChat"),
                    rs.getInt("group_id"),
                    rs.getInt("is_file"),
                    rs.getString("original_file_name"),
                    rs.getString("generated_file_name"));
            if (messageInfo.getIsFile() == 0) {
                JPanel messageContainer = new JPanel();
                JLabel username = new JLabel(messageInfo.getUsername() + " - " + messageInfo.getCreatedDate());
                JTextArea theMessage = new JTextArea(messageInfo.getMessage());
                username.setPreferredSize(new Dimension(500, 30));
                theMessage.setColumns(50);
                theMessage.setRows(3);
                theMessage.setEditable(false);

                JScrollPane messageScroll = new JScrollPane(theMessage);

                messageContainer.setPreferredSize(new Dimension(600, 100));
                messageContainer.setLayout(new FlowLayout());
                messageContainer.add(username);
                messageContainer.add(messageScroll);
                messagesContainer.add(messageContainer);
            } else {
                JPanel messageContainer = new JPanel();
                JLabel username = new JLabel(messageInfo.getUsername() + " - " + messageInfo.getCreatedDate());
                JTextArea theMessage = new JTextArea(messageInfo.getOriginalFileName() + "\n" + rs.getFloat("file_size") + " MB");
                username.setPreferredSize(new Dimension(500, 30));
                theMessage.setColumns(30);
                theMessage.setRows(3);
                theMessage.setEditable(false);
                theMessage.setFont(new Font("Dialog", Font.BOLD, 12));

                JScrollPane messageScroll = new JScrollPane(theMessage);
                JButton downloadBtn = new JButton("Download");
                downloadBtn.addActionListener(e -> {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setCurrentDirectory(new java.io.File("."));
                        chooser.setDialogTitle("Choose directory to save the file");
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        //
                        // disable the "All files" option.
                        //
                        chooser.setAcceptAllFileFilterUsed(false);
                        //
                        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                            String dir = String.valueOf(chooser.getSelectedFile());
                            Socket downloadSocket = new Socket("localhost", 7777);
                            OutputStream outputStream = downloadSocket.getOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                            objectOutputStream.writeObject(new FileSend(messageInfo.getOriginalFileName(), messageInfo.getGeneratedFileName()));

                            FileOutputStream fileOutputStream = new FileOutputStream(dir + "\\" + messageInfo.getOriginalFileName());
                            JFrame downloadFrame = new JFrame();
                            downloadFrame.setSize(new Dimension(300, 200));
                            JLabel label = new JLabel("Downloading...");
                            downloadFrame.setLayout(new FlowLayout());
                            downloadFrame.add(label);
                            downloadFrame.setVisible(true);

                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = downloadSocket.getInputStream().read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, bytesRead);
                            }
                            label.setText("Download finished");
                            fileOutputStream.close();
                            objectOutputStream.close();
                            downloadSocket.close();
                        }

                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                });

                messageContainer.setPreferredSize(new Dimension(600, 100));
                messageContainer.setLayout(new FlowLayout());
                messageContainer.add(username);
                messageContainer.add(messageScroll);
                messageContainer.add(downloadBtn);
                messagesContainer.add(messageContainer);
            }
        }
        messagesContainer.validate();
        messagesContainer.repaint();
        messagesContainerScroll.validate();
        JScrollBar vertical = messagesContainerScroll.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

        // scroll to bottom when new messages are populated
    }

    public void populateMessageToContainer(String sender, String receiver) throws SQLException {
        messagesContainer.removeAll();
        Connection connection = DbUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM chat_history WHERE (sender=? AND receiver=?) OR (receiver=? AND sender=?)"
        );
        preparedStatement.setString(1, sender);
        preparedStatement.setString(2, receiver);
        preparedStatement.setString(3, sender);
        preparedStatement.setString(4, receiver);

        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            MessageInfo messageInfo = new MessageInfo(
                    rs.getString("sender"),
                    rs.getString("receiver"),
                    rs.getString("content"),
                    rs.getString("createdDate"),
                    rs.getInt("is_file"),
                    rs.getString("original_file_name"),
                    rs.getString("generated_file_name"));
            if (messageInfo.getIsFile() == 0) {
                JPanel messageContainer = new JPanel();
                JLabel username = new JLabel(messageInfo.getUsername() + " - " + messageInfo.getCreatedDate());
                JTextArea theMessage = new JTextArea(messageInfo.getMessage());
                username.setPreferredSize(new Dimension(500, 30));
                theMessage.setColumns(50);
                theMessage.setRows(3);
                theMessage.setEditable(false);


                JScrollPane messageScroll = new JScrollPane(theMessage);

                messageContainer.setPreferredSize(new Dimension(600, 100));
                messageContainer.setLayout(new FlowLayout());
                messageContainer.add(username);
                messageContainer.add(messageScroll);
                messagesContainer.add(messageContainer);
            } else {
                JPanel messageContainer = new JPanel();
                JLabel username = new JLabel(messageInfo.getUsername() + " - " + messageInfo.getCreatedDate());
                JTextArea theMessage = new JTextArea(messageInfo.getOriginalFileName() + "\n" + rs.getFloat("file_size") + " MB");
                username.setPreferredSize(new Dimension(500, 30));
                theMessage.setColumns(30);
                theMessage.setRows(3);
                theMessage.setEditable(false);
                theMessage.setFont(new Font("Dialog", Font.BOLD, 12));

                JScrollPane messageScroll = new JScrollPane(theMessage);
                JButton downloadBtn = new JButton("Download");
                downloadBtn.addActionListener(e -> {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setCurrentDirectory(new java.io.File("."));
                        chooser.setDialogTitle("Choose directory to save the file");
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        //
                        // disable the "All files" option.
                        //
                        chooser.setAcceptAllFileFilterUsed(false);
                        //
                        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                            String dir = String.valueOf(chooser.getSelectedFile());
                            Socket downloadSocket = new Socket("localhost", 7777);
                            OutputStream outputStream = downloadSocket.getOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                            objectOutputStream.writeObject(new FileSend(messageInfo.getOriginalFileName(), messageInfo.getGeneratedFileName()));

                            FileOutputStream fileOutputStream = new FileOutputStream(dir + "\\" + messageInfo.getOriginalFileName());
                            JFrame downloadFrame = new JFrame();
                            downloadFrame.setSize(new Dimension(300, 200));
                            JLabel label = new JLabel("Downloading...");
                            downloadFrame.setLayout(new FlowLayout());
                            downloadFrame.add(label);
                            downloadFrame.setVisible(true);

                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = downloadSocket.getInputStream().read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, bytesRead);
                            }
                            label.setText("Download finished");
                            fileOutputStream.close();
                            objectOutputStream.close();
                            downloadSocket.close();
                        }

                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                });
                messageContainer.setPreferredSize(new Dimension(600, 100));
                messageContainer.setLayout(new FlowLayout());
                messageContainer.add(username);
                messageContainer.add(messageScroll);
                messageContainer.add(downloadBtn);
                messagesContainer.add(messageContainer);
            }

        }
        messagesContainer.validate();
        messagesContainer.repaint();
        messagesContainerScroll.validate();
        JScrollBar vertical = messagesContainerScroll.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

        // scroll to bottom when new messages are populated

    }

    private void populateGroupChat(JPanel container) throws SQLException {
        container.removeAll();
        JLabel label = new JLabel("Your groups", SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(190, 30));
        label.setMaximumSize(new Dimension(190, 30));
        label.setMinimumSize(new Dimension(190, 30));
        container.add(label);
        String query = "SELECT group_id, group_chat.name\n" +
                "FROM users_groups\n" +
                "JOIN group_chat\n" +
                "ON group_chat.id = users_groups.group_id\n" +
                "WHERE users_groups.username =?";
        Connection connection = DbUtils.getConnection();
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, ClientFrame.username);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int groupId = rs.getInt(1);
            String groupName = rs.getString(2);
            JButton group = new JButton(groupName);
            group.setPreferredSize(new Dimension(190, 30));
            group.setMaximumSize(new Dimension(190, 30));
            group.setMinimumSize(new Dimension(190, 30));
            container.add(group);
            group.addActionListener(e -> {
                ClientFrame.isGroupChat = true;
                ClientFrame.groupId = groupId;
                receiverBox.removeAll();
                JLabel theReceiver = new JLabel("Chatting with group: " + groupName);
                receiverBox.add(theReceiver);
                receiverBox.revalidate();
                receiverBox.repaint();
                try {
                    populateGroupChatToContainer(ClientFrame.username, ClientFrame.groupId);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        container.revalidate();
        container.repaint();
    }

    public void populateOnlineUsers(ArrayList<OnlineUserInfo> onlineUserInfos, JPanel container) throws SQLException {
        container.removeAll();
        JLabel label = new JLabel("Users", SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(190, 30));
        label.setMaximumSize(new Dimension(190, 30));
        label.setMinimumSize(new Dimension(190, 30));
        container.add(label);

        List<String> allUsers = new ArrayList<>();

        Connection connection = DbUtils.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT username FROM account");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            allUsers.add(rs.getString(1));
        }

        List<String> onlineUsers = new ArrayList<>();
        for (OnlineUserInfo onlineUserInfo : onlineUserInfos) {
            onlineUsers.add(onlineUserInfo.getUsername());
        }

        for (String user : allUsers) {
            if (user.equals(ClientFrame.username)) {
                continue;
            }
            if (onlineUsers.contains(user)) {
                JButton online = new JButton(user + " - online");
                online.setBackground(Color.green);
                online.setPreferredSize(new Dimension(200, 30));
                online.setMaximumSize(new Dimension(200, 30));
                online.setMinimumSize(new Dimension(200, 30));
                container.add(online);
                online.addActionListener(e -> {
                    ClientFrame.currentReceiver = user;
                    ClientFrame.isGroupChat = false;
                    // show current receiver
                    JLabel theReceiver = new JLabel("Chatting with user: " + ClientFrame.currentReceiver);
                    receiverBox.removeAll();
                    receiverBox.add(theReceiver);
                    receiverBox.revalidate();
                    receiverBox.repaint();
                    try {
                        populateMessageToContainer(ClientFrame.username, ClientFrame.currentReceiver);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });

            } else {
                JButton online = new JButton(user + " - offline");
                online.setPreferredSize(new Dimension(200, 30));
                online.setMaximumSize(new Dimension(200, 30));
                online.setMinimumSize(new Dimension(200, 30));
                container.add(online);
                online.addActionListener(e -> {
                    ClientFrame.currentReceiver = user;
                    ClientFrame.isGroupChat = false;
                    JLabel theReceiver = new JLabel("Chatting with user: " + ClientFrame.currentReceiver);
                    receiverBox.removeAll();
                    receiverBox.add(theReceiver);
                    receiverBox.revalidate();
                    receiverBox.repaint();
                    try {
                        populateMessageToContainer(ClientFrame.username, ClientFrame.currentReceiver);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });

            }
        }
        container.revalidate();
        container.repaint();
    }
}