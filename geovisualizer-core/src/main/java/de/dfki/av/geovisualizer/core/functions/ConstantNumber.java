/*
 * ConstantNumber.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.functions.ui.TFPConstantNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ConstantNumber extends NumberTransferFunction {

    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(ConstantNumber.class);
    /**
     * The UI for the transfer function.
     */
    private AbstractTransferFunctionPanel panel;
    /**
     * The constant number.
     */
    private Double constant;

    /**
     * Creates a a constant number object.
     */
    public ConstantNumber() {
        this.constant = Double.valueOf(100.0);
    }

    @Override
    public Object calc(Object o) {
        Double value = null;
        try {
            value = constant;
        } catch (NumberFormatException nfe) {
            log.error("Value {} not a number. [}", o.toString(), nfe.toString());
        }
        return value;
    }

    @Override
    public String getName() {
        return "Constant value";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
        log.debug("No pre-processing required.");
    }

    @Override
    public AbstractTransferFunctionPanel getPanel() {
        if (this.panel == null) {
            this.panel = new TFPConstantNumber(this);
        }
        return this.panel;
    }

    /**
     * Return the value of the constant.
     *
     * @return the constant to return.
     */
    public Double getConstant() {
        return this.constant;
    }

    /**
     * Set the constant value.
     *
     * @param value the value to set.
     */
    public void setConstant(double value) {
        this.constant = Double.valueOf(value);
    }
}
