/*
 *  BuildingShapefileLoader.java 
 *
 *  Created by DFKI AV on 12.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.layer.fake;

import com.dfki.av.sudplan.layer.ShapefileLoader2;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.util.VecBuffer;
import gov.nasa.worldwind.util.WWMath;
import gov.nasa.worldwind.util.WWUtil;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class BuildingShapefileLoader extends ShapefileLoader2 {

    public static final BasicShapeAttributes buildingAttr;

    static {
        buildingAttr = new BasicShapeAttributes();
        buildingAttr.setDrawOutline(false);
        buildingAttr.setInteriorOpacity(1.0);
        buildingAttr.setInteriorMaterial(Material.DARK_GRAY);
    }

    @Override
    protected void createPolygon(ShapefileRecord record, ShapeAttributes attrs, RenderableLayer layer) {

        Object object = getValue(record, "Elevation");
        Double height = null;
        if (object instanceof Number) {
            height = ((Number) object).doubleValue();
        }

        if (object instanceof String) {
            height = WWUtil.convertStringToDouble(object.toString());
        }
        
        if (height != null) // create extruded polygons
        {
            ExtrudedPolygon ep = new ExtrudedPolygon();
            if (height < 0) {
                height = height * -1;
            } else if (height == 0) {
                height += 0.01;
            }
            ep.setHeight(height);
            ep.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            ep.setAttributes(attrs);
            layer.addRenderable(ep);

            for (int i = 0; i < record.getNumberOfParts(); i++) {
                // Although the shapefile spec says that inner and outer boundaries can be listed in any order, it's
                // assumed here that inner boundaries are at least listed adjacent to their outer boundary, either
                // before or after it. The below code accumulates inner boundaries into the extruded polygon until an
                // outer boundary comes along. If the outer boundary comes before the inner boundaries, the inner
                // boundaries are added to the polygon until another outer boundary comes along, at which point a new
                // extruded polygon is started.

                VecBuffer buffer = record.getCompoundPointBuffer().subBuffer(i);
                if (WWMath.computeWindingOrderOfLocations(buffer.getLocations()).equals(AVKey.CLOCKWISE)) {
                    if (!ep.getOuterBoundary().iterator().hasNext()) // has no outer boundary yet
                    {
                        ep.setOuterBoundary(buffer.getLocations());
                    } else {
                        ep = new ExtrudedPolygon();
                        ep.setAttributes(attrs);
                        ep.setOuterBoundary(record.getCompoundPointBuffer().getLocations());
                        layer.addRenderable(ep);
                    }
                } else {
                    ep.addInnerBoundary(buffer.getLocations());
                }
            }
        } 
    }


    @Override
    protected ShapeAttributes createPolygonAttributes(gov.nasa.worldwind.formats.shapefile.ShapefileRecord record) {
        return buildingAttr;
    }
}
