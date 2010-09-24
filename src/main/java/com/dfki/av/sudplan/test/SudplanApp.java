package com.dfki.av.sudplan.test;

import com.dfki.av.sudplan.javax.swing.JPanel3D;
import com.dfki.av.sudplan.javax.swing.JPanelOuter2;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author
 */
public class SudplanApp {

    public JPanel3D visPanel;
    public JPanelOuter2 outerPanel;

    public static ByteArrayOutputStream outputstream = new ByteArrayOutputStream();

    public SudplanApp(String filename) {
        initComponents(filename);
    }

    private void initComponents(String filename) {
        JFrame frame = new JFrame("SudplanApp");

        try {
            visPanel = new JPanel3D(filename);
            outerPanel = new JPanelOuter2(visPanel);
            //frame.add(new JPanel3D(filename));
            frame.setLayout(new GridLayout(0,2));            
            frame.add(visPanel);
            frame.add(outerPanel);

        } catch (URISyntaxException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(-1);
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
//        if(args.length < 1){
//            System.out.println("Missing argument! Please enter data file!");
//            System.exit(-1);
//        }

        new SudplanApp("test.txt");

    }
}
