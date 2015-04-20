/*
 * LayerAction.java
 *
 * Created by DFKI AV on 15.09.2011.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.vis;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.layers.Layer;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;

/**
 *
 */
public class LayerAction extends AbstractAction {

    /**
     * The {@link WorldWindow}.
     */
    private WorldWindow wwd;
    /**
     * The {@link Layer}.
     */
    private Layer layer;
    /**
     * Whether the {@link #layer} is selected or not.
     */
    private boolean selected;

    /**
     * Creates a action for the layer {@code layer}.
     *
     * @param layer the {@link Layer} to handle
     * @param wwd the {@link WorldWindow}
     * @param selected {@code true} if selected. Otherwise {@code false}
     */
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

    /**
     * Returns whether the {@link #layer} is selected or not.
     *
     * @return whether the {@link #layer} is selected or not.
     */
    public boolean isSelected() {
        return this.selected;
    }
}
