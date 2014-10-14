/*
 * IClassificationProvider.java
 *
 * Created by DFKI AV on 21.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.spi;

import de.dfki.av.geovisualizer.core.IClassification;
import java.util.List;

/**
 * An implementation of the {@link IClassificationProvider} collects a list of
 * available {@link IClassification}.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface IClassificationProvider {

    /**
     * Returns a {@link List} of full qualified class names that implement the
     * interface {@link IClassification}.
     *
     * @return the {@link List} of names of available {@link IClassification}
     * implementations.
     */
    List<String> getClassifications();

    /**
     * Returns an instance for the full qualified {@code name} of an
     * {@link IClassification} implementation.
     *
     * @param name the name of the {@link IClassification} implementation
     * @return an instance of the {@link IClassification} implementation
     */
    IClassification get(String name);
}
