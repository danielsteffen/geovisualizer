/*
 *  StringCategory.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm.functions.categorization;

/**
 *
 * @author steffen
 */
public class StringCategory implements ICategory {

    /**
     * The string defining this category.
     */
    private String string;

    /**
     * Constructor.
     *
     * @param s the string defining this category.
     */
    public StringCategory(String s) {
        this.string = s;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof String) {
            String s = (String) o;
            return s.equalsIgnoreCase(string);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StringCategory) {
            StringCategory sc = (StringCategory) o;
            return sc.string.equalsIgnoreCase(string);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.string != null ? this.string.hashCode() : 0);
        return hash;
    }
}
