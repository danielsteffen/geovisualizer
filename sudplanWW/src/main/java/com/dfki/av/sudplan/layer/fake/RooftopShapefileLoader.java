/*
 *  RooftopShapefileLoader.java 
 *
 *  Created by DFKI AV on 12.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.layer.fake;

import com.dfki.av.sudplan.layer.ShapefileLoader2;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecordPolygon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygons;
import gov.nasa.worldwind.util.WWUtil;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class RooftopShapefileLoader extends ShapefileLoader2 {

    public static final BasicShapeAttributes bsay;
    public static final BasicShapeAttributes bsag;
    public static final BasicShapeAttributes bsar;

    static {
        bsay = new BasicShapeAttributes();
        bsay.setDrawOutline(false);
        bsay.setInteriorOpacity(0.5);
        bsay.setInteriorMaterial(Material.YELLOW);

        bsag = new BasicShapeAttributes();
        bsag.setDrawOutline(false);
        bsag.setInteriorOpacity(0.5);
        bsag.setInteriorMaterial(Material.GREEN);

        bsar = new BasicShapeAttributes();
        bsar.setDrawOutline(false);
        bsar.setInteriorOpacity(0.5);
        bsar.setInteriorMaterial(Material.RED);
    }

    @Override
    protected void createPolygon(ShapefileRecord record, ShapeAttributes attrs, RenderableLayer layer) {

        Object object = getValue(record, "NO2dygn");
        Double no2dygn = null;
        if (object instanceof Number) {
            no2dygn = ((Number) object).doubleValue();
        }

        if (object instanceof String) {
            no2dygn = WWUtil.convertStringToDouble(object.toString());
        }

        if (no2dygn != null) // create extruded polygons
        {
            SurfacePolygons shape = new SurfacePolygons(
                    Sector.fromDegrees(((ShapefileRecordPolygon) record).getBoundingRectangle()),
                    record.getCompoundPointBuffer());
            // This is the hard coded transfer function for 
            // the classification of the results.
            if (no2dygn.doubleValue() < 40) {
                shape.setAttributes(bsag);
            } else if (no2dygn.doubleValue() < 50) {
                shape.setAttributes(bsay);
            } else {
                shape.setAttributes(bsar);                
            }

            layer.addRenderable(shape);
        } 
    }
}
