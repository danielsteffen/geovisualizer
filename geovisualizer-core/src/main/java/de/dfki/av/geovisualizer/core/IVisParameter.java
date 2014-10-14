/*
 * IVisParameter.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface IVisParameter {

    /**
     * Returns the name of the {@link IVisParameter}.
     *
     * @return the name of the {@link IVisParameter} to return;
     */
    String getName();

    /**
     * Returns a list of names of available {@link ITransferFunction}s. The name
     * will be the same as if calling {@code getName()} of the class
     * implementing {@link ITransferFunction}.
     *
     * @return a list of {@link String} of available {@link ITransferFunction}s
     * for this {@link IVisParameter}.
     */
    List<String> getAvailableTransferFunctions();

    /**
     * Returns the {@link ITransferFunction} set for this parameter.
     *
     * @return the {@link ITransferFunction} to return.
     */
    ITransferFunction getTransferFunction();

    /**
     * Set the {@link ITransferFunction} for this parameter.
     *
     * @param function the {@link ITransferFunction} to set.
     * @throws IllegalArgumentException if {@code function == null}.
     */
    void setTransferFunction(ITransferFunction function);

    /**
     * Adds a name of an available {@link ITransferFunction} for this parameter.
     * The name has to be the name of the class implementing the
     * {@link ITransferFunction}.
     *
     * @param name the name of the {@link ITransferFunction}
     * @return true (as specified by {@link Collection#add(java.lang.Object)})
     */
    boolean addTransferFunction(String name);
}
