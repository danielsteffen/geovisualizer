/*
 *  IClassification.java 
 *
 *  Created by DFKI AV on 20.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.core;

import java.util.List;

/**
 *
 * @author steffen
 */
public interface IClassification {

    /**
     * 
     * @param data
     * @param attribute
     * @param numClasses
     * @return 
     */
    public List<IClass> classify(ISource data, String attribute, int numClasses);
    
    /**
     * 
     * @return 
     */
    public String getName();
}
