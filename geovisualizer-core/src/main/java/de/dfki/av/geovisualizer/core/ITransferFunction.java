/*
 * ITransferFunction.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface ITransferFunction {

    /**
     * Calculate the value for input object {@code object}.
     *
     * @param object the input value.
     * @return the calculated value.
     */
    Object calc(Object object);

    /**
     * Return the name for the transfer function.
     *
     * @return the name to return.
     */
    String getName();

    /**
     * Preprocess the data if required.
     *
     * @param data the input data for the visualization.
     * @param attribute the attributes to be visualized from the data.
     */
    void preprocess(ISource data, String attribute);

    /**
     * Return the user interface panel for this transfer function.
     *
     * @return the UI panel to return. null if no UI element is available.
     */
    AbstractTransferFunctionPanel getPanel();
}
