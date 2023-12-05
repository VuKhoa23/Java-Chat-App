package com.vukhoa23.app.ClientUI;

import com.vukhoa23.app.entity.MessageInfo;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class HomePage extends JPanel {
    HomePage() throws IOException {
        // connect to server
        Socket socket = new Socket("localhost", 7777);
        System.out.println("Connected!");
        // get the output stream from the socket.
        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        //DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        this.setLayout(null);
        this.setBounds(0, 0, 800, 750);
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
        messageInpContainer.setBounds(100, 600, 600, 90);
        messageInpContainer.add(messageInpScroll);
        messageInpContainer.add(sendBtn);

        this.add(messageInpContainer);

        JPanel messagesContainer = new JPanel();
        messagesContainer.setBounds(100, 50, 600, 600);
        messagesContainer.setBackground(Color.darkGray);
        this.add(messagesContainer);

        //create thread that receive message from server
        Thread receiveThread = new Thread(() -> {
            try {
                while (true) {
                    // create a DataInputStream so we can read data from it.
                    InputStream inputStream = socket.getInputStream();
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    MessageInfo messageInfo = (MessageInfo) objectInputStream.readObject();
                    System.out.println(messageInfo);
                    if (messageInfo.getMessage().equals("quit")) {
                        System.exit(0);
                        break;
                    }
                }
            } catch (IOException err) {
                System.out.println("Error when receive message from client");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        receiveThread.start();

        sendBtn.addActionListener((e) -> {
            try {
                String theString = messageInp.getText();
                MessageInfo messageInfo = new MessageInfo(ClientFrame.username, theString);
                // write the message we want to send
                objectOutputStream.writeObject(messageInfo);
                //objectOutputStream.flush();
                //
            } catch (IOException err) {
                {
                    throw new RuntimeException("Error when sending message from client");
                }
            }
        });
    }
}
