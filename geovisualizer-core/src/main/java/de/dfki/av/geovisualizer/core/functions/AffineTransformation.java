/*
 * AffineTransformation.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.functions.ui.TFPAffineTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class AffineTransformation extends NumberTransferFunction {

    /*
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AffineTransformation.class);
    /**
     * The UI for the transfer function.
     */
    private AbstractTransferFunctionPanel panel;
    /**
     * The scale factor.
     */
    private Double scaleValue;
    /**
     * The constant value to add.
     */
    private Double constantValue;

    /**
     * Constructor.
     */
    public AffineTransformation() {
        this.scaleValue = 1.0;
        this.constantValue = 0.0;
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
            result = n.doubleValue() * scaleValue + constantValue;
        } else {
            LOG.error("Data type {} not supported", o.getClass().getSimpleName());
            throw new IllegalArgumentException("Data type not supported.");
        }
        return result;
    }

    @Override
    public String getName() {
        return "Affine Map - f(x) = a * x + b";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
        LOG.debug("No pre-processing required.");
    }

    @Override
    public AbstractTransferFunctionPanel getPanel() {
        if (this.panel == null) {
            this.panel = new TFPAffineTransformation(this);
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

    /**
     * Return the constant value.
     *
     * @return the constantValue to return.
     */
    public Double getConstantValue() {
        return constantValue;
    }

    /**
     * Set the constant value.
     *
     * @param constant the constValue to set
     */
    public void setConstantValue(double constant) {
        this.constantValue = Double.valueOf(constant);
    }
}
