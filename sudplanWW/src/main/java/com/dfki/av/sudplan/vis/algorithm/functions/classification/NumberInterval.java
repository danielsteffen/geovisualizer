/*
 *  NumberInterval.java 
 *
 *  Created by DFKI AV on 09.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm.functions.classification;

/**
 *
 * @author steffen
 */
public class NumberInterval implements IClass {

    /**
     *
     */
    private double minValue;
    /**
     *
     */
    private double maxValue;

    /**
     *
     */
    public NumberInterval() {
        this(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     *
     * @param min
     * @param max
     */
    public NumberInterval(Number min, Number max) {
        this.minValue = min.doubleValue();
        this.maxValue = max.doubleValue();
    }

    @Override
    public boolean contains(Number n) {
        if (n == null) {
            throw new IllegalArgumentException();
        }
        double value = ((Number) n).doubleValue();

        if (getMinValue() <= value && value <= getMaxValue()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the minValue
     */
    public double getMinValue() {
        return minValue;
    }

    /**
     * @param minValue the minValue to set
     */
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    /**
     * @return the maxValue
     */
    public double getMaxValue() {
        return maxValue;
    }

    /**
     * @param maxValue the maxValue to set
     */
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }
}
