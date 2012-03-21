/*
 *  ColorParameter.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.core;

import com.dfki.av.sudplan.vis.functions.ColorTransferFunction;
import com.dfki.av.sudplan.vis.functions.ConstantColor;

/**
 *
 * @author steffen
 */
public class ColorParameter extends VisParameterImpl {

    /**
     *
     * @param name
     */
    public ColorParameter(String name) {
        super(name, new ConstantColor());
    }

    @Override
    public void setTransferFunction(ITransferFunction f) {
        if (!(f instanceof ColorTransferFunction)) {
            throw new IllegalArgumentException("Can not set transferfunction. "
                    + "Must be of type " + ColorTransferFunction.class.getSimpleName());
        }

        super.setTransferFunction(f);
    }
}
