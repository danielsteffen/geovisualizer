/*
 * IVisAlgorithmProvider.java
 *
 * Created by DFKI AV on 26.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.spi;

import de.dfki.av.geovisualizer.core.IVisAlgorithm;
import java.util.List;

/**
 * The interface for the SPI provider handling the {@link IVisAlgorithm}.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface IVisAlgorithmProvider {

    /**
     * Returns the {@link List} of the visualizations. The name is the name of
     * the class ({@code getClass.getName()} implementing the
     * {@link IVisAlgorithm}, {@code null} or an empty list.
     *
     * @return the {@link List} of {@link String} names
     */
    List<String> getVisualizationNames();

    /**
     * Returns the {@link IVisAlgorithm} with class name {@code name}.
     *
     * @param name the name of the {@link IVisAlgorithm}
     * @return the {@link IVisAlgorithm} to return or null
     */
    IVisAlgorithm get(String name);
}
