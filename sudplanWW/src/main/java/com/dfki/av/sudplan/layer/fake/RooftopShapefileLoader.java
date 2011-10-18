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

    private static final BasicShapeAttributes bsay;
    private static final BasicShapeAttributes bsag;
    private static final BasicShapeAttributes bsar;

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

    public RooftopShapefileLoader(String attr) {
        super(attr);
    }
    
    @Override
    protected void createPolygon(ShapefileRecord record, ShapeAttributes attrs, RenderableLayer layer) {

        Object object = getValue(record, attribute);
        Double value = null;
        if (object instanceof Number) {
            value = ((Number) object).doubleValue();
        }

        if (object instanceof String) {
            value = WWUtil.convertStringToDouble(object.toString());
        }

        if (value != null) // create extruded polygons
        {
            SurfacePolygons shape = new SurfacePolygons(
                    Sector.fromDegrees(((ShapefileRecordPolygon) record).getBoundingRectangle()),
                    record.getCompoundPointBuffer());
            // This is the hard coded transfer function for 
            // the classification of the results.
            if (value.doubleValue() < 40) {
                shape.setAttributes(bsag);
            } else if (value.doubleValue() < 50) {
                shape.setAttributes(bsay);
            } else {
                shape.setAttributes(bsar);                
            }

            layer.addRenderable(shape);
        } 
    }
}
