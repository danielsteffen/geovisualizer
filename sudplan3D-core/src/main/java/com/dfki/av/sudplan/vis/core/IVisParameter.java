/*
 *  IVisParameter.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.core;

import java.util.List;

/**
 *
 * @author steffen
 */
public interface IVisParameter {

    /**
     * 
     * @return 
     */
    public String getName();
    /**
     * 
     * @return 
     */
    public List<ITransferFunction> getTransferFunctions();
    /**
     * 
     * @return 
     */
    public ITransferFunction getSelectedTransferFunction();
    /**
     * 
     * @param f 
     */
    public void setSelectedTransferFunction(ITransferFunction f);
    /**
     * 
     * @param f
     * @return 
     */
    public boolean addTransferFunction(ITransferFunction f);
}
