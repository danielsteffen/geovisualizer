/*
 *  ICategory.java 
 *
 *  Created by DFKI AV on 09.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.core;

/**
 * An interface for categories. Categories are basically sets of 1 to n objects.
 * No relationship is defined for categories. Objects of a categorie have the
 * same features.
 * 
 * @author steffen
 */
public interface ICategory {

    /**
     * Checks whether the category contains the element.
     * @param o the object to look for.
     * @return <code>true</code> if the category contains the object. 
     * Otherwise <code>false</code>.
     */
    public boolean contains(Object o);
}
