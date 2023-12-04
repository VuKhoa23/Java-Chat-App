package com.vukhoa23.app.ClientUI;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JPanel {
    HomePage(){
        this.setBounds(0,0,800,750);
        this.setBackground(Color.red);
        JLabel home = new JLabel("Home");
        this.add(home);
    }
}
