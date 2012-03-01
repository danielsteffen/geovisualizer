/*
 *  ColorClassification.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm.functions;

import com.dfki.av.sudplan.io.DataInput;

/**
 *
 * @author steffen
 */
public class ColorClassification extends ColorTransferFunction{

    @Override
    public Object calc(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return "Color Classification";
    }

    @Override
    public void preprocess(DataInput data, String attribute) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
