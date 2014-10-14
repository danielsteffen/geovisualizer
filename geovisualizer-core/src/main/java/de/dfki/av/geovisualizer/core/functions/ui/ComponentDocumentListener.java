/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.av.geovisualizer.core.functions.ui;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tzimmermann
 */
public class ComponentDocumentListener implements DocumentListener {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ComponentDocumentListener.class);
    private Timer timer;
    private final JComponent component;

    public ComponentDocumentListener(JComponent component) {
        this.component = component;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        if(timer != null){
            timer.purge();
            timer.cancel();
        }
        timer = new Timer();
        try {
            timer.schedule(new FocusTask(), 500);
        } catch (ClassCastException ex) {
            log.error("" + ex);
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if(timer != null){
            timer.purge();
            timer.cancel();
        }
        timer = new Timer();
        try {
            timer.schedule(new FocusTask(), 500);
        } catch (ClassCastException ex) {
            log.error("" + ex);
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        if(timer != null){
            timer.purge();
            timer.cancel();
        }
        timer = new Timer();
        try {
            timer.schedule(new FocusTask(), 500);
        } catch (ClassCastException ex) {
            log.error("" + ex);
        }
    }

    class FocusTask extends TimerTask {
        @Override
        public void run() {
            component.getParent().requestFocusInWindow();
            component.requestFocus();
        }
    }
}
