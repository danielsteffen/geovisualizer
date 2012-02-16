/*
 *  VisCreateTexture.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import gov.nasa.worldwind.layers.Layer;
import java.util.List;

/**
 *
 * @author steffen
 */
public class VisCreateTexture extends VisAlgorithmAbstract {

    /**
     * 
     */
    public VisCreateTexture() {
        super("CreateTexture", "Creates a texture visualization");
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
