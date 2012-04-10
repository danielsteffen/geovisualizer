/*
 *  VisExtrudePolygon.java 
 *
 *  Created by DFKI AV on 26.01.2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.basic;

import com.dfki.av.sudplan.vis.core.*;
import com.dfki.av.sudplan.vis.functions.*;
import com.dfki.av.sudplan.vis.io.shapefile.Shapefile;
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
import javax.swing.Icon;
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
    private ColorParameter parCapColor;
    /**
     *
     */
    private ColorParameter parSideColor;

    /**
     *
     * @param drawOutline
     */
    protected VisExtrudePolygon() {
        this("Extrude Polygons", "No description available.",
                new ImageIcon(VisExtrudePolygon.class.getClassLoader().
                getResource("icons/VisExtrudePolygon.png")));
    }

    /**
     *
     * @param drawOutline
     */
    protected VisExtrudePolygon(String name, String description, Icon icon) {
        super(name, description, icon);

        this.parHeight = new NumberParameter("Extrusion of polygon [m]");
        this.parHeight.addTransferFunction(IdentityFunction.class.getName());
        this.parHeight.addTransferFunction(ScalarMultiplication.class.getName());
        this.parHeight.addTransferFunction(ConstantNumber.class.getName());
        addVisParameter(this.parHeight);

        this.parCapColor = new ColorParameter("Color of cap");
        this.parCapColor.addTransferFunction(ConstantColor.class.getName());
        this.parCapColor.addTransferFunction(RedGreenColorrampClassification.class.getName());
        this.parCapColor.addTransferFunction(ColorrampClassification.class.getName());
        this.parCapColor.addTransferFunction(ColorrampCategorization.class.getName());
        addVisParameter(this.parCapColor);

        this.parSideColor = new ColorParameter("Color of side");
        this.parSideColor.addTransferFunction(ConstantColor.class.getName());
        this.parSideColor.addTransferFunction(RedGreenColorrampClassification.class.getName());
        this.parSideColor.addTransferFunction(ColorrampClassification.class.getName());
        addVisParameter(this.parSideColor);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {

        log.debug("Running {}", this.getClass().getSimpleName());
        setProgress(0);
        List<Layer> layers = new ArrayList<Layer>();
        String attribute0 = IVisAlgorithm.NO_ATTRIBUTE;
        String attribute1 = IVisAlgorithm.NO_ATTRIBUTE;
        String attribute2 = IVisAlgorithm.NO_ATTRIBUTE;
        Shapefile shapefile;

        // 0 - Check data
        if (!(data instanceof Shapefile)) {
            log.error("Data type {} not supported for {}.",
                    data.getClass().getSimpleName(), this.getName());
            return layers;
        } else {
            shapefile = (Shapefile) data;
        }
        setProgress(5);

        // 1 - Check and set all attributes
        if (attributes == null || attributes.length == 0) {
            log.warn("Attributes set to null. First and second attribute set to default.");
        } else if (attributes.length == 1) {
            log.warn("Using only one attribute. Second attribute set to default.");
            attribute0 = checkAttribute(attributes[0]);
        } else if (attributes.length == 2) {
            attribute0 = checkAttribute(attributes[0]);
            attribute1 = checkAttribute(attributes[1]);
        } else if (attributes.length == 3) {
            attribute0 = checkAttribute(attributes[0]);
            attribute1 = checkAttribute(attributes[1]);
            attribute2 = checkAttribute(attributes[2]);
        }
        log.debug("Using attributes: " + attribute0 + ", " + attribute1 + ", " + attribute2);
        setProgress(10);
        
        // 2 - Preprocessing data
        ITransferFunction function0 = parHeight.getTransferFunction();
        log.debug("Using transfer function {} for attribute.", function0.getClass().getSimpleName());
        function0.preprocess(shapefile, attribute0);
        setProgress(15);
        
        ITransferFunction function1 = parCapColor.getTransferFunction();
        log.debug("Using transfer function {} for attribute.", function1.getClass().getSimpleName());
        function1.preprocess(shapefile, attribute1);
        setProgress(20);
        ITransferFunction function2 = parSideColor.getTransferFunction();
        log.debug("Using transfer function {} for attribute.", function2.getClass().getSimpleName());
        function2.preprocess(shapefile, attribute2);
        setProgress(25);
        
        // 3 - Create visualization
        createRenderablesForPolygons(shapefile, attribute0, attribute1, layers);
        setProgress(100);
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
            setProgress(25 + (int)(75 * i / (double)shp.getFeatureCount()));
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

        //
        // Use the transfer function for parameter HEIGHT
        //
        Object object0 = shpfile.getAttributeOfFeature(featureId, attribute);
        ITransferFunction tfHeight = parHeight.getTransferFunction();
        Number result = (Number) tfHeight.calc(object0);
        double dResult = result.doubleValue();
        if (dResult < 0) {
            log.error("The input value for ExtrudedPolygon < 0."
                    + "Setting value to 0.01.");
            dResult = 0.01;
        } else if (result.doubleValue() == 0) {
            log.warn("The input value for ExtrudedPolygon = 0."
                    + "Setting value to 0.01.");
            dResult += 1.0;
        }

        //
        // Use the transfer function for parameter CAP COLOR
        //
        Object object1 = shpfile.getAttributeOfFeature(featureId, attribute1);
        ITransferFunction tfCapColor = parCapColor.getTransferFunction();
        Color capColor = (Color) tfCapColor.calc(object1);
        Material m = new Material(capColor);
        BasicShapeAttributes attrCap = new BasicShapeAttributes();
        attrCap.setDrawOutline(false);
        attrCap.setInteriorOpacity(1.0);
        attrCap.setInteriorMaterial(m);

        //
        // Use the transfer function for parameter SIDE COLOR
        //
        ITransferFunction tfSideColor = parSideColor.getTransferFunction();
        Color sideColor = (Color) tfSideColor.calc(object1);
        Material sideMaterial = new Material(sideColor);
        BasicShapeAttributes attrSide = new BasicShapeAttributes();
        attrSide.setDrawOutline(true);
        attrSide.setInteriorOpacity(1.0);
        attrSide.setInteriorMaterial(sideMaterial);

        //
        // Putting everything together.
        //
        ExtrudedPolygon ep = new ExtrudedPolygon();
        ep.setHeight(dResult);
        ep.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        ep.setCapAttributes(attrCap);
        ep.setSideAttributes(attrSide);
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
                    ep.setSideAttributes(attrSide);
                    ep.setOuterBoundary(positionList);
                    layer.addRenderable(ep);
                }
            } else {
                ep.addInnerBoundary(positionList);
            }
        }
    }

}
