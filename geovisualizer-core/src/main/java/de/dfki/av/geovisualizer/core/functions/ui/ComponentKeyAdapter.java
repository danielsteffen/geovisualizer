/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.av.geovisualizer.core.functions.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tzimmermann
 */
public class ComponentKeyAdapter extends KeyAdapter {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ComponentKeyAdapter.class);

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            try {
                JComponent textField = (JComponent) e.getSource();
                textField.getParent().requestFocusInWindow();
                textField.requestFocus();
            } catch (ClassCastException ex) {
                log.error("" + ex);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }
}
