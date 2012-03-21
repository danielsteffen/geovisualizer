/*
 *  IVisAlgorithmProvider.java 
 *
 *  Created by DFKI AV on 21.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.spi;

import com.dfki.av.sudplan.vis.core.ITransferFunction;
import java.util.List;

/**
 *
 * @author steffen
 */
public interface ITransferFunctionProvider {

    /**
     *
     * @return
     */
    public List<String> getTransferFunctions();

    /**
     *
     * @param name
     * @return
     */
    public ITransferFunction get(String name);
}
