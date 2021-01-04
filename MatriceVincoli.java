package com.company;

import javax.swing.*;
import java.awt.*;

class MatriceVincoli extends JPanel {
    final int cols;
    final int rows;

    public MatriceVincoli(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        setLayout(new GridLayout(rows, cols));
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                add(new JTextField());
            }
        }

    }

}
