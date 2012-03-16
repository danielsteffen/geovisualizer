/*
 *  VisExtrudePolygon.java 
 *
 *  Created by DFKI AV on 09.03.2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.basic;

import com.dfki.av.sudplan.vis.core.VisAlgorithmAbstract;
import com.dfki.av.sudplan.vis.core.NumberParameter;
import com.dfki.av.sudplan.vis.core.ColorParameter;
import com.dfki.av.sudplan.vis.io.shapefile.Shapefile;
import com.dfki.av.sudplan.vis.core.ITransferFunction;
import com.dfki.av.sudplan.vis.core.IVisAlgorithm;
import com.dfki.av.sudplan.vis.functions.*;
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
public class VisGeoCPM extends VisAlgorithmAbstract {

    /**
     *
     */
    private final int numPolygonsPerLayer = 100000;
    /**
     *
     */
    private NumberParameter parHeight;
    /**
     *
     */
    private ColorParameter parCapColor;
    /**
     * Filter to reduce number of Polygons.
     */
    private NumberParameter parFilter;
    
    /**
     *
     */
    protected VisGeoCPM() {
        super("GeoCPM Visualization", "Visualization of GeoCPM results of Wuppertal.",
                new ImageIcon(VisGeoCPM.class.getClassLoader().
                getResource("icons/VisGeoCPM.png")));

        this.parHeight = new NumberParameter("Height [m]");
        this.parHeight.addTransferFunction(new IdentityFunction());
        this.parHeight.addTransferFunction(new ScalarMultiplication());
        this.parHeight.addTransferFunction(new ConstantNumber());
        addVisParameter(this.parHeight);

        this.parCapColor = new ColorParameter("Color of surface");
        this.parCapColor.addTransferFunction(new ConstantColor());
        this.parCapColor.addTransferFunction(new RedGreenColorrampClassification());
        this.parCapColor.addTransferFunction(new ColorrampClassification());
        addVisParameter(this.parCapColor);

        this.parFilter = new NumberParameter("Filter");
        ConstantNumber cntf = new ConstantNumber();
        cntf.setConstant(0.5);
        this.parFilter.addTransferFunction(cntf);
        addVisParameter(this.parFilter);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {

        log.debug("Running {}", this.getClass().getSimpleName());

        List<Layer> layers = new ArrayList<Layer>();
        String attribute0 = IVisAlgorithm.NO_ATTRIBUTE;
        String attribute1 = IVisAlgorithm.NO_ATTRIBUTE;
        Shapefile shapefile;

        // 0 - Check data
        if (!(data instanceof Shapefile)) {
            log.error("Data type {} not supported for {}.",
                    data.getClass().getSimpleName(), this.getName());
            return layers;
        } else {
            shapefile = (Shapefile) data;
        }

        // 1 - Check and set all attributes
        // Attention you will receive an array that has the size of the ...
        if (attributes == null || attributes.length == 0) {
            log.warn("Attributes set to null. First and second attribute set to default.");
        } else if (attributes.length == 1) {
            log.warn("Using only one attribute. Second attribute set to default.");
            attribute0 = checkAttribute(attributes[0]);
        } else if (attributes.length >= 2) {
            attribute0 = checkAttribute(attributes[0]);
            attribute1 = checkAttribute(attributes[1]);
        }
        log.debug("Using attributes: " + attribute0 + ", " + attribute1);

        // 2 - Preprocessing data
        ITransferFunction function0 = parHeight.getSelectedTransferFunction();
        log.debug("Using transfer function {} for attribute.", function0.getClass().getSimpleName());
        function0.preprocess(shapefile, attribute0);

        ITransferFunction function1 = parCapColor.getSelectedTransferFunction();
        log.debug("Using transfer function {} for attribute.", function1.getClass().getSimpleName());
        function1.preprocess(shapefile, attribute1);

        // 3 - Create visualization
        createRenderablesForPolygons(shapefile, attribute0, attribute1, layers);

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
    private void createRenderablesForPolygons(Shapefile shp, String attribute0, String attribute1, List<Layer> layers) {

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
     * @param attribute0
     * @param layer
     */
    private void createExtrudedPolygon(Shapefile shpfile, int featureId, String attribute0, String attribute1, RenderableLayer layer) {

        //
        // Use the transfer function for parameter HEIGHT
        //
        Object object0 = shpfile.getAttributeOfFeature(featureId, attribute0);
        ITransferFunction tfHeight = parHeight.getSelectedTransferFunction();
        Number result = (Number) tfHeight.calc(object0);
        double dResult = result.doubleValue();
        if (dResult < 0) {
            log.error("The input value for ExtrudedPolygon < 0."
                    + "Setting value to 0.01.");
            dResult = 0.01;
        } else if (result.doubleValue() == 0) {
            log.warn("The input value for ExtrudedPolygon = 0."
                    + "Setting value to 0.01.");
            dResult = 0.01;
        } 
        
        //
        // Skip all values lower than a determined value.
        //
        ITransferFunction tfFilter = parFilter.getSelectedTransferFunction();
        Number limit = (Number)tfFilter.calc(null);
        if(dResult < limit.doubleValue()){
            return;
        }

        //
        // Use the transfer function for parameter CAP COLOR
        //
        Object object1 = shpfile.getAttributeOfFeature(featureId, attribute1);
        ITransferFunction tfCapColor = parCapColor.getSelectedTransferFunction();
        Color capColor = (Color) tfCapColor.calc(object1);
        Material m = new Material(capColor);
        BasicShapeAttributes attrCap = new BasicShapeAttributes();
        attrCap.setDrawOutline(false);
        attrCap.setInteriorOpacity(0.8);
        attrCap.setInteriorMaterial(m);

        //
        // Putting everything together.
        //
        ExtrudedPolygon ep = new ExtrudedPolygon();
        ep.setHeight(dResult);
        ep.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        ep.setCapAttributes(attrCap);
        ep.setSideAttributes(attrCap);
        //ep.setEnableSides(false);
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
                if (!ep.getOuterBoundary().iterator().hasNext()) {
                    // has no outer boundary yet
                    ep.setOuterBoundary(positionList);
                } else {
                    ep = new ExtrudedPolygon();
                    ep.setCapAttributes(attrCap);
                    ep.setOuterBoundary(positionList);
                    ep.setEnableSides(false);
                    layer.addRenderable(ep);
                }
            } else {
                ep.addInnerBoundary(positionList);
            }
        }
    }
}
