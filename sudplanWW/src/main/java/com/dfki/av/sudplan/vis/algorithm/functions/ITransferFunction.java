/*
 *  ITransferFunction.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm.functions;

import com.dfki.av.sudplan.io.DataSource;

/**
 *
 * @author steffen
 */
public interface ITransferFunction {

    public Object calc(Object o);

    public String getName();

    public void preprocess(DataSource data, String attribute);

}
