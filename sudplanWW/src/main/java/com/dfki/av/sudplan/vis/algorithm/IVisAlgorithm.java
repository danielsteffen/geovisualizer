/*
 *  IVisAlgorithm.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import gov.nasa.worldwind.layers.Layer;
import java.util.List;
import javax.swing.Icon;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface IVisAlgorithm {

    /**
     *
     * @return
     */
    public List<IVisParameter> getVisParameters();

//    /**
//     *
//     */
//    public List<VisParameter> getVisParameter();

    /**
     *
     * @return
     */
    public Icon getIcon();

    /**
     *
     * @return
     */
    public String getName();

    /**
     *
     * @return
     */
    public String getDescription();

    /**
     *
     * @param data
     * @return
     */
    public List<Layer> createLayersFromData(Object data, Object[] attributes);
}
