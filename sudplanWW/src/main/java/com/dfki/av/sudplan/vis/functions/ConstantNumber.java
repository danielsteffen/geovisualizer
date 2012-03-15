/*
 *  ConstantNumber.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.io.DataSource;

/**
 *
 * @author steffen
 */
public class ConstantNumber extends NumberTransferFunction {

    /**
     * 
     */
    private Double constant;

    /**
     * 
     */
    public ConstantNumber() {
        this.constant = Double.valueOf(100.0);
    }

    @Override
    public Object calc(Object o) {
        return Double.valueOf(constant);
    }

    @Override
    public String getName() {
        return "Constant transfer function";
    }

    public Double getConstant() {
        return this.constant;
    }

    public void setConstant(double value) {
        this.constant = Double.valueOf(value);
    }

    @Override
    public void preprocess(DataSource data, String attribute) {
        // Nothing to do.
    }
}
