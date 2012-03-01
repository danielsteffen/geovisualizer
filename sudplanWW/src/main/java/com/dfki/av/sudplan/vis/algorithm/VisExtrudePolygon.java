/*
 *  VisExtrudePolygon.java 
 *
 *  Created by DFKI AV on 26.01.2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
import com.dfki.av.sudplan.vis.algorithm.functions.*;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.util.WWMath;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.gdal.ogr.Geometry;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisExtrudePolygon extends VisAlgorithmAbstract {

    /**
     *
     */
    private final int numPolygonsPerLayer = 10000;
    /**
     *
     */
    private NumberParameter parHeight;
    /**
     * 
     */
    private ColorParameter parColor;

    /**
     *
     * @param attribute
     */
    public VisExtrudePolygon() {
        super("Extrude Polygons", "No description available.",
                new ImageIcon(VisExtrudePolygon.class.getClassLoader().
                getResource("icons/VisExtrudePolygon.png")));

        this.parHeight = new NumberParameter("Extrusion of polygon");
        this.parHeight.addTransferFunction(new IdentityFunction());
        this.parHeight.addTransferFunction(new ScalarMultiplication());
        this.parHeight.addTransferFunction(new ConstantNumberTansferFunction());
        addVisParameter(this.parHeight);
        
        this.parColor = new ColorParameter("Color of top surface");
        this.parColor.addTransferFunction(new ConstantColorTransferFunction());
        this.parColor.addTransferFunction(new RedGreenColorrampTransferFunction());
        addVisParameter(this.parColor);
        
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {

        log.debug("Running {}", this.getClass().getSimpleName());
        
        if (attributes == null || attributes.length < 2) {
            throw new IllegalArgumentException("Need 1 attributes "
                    + "to define visualization parameters.");
        }
        
        List<Layer> layers = new ArrayList<Layer>();
        if (data instanceof Shapefile
                && attributes[0] instanceof String) {
            Shapefile shapefile = (Shapefile) data;
            String attribute0 = (String) attributes[0];
            String attribute1 = (String) attributes[1];
            
            // 1 - Pre-processing data
            ITransferFunction function0 = parHeight.getSelectedTransferFunction();
            log.debug("Using transfer function {} for attribute.", function0.getClass().getSimpleName());
            function0.preprocess(shapefile, attribute0);

            ITransferFunction function1 = parColor.getSelectedTransferFunction();
            log.debug("Using transfer function {} for attribute.", function1.getClass().getSimpleName());
            function1.preprocess(shapefile, attribute1);

            // 2 - Create visualization
            addRenderablesForPolygons(shapefile, attribute0, attribute1, layers);
        } else {
            log.warn("Data type or attribute types not supported for Extrude Polygon Visualization.");
        }
        
        log.debug("Finished {}", this.getClass().getSimpleName());
        
        return layers;
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
    private void addRenderablesForPolygons(Shapefile shp, String attribute0, String attribute1, List<Layer> layers) {
        
        RenderableLayer layer = new RenderableLayer();
        int numLayers = 0;
        layer.setName(shp.getLayerName() + "-" + numLayers);
        layers.add(layer);

        for (int i = 0; i < shp.getFeatureCount(); i++) {

            createExtrudedPolygon(shp, i, attribute0, attribute1, layer);

            if (layer.getNumRenderables() > this.numPolygonsPerLayer) {
                layer = new RenderableLayer();
                layer.setName(shp.getLayerName() + "-" + ++numLayers);
                layer.setEnabled(false);
                layers.add(layer);
            }
        }
    }

    /**
     *
     * @param shpfile
     * @param featureId
     * @param attribute
     * @param layer
     */
    private void createExtrudedPolygon(Shapefile shpfile, int featureId, String attribute, String attribute1, RenderableLayer layer) {

        Object object;
        if (attribute.equalsIgnoreCase("<<NO_ATTRIBUTE>>")) {
            object = null;
        } else {
            object = shpfile.getAttributeOfFeature(featureId, attribute);
        }
        
        ITransferFunction tf = parHeight.getSelectedTransferFunction();
        Number result = (Number) tf.calc(object);
        double dResult = result.doubleValue();
        
        if (dResult < 0) {
            log.error("The input value for ExtrudedPolygon < 0."
                    + "Setting value to 0.01.");
            dResult = 0.01;
        } else if (result.doubleValue() == 0) {
            log.warn("The input value for ExtrudedPolygon = 0."
                    + "Setting value to 0.01.");
            dResult += 0.01;
        }

        Object object1;
        if (attribute1.equalsIgnoreCase("<<NO_ATTRIBUTE>>")) {
            object1 = null;
        } else {
            object1 = shpfile.getAttributeOfFeature(featureId, attribute1);
        }
        
        ITransferFunction tf1 = parColor.getSelectedTransferFunction();
        Color c = (Color) tf1.calc(object1);
        
        Material m = new Material(c);
        BasicShapeAttributes bsa = new BasicShapeAttributes();
        bsa.setDrawOutline(false);
        bsa.setInteriorOpacity(1.0);
        bsa.setInteriorMaterial(m);
            
        ExtrudedPolygon ep = new ExtrudedPolygon();
        ep.setHeight(dResult);
        ep.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        ep.setAttributes(bsa);
        layer.addRenderable(ep);

        List<Geometry> list = shpfile.getGeometryList(featureId);
        for (int i = 0; i < list.size(); i++) {

            Geometry g = list.get(i);
            List<Position> positionList = new ArrayList<Position>();

            for (int j = 0; j < g.GetPointCount(); j++) {
                double[] point = g.GetPoint_2D(j);
                // ... swap geo positions
                positionList.add(Position.fromDegrees(point[1], point[0], dResult));
            }

            if (WWMath.computeWindingOrderOfLocations(positionList).equals(AVKey.CLOCKWISE)) {
                if (!ep.getOuterBoundary().iterator().hasNext()) // has no outer boundary yet
                {
                    ep.setOuterBoundary(positionList);
                } else {
                    ep = new ExtrudedPolygon();
                    ep.setAttributes(bsa);
                    ep.setOuterBoundary(positionList);
                    layer.addRenderable(ep);
                }
            } else {
                ep.addInnerBoundary(positionList);
            }
        }
    }
}
