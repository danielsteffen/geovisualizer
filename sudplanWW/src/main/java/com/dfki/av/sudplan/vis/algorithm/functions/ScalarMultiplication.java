/*
 *  ScalarMultiplication.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm.functions;

import com.dfki.av.sudplan.io.DataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class ScalarMultiplication extends LinearTransformation {

    /*
     *
     */
    private static final Logger log = LoggerFactory.getLogger(ScalarMultiplication.class);
    /**
     *
     */
    private Double scaleValue;

    /**
     *
     */
    public ScalarMultiplication() {
        this.scaleValue = 1.0;
    }

    @Override
    public Object calc(Object o) {
        if (o == null) {
            log.error("Argument set to null.");
            return new Double(0.0);
        }
        
        Double result = null;
        if (o instanceof Number) {
            Number n = (Number) o;
            result = n.doubleValue() * scaleValue;
        } else {
            log.error("Data type {} not supported", o.getClass().getSimpleName());
            throw new IllegalArgumentException("Data type not supported.");
        }
        return result;
    }

    @Override
    public String getName() {
        return "Scalar Mulitplication";
    }

    /**
     *
     * @return
     */
    public Double getScaleValue() {
        return this.scaleValue;
    }

    /**
     *
     * @param d
     */
    public void setScaleValue(double d) {
        this.scaleValue = Double.valueOf(d);
    }

    @Override
    public void preprocess(DataInput data, String attribute) {
        log.debug("No pre-processing required.");
    }
}
