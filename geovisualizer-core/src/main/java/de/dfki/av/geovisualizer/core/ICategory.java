/*
 * ICategory.java
 *
 * Created by DFKI AV on 09.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

/**
 * An interface for categories. Categories are basically sets of 1 to n objects.
 * No relationship is defined for categories. Objects of a category have the
 * same features.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface ICategory {

    /**
     * Checks whether the category contains the element.
     *
     * @param object the object to look for.
     * @return <code>true</code> if the category contains the object.
     * Otherwise <code>false</code>.
     */
    boolean contains(Object object);
}
