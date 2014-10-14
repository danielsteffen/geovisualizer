/*
 * StringCategory.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.ICategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class StringCategory implements ICategory {

    /*
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(StringCategory.class);
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
        if (s == null) {
            String msg = "s == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.string = s;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            String msg = "o == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        
        if (o instanceof String) {
            String s = (String) o;
            return s.equalsIgnoreCase(string);
        }
        
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            String msg = "o == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        
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

    @Override
    public String toString() {
        return this.string;
    }
}
