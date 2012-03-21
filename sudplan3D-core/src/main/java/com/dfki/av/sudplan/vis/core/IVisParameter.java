/*
 *  IVisParameter.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.core;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author steffen
 */
public interface IVisParameter {

    /**
     * Returns the name of the {@link IVisParameter}.
     *
     * @return the name of the {@link IVisParameter} to return;
     */
    public String getName();

    /**
     * Returns a list of names of available {@link ITransferFunction}s. The name
     * will be the same as if calling {@code getName()} of the class
     * implementing {@link ITransferFunction}.
     *
     * @return a list of {@link String} of available {@link ITransferFunction}s
     * for this {@link IVisParameter}.
     */
    public List<String> getAvailableTransferFunctions();

    /**
     * Returns the {@link ITransferFunction} set for this parameter.
     *
     * @return the {@link ITransferFunction} to return.
     */
    public ITransferFunction getTransferFunction();

    /**
     * Set the {@link ITransferFunction} for this parameter.
     *
     * @param f the {@link ITransferFunction} to set.
     * @throws IllegalArgumentException if {@code f == null}.
     */
    public void setTransferFunction(ITransferFunction f);

    /**
     * Adds a name of an available {@link ITransferFunction} for this parameter.
     * The name has to be the name of the class implementing the {@link ITransferFunction}.
     * 
     * @param name the name of the {@link ITransferFunction}
     * @return true (as specified by {@link Collection#add(java.lang.Object)}) 
     */
    public boolean addTransferFunction(String name);
}
