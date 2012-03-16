/*
 *  ITransferFunction.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.core;

/**
 *
 * @author steffen
 */
public interface ITransferFunction {

    /**
     * 
     * @param o
     * @return 
     */
    public Object calc(Object o);

    /**
     * 
     * @return 
     */
    public String getName();

    /**
     * 
     * @param data
     * @param attribute 
     */
    public void preprocess(ISource data, String attribute);

}
