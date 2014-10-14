/*
 * ColorParameter.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import de.dfki.av.geovisualizer.core.functions.ColorTransferFunction;
import de.dfki.av.geovisualizer.core.functions.ConstantColor;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
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
