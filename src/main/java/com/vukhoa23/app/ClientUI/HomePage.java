package com.vukhoa23.app.ClientUI;

import com.vukhoa23.app.entity.MessageInfo;
import com.vukhoa23.app.entity.OnlineUserInfo;
import com.vukhoa23.utils.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomePage extends JPanel {
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
        onlineUsersContainer.setBounds(0, 50, 200, 500);
        onlineUsersContainer.setBackground(Color.red);
        onlineUsersContainer.setLayout(new FlowLayout());
        this.add(onlineUsersContainer);

        JPanel messagesContainer = new JPanel();
        messagesContainer.setBackground(Color.darkGray);
        messagesContainer.setLayout(new GridLayout(0, 1));
        JScrollPane messagesContainerScroll = new JScrollPane(messagesContainer);
        messagesContainerScroll.setBounds(200, 50, 600, 500);
        this.add(messagesContainerScroll);

        messagesContainerScroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });

        //create thread that receive message from server
        Thread receiveThread = new Thread(() -> {
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
                        JPanel messageContainer = new JPanel();
                        JLabel username = new JLabel(messageInfo.getUsername() + " - " + messageInfo.getCreatedDate());
                        JTextArea theMessage = new JTextArea(messageInfo.getMessage());
                        username.setPreferredSize(new Dimension(500, 30));
                        theMessage.setColumns(50);
                        theMessage.setRows(3);

                        messageContainer.setPreferredSize(new Dimension(600, 100));
                        messageContainer.setLayout(new FlowLayout());
                        messageContainer.add(username);
                        messageContainer.add(theMessage);

                        messagesContainer.add(messageContainer);
                        messagesContainer.revalidate();
                        messagesContainer.repaint();
                    } else if (object instanceof List) {
                        ArrayList<OnlineUserInfo> connectedUsers = (ArrayList<OnlineUserInfo>) object;
                        populateOnlineUsers(connectedUsers, onlineUsersContainer);
                    }
                }
            //
            } catch (IOException | SQLException err) {
                System.out.println("Error when receive message from client");
                System.exit(0);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        receiveThread.start();

        sendBtn.addActionListener((e) -> {
            try {
                String theString = messageInp.getText();
                MessageInfo messageInfo = new MessageInfo(ClientFrame.username, theString, new Date());
                // write the message we want to send
                objectOutputStream.writeObject(messageInfo);
                //objectOutputStream.flush();

            } catch (IOException err) {
                throw new RuntimeException("Error when sending message from client");
            }
        });
    }

    public void populateOnlineUsers(ArrayList<OnlineUserInfo> onlineUserInfos, JPanel container) throws SQLException {
        container.removeAll();
        JLabel label = new JLabel("Users");
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
                online.setPreferredSize(new Dimension(190, 30));
                container.add(online);
            } else {
                JButton online = new JButton(user);
                online.setPreferredSize(new Dimension(190, 30));
                container.add(online);
            }
        }
        container.revalidate();
        container.repaint();
    }
}
