/*
 *  IconParameter.java 
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
public class IconParameter implements IVisParameter{

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ITransferFunction> getTransferFunctions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITransferFunction getSelectedTransferFunction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSelectedTransferFunction(ITransferFunction f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addTransferFunction(ITransferFunction f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
