/*
 *  NumberParameter.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.core;

import com.dfki.av.sudplan.vis.functions.ConstantNumber;
import com.dfki.av.sudplan.vis.functions.NumberTransferFunction;

/**
 *
 * @author steffen
 */
public class NumberParameter extends VisParameterImpl {

    /**
     *
     * @param name
     */
    public NumberParameter(String name) {
        super(name, new ConstantNumber());
    }

    @Override
    public void setTransferFunction(ITransferFunction f) {
        if (!(f instanceof NumberTransferFunction)) {
            throw new IllegalArgumentException("Can not set transferfunction. "
                    + "Must be of type " + NumberTransferFunction.class.getSimpleName());
        }
        
        super.setTransferFunction(f);
    }
}
