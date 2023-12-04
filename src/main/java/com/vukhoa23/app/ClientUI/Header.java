package com.vukhoa23.app.ClientUI;

import javax.swing.*;
import java.awt.*;

public class Header extends JPanel {
    public static JLabel label = new JLabel("Not logged in");

    public Header(){
        this.setPreferredSize(new Dimension(600, 50));
        this.setBackground(Color.black);
        this.setLayout(new BorderLayout());
        label.setFont(new Font("SansSerif",Font.BOLD, 30));
        label.setForeground(Color.white);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        this.add(label);
    }

    public static void setHeader(String content){
        label.setText(content);
    }
}
