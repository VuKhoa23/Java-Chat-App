package com.vukhoa23.app.ClientUI;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ClientFrame extends JFrame {
    private static JPanel body = new JPanel();
    public static boolean isLoggedIn = false;
    public ClientFrame() throws SQLException {
        this.setLayout(new BorderLayout());
        body.setLayout(null);

        this.add(new Header(), BorderLayout.NORTH);
        this.add(body, BorderLayout.CENTER);
        setBody(new LoginRegisterForm());

        this.setSize(new Dimension(800, 800));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static void loggedInSuccess(String username) {
        isLoggedIn = true;
        setBody(new HomePage());
        Header.setHeader("Logged in as " + username);
    }

    private static void setBody(JPanel content){
        body.removeAll();
        body.add(content);
        body.revalidate();
        body.repaint();
    }
}
