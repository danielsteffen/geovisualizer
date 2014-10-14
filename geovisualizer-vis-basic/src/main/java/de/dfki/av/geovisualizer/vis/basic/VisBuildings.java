/*
 * VisBuildings.java
 *
 * Created by DFKI AV on 08.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.vis.basic;

import de.dfki.av.geovisualizer.core.*;
import de.dfki.av.geovisualizer.core.functions.*;
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
import java.util.UUID;
import javax.swing.ImageIcon;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisBuildings extends VisAlgorithmAbstract {

    /**
     * The description for the {@link VisBuildings} algorithm.
     */
    public static final String DESCRIPTION = "For the building visualization "
            + "algorithm three visualization parameters can be configured.\n\n"
            + "Besides the cap color and the color of the walls the height of"
            + "the building (extrusion) can be defined.\n\n"
            + "An example is available at:\n"
            + "http://sudplan.kl.dfki.de/testdata/Buildings.zip";
    /**
     * The number of polygons to add to one {@link Layer}.
     */
    private static final int NUM_POLYGONS_PER_LAYER = 10000;
    /**
     * The default height of the buildings.
     */
    private static final Number DEFAULT_HEIGHT = 1.0;
    /**
     * The default color of the building's cap.
     */
    private static final Color DEFAULT_CAP_COLOR = Color.BLACK;
    /**
     * The default color of the building's walls.
     */
    private static final Color DEFAULT_SIDE_COLOR = Color.GRAY;
    /**
     * The {@link NumberParameter} to change the height of the building.
     */
    private NumberParameter parHeight;
    /**
     * The {@link ColorParameter} to change the color of the cap.
     */
    private ColorParameter parCapColor;
    /**
     * The {@link ColorParameter} to change the color of the wall.
     */
    private ColorParameter parSideColor;
    /**
     * A random unique identifier for an instance of {@link VisBuildings}.
     */
    private final UUID uuid;

    /**
     * Constructor
     */
    protected VisBuildings() {
        super("Buildings", VisBuildings.DESCRIPTION,
                new ImageIcon(VisExtrudePolygon.class.getClassLoader().
                getResource("icons/VisBuildings.png")));

        uuid = UUID.randomUUID();

        this.parHeight = new NumberParameter("Height of building [m]");
        this.parHeight.addTransferFunction(IdentityFunction.class.getName());
        this.parHeight.addTransferFunction(ScalarMultiplication.class.getName());
        this.parHeight.addTransferFunction(ConstantNumber.class.getName());
        addVisParameter(this.parHeight);

        this.parCapColor = new ColorParameter("Color of roof");
        this.parCapColor.addTransferFunction(ConstantColor.class.getName());
        this.parCapColor.addTransferFunction(RedGreenColorrampClassification.class.getName());
        this.parCapColor.addTransferFunction(ColorrampClassification.class.getName());
        this.parCapColor.addTransferFunction(ColorrampCategorization.class.getName());
        addVisParameter(this.parCapColor);

        this.parSideColor = new ColorParameter("Color of walls");
        this.parSideColor.addTransferFunction(ConstantColor.class.getName());
        addVisParameter(this.parSideColor);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {

        log.debug("Running {}", this.getClass().getSimpleName());
        setProgress(0);
        List<Layer> layers = new ArrayList<>();
        String attribute0 = IVisAlgorithm.NO_ATTRIBUTE;
        String attribute1 = IVisAlgorithm.NO_ATTRIBUTE;
        String attribute2 = IVisAlgorithm.NO_ATTRIBUTE;
        ISource iSource;

        // 0 - Check data
        if (data == null) {
            String msg = "data == null";
            log.error(msg);
            return null;
        }

        if (!(data instanceof ISource)) {
            log.error("Data type {} not supported for {}.",
                    data.getClass().getSimpleName(), this.getName());
            return null;
        } else {
            iSource = (ISource) data;
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
        function0.preprocess(iSource, attribute0);
        setProgress(15);

        ITransferFunction function1 = parCapColor.getTransferFunction();
        log.debug("Using transfer function {} for attribute.", function1.getClass().getSimpleName());
        function1.preprocess(iSource, attribute1);
        setProgress(20);

        ITransferFunction function2 = parSideColor.getTransferFunction();
        log.debug("Using transfer function {} for attribute.", function2.getClass().getSimpleName());
        function2.preprocess(iSource, attribute2);
        setProgress(25);

        // 3 - Create visualization
        createRenderablesForPolygons(iSource, attribute0, attribute1, attribute2, layers);
        setProgress(95);

        // 4 - Create legends for visualization (if available)
        List<Layer> legends = createLegends(iSource.getName() + " (" + uuid.toString() + ")");
        layers.addAll(legends);

        setProgress(100);
        log.debug("Finished {}", this.getClass().getSimpleName());

        return layers;
    }

    /**
     * Creates renderables for all the polygons in the shapefile. Polygon is the
     * only shape that potentially returns a more than one layer, which it does
     * when the polygons per layer limit is exceeded.
     *
     * @param source the shapefile to read
     * @param layers a list in which to place the layers created. May not be
     * null.
     */
    private void createRenderablesForPolygons(ISource source, String attribute0,
            String attribute1, String attribute2, List<Layer> layers) {

        RenderableLayer layer = new RenderableLayer();
        int numLayers = 0;
        layer.setName(source.getName() + "-" + numLayers);
        layers.add(layer);

        for (int i = 0; i < source.getFeatureCount(); i++) {

            createExtrudedPolygon(source, i, attribute0, attribute1, attribute2, layer);
            setProgress(25 + (int) (75 * i / (double) source.getFeatureCount()));

            if (layer.getNumRenderables() > NUM_POLYGONS_PER_LAYER) {
                layer = new RenderableLayer();
                layer.setName(source.getName() + "-" + ++numLayers + " (" + uuid.toString() + ")");
                layer.setEnabled(false);
                layers.add(layer);
            }
        }
    }

    /**
     *
     * @param source
     * @param featureId
     * @param attr0
     * @param attr1
     * @param attr2
     * @param layer
     */
    private void createExtrudedPolygon(ISource source, int featureId,
            String attr0, String attr1, String attr2, RenderableLayer layer) {

        //
        // Use the transfer function for parameter HEIGHT
        //
        Object object0 = source.getValue(featureId, attr0);
        ITransferFunction tfHeight = parHeight.getTransferFunction();
        Number result = (Number) tfHeight.calc(object0);
        if (result == null) {
            result = DEFAULT_HEIGHT;
        }

        double dResult = result.doubleValue();
        if (dResult <= 0) {
            log.error("The input value for ExtrudedPolygon < 0."
                    + "Setting value to 0.01.");
            dResult = 0.01;
        }

        //
        // Use the transfer function for parameter CAP COLOR
        //
        Object object1 = source.getValue(featureId, attr1);
        ITransferFunction tfCapColor = parCapColor.getTransferFunction();
        Color capColor = (Color) tfCapColor.calc(object1);
        if (capColor == null) {
            capColor = DEFAULT_CAP_COLOR;
        }

        Material capMaterial = new Material(capColor);
        BasicShapeAttributes attrCap = new BasicShapeAttributes();
        attrCap.setDrawOutline(false);
        attrCap.setInteriorOpacity(1.0);
        attrCap.setInteriorMaterial(capMaterial);

        //
        // Use the transfer function for parameter WALL COLOR
        //
        Object object2 = source.getValue(featureId, attr2);
        ITransferFunction tfSideColor = parSideColor.getTransferFunction();
        Color sideColor = (Color) tfSideColor.calc(object2);
        if (sideColor == null) {
            sideColor = DEFAULT_SIDE_COLOR;
        }

        Material sideMaterial = new Material(sideColor);
        BasicShapeAttributes attrSide = new BasicShapeAttributes();
        attrSide.setDrawOutline(true);
        attrSide.setInteriorOpacity(1.0);
        attrSide.setInteriorMaterial(sideMaterial);

        //
        // Putting all together
        //
        ExtrudedPolygon ep = new ExtrudedPolygon();
        ep.setHeight(dResult);
        ep.setCapAttributes(attrCap);
        ep.setSideAttributes(attrSide);
        layer.addRenderable(ep);

        List<List<double[]>> list = source.getPoints(featureId);
        for (int i = 0; i < list.size(); i++) {
            List<Position> positionList = new ArrayList<>();

            for (double[] point : list.get(i)) {
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
