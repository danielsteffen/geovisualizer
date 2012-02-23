/*
 *  TransferFunction.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

/**
 *
 * @author steffen
 */
public interface TransferFunction {
    
    public Object execute(Object value);
    
    public String getName();
}
