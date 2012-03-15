/*
 *  VisCreateTexture.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.basic;

import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
        
        log.debug("Running {}", this.getClass().getSimpleName());        
        
        ArrayList<Layer> list = new ArrayList<Layer>();
        if (attributes == null || attributes.length == 0) {
            log.warn("No attributes specified.");
        }
        
        if (data instanceof File) {
            File file = (File) data;
            SurfaceImageLayer sul = new SurfaceImageLayer();
            sul.setOpacity(0.8);
            sul.setPickEnabled(false);
            sul.setName(file.getName());
            try {
                sul.addImage(file.getAbsolutePath());
            } catch (IOException ex) {
                log.error("Could not add image {}", ex.toString());
                sul = null;
            }
            list.add(sul);
        } else {
            log.error("Data type not supported.");
        }
        
        log.debug("Finished {}", this.getClass().getSimpleName());
        
        return list;
    }
}
