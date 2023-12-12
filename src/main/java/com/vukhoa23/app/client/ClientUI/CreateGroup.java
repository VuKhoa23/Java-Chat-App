package com.vukhoa23.app.client.ClientUI;

import com.vukhoa23.app.entity.AppConstants;
import com.vukhoa23.app.entity.GroupCreated;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class CreateGroup extends JPanel {
    CreateGroup() {
        try{
            this.setLayout(null);
            this.setBounds(0, 0, 1000, 750);
            this.setBackground(Color.gray);

            JPanel checkBoxesContainer = new JPanel();
            checkBoxesContainer.setBackground(Color.blue);
            checkBoxesContainer.setLayout(new GridLayout(0, 1));
            JScrollPane checkBoxesContainerScroll = new JScrollPane(checkBoxesContainer);
            checkBoxesContainerScroll.setBounds(200, 50, 600, 500);

            // get all users
            Socket allUsersSocket = new Socket(AppConstants.SERVER_HOST, AppConstants.PORT);
            OutputStream allUsersOutputStream = allUsersSocket.getOutputStream();
            ObjectOutputStream allUsersObjectOutputStream = new ObjectOutputStream(allUsersOutputStream);
            Integer option = 5;
            allUsersObjectOutputStream.writeObject(option);

            InputStream allUsersInputStream = allUsersSocket.getInputStream();
            ObjectInputStream allUsersObjectInputStream = new ObjectInputStream(allUsersInputStream);
            ArrayList<String> allUsers = (ArrayList<String>) allUsersObjectInputStream.readObject();
            allUsersSocket.close();

            ArrayList<JCheckBox> listOfUsersCbx = new ArrayList<JCheckBox>();
            for (String user : allUsers) {
                JCheckBox checkbox = new JCheckBox(user);
                checkbox.setPreferredSize(new Dimension(500, 50));
                checkBoxesContainer.add(checkbox);
                checkBoxesContainer.revalidate();
                checkBoxesContainer.repaint();
                checkBoxesContainer.validate();
                listOfUsersCbx.add(checkbox);
            }


            JButton backBtn = new JButton("Back to home");
            backBtn.setBounds(820, 600, 150, 50);
            backBtn.addActionListener(e -> {
                ClientFrame.createGroupToHome();
            });
            this.add(backBtn);
            this.add(checkBoxesContainerScroll);

            JLabel groupNameLbl = new JLabel("Input group name");
            groupNameLbl.setBounds(200, 580, 100, 30);

            JTextField groupNameInput = new JTextField();
            groupNameInput.setBounds(310, 580, 200, 30);
            this.add(groupNameInput);
            this.add(groupNameLbl);


            JButton createBtn = new JButton("Create group");
            createBtn.setBounds(520, 580, 120, 30);
            createBtn.addActionListener(e -> {
                String groupName = groupNameInput.getText();
                if (groupName == null || groupName.length() == 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Group name cannot be empty !",
                            "Alert",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        Socket createGroupSocket = new Socket(AppConstants.SERVER_HOST, AppConstants.PORT);
                        OutputStream createGroupOutputStream = createGroupSocket.getOutputStream();
                        ObjectOutputStream createGroupObjectOutputStream = new ObjectOutputStream(createGroupOutputStream);

                        Integer option2 = 8;
                        createGroupObjectOutputStream.writeObject(option2);
                        createGroupObjectOutputStream.writeObject(groupName);

                        InputStream createGroupInputStream = createGroupSocket.getInputStream();
                        ObjectInputStream createGroupObjectInputStream = new ObjectInputStream(createGroupInputStream);

                        Integer groupId = (Integer) createGroupObjectInputStream.readObject();
                        createGroupObjectInputStream.close();
                        createGroupObjectOutputStream.close();
                        createGroupSocket.close();
                        GroupCreated groupCreated = new GroupCreated();



                        listOfUsersCbx.forEach(cbx -> {
                            if (cbx.isSelected()) {
                                try {
                                    Socket insertMemeberSocket = new Socket(AppConstants.SERVER_HOST, AppConstants.PORT);
                                    OutputStream insertMemeberOutputStream = insertMemeberSocket.getOutputStream();
                                    ObjectOutputStream insertMemeberObjectOutputStream = new ObjectOutputStream(insertMemeberOutputStream);

                                    Integer option3 = 9;
                                    insertMemeberObjectOutputStream.writeObject(option3);
                                    insertMemeberObjectOutputStream.writeObject(cbx.getText());
                                    insertMemeberObjectOutputStream.writeObject(groupId);

                                    groupCreated.getUsersInGroup().add(cbx.getText());

                                    insertMemeberSocket.close();
                                    insertMemeberObjectOutputStream.close();
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });
                        Socket socket = new Socket(AppConstants.SERVER_HOST, AppConstants.PORT);
                        OutputStream outputStream = socket.getOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                        objectOutputStream.writeObject(groupCreated);
                        outputStream.close();
                        JOptionPane.showMessageDialog(
                                this,
                                "Group created successfully",
                                "Alert",
                                JOptionPane.INFORMATION_MESSAGE);
                        ClientFrame.createGroupToHome();
                    } catch (IOException | ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            this.add(createBtn);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
