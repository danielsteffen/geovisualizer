/*
 *  ConstantColor.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.vis.core.ISource;
import java.awt.Color;

/**
 *
 * @author steffen
 */
public class ConstantColor extends ColorTransferFunction {

    /**
     * The constant {@link Color} to be used.
     */
    private Color color;

    /**
     * Constructor of {@link ConstantColor}. The {@link #color} member is set to {@link Color#GRAY}.
     */
    public ConstantColor() {
        this.color = Color.GRAY;
    }

    @Override
    public Object calc(Object o) {
        return color;
    }

    @Override
    public String getName() {
        return "Constant Color";
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
        return this.color;
    }

    /**
     * Sets the constant {@link #color} for this transfer function.
     *
     * @param c the {@link Color} to set.
     */
    public void setColor(Color c) {
        this.color = c;
    }
}
