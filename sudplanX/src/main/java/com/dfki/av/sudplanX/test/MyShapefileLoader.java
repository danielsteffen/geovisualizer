/*
 *  Created by DFKI AV on 13/09/2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
 */
package com.dfki.av.sudplanX.test;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.formats.shapefile.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwindx.examples.util.ShapefileLoader;
import java.util.*;

/**
 * Converts Shapefile geometry into World Wind renderable objects. Shapefile geometries are mapped to World Wind objects
 * as follows: <table> <tr><th>Shapefile Geometry</th><th>World Wind Object</th></tr> <tr><td>Point</td><td>{@link
 * gov.nasa.worldwind.render.WWIcon}</td></tr> <tr><td>MultiPoint</td><td>List of {@link
 * gov.nasa.worldwind.render.WWIcon}</td></tr> <tr><td>Polyline</td><td>{@link gov.nasa.worldwind.render.SurfacePolylines}</td></tr>
 * <tr><td>Polygon</td><td>{@link gov.nasa.worldwind.render.SurfacePolygons}</td></tr> </table>
 * <p/>
 * Shapefiles do not contain a standard definition for color and other visual attributes. Though some Shapefiles contain
 * color information in each record's key-value attributes, ShapefileLoader does not attempt to interpret that
 * information. Instead, the World Wind renderable objects created by ShapefileLoader are assigned a random color.
 * Callers can replace or extend this behavior by defining a subclass of ShapefileLoader and overriding the following
 * methods: <ul> <li>{@link #createPointIconSource(gov.nasa.worldwind.formats.shapefile.ShapefileRecord)}</li>
 * <li>{@link #createPolylineAttributes(gov.nasa.worldwind.formats.shapefile.ShapefileRecord)}</li> <li>{@link
 * #createPolygonAttributes(gov.nasa.worldwind.formats.shapefile.ShapefileRecord)}</li></ul>.
 *
 * @author dcollins
 * @version $Id: ShapefileLoader.java 1 2011-07-16 23:22:47Z dcollins $
 */
/**
 *
 * @author steffen
 */
public class MyShapefileLoader extends ShapefileLoader{

    public static BasicShapeAttributes bsay;
    public static BasicShapeAttributes bsag;
    public static BasicShapeAttributes bsar;

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


    //**************************************************************//
    //********************  Primitive Geometry Construction  *******//
    //**************************************************************//
    protected void createPolygon(ShapefileRecord record, ShapeAttributes attrs, RenderableLayer layer) {

        // In order to visualize and classify the rooftop results.
        Double no2dygn = this.getNO2dygn(record);
        if (no2dygn != null) {
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

//            shape.setWindingRule(AVKey.CLOCKWISE);
//            shape.setPolygonRingGroups(new int[]{0});
//            shape.setPolygonRingGroups(new int[]{0});
            layer.addRenderable(shape);
        } else {
            System.out.println("Value: no value.");
        }


        Double height = this.getHeight(record);
        if (height != null) // create extruded polygons
        {
            ExtrudedPolygon ep = new ExtrudedPolygon(height);
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
        } else // create surface polygons
        {
//            SurfacePolygons shape = new SurfacePolygons(
//                    Sector.fromDegrees(((ShapefileRecordPolygon) record).getBoundingRectangle()),
//                    record.getCompoundPointBuffer());
//            shape.setAttributes(attrs);
//            // Configure the SurfacePolygons as a single large polygon.
//            // Configure the SurfacePolygons to correctly interpret the Shapefile polygon record. Shapefile polygons may
//            // have rings defining multiple inner and outer boundaries. Each ring's winding order defines whether it's an
//            // outer boundary or an inner boundary: outer boundaries have a clockwise winding order. However, the
//            // arrangement of each ring within the record is not significant; inner rings can precede outer rings and vice
//            // versa.
//            //
//            // By default, SurfacePolygons assumes that the sub-buffers are arranged such that each outer boundary precedes
//            // a set of corresponding inner boundaries. SurfacePolygons traverses the sub-buffers and tessellates a new
//            // polygon each  time it encounters an outer boundary. Outer boundaries are sub-buffers whose winding order
//            // matches the SurfacePolygons' windingRule property.
//            //
//            // This default behavior does not work with Shapefile polygon records, because the sub-buffers of a Shapefile
//            // polygon record can be arranged arbitrarily. By calling setPolygonRingGroups(new int[]{0}), the
//            // SurfacePolygons interprets all sub-buffers as boundaries of a single tessellated shape, and configures the
//            // GLU tessellator's winding rule to correctly interpret outer and inner boundaries (in any arrangement)
//            // according to their winding order. We set the SurfacePolygons' winding rule to clockwise so that sub-buffers
//            // with a clockwise winding ordering are interpreted as outer boundaries.
//            shape.setWindingRule(AVKey.CLOCKWISE);
//            shape.setPolygonRingGroups(new int[]{0});
//            shape.setPolygonRingGroups(new int[]{0});
//            layer.addRenderable(shape);
        }
    }

    protected Double getNO2dygn(ShapefileRecord record) {
        if (record.getAttributes() == null) {
            return null;
        }

        for (Map.Entry<String, Object> attr : record.getAttributes().getEntries()) {
            if (!attr.getKey().equals("NO2dygn")) {
                continue;
            }

            Object o = attr.getValue();
            if (o instanceof Number) {
                return ((Number) o).doubleValue();
            }

            if (o instanceof String) {
                return WWUtil.convertStringToDouble(o.toString());
            }
        }

        return null;
    }

    //**************************************************************//
    //********************  Attribute Construction  ****************//
    //**************************************************************//

    protected ShapeAttributes createPolylineAttributes(gov.nasa.worldwind.formats.shapefile.ShapefileRecord record) {
        return bsag;
    }

    protected ShapeAttributes createPolygonAttributes(gov.nasa.worldwind.formats.shapefile.ShapefileRecord record) {
        return bsar;
    }
}
