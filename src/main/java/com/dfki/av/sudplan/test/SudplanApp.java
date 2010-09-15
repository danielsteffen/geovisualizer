package com.dfki.av.sudplan.test;

import com.dfki.av.sudplan.javax.swing.JPanel3D;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Martin Weller
 */
public class SudplanApp {

    public SudplanApp(String filename) {
        initComponents(filename);
    }

    private void initComponents(String filename) {
        JFrame frame = new JFrame("SudplanApp");

        try {
            frame.add(new JPanel3D(filename));
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(-1);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(-1);
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setMinimumSize(new Dimension(
                1000, 1000));
        frame.pack();
        frame.setVisible(
                true);
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if(args.length < 1){
            System.out.println("Missing argument! Please enter data file!");
            System.exit(-1);
        }

        new SudplanApp(args[0]);
    }
}
