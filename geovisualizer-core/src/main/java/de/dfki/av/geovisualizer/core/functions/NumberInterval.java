/*
 * NumberInterval.java
 *
 * Created by DFKI AV on 09.03.2012.
 * Copyright (c) 2011-2014 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.IClass;
import java.text.DecimalFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class NumberInterval implements IClass {

    /*
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NumberInterval.class);
    /**
     * Format in the standard notation.
     */
    private static final DecimalFormat DF_0 = new DecimalFormat("#.0000");
    /**
     * Format in the engineering notation, in which the exponent is a multiple
     * of three.
     */
    private static final DecimalFormat DF_1 = new DecimalFormat("##0.#####E0");
    /**
     * The lower bound of the interval.
     */
    private double minValue;
    /**
     * The upper bound of the interval.
     */
    private double maxValue;

    /**
     * Constructor. Setting upper and lower border of interval to
     * {@link Double#MIN_VALUE} and {@link Double#MAX_VALUE} respectively.
     */
    public NumberInterval() {
        this(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Creates a interval. Sets the lower and upper border of the interval to
     * {@code min} and {@code max}.
     *
     * @param min the lower border of the interval to set.
     * @param max the upper border of the interval to set.
     * @throws IllegalArgumentException if {@code min == null} or
     * {@code max == null}.
     */
    public NumberInterval(Number min, Number max) {
        if (min == null) {
            String msg = "min == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (max == null) {
            String msg = "max == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.minValue = min.doubleValue();
        this.maxValue = max.doubleValue();
    }

    @Override
    public boolean contains(Number n) {
        if (n == null) {
            String msg = "n == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double value = n.doubleValue();
        LOG.debug("Check if '{}' contains '{}'.", this.toString(), value);
        if (Math.abs(getMinValue() - value) < Double.MIN_VALUE) {
            // Min value equals value.
            // Thus it must be contained in this NumberInterval.
            return true;
        }

        if (Math.abs(getMaxValue() - value) < Double.MIN_VALUE) {
            // Max value equals value.
            // Thus it must be contained in this NumberInterval.
            return true;
        }

        if (getMinValue() < value && value < getMaxValue()) {
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

    @Override
    public String toString() {

        String sMinValue = minValue > 100000
                ? DF_1.format(minValue) : DF_0.format(minValue);
        String sMaxValue = maxValue > 100000
                ? DF_1.format(maxValue) : DF_0.format(maxValue);

        String s = sMinValue.concat(" - ").concat(sMaxValue);
        return s;
    }
}
