/*
 *  VisAlgorithmDefault.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.formats.shapefile.*;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwindx.examples.util.RandomShapeAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
  
/**
 *
 * @author steffen
 */
public class VisAlgorithmDefault extends VisAlgorithmAbstract {

    /**
     * 
     */
    private static final RandomShapeAttributes randomAttrs = new RandomShapeAttributes();
    /**
     * 
     */
    private final int numPolygonsPerLayer = 10000;
    /**
     *
     */
    public VisAlgorithmDefault() {
        super("Default Visualization",
                "No description available.",
                new ImageIcon(VisAlgorithmAbstract.class.getClassLoader().
                getResource("icons/VisAlgorithmDefault.png")));
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {

        log.debug("Creating layer from data: {}", data);
        List<Layer> layers = new ArrayList<Layer>();

        if (data instanceof Shapefile) {
            Shapefile shp = (Shapefile) data;
            if (Shapefile.isPointType(shp.getShapeType())) {
                Layer layer = new IconLayer();
                this.addIconsForPoints(shp, (IconLayer) layer);
                layers.add(layer);
            } else if (Shapefile.isMultiPointType(shp.getShapeType())) {
                Layer layer = new IconLayer();
                this.addIconsForMultiPoints(shp, (IconLayer) layer);
                layers.add(layer);
            } else if (Shapefile.isPolylineType(shp.getShapeType())) {
                Layer layer = new RenderableLayer();
                this.addRenderablesForPolylines(shp, (RenderableLayer) layer);
                layers.add(layer);
            } else if (Shapefile.isPolygonType(shp.getShapeType())) {
                this.addRenderablesForPolygons(shp, layers);
            } else {
                log.warn("Unrecognized shape type: {}", shp.getShapeType());
            }
        }
        log.debug("Finished.");
        return layers;
    }

    /**
     *
     * @param shp
     * @param layer
     */
    private void addIconsForPoints(Shapefile shp, IconLayer layer) {
        while (shp.hasNext()) {
            ShapefileRecord record = shp.nextRecord();

            if (!Shapefile.isPointType(record.getShapeType())) {
                continue;
            }

            double[] point = ((ShapefileRecordPoint) record).getPoint();
            layer.addIcon(new UserFacingIcon("icons/dot8x8.png", Position.fromDegrees(point[1], point[0], 0)));
        }
    }

    /**
     *
     * @param shp
     * @param layer
     */
    private void addIconsForMultiPoints(Shapefile shp, IconLayer layer) {
        while (shp.hasNext()) {
            ShapefileRecord record = shp.nextRecord();

            if (!Shapefile.isMultiPointType(record.getShapeType())) {
                continue;
            }

            Iterable<double[]> iterable = ((ShapefileRecordMultiPoint) record).getPoints(0);

            for (double[] point : iterable) {
                layer.addIcon(new UserFacingIcon("icons/dot8x8.png", Position.fromDegrees(point[1], point[0], 0)));
            }
        }
    }

    private void addRenderablesForPolylines(Shapefile shp, RenderableLayer layer) {
        // Reads all records from the Shapefile, but ignores each records unique information. We do this to create one
        // WWJ object representing the entire shapefile, which as of 8/10/2010 is required to display very large
        // polyline Shapefiles. To create one WWJ object for each Shapefile record, replace this method's contents with
        // the following:
        //
        while (shp.hasNext())
        {
            ShapefileRecord record = shp.nextRecord();
        
            if (!Shapefile.isPolylineType(record.getShapeType()))
                continue;
        
            ShapeAttributes attrs = randomAttrs.nextPolylineAttributes();
            layer.addRenderable(this.createPolyline(record, attrs));
        }

//        while (shp.hasNext()) {
//            shp.nextRecord();
//        }
//
//        layer.addRenderable(this.createPolyline(shp, DefaultVisAlgorithm.attribute));
    }

    /**
     * Creates renderables for all the polygons in the shapefile. Polygon is the
     * only shape that potentially returns a more than one layer, which it does
     * when the polygons per layer limit is exceeded.
     *
     * @param shp the shapefile to read
     * @param layers a list in which to place the layers created. May not be
     * null.
     */
    private void addRenderablesForPolygons(Shapefile shp, List<Layer> layers) {
        RenderableLayer layer = new RenderableLayer();
        layers.add(layer);

        int recordNumber = 0;
        while (shp.hasNext()) {
            try {
                ShapefileRecord record = shp.nextRecord();
                recordNumber = record.getRecordNumber();

                if (!Shapefile.isPolygonType(record.getShapeType())) {
                    continue;
                }

                ShapeAttributes attrs = randomAttrs.nextPolygonAttributes();                
                this.createPolygon(record, attrs, layer);

                if (layer.getNumRenderables() > this.numPolygonsPerLayer) {
                    layer = new RenderableLayer();
                    layer.setEnabled(false);
                    layers.add(layer);
                }
            } catch (Exception e) {
                log.warn("Could not convert to ShapefileRecord: {}, {}", recordNumber, e);
            }
        }
    }

    /**
     *
     * @param record
     * @param attrs
     * @return
     */
    private Renderable createPolyline(ShapefileRecord record, ShapeAttributes attrs) {
        SurfacePolylines shape = new SurfacePolylines(
                Sector.fromDegrees(((ShapefileRecordPolyline) record).getBoundingRectangle()),
                record.getCompoundPointBuffer());
        shape.setAttributes(attrs);
        return shape;
    }

    /**
     *
     * @param shp
     * @param attrs
     * @return
     */
    private Renderable createPolyline(Shapefile shp, ShapeAttributes attrs) {
        SurfacePolylines shape = new SurfacePolylines(Sector.fromDegrees(shp.getBoundingRectangle()),
                shp.getPointBuffer());
        shape.setAttributes(attrs);

        return shape;
    }

    /**
     *
     * @param record
     * @param attrs
     * @param layer
     */
    private void createPolygon(ShapefileRecord record, ShapeAttributes attrs, RenderableLayer layer) {
        SurfacePolygons shape = new SurfacePolygons(
                Sector.fromDegrees(((ShapefileRecordPolygon) record).getBoundingRectangle()),
                record.getCompoundPointBuffer());
        shape.setAttributes(attrs);
        // Configure the SurfacePolygons as a single large polygon.
        // Configure the SurfacePolygons to correctly interpret the Shapefile polygon record. Shapefile polygons may
        // have rings defining multiple inner and outer boundaries. Each ring's winding order defines whether it's an
        // outer boundary or an inner boundary: outer boundaries have a clockwise winding order. However, the
        // arrangement of each ring within the record is not significant; inner rings can precede outer rings and vice
        // versa.
        //
        // By default, SurfacePolygons assumes that the sub-buffers are arranged such that each outer boundary precedes
        // a set of corresponding inner boundaries. SurfacePolygons traverses the sub-buffers and tessellates a new
        // polygon each  time it encounters an outer boundary. Outer boundaries are sub-buffers whose winding order
        // matches the SurfacePolygons' windingRule property.
        //
        // This default behavior does not work with Shapefile polygon records, because the sub-buffers of a Shapefile
        // polygon record can be arranged arbitrarily. By calling setPolygonRingGroups(new int[]{0}), the
        // SurfacePolygons interprets all sub-buffers as boundaries of a single tessellated shape, and configures the
        // GLU tessellator's winding rule to correctly interpret outer and inner boundaries (in any arrangement)
        // according to their winding order. We set the SurfacePolygons' winding rule to clockwise so that sub-buffers
        // with a clockwise winding ordering are interpreted as outer boundaries.
        shape.setWindingRule(AVKey.CLOCKWISE);
        shape.setPolygonRingGroups(new int[]{0});
        shape.setPolygonRingGroups(new int[]{0});
        layer.addRenderable(shape);
    }
}
