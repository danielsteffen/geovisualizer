/*
 *  VisParameter.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

/**
 *
 * @author steffen
 */
public class VisParameter {

    /**
     * The description name of the visualization parameter.
     */
    private String name;
    /**
     * The transfer function for the visualization parameter.
     */
    private TransferFunction transferFunction;

    /**
     * Creates object
     * <code>VisParameter</code> with the name {@link #name}.
     *
     * @param name the name of the
     * <code>VisParameter</code>.
     * @throws IllegalArgumentException if parameter
     * <code>name</code> equals
     * <code>null</code>.
     */
    public VisParameter(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Parameter name for VisParameter equals null.");
        }
        this.name = name;
        this.transferFunction = new DefaultTransferFunction();
    }

    /**
     * Returns the {@link #name} of this visualization parameter.
     *
     * @return the {@link #name} to return.
     */
    public String getName() {
        return this.name;
    }

    /**
     * 
     * @param c 
     */
    public void setTransferFunction(TransferFunction c) {
        if (c == null) {
            throw new IllegalArgumentException("Can't set classification to null.");
        }
        this.transferFunction = c;
    }

    /**
     *
     * @return
     */
    public TransferFunction getTransferFunction() {
        return this.transferFunction;
    }

}
