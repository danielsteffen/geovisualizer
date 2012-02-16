/*
 *  SödermalmRooftopLayerWithElevation.java 
 *
 *  Created by DFKI AV on 11.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplanX.test;

//import com.dfki.av.sudplan.layer.TexturedLayer;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
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
public class SödermalmRooftopLayerWithElevation extends RenderableLayer {
    /*
     * Logger.
     */

    private final Logger log = LoggerFactory.getLogger(getClass());

    public SödermalmRooftopLayerWithElevation() {

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
//        SurfaceImage si = new SurfaceImage(roofTopResultImage, imageSector);
//        si.setOpacity(0.6);
//        this.addRenderable(si);

//        List positions = Arrays.asList(
//                Position.fromDegrees(59.2940608, 17.9849627, 200), // lower left
//                Position.fromDegrees(59.2940608, 18.119758, 200), // lower right
//                Position.fromDegrees(59.3589690, 18.119758, 200), // upper right
//                Position.fromDegrees(59.3589690, 17.9849627, 200)); // upper left
//        Polygon p = new Polygon(positions);
//        BasicShapeAttributes attrs = new BasicShapeAttributes();
//        attrs.setInteriorMaterial(Material.GRAY);
//        attrs.setOutlineMaterial(Material.WHITE);
//        attrs.setInteriorOpacity(0.8);
//        attrs.setOutlineOpacity(0.6);
//        attrs.setOutlineWidth(2);
//        attrs.setImageScale(0.5);
//        p.setAttributes(attrs);
//        p.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
//        float[] texCoords = new float[]{
//            0.0f, 0.035f,
//            1.0f - 0.032f, 0.0f,
//            1.0f, 1.0f - 0.035f,
//            0.031f, 1.0f
//        };
//        p.setTextureImageSource(roofTopResultImage, texCoords, 4);
//        this.addRenderable(p);
//        this.setName("Roof Top Results (Södermalm)");
//        this.setPickEnabled(false);
//        this.setEnabled(false);

//        TexturedLayer tl = new TexturedLayer(roofTopResultImage, imageSector);
//        tl.setOpacity(0.6);
//        tl.setElevation(150.0);
//        this.addRenderable(tl);
    }
}
