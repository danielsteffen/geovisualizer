/*
 *  IVisAlgorithmProvider.java 
 *
 *  Created by DFKI AV on 26.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.spi;

import com.dfki.av.sudplan.vis.core.IVisAlgorithm;
import java.util.List;

/**
 *
 * @author steffen
 */
public interface IVisAlgorithmProvider {
    /**
     * 
     * @return 
     */
    public List<String> getVisualizationNames();
    /**
     * 
     * @param name
     * @return 
     */
    public IVisAlgorithm get(String name);
}
