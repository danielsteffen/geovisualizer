/*
 * ScalarMultiplication.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.functions.ui.TFPScalarMultiplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ScalarMultiplication extends LinearTransformation {

    /*
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ScalarMultiplication.class);
    /**
     * The UI for the transfer function.
     */
    private AbstractTransferFunctionPanel panel;
    /**
     * The value for scaling.
     */
    private Double scaleValue;

    /**
     * Constructor.
     */
    public ScalarMultiplication() {
        this.scaleValue = 1.0;
    }

    @Override
    public Object calc(Object o) {
        Double result = null;
        if (o == null) {
            LOG.error("Argument set to null.");
            return result;
        }

        if (o instanceof Number) {
            Number n = (Number) o;
            result = n.doubleValue() * scaleValue;
        } else {
            LOG.error("Data type {} not supported", o.getClass().getSimpleName());
            // Means return null;
        }
        return result;
    }

    @Override
    public String getName() {
        return "Scalar Mulitplication";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
        LOG.debug("No pre-processing required.");
    }

    @Override
    public AbstractTransferFunctionPanel getPanel() {
        if (this.panel == null) {
            this.panel = new TFPScalarMultiplication(this);
        }
        return this.panel;
    }

    /**
     * Return the scale value.
     *
     * @return the scale value to return.
     */
    public Double getScaleValue() {
        return this.scaleValue;
    }

    /**
     * Set the scale value.
     *
     * @param d the scale value to set.
     */
    public void setScaleValue(double d) {
        this.scaleValue = Double.valueOf(d);
    }
}
