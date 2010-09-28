/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.test;

import com.dfki.av.sudplan.javax.swing.ControlPanel;
import com.dfki.av.sudplan.javax.swing.JPanel3D;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author frankmichel
 */
public class Main {

    private JFrame mainFrame;
    private ControlPanel controlPanel;
    private JPanel3D visPanel;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Main();
        
    }

    public Main(){

        initComponents();
    }

    private void initComponents()
    {
        mainFrame = new JFrame("SudplanApp");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setMinimumSize(new Dimension(1024, 800));
        mainFrame.setLayout(new BorderLayout());
        visPanel = new JPanel3D();
        controlPanel = new ControlPanel(visPanel);
        

        mainFrame.add(controlPanel, BorderLayout.WEST);
        mainFrame.add(visPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

}
