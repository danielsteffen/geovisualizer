/*
 *  VisExtrudePolyline.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
import com.dfki.av.sudplan.vis.algorithm.functions.*;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.gdal.ogr.Geometry;

/**
 * This visualization technique enables to visualize a shapefile of type
 * Polyline. 2 attributes have to be defined.
 *
 * @author steffen
 */
public class VisExtrudePolyline extends VisAlgorithmAbstract {

    /**
     *
     */
    private ColorParameter parColor;
    /**
     *
     */
    private NumberParameter parHeight;

    /**
     *
     */
    public VisExtrudePolyline() {
        super("Extrude Polylines", "You have to select 2 attributes for this "
                + "visualization technique.\n\nThe Polyline technique maps one "
                + "scalar attribute 'a' of your data source to the parameter "
                + "color. The second attirbute 'b' is used for the extrution.",
                new ImageIcon(VisExtrudePolyline.class.getClassLoader().
                getResource("icons/VisExtrudePolyline.png")));

        this.parColor = new ColorParameter("Color of surface");
        this.parColor.addTransferFunction(new RedGreenColorrampTransferFunction());
        this.parColor.addTransferFunction(new ConstantColorTransferFunction());
        addVisParameter(parColor);

        this.parHeight = new NumberParameter("Extrusion of surface");
        this.parHeight.addTransferFunction(new IdentityFunction());
        this.parHeight.addTransferFunction(new ScalarMultiplication());
        this.parHeight.addTransferFunction(new ConstantNumberTansferFunction());
        addVisParameter(parHeight);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {

        log.debug("Running {}", this.getClass().getSimpleName());

        // First of all check whether enough attributes have been specified for
        // this visualization.
        if (attributes == null || attributes.length < 2) {
            throw new IllegalArgumentException("Need 2 attributes "
                    + "to define visualization parameters.");
        }

        List<Layer> layers = new ArrayList<Layer>();
        if (data instanceof Shapefile
                && attributes[0] instanceof String
                && attributes[1] instanceof String) {
            Shapefile shapefile = (Shapefile) data;
            String attribute0 = (String) attributes[0];
            String attribute1 = (String) attributes[1];

            // 1 - Pre-processing data
            ITransferFunction function0 = parColor.getSelectedTransferFunction();
            log.debug("Using transfer function {} for attribute 0.", function0.getClass().getSimpleName());
            function0.preprocess(shapefile, attribute0);

            ITransferFunction function1 = parHeight.getSelectedTransferFunction();
            log.debug("Using transfer function {} for attribute 1.", function1.getClass().getSimpleName());
            function1.preprocess(shapefile, attribute0);

            // 2 - Create visualization
            if (Shapefile.isPolylineType(shapefile.getShapeType())) {
                RenderableLayer layer = new RenderableLayer();
                for (int i = 0; i < shapefile.getFeatureCount(); i++) {
                    Renderable r = createExtrudedPolyline(shapefile, i, attribute0, attribute1);
                    layer.addRenderable(r);
                }
                layer.setName(shapefile.getLayerName());
                layers.add(layer);
            } else {
                log.warn("Extrude Polyline Visualization does not support shape type {}.", shapefile.getShapeType());
            }
        } else {
            log.debug("Data type not supported.");
        }
        
        log.debug("Finished {}", this.getClass().getSimpleName());
        
        return layers;
    }

    /**
     *
     * @param shpfile
     * @param featureId
     * @return
     */
    private Renderable createExtrudedPolyline(Shapefile shpfile, int featureId, String attribute0, String attribute1) {

        //
        // Use the transfer function for parameter COLOR
        //
        Object object0;
        if (attribute0.equalsIgnoreCase("<<NO_ATTRIBUTE>>")) {
            object0 = null;
        } else {
            object0 = shpfile.getAttributeOfFeature(featureId, attribute0);
        }

        ITransferFunction function0 = parColor.getSelectedTransferFunction();
        Color c = (Color) function0.calc(object0);
        Material m = new Material(c);

        ShapeAttributes sa = new BasicShapeAttributes();
        sa.setOutlineWidth(0.3);
        sa.setInteriorOpacity(0.6);
        sa.setOutlineOpacity(0.3);
        sa.setInteriorMaterial(m);
        sa.setOutlineMaterial(m);

        //
        // Use the transfer function for parameter HEIGHT
        //
        Object object1;
        if (attribute1.equalsIgnoreCase("<<NO_ATTRIBUTE>>")) {
            object1 = null;
        } else {
            object1 = shpfile.getAttributeOfFeature(featureId, attribute1);
        }

        ITransferFunction function1 = parHeight.getSelectedTransferFunction();
        Double value1 = (Double) function1.calc(object1);

        //double scaledValue = getScaledValueForAttribute1(value1);
        List<Position> positionList = new ArrayList<Position>();
        List<Geometry> list = shpfile.getGeometryList(featureId);
        for (int i = 0; i < list.size(); i++) {
            Geometry g = list.get(i);
            for (int j = 0; j < g.GetPointCount(); j++) {
                double[] point = g.GetPoint_2D(j);
                // ... swap geo positions ???! why
                positionList.add(Position.fromDegrees(point[1], point[0], value1));
            }
        }

        //
        // Putting everything together ...
        //
        Path path = new Path(positionList);
        path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        path.setExtrude(true);
        path.setAttributes(sa);

        return path;
    }
}
