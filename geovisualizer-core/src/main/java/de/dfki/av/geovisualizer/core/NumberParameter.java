/*
 * NumberParameter.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import de.dfki.av.geovisualizer.core.functions.ConstantNumber;
import de.dfki.av.geovisualizer.core.functions.NumberTransferFunction;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
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
