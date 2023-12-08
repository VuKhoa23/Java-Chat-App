package com.vukhoa23.app.ClientUI;

import javax.swing.*;
import java.awt.*;

public class CreateGroup extends JPanel {
    CreateGroup(){
        this.setLayout(null);
        this.setBounds(0, 0, 1000, 750);
        this.setBackground(Color.gray);



        JButton backBtn = new JButton("Back to home");
        backBtn.setBounds(820, 600, 150, 50);
        backBtn.addActionListener(e->{
            ClientFrame.createGroupToHome();
        });
        this.add(backBtn);
    }
}
