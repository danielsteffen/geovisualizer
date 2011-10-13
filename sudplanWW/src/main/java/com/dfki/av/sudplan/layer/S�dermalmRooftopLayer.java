/*
 *  SödermalmRooftopLayer.java 
 *
 *  Created by DFKI AV on 27.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.layer;

import gov.nasa.worldwind.layers.SurfaceImageLayer;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This layer represents the rooftop results of Södermalm. It is included
 * using the xml configuration for layers.
 * 
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class SödermalmRooftopLayer extends SurfaceImageLayer {
    /*
     * Logger.
     */

    private final Logger log = LoggerFactory.getLogger(getClass());

    public SödermalmRooftopLayer() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing södermalm Rooftop results.");
        }
        ClassLoader cl = this.getClass().getClassLoader();
        URL url = cl.getResource("rooftop3.tiff");
        this.setOpacity(0.8);
        this.setPickEnabled(false);
        this.setName("Rooftop results Södermalm");
        try {
            this.addImage(url.toString());
        } catch (IOException ex) {
            if (log.isErrorEnabled()) {
                log.error("Could not add image from url: {}", ex);
            }
        }
    }
}
