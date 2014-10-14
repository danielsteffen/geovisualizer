/*
 * ConstantColor.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.functions.ui.TFPConstantColor;
import de.dfki.av.geovisualizer.core.render.Legend;
import java.awt.Color;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ConstantColor extends ColorTransferFunction {

    /**
     * The UI for the transfer function.
     */
    private AbstractTransferFunctionPanel panel;

    /**
     * Constructor of {@link ConstantColor}. The {@link #colorList} member is
     * set to {@link Color#GRAY}.
     */
    public ConstantColor() {
        super();
        this.colorList.add(Color.GRAY);
    }

    @Override
    public Object calc(Object o) {
        return colorList.get(0);
    }

    @Override
    public String getName() {
        return "Constant color";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
    }

    /**
     * Returns the constant {@link Color} object.
     *
     * @return the {@link Color} to return.
     */
    public Color getColor() {
        return colorList.get(0);
    }

    /**
     * Sets the constant {@link Color} for this transfer function.
     *
     * @param color the {@link Color} to set.
     * @throws IllegalArgumentException if c set to {@code null}.
     */
    public void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        this.colorList.remove(0);
        this.colorList.add(color);
    }

    @Override
    public AbstractTransferFunctionPanel getPanel() {
        if (this.panel == null) {
            this.panel = new TFPConstantColor(this);
        }
        return this.panel;
    }

    @Override
    public Legend getLegend(String title) {
        return getLegend();
    }
}
