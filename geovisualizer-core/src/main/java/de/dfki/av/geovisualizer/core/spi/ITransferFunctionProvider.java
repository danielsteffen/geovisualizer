/*
 * ITransferFunctionProvider.java
 *
 * Created by DFKI AV on 21.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.spi;

import de.dfki.av.geovisualizer.core.ITransferFunction;
import java.util.List;

/**
 * An implementation of the {@link ITransferFunctionProvider} collects a list of
 * available {@link ITransferFunction}.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface ITransferFunctionProvider {

    /**
     * Returns a {@link List} of full qualified class names that implement the
     * interface {@link ITransferFunction}.
     *
     * @return the {@link List} of names of available {@link ITransferFunction}
     * implementations.
     */
    List<String> getTransferFunctions();

    /**
     * Returns an instance for the full qualified {@code name} of an
     * {@link ITransferFunction} implementation.
     *
     * @param name the name of the {@link ITransferFunction} implementation
     * @return an instance of the {@link ITransferFunction} implementation
     */
    ITransferFunction get(String name);
}
