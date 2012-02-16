/*
 *  VisExtrudePolyline.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
import com.dfki.av.utils.ColorUtils;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.WWUtil;
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
    private static final int DEFAULT_NUM_CLASSES = 4;
    /**
     *
     */
    private double[] boundaries;
    /**
     *
     */
    private Material[] materials;

    /**
     *
     */
    public VisExtrudePolyline() {
        super("Extrude polyline visualization", "You have to select 2 attributes for this visualization technique.\nThe Polyline technique maps one scalar attribute 'a' of your data source to the parameter color. The second attirbute 'b' is used for the extrution.",
                new ImageIcon(VisAlgorithmAbstract.class.getClassLoader().
                getResource("icons/vis_color_height.png")));
        VisParameter parameter0 = new VisParameter("Height");
        parameters.add(parameter0);
        VisParameter parameter1 = new VisParameter("Color");
        parameters.add(parameter1);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {

        if (attributes == null || attributes.length != 2) {
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

            // Pre-processing data
            this.boundaries = DataAttributeUtils.AutoClassificationOfNumberAttribute(shapefile, attribute0, DEFAULT_NUM_CLASSES);
            Color[] colors = ColorUtils.CreateRedGreenColorGradientAttributes(DEFAULT_NUM_CLASSES);

            this.materials = new Material[DEFAULT_NUM_CLASSES];
            for (int i = 0; i < materials.length; i++) {
                materials[i] = new Material(colors[i]);
            }

            // Create visualization
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
        // Mapping for visualization parameter COLOR
        //
        Object object0 = shpfile.getAttributeOfFeature(featureId, attribute0);
        Double value0 = null;
        if (object0 instanceof Number) {
            value0 = ((Number) object0).doubleValue();
        }

        if (object0 instanceof String) {
            value0 = WWUtil.convertStringToDouble(object0.toString());
        }

        ShapeAttributes sa = new BasicShapeAttributes();
        sa.setOutlineWidth(0.3);
        sa.setInteriorOpacity(0.6);
        sa.setOutlineOpacity(0.3);

        if (value0 != null) {
            for (int i = 1; i < boundaries.length; i++) {
                if (value0 < boundaries[i]) {
                    sa.setInteriorMaterial(materials[i - 1]);
                    sa.setOutlineMaterial(materials[i - 1]);
                    break;
                }
            }
        } else {
            log.debug("Setting attribute 'color' to gray.");
            sa.setInteriorMaterial(Material.GRAY);
            sa.setOutlineMaterial(Material.GRAY);
        }

        //
        // Mapping for visiulization parameter HEIGHT
        //
        Object object1 = shpfile.getAttributeOfFeature(featureId, attribute1);
        Double value1 = null;
        if (object1 instanceof Number) {
            value1 = ((Number) object1).doubleValue();
        }

        if (object1 instanceof String) {
            value1 = WWUtil.convertStringToDouble(object1.toString());
        }

        if (value1 == null) {
            log.warn("Value for ExtrudePolyline equals null. "
                    + "Setting attribute to default value.");
            value1 = 30.0;
        }

        List<Position> positionList = new ArrayList<Position>();
        List<Geometry> list = shpfile.getGeometryList(featureId);
        for (int i = 0; i < list.size(); i++) {
            Geometry g = list.get(i);
            for (int j = 0; j < g.GetPointCount(); j++) {
                double[] point = g.GetPoint_2D(j);
                // ... swap geo positions ???! why
                positionList.add(Position.fromDegrees(point[1], point[0], value1/120.0));
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
