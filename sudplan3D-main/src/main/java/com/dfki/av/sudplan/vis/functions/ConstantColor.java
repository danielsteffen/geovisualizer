/*
 *  ConstantColor.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.vis.core.DataSource;
import java.awt.Color;

/**
 *
 * @author steffen
 */
public class ConstantColor extends ColorTransferFunction {

    /**
     *
     */
    private Color color;

    /**
     *
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
    public void preprocess(DataSource data, String attribute) {
        // Nothing to do.
    }

    /**
     *
     * @return
     */
    public Color getColor() {
        return this.color;
    }

    /**
     *
     * @param c
     */
    public void setColor(Color c) {
        this.color = c;
    }
}
