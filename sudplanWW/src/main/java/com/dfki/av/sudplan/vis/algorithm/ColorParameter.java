/*
 *  ColorParameter.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.vis.ITransferFunction;
import com.dfki.av.sudplan.vis.IVisParameter;
import com.dfki.av.sudplan.vis.functions.ColorTransferFunction;
import com.dfki.av.sudplan.vis.functions.ConstantColor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author steffen
 */
public class ColorParameter implements IVisParameter {

    /**
     * The name to display for this
     * <code>ColorParameter</code>.
     */
    private String name;
    /**
     * The list of transfer functions the
     * <code>ColorParameter</code> supports.
     */
    private List<ITransferFunction> transferFunctions;
    /**
     *
     */
    private ITransferFunction transferFunction;

    /**
     *
     * @param name
     */
    public ColorParameter(String name) {
        this.name = name;
        this.transferFunctions = new ArrayList<ITransferFunction>();
        // Setting default transfer function.
        this.transferFunction = new ConstantColor();
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
        return transferFunction;
    }

    @Override
    public void setSelectedTransferFunction(ITransferFunction f) {
        this.transferFunction = f;
    }

    @Override
    public boolean addTransferFunction(ITransferFunction f) {
        if(!(f instanceof ColorTransferFunction))                {
            throw new IllegalArgumentException("Transferfunction must be of typ "
                    + ColorTransferFunction.class.getSimpleName());
        } 
        if(f == null){
            throw new IllegalArgumentException("Transferfunction is null");
        }
        return this.transferFunctions.add(f);
    }
}
