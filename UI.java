package com.company;

import javax.swing.*;
import java.awt.*;

public class UI extends JFrame {
    int rows;
    int cols;
    int[] funzioneObiettivo;
    boolean fAcquisita = false;
    int[][] A;
    boolean vincoliAcquisiti = false;
    boolean bAcquisita = false;
    int[] b;
    AcqVettore bAcq;
    AcqVettore funzFrame;
    MatriceVincoli matVinc;
    final JButton btn = new JButton("calculate");

    public UI() {
        rows = Integer.parseInt(JOptionPane.showInputDialog("Inserisci il numero di righe di A"));
        cols = Integer.parseInt(JOptionPane.showInputDialog("Inserisci il numero di colonne (variabili) di A"));
        funzioneObiettivo = new int[cols];
        A = new int[rows][cols];
        b = new int[rows];
        Act actionListener = new Act(this);
        setLayout(new BorderLayout());
        funzFrame = new AcqVettore(cols, funzioneObiettivo);




        add(btn, BorderLayout.EAST);
        btn.addActionListener(actionListener);
        add(funzFrame, BorderLayout.NORTH);


        setTitle("Simplesso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    void updateUI() {
        if (fAcquisita) {
            matVinc = new MatriceVincoli(cols, rows);
            remove(funzFrame);
            add(matVinc, BorderLayout.CENTER);
            pack();

        }
        if (vincoliAcquisiti) {
            remove(matVinc);
            bAcq = new AcqVettore(rows, b);
            add(bAcq, BorderLayout.CENTER);
            pack();
        }
        if (bAcquisita) {
            remove(bAcq);
            String s = "min ";
            for (int i=0;  i<funzioneObiettivo.length; i++) {
                s += funzioneObiettivo[i] + " x" + i + "+ ";
            }
            s += "\n";

            add(new JLabel(s));
        }
    }
}
