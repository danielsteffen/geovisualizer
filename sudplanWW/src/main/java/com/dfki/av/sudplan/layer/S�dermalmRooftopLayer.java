/*
 *  SödermalmRooftopLayer.java 
 *
 *  Created by DFKI AV on 27.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.layer;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceImage;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This layer represents the rooftop results of Södermalm. It is included
 * using the xml configuration for layers.
 * 
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class SödermalmRooftopLayer extends RenderableLayer{
    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    public SödermalmRooftopLayer(){
        
        if (log.isDebugEnabled()) {
            log.debug("Initializing södermalm Rooftop results.");
        }
        // TODO <steffen>: Get sector corners from converted image file.
        String roofTopResultImage = "rooftop3.tiff";
        List corners = Arrays.asList(
                LatLon.fromDegrees(59.2940608, 17.9849627), // lower left
                LatLon.fromDegrees(59.2940608, 18.119758), // lower right
                LatLon.fromDegrees(59.3589690, 18.119758), // upper right
                LatLon.fromDegrees(59.3589690, 17.9849627)); // upper left
        Sector imageSector = Sector.boundingSector(corners);
        
        SurfaceImage si = new SurfaceImage(roofTopResultImage, imageSector);
        si.setOpacity(0.6);

        this.setName("Roof Top Results (Södermalm)");
        this.setPickEnabled(false);
        this.setEnabled(false);
        this.addRenderable(si);
    }
 }
