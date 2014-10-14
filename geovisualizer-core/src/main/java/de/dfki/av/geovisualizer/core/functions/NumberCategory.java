/*
 * NumberCategory.java
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.ICategory;
import java.text.DecimalFormat;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class NumberCategory implements ICategory {

    /**
     * The {@link Number} representing this category.
     */
    private Number number;

    /**
     * Constructor for number category.
     */
    public NumberCategory(Number n) {
        if (n == null) {
            throw new IllegalArgumentException("Parameter null.");
        }
        this.number = n;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Number) {
            Number n = (Number) o;
            double diff = n.doubleValue() - number.doubleValue();
            return Math.abs(diff) < Double.MIN_VALUE;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NumberCategory) {
            NumberCategory n = (NumberCategory) o;
            double diff = n.number.doubleValue() - number.doubleValue();
            return  Math.abs(diff) < Double.MIN_VALUE;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.number != null ? this.number.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.0000");
        return df.format(this.number);
    }
}
