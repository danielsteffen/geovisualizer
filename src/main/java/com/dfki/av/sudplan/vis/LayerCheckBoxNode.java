/*
 *  LayerCheckBoxNode.java 
 *
 *  Created by DFKI AV on 20.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import gov.nasa.worldwind.layers.Layer;

/**
 * The class covering the {@link Layer} object.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class LayerCheckBoxNode {

    /**
     * The text to be displayed at the checkbox node.
     */
    private String text;
    /**
     * Whether the checkbos is selected or not.
     */
    private boolean selected;

    /**
     * Constructor.
     *
     * @param layer the {@link Layer} to set.
     */
    public LayerCheckBoxNode(String text, boolean selected) {
        this.text = text;
        this.selected = selected;
    }

    @Override
    public String toString() {
        return this.getText();
    }

    /**
     * Returns the text to be displayed for the node.
     *
     * @return the text to return.
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
