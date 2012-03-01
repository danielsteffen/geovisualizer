/*
 *  RedGreenColorrampTransferFunction.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm.functions;

import com.dfki.av.sudplan.io.DataInput;
import com.dfki.av.utils.ColorUtils;
import java.awt.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class RedGreenColorrampTransferFunction extends ColorTransferFunction {

    /*
     *
     */
    private static final Logger log = LoggerFactory.getLogger(RedGreenColorrampTransferFunction.class);
    /**
     *
     */
    private double min;
    /**
     *
     */
    private double max;
    /**
     *
     */
    private int numCategories;
    /**
     *
     */
    private Color[] colorramp;

    /**
     *
     */
    public RedGreenColorrampTransferFunction() {
        this.min = Double.MIN_VALUE;
        this.max = Double.MAX_VALUE;
        this.numCategories = 5;
        this.colorramp = ColorUtils.CreateRedGreenColorGradientAttributes(numCategories);
    }

    @Override
    public Object calc(Object o) {
        if (o instanceof Number) {
            double arg = ((Number) o).doubleValue();
            double categorieSize = (this.max - this.min) / (double) this.numCategories;
            for (int i = 0; i < numCategories; i++) {
                if (arg <= min + (i + 1) * categorieSize) {
                    return colorramp[i];
                }
            }
            log.error("Should not reach this part.");
        } else {
            log.error("Data type {} not supported", o.getClass().getSimpleName());
            return Color.GRAY;
        }
        return Color.GRAY;

    }

    @Override
    public String getName() {
        return "Red-Green Color ramp (uniformly distributed; 5 classes)";
    }

    @Override
    public void preprocess(DataInput data, String attribute) {
        log.debug("Pre-processing data.");
        this.min = data.min(attribute);
        log.debug("Minimum for attribute {} is {}.", attribute, min);
        this.max = data.max(attribute);
        log.debug("Maximum for attribute {} is {}.", attribute, max);
        log.debug("Pre-processing finished.");
    }
}
