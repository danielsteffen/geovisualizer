/*
 * SensorMapAction.java
 *
 * Created by wearHEALTH on 20.04.2015.
 * Copyright (c) 2015 TU Kaiserslautern, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.uni.kl.cs.geovisualizer.plugin.sensormap;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an implementation of {@link AbstractAction}.
 */
public class SensorMapAction extends AbstractAction{
    
    /*
     * Logger for this class.
     */
    private final static Logger LOG = LoggerFactory.getLogger(SensorMapAction.class);
    
    /**
     * Constructor.
     */
    public SensorMapAction(){
        super("ExampleAction");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.debug("actionPerformed({})", e.toString());
        JDialog dialog = new JDialog();
        dialog.setSize(100, 100);
        dialog.setTitle("Example Plugin Dialog");
        dialog.setVisible(true);
    }
}
