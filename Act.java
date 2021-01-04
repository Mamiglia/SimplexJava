package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

class Act implements ActionListener {
    final UI frame;

    public Act(UI frame){

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().getClass().equals(JButton.class)) {
            String title = ((JButton) e.getSource()).getText();
            if (!frame.fAcquisita) {
                Component[] comps = frame.funzFrame.getComponents();
                int i = 0;
                for (Component c : comps) {
                    if (c.getClass().equals(JTextField.class)) {
                        JTextField tf = (JTextField) c;
                        frame.funzioneObiettivo[i] = Integer.parseInt(tf.getText());
                        i++;
                    }
                }
                System.out.println(Arrays.toString(frame.funzioneObiettivo));
                frame.fAcquisita = true;
                frame.updateUI();
            } else if (!frame.vincoliAcquisiti) {
                Component[] comps = frame.matVinc.getComponents();
                for (int i=0; i< frame.cols*frame.rows; i++) {
                    frame.A[i/ frame.cols][i%frame.cols] = Integer.parseInt(((JTextField) comps[i]).getText());
                }
                for (int[] a : frame.A) {
                    System.out.println(Arrays.toString(a));
                }
                frame.vincoliAcquisiti = true;
                frame.updateUI();
            } else if (!frame.bAcquisita) {
                Component[] comps = frame.bAcq.getComponents();
                int i = 0;
                for (Component c : comps) {
                    if (c.getClass().equals(JTextField.class)) {
                        frame.b[i] = Integer.parseInt(((JTextField) c).getText());
                        i++;
                    }
                }
                System.out.println(Arrays.toString(frame.b));
                frame.bAcquisita = true;
                frame.updateUI();
            }
        }
    }
}
