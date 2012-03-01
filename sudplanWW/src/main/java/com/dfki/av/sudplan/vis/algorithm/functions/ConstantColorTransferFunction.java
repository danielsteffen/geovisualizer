/*
 *  ConstantColorTransferFunction.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm.functions;

import com.dfki.av.sudplan.io.DataInput;
import java.awt.Color;

/**
 *
 * @author steffen
 */
public class ConstantColorTransferFunction extends ColorTransferFunction {

    /**
     *
     */
    private Color color;

    /**
     *
     */
    public ConstantColorTransferFunction() {
        this.color = Color.WHITE;
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
    public void preprocess(DataInput data, String attribute) {
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
