package com.vukhoa23.app.server;

import com.vukhoa23.utils.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ServerFrame extends JFrame {
    JPanel body = new JPanel();
    ServerFrame() {
        this.setSize(new Dimension(500, 300));
        this.setLayout(null);
        body.setBounds(0,0,500,300);
        body.setLayout(new FlowLayout());
        this.add(body);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTextField usernameInp = new JTextField();
        usernameInp.setPreferredSize(new Dimension(300, 50));
        JLabel usernameLbl = new JLabel("Input MySQL username");
        JPanel usernameContainer = new JPanel();
        usernameContainer.setLayout(new FlowLayout());
        usernameContainer.setPreferredSize(new Dimension(480, 50));
        usernameContainer.add(usernameLbl);
        usernameContainer.add(usernameInp);
        body.add(usernameContainer);

        JPasswordField passwordInp = new JPasswordField();
        passwordInp.setPreferredSize(new Dimension(300, 50));
        JLabel passwordLbl = new JLabel("Input MySQL password");
        JPanel passwordContainer = new JPanel();
        passwordContainer.setLayout(new FlowLayout());
        passwordContainer.setPreferredSize(new Dimension(480, 50));
        passwordContainer.add(passwordLbl);
        passwordContainer.add(passwordInp);
        body.add(passwordContainer);

        JTextField portInp = new JTextField();
        portInp.setPreferredSize(new Dimension(300, 50));
        JLabel portLbl = new JLabel("          Input MySQL port");
        JPanel portContainer = new JPanel();
        portContainer.setLayout(new FlowLayout());
        portContainer.setPreferredSize(new Dimension(480, 50));
        portContainer.add(portLbl);
        portContainer.add(portInp);
        body.add(portContainer);

        JLabel sqlLbl = new JLabel("The url will be: jdbc:mysql://localhost:<PORT>/chatapp", SwingConstants.CENTER);
        sqlLbl.setPreferredSize(new Dimension(480, 50));
        body.add(sqlLbl);

        JButton submitBtn = new JButton("RUN SERVER");
        body.add(submitBtn);

        submitBtn.addActionListener(e->{
            DbUtils.username = usernameInp.getText();
            DbUtils.password = passwordInp.getText();
            DbUtils.port = portInp.getText();
            try {
                Connection connection = DbUtils.getConnection();
                JOptionPane.showMessageDialog(
                        this,
                        "Server run successfully",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE
                );
                body.removeAll();
                JLabel serverRunning = new JLabel("Server is running");
                body.add(serverRunning);
                body.revalidate();
                body.repaint();
                connection.close();
                // if connection is made, run server functionalities
                Server.serverFunctionalities();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cannot connect to database",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        this.setVisible(true);
        this.setResizable(false);
    }
}
