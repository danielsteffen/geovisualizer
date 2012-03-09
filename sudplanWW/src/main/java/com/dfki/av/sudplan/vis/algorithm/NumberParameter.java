/*
 *  NumberParameter.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.vis.algorithm.functions.ConstantNumberTansferFunction;
import com.dfki.av.sudplan.vis.algorithm.functions.ITransferFunction;
import com.dfki.av.sudplan.vis.algorithm.functions.NumberTransferFunction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author steffen
 */
public class NumberParameter implements IVisParameter {

    /**
     *
     */
    private List<ITransferFunction> transferFunctions;
    /**
     *
     */
    private String name;
    /**
     *
     */
    private ITransferFunction transferFunction;

    /**
     *
     * @param name
     */
    public NumberParameter(String name) {
        this.name = name;
        this.transferFunctions = new ArrayList<ITransferFunction>();
        // Setting default transfer function
        this.transferFunction = new ConstantNumberTansferFunction();
    }

    @Override
    public List<ITransferFunction> getTransferFunctions() {
        return this.transferFunctions;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ITransferFunction getSelectedTransferFunction() {
        return this.transferFunction;
    }

    @Override
    public void setSelectedTransferFunction(ITransferFunction f) {
        this.transferFunction = f;
    }

    @Override
    public boolean addTransferFunction(final ITransferFunction f) {
        if(!(f instanceof NumberTransferFunction))                {
            throw new IllegalArgumentException("Transferfunction must be of typ "
                    + NumberTransferFunction.class.getSimpleName());
        } 
        if(f == null){
            throw new IllegalArgumentException("Transferfunction is null");
        }
        return this.transferFunctions.add(f);
    }
}
