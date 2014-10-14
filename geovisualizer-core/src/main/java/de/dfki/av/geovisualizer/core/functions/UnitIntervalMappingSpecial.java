/*
 * UnitIntervalMappingSpecial.java
 *
 * Created by DFKI AV on 09.11.2012.
 * Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.ISource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class UnitIntervalMappingSpecial extends AffineTransformation {

    /*
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(de.dfki.av.geovisualizer.core.functions.UnitIntervalMappingSpecial.class);
    /**
     * The minimum value for the chosen attribute in the data set.
     */
    private double min;
    /**
     * The maximum value for the chosen attribute in the data set.
     */
    private double max;

    /**
     * Constructor
     */
    public UnitIntervalMappingSpecial() {
        super();
        this.min = Double.MIN_VALUE;
        this.max = Double.MAX_VALUE;
    }

    @Override
    public Object calc(Object o) {
        //Mapping to unit interval
        Double result = null;
        if (o == null) {
            LOG.error("Argument set to null.");
            return result;
        }

        if (o instanceof Number) {
            Number n = (Number) o;
            result = (n.doubleValue() - min) / Math.abs(max - min);
        } else {
            LOG.error("Data type {} not supported", o.getClass().getSimpleName());
            throw new IllegalArgumentException("Data type not supported.");
        }
        // Perhaps scaling...
        return super.calc(result);
    }

    @Override
    public String getName() {
        return "Unit Interval Mapping - f(x) = a * (x - min) / |max-min| + b";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
        LOG.debug("Preprocessing ...");
        min = data.min(attribute);
        max = data.max(attribute);
        LOG.debug("Minimum for attribute {}: {}", attribute, min);
        LOG.debug("Maximum for attribute {}: {}", attribute, max);
        LOG.debug("Preprocessing finished");
    }
}
