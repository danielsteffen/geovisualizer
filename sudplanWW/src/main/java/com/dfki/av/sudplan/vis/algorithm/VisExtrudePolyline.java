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
    private Material[] materials;
    /**
     * 
     */
    private List<Category> categories;
    
    /**
     *
     */
    public VisExtrudePolyline() {
        super("Extrude Polylines", "You have to select 2 attributes for this "
                + "visualization technique.\n\nThe Polyline technique maps one "
                + "scalar attribute 'a' of your data source to the parameter "
                + "color. The second attirbute 'b' is used for the extrution.",
                new ImageIcon(VisAlgorithmAbstract.class.getClassLoader().
                getResource("icons/VisExtrudePolyline.png")));
        
        VisParameter parameter0 = new VisParameter("Color", true);
        parameter0.setCategorization(new CategorizationAuto(5));
        parameters.add(parameter0);
        
        VisParameter parameter1 = new VisParameter("Height");
        parameters.add(parameter1);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {

        // First of all check whether enough attributes have been specified for
        // this visualization.
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

            //
            // Pre-processing data for attribute 0 (color) if needed.
            //
            Categorization cat = parameters.get(0).getCategorization();
            this.categories = cat.execute(shapefile, attribute0);
            
            //
            // Mapping of data categories to visual categories.
            //
            Color[] colors = ColorUtils.CreateRedGreenColorGradientAttributes(categories.size());
            this.materials = new Material[categories.size()];
            for (int i = 0; i < materials.length; i++) {
                materials[i] = new Material(colors[i]);
            }

            //
            // Create the visualization
            //
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
        Material m = getMaterialForValue(object0);
        ShapeAttributes sa = new BasicShapeAttributes();
        sa.setOutlineWidth(0.3);
        sa.setInteriorOpacity(0.6);
        sa.setOutlineOpacity(0.3);
        sa.setInteriorMaterial(m);
        sa.setOutlineMaterial(m);

        //
        // Mapping for visualization parameter HEIGHT
        //
        Object object1 = shpfile.getAttributeOfFeature(featureId, attribute1);
        Double value1;
        if (object1 instanceof Number) {
            value1 = ((Number) object1).doubleValue();
        } else {
            log.warn("Data type for extrude Polyline has to be of type Number."
                    + "Setting 'height=0.5'.");
            value1 = 0.5;
        }

        double scaledValue = getScaledValueForAttribute1(value1);
        List<Position> positionList = new ArrayList<Position>();
        List<Geometry> list = shpfile.getGeometryList(featureId);
        for (int i = 0; i < list.size(); i++) {
            Geometry g = list.get(i);
            for (int j = 0; j < g.GetPointCount(); j++) {
                double[] point = g.GetPoint_2D(j);
                // ... swap geo positions ???! why
                positionList.add(Position.fromDegrees(point[1], point[0], scaledValue));
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

    /**
     *
     * @param d
     * @return
     */
    private Material getMaterialForValue(Object o) {

        for (int i = 0; i < categories.size(); i++) {
            Category c = categories.get(i);
            if (c.includes(o)) {
                return materials[i];
            }
        }
        return Material.GRAY;
    }

    /**
     *
     * @param d
     * @return
     */
    private double getScaledValueForAttribute1(Double d) {
        return d / 120.0;
    }
}
