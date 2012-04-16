/*
 *  LayerAction.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.layers.Layer;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class LayerAction extends AbstractAction {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private WorldWindow wwd;
    private Layer layer;
    private boolean selected;

    public LayerAction(Layer layer, WorldWindow wwd, boolean selected) {
        super(layer.getName());
        this.wwd = wwd;
        this.layer = layer;
        this.selected = selected;
        this.layer.setEnabled(this.selected);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (((JCheckBoxMenuItem) actionEvent.getSource()).isSelected()) {
            this.layer.setEnabled(true);
        } else {
            this.layer.setEnabled(false);
        }
        wwd.redraw();
    }

    public boolean isSelected() {
        return this.selected;
    }
}
