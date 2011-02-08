/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.control;

import javax.swing.JFrame;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ComponentBroker {

    private JFrame mainFrame;

    /*ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this should be configured somewhere central and it
     must be ensured that everything to the universe will be scaled this way. ApplicationConfiguration
     could be a starting point and afterwards the universe scaling or something like that.
     */
//    private double scalingFactor=0.00001;
    private double scalingFactor=0.001;   

    private ComponentBroker() {
    }

    public static ComponentBroker getInstance() {
        return ComponentBrokerHolder.INSTANCE;
    }

    private static class ComponentBrokerHolder {
        private static final ComponentBroker INSTANCE = new ComponentBroker();
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(final JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public double getScalingFactor() {
        return scalingFactor;
    }

    public void setScalingFactor(final double scalingFactor) {
        this.scalingFactor = scalingFactor;
    }

 }
