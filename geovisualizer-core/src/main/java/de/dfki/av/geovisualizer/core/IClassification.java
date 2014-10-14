/*
 * IClassification.java
 *
 * Created by DFKI AV on 20.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import java.util.List;

/**
 * Interface to classify an {@link ISource} using an {@code attribute} into
 * {@code numClasses} classes of type {@link IClass}.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface IClassification {

    /**
     * Classification of the {@link ISource} data. Based on the
     * {@code attribute} the algorithm returns {@code numClasses} classes.
     *
     * @param data the {@link ISource} to classify.
     * @param attribute the {@code attribute} to classify.
     * @param numClasses the number of resulting classes.
     * @return a {@link List} of {@link IClass} elements.
     */
    List<IClass> classify(ISource data, String attribute, int numClasses);

    /**
     * Return the name of the classification algorithm.
     *
     * @return the name to return.
     */
    String getName();
}
