/*
 *  IVisAlgorithmProvider.java 
 *
 *  Created by DFKI AV on 21.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.spi;

import com.dfki.av.sudplan.vis.core.IClassification;
import java.util.List;

/**
 *
 * @author steffen
 */
public interface IClassificationProvider {

    /**
     *
     * @return
     */
    public List<String> getClassifications();

    /**
     *
     * @param name
     * @return
     */
    public IClassification get(String name);
}
