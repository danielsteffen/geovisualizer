/*
 *  IVisParameter.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.vis.algorithm.functions.ITransferFunction;
import java.util.List;

/**
 *
 * @author steffen
 */
public interface IVisParameter {

    public String getName();
    public List<ITransferFunction> getTransferFunctions();
    public ITransferFunction getSelectedTransferFunction();
    public void setSelectedTransferFunction(ITransferFunction f);
    public boolean addTransferFunction(ITransferFunction f);
}
