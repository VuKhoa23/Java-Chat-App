package com.vukhoa23.app.client.ClientUI;

import com.vukhoa23.app.client.entity.GroupCreated;
import com.vukhoa23.utils.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;

public class CreateGroup extends JPanel {
    CreateGroup() throws SQLException {
        this.setLayout(null);
        this.setBounds(0, 0, 1000, 750);
        this.setBackground(Color.gray);

        JPanel checkBoxesContainer = new JPanel();
        checkBoxesContainer.setBackground(Color.blue);
        checkBoxesContainer.setLayout(new GridLayout(0, 1));
        JScrollPane checkBoxesContainerScroll = new JScrollPane(checkBoxesContainer);
        checkBoxesContainerScroll.setBounds(200, 50, 600, 500);

        // get all users
        Connection connection = DbUtils.getConnection();
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT username FROM account"
        );
        ResultSet rs = stmt.executeQuery();
        ArrayList<JCheckBox> listOfUsersCbx = new ArrayList<JCheckBox>();
        while (rs.next()) {
            JCheckBox checkbox = new JCheckBox(rs.getString("username"));
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
                    PreparedStatement createGroup = connection.prepareStatement(
                            "INSERT INTO group_chat(name) VALUES(?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    createGroup.setString(1, groupName);
                    createGroup.executeUpdate();
                    ResultSet generatedKeys = createGroup.getGeneratedKeys();
                    Integer groupId;
                    if (generatedKeys.next()) {
                        groupId = generatedKeys.getInt(1);
                    } else {
                        groupId = null;
                    }
                    GroupCreated groupCreated = new GroupCreated();
                    listOfUsersCbx.forEach(cbx -> {
                        if (cbx.isSelected()) {
                            try {
                                PreparedStatement createUserGroup = connection.prepareStatement(
                                        "INSERT INTO users_groups(username, group_id) VALUES(?, ?)"
                                );
                                groupCreated.getUsersInGroup().add(cbx.getText());
                                createUserGroup.setString(1, cbx.getText());
                                createUserGroup.setInt(2, groupId);
                                createUserGroup.executeUpdate();
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    });
                    Socket socket = new Socket("localhost", 7777);
                    System.out.println("Connected!");
                    OutputStream outputStream = socket.getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(groupCreated);
                    outputStream.close();
                    connection.close();
                    stmt.close();
                    createGroup.close();
                    JOptionPane.showMessageDialog(
                            this,
                            "Group created successfully",
                            "Alert",
                            JOptionPane.INFORMATION_MESSAGE);
                    ClientFrame.createGroupToHome();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        this.add(createBtn);
    }
}
