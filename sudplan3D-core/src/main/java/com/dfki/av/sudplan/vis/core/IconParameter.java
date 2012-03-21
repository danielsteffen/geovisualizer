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
    public List<String> getAvailableTransferFunctions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addTransferFunction(String f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITransferFunction getTransferFunction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTransferFunction(ITransferFunction f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
