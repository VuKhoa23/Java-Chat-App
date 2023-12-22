package com.vukhoa23.app.client.ClientUI;

import com.vukhoa23.app.entity.AccountInfo;
import com.vukhoa23.app.entity.AppConstants;
import com.vukhoa23.app.entity.OnlineUserInfo;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class LoginRegisterForm extends JPanel {
    public class LoginForm extends JPanel {
        LoginForm(){
            JTextField usernameInp = new JTextField();
            usernameInp.setBounds(20, 10, 150, 30);
            JLabel usernameLbl = new JLabel("Username");
            usernameLbl.setBounds(20, 10, 80, 30);

            JPasswordField passwordInp = new JPasswordField();
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
            this.setBounds(340, 200, 300, 190);

            loginBtn.addActionListener((e -> {
                try {
                    String inputUsername = usernameInp.getText();
                    String inputPassword = passwordInp.getText();
                    // get user from server
                    Socket getUserSocket = new Socket(AppConstants.SERVER_HOST, AppConstants.PORT);
                    OutputStream getUserOutputStream = getUserSocket.getOutputStream();
                    ObjectOutputStream getUserObjectOutputStream = new ObjectOutputStream(getUserOutputStream);
                    Integer getUserOption = 10;
                    getUserObjectOutputStream.writeObject(getUserOption);
                    getUserObjectOutputStream.writeObject(inputUsername);

                    InputStream getUserInputStream = getUserSocket.getInputStream();
                    ObjectInputStream getUserObjectInputStream = new ObjectInputStream(getUserInputStream);
                    AccountInfo accountInfo = (AccountInfo) getUserObjectInputStream.readObject();
                    getUserSocket.close();
                    // handle button events

                    if (accountInfo.getUsername() == null) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Account doesn't exists",
                                "Alert",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (!accountInfo.getPassword().equals(inputPassword)) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Wrong password",
                                    "Alert",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            Socket socket = new Socket(AppConstants.SERVER_HOST, AppConstants.PORT);
                            OutputStream outputStream = socket.getOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                            Integer option = 1;
                            objectOutputStream.writeObject(option);

                            InputStream inputStream = socket.getInputStream();
                            // create a DataInputStream so we can read data from it.
                            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                            ArrayList<OnlineUserInfo> onlineUserInfos = (ArrayList<OnlineUserInfo>) objectInputStream.readObject();
                            boolean isLoggedIn = false;
                            for (OnlineUserInfo onlineUserInfo : onlineUserInfos) {
                                if (accountInfo.getUsername().equals(onlineUserInfo.getUsername())) {
                                    isLoggedIn = true;
                                    break;
                                }
                            }
                            if (!isLoggedIn) {
                                JOptionPane.showMessageDialog(
                                        this,
                                        "Login succeed",
                                        "Alert",
                                        JOptionPane.INFORMATION_MESSAGE);
                                ClientFrame.loggedInSuccess(accountInfo.getUsername());
                            } else {
                                JOptionPane.showMessageDialog(
                                        this,
                                        "This account is already logged in",
                                        "Alert",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } catch (IOException err) {
                    throw new RuntimeException("Database error when login btn event trigger");
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }));

            registerBtn.addActionListener((e -> {
                try {
                    String inputUsername = usernameInp.getText();
                    String inputPassword = passwordInp.getText();
                    // handle button events

                    Socket getUserSocket = new Socket(AppConstants.SERVER_HOST, AppConstants.PORT);
                    OutputStream getUserOutputStream = getUserSocket.getOutputStream();
                    ObjectOutputStream getUserObjectOutputStream = new ObjectOutputStream(getUserOutputStream);
                    Integer getUserOption = 10;
                    getUserObjectOutputStream.writeObject(getUserOption);
                    getUserObjectOutputStream.writeObject(inputUsername);

                    InputStream getUserInputStream = getUserSocket.getInputStream();
                    ObjectInputStream getUserObjectInputStream = new ObjectInputStream(getUserInputStream);
                    AccountInfo accountInfo = (AccountInfo) getUserObjectInputStream.readObject();
                    getUserSocket.close();

                    if (!(accountInfo.getUsername() == null)) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Account already exists",
                                "Alert",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        Socket createAccountSocket = new Socket(AppConstants.SERVER_HOST, AppConstants.PORT);
                        OutputStream createAccountOutputStream = createAccountSocket.getOutputStream();
                        ObjectOutputStream createAccountObjectOutputStream = new ObjectOutputStream(createAccountOutputStream);
                        Integer createAccountOption = 11;
                        createAccountObjectOutputStream.writeObject(createAccountOption);
                        AccountInfo accountToBeCreated = new AccountInfo(usernameInp.getText(), passwordInp.getText());
                        createAccountObjectOutputStream.writeObject(accountToBeCreated);
                        createAccountSocket.close();
                        createAccountObjectOutputStream.close();
                        JOptionPane.showMessageDialog(
                                this,
                                "Account created successfully",
                                "Alert",
                                JOptionPane.INFORMATION_MESSAGE);
                        try {
                            Socket socket = new Socket(AppConstants.SERVER_HOST, AppConstants.PORT);
                            OutputStream outputStream = socket.getOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                            Integer option = 2;
                            objectOutputStream.writeObject(option);
                        } catch (UnknownHostException ex) {
                            throw new RuntimeException(ex);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                }catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }));
        }
    }

    LoginRegisterForm() {
        this.setBounds(0, 0, 1000, 750);
        this.setLayout(null);
        this.setBackground(Color.lightGray);

        this.add(new LoginForm());

    }
}
