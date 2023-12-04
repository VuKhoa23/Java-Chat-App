package com.vukhoa23.app.ClientUI;

import com.vukhoa23.utils.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRegisterForm extends JPanel {
    public class LoginForm extends JPanel {
        LoginForm() throws SQLException {
            JTextField usernameInp = new JTextField();
            usernameInp.setBounds(20, 10, 150, 30);
            JLabel usernameLbl = new JLabel("Username");
            usernameLbl.setBounds(20, 10, 80, 30);

            JTextField passwordInp = new JTextField();
            passwordInp.setBounds(20, 50, 150, 30);
            JLabel passwordLbl = new JLabel("Password");
            passwordLbl.setBounds(20, 50, 80, 30);

            JPanel labelContainer = new JPanel();
            labelContainer.setPreferredSize(new Dimension(100, 100));
            labelContainer.setLayout(null);
            labelContainer.add(usernameLbl);
            labelContainer.add(passwordLbl);

            JPanel inputContainer = new JPanel();
            inputContainer.setPreferredSize(new Dimension(200, 100));
            inputContainer.setLayout(null);
            inputContainer.add(usernameInp);
            inputContainer.add(passwordInp);

            JLabel login = new JLabel("Login", SwingConstants.CENTER);
            login.setPreferredSize(new Dimension(300, 40));

            JPanel btnContainer = new JPanel();
            btnContainer.setLayout(new FlowLayout());
            btnContainer.setPreferredSize(new Dimension(300, 100));
            JButton loginBtn = new JButton("Login");
            JButton registerBtn = new JButton("Register now");

            this.setLayout(new FlowLayout(0, 0, 0));
            this.setBackground(Color.gray);

            this.add(login);
            this.add(labelContainer);
            this.add(inputContainer);
            btnContainer.add(loginBtn);
            btnContainer.add(registerBtn);
            this.add(btnContainer);
            this.setBounds(240, 200, 300, 190);

            loginBtn.addActionListener((e -> {
                try {
                    String inputUsername = usernameInp.getText();
                    String inputPassword = passwordInp.getText();
                    // handle button events
                    Connection connection = DbUtils.getConnection();
                    PreparedStatement stmt = connection.prepareStatement("SELECT * FROM account WHERE username=?");
                    stmt.setString(1, inputUsername);
                    ResultSet rs = stmt.executeQuery();
                    String username = null;
                    String password = null;
                    while (rs.next()) {
                        username = rs.getString(1);
                        password = rs.getString(2);
                    }
                    if(username == null){
                        JOptionPane.showMessageDialog(
                                this,
                                "Account doesn't exists",
                                "Alert",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        if(!password.equals(inputPassword)){
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Wrong password",
                                    "Alert",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else{
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Login succeed",
                                    "Alert",
                                    JOptionPane.INFORMATION_MESSAGE);
                            ClientFrame.loggedInSuccess(username);
                        }
                    }
                    connection.close();
                    stmt.close();
                }
                catch(SQLException err){
                    throw new RuntimeException("Database error when login btn event trigger");
                }
            }));

            registerBtn.addActionListener((e -> {
                try {
                    String inputUsername = usernameInp.getText();
                    String inputPassword = passwordInp.getText();
                    // handle button events
                    Connection connection = DbUtils.getConnection();
                    PreparedStatement stmt = connection.prepareStatement("SELECT * FROM account WHERE username=?");
                    stmt.setString(1, inputUsername);
                    ResultSet rs = stmt.executeQuery();
                    String username = null;
                    String password = null;
                    while (rs.next()) {
                        username = rs.getString(1);
                        password = rs.getString(2);
                    }
                    if(!(username == null)){
                        JOptionPane.showMessageDialog(
                                this,
                                "Account already exists",
                                "Alert",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        String createQuery = "INSERT INTO ACCOUNT VALUES(?, ?)";
                        PreparedStatement createStmt = connection.prepareStatement(createQuery);
                        createStmt.setString(1, inputUsername);
                        createStmt.setString(2, inputPassword);
                        createStmt.executeUpdate();
                        JOptionPane.showMessageDialog(
                                this,
                                "Account created successfully",
                                "Alert",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    connection.close();
                    stmt.close();
                }
                catch(SQLException err){
                    throw new RuntimeException("Database error when login btn event trigger");
                }
            }));
        }
    }

    LoginRegisterForm() throws SQLException {
        this.setBounds(0, 0, 800, 750);
        this.setLayout(null);
        this.setBackground(Color.lightGray);

        this.add(new LoginForm());

    }
}
