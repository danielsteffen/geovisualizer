/*
 * IconParameter.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class IconParameter implements IVisParameter {

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
