package com.company;

import javax.swing.*;
import java.awt.*;

public class AcqVettore extends JPanel {
    int vars;
    int[] vett;

    public AcqVettore(int vars, int[] vett) {
        this.vars = vars;
        this.vett = vett;

        setLayout(new GridLayout(1, vars));
        for (int i=0; i<vars; i++) {
            add(new JTextField());
        }


    }
}
