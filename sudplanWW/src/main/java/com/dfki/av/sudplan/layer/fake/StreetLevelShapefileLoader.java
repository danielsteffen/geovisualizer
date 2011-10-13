/*
 *  StreetLevelShapefileLoader.java 
 *
 *  Created by DFKI AV on 12.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.layer.fake;

import com.dfki.av.sudplan.layer.ShapefileLoader2;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.formats.shapefile.*;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.util.ShapefileLoader;
import java.awt.Color;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 * @see ShapefileLoader
 */
public class StreetLevelShapefileLoader extends ShapefileLoader2 {

    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void addRenderablesForPolylines(Shapefile shp, RenderableLayer layer) {
        // Reads all records from the Shapefile, but ignores each records unique information. We do this to create one
        // WWJ object representing the entire shapefile, which as of 8/10/2010 is required to display very large
        // polyline Shapefiles. To create one WWJ object for each Shapefile record, replace this method's contents with
        // the following:
        //
        while (shp.hasNext()) {
            ShapefileRecord record = shp.nextRecord();

            if (!Shapefile.isPolylineType(record.getShapeType())) {
                continue;
            }

            ShapeAttributes attrs = this.createPolylineAttributes(record);
            layer.addRenderable(this.createPolyline(record, attrs));
        }

//        while (shp.hasNext()) {
//            shp.nextRecord();
//        }
//
//        ShapeAttributes attrs = this.createPolylineAttributes(null);
//        layer.addRenderable(this.createPolyline(shp, attrs));
    }

    @Override
    protected Renderable createPolyline(Shapefile shp, ShapeAttributes attrs) {

        SurfacePolylines shape = new SurfacePolylines(Sector.fromDegrees(shp.getBoundingRectangle()),
                shp.getPointBuffer());
        shape.setAttributes(attrs);

        Iterable<? extends Position> iterable = shp.getPointBuffer().getPositions();
        List<Position> newPosList = new ArrayList<Position>();
        for (Iterator it = iterable.iterator(); it.hasNext();) {
            Position pos = (Position) it.next();
            pos.add(Position.fromDegrees(0.0, 0.0, 1000.0));
            Position newPos = Position.fromDegrees(pos.getLatitude().getDegrees(), pos.getLongitude().getDegrees(), 100);
            newPosList.add(newPos);
        }

        Path path = new Path(newPosList);
        path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        path.setExtrude(true);
        path.setPathType(AVKey.GREAT_CIRCLE);
        path.setAttributes(attrs);

        return shape;
    }

    @Override
    protected Renderable createPolyline(ShapefileRecord record, ShapeAttributes attrs) {

        // Here starts the transformation process.
        Object object = getValue(record, "Perc98d");
        Double perc98d = null;
        if (object instanceof Number) {
            perc98d = ((Number) object).doubleValue();
        }

        if (object instanceof String) {
            perc98d = WWUtil.convertStringToDouble(object.toString());
        }


        if (object != null) {
            Double dPercDouble = Double.parseDouble(object.toString());
            perc98d = dPercDouble.doubleValue();

            ShapeAttributes attributes = new BasicShapeAttributes();
            attributes.setOutlineMaterial(new Material(Color.BLACK));
            attributes.setOutlineWidth(0.5);
            attributes.setInteriorOpacity(0.6);
            attributes.setOutlineOpacity(0.4);
            if (perc98d >= 90.0) {
                attributes.setInteriorMaterial(new Material(Color.RED));
            } else if (perc98d >= 60.0) {
                attributes.setInteriorMaterial(new Material(Color.YELLOW));
            } else {
                attributes.setInteriorMaterial(new Material(Color.GREEN));
            }

            // ... and set the geometry.
            Iterable<? extends Position> iterable = record.getCompoundPointBuffer().getPositions();
            List<Position> posList = new ArrayList<Position>();
            for (Iterator it = iterable.iterator(); it.hasNext();) {
                Position pos = (Position) it.next();
                pos.add(Position.fromDegrees(0.0, 0.0, 1000.0));
                Position newPos = Position.fromDegrees(pos.getLatitude().getDegrees(), pos.getLongitude().getDegrees(), perc98d);
                posList.add(newPos);
            }

            Path path = new Path(posList);
            path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            path.setExtrude(true);
            path.setAttributes(attributes);

            return path;
        } else {
            if (log.isWarnEnabled()) {
                log.warn("No value available.");
            }
        }
        return null;
    }
}