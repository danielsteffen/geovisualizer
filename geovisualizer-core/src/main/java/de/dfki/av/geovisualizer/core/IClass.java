/*
 * IClass.java
 *
 * Created by DFKI AV on 09.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface IClass {

    /**
     * Returns {@code true} if the object contains the {@link Number}.
     *
     * @param number the {@link Number} to check.
     * @return {@code true} if it contains the {@link Number}. Otherwise
     * {@code false}.
     */
    boolean contains(Number number);
}
