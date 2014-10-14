/*
 * VisExtrudePolyline.java
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.vis.basic;

import de.dfki.av.geovisualizer.core.*;
import de.dfki.av.geovisualizer.core.functions.*;
import de.dfki.av.geovisualizer.core.io.GeometryType;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.ImageIcon;

/**
 * This visualization technique enables to visualize a shapefile of type
 * Polyline. Two attributes have to be defined for correct usage.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisExtrudePolyline extends VisAlgorithmAbstract {

    /**
     * The description for the {@link VisExtrudePolyline} algorithm.
     */
    public static final String DESCRIPTION = "You have to select 2 attributes "
            + "for this visualization technique.\n\nThe Polyline technique"
            + "maps one scalar attribute 'a' of your data source to the "
            + "parameter color. The second attirbute 'b' is used for the "
            + "extrution.\n\nAn example is available at:\n"
            + "http://sudplan.kl.dfki.de/testdata/AirQualityStreetLevel.zip";
    /**
     * The default height of the extruded polyline.
     */
    private static final Double DEFAULT_HEIGHT = 1.0;
    /*
     * The default color of the extruded polyline.
     */
    private static final Color DEFAULT_COLOR = new Color(1.0f, 1.0f, 1.0f, 0.0f);
    /**
     * The surface color of the extruded polyline.
     */
    private ColorParameter parColor;
    /**
     * The extrusion height of the polyline.
     */
    private NumberParameter parHeight;
    /**
     * A random unique identifier for an instance of {@link VisExtrudePolyline}.
     */
    private final UUID uuid;

    /**
     * Creates an polyline extrusion algorithm.
     */
    protected VisExtrudePolyline() {
        super("Extrude Polylines",
                VisExtrudePolyline.DESCRIPTION,
                new ImageIcon(VisExtrudePolyline.class.getClassLoader().
                getResource("icons/VisExtrudePolyline.png")));

        uuid = UUID.randomUUID();

        this.parColor = new ColorParameter("Color of surface");
        this.parColor.addTransferFunction(ConstantColor.class.getName());
        this.parColor.addTransferFunction(RedGreenColorrampClassification.class.getName());
        this.parColor.addTransferFunction(ColorrampCategorization.class.getName());
        addVisParameter(parColor);

        this.parHeight = new NumberParameter("Extrusion of line [m]");
        this.parHeight.addTransferFunction(IdentityFunction.class.getName());
        this.parHeight.addTransferFunction(ScalarMultiplication.class.getName());
        this.parHeight.addTransferFunction(ConstantNumber.class.getName());
        addVisParameter(parHeight);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {

        log.debug("Running {}", this.getClass().getSimpleName());
        setProgress(0);
        List<Layer> layers = new ArrayList<>();
        String attribute0 = IVisAlgorithm.NO_ATTRIBUTE;
        String attribute1 = IVisAlgorithm.NO_ATTRIBUTE;
        ISource iSource;

        // 0 - Check data
        if (!(data instanceof ISource)) {
            log.error("Data type {} not supported for {}.",
                    data.getClass().getSimpleName(), this.getName());
            return layers;
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
        }
        log.debug("Using {} and {} as attributes.", attribute0, attribute1);
        setProgress(10);

        // 2 - Pre-processing data
        ITransferFunction function0 = parColor.getTransferFunction();
        log.debug("Using transfer function {} for attribute 0.", function0.getClass().getSimpleName());
        function0.preprocess(iSource, attribute0);
        setProgress(15);

        ITransferFunction function1 = parHeight.getTransferFunction();
        log.debug("Using transfer function {} for attribute 1.", function1.getClass().getSimpleName());
        function1.preprocess(iSource, attribute1);
        setProgress(20);

        // 3 - Create visualization
        if (iSource.getGeometryType().equals(GeometryType.POLYLINE)) {
            RenderableLayer layer = new RenderableLayer();
            for (int i = 0; i < iSource.getFeatureCount(); i++) {
                Renderable r = createExtrudedPolyline(iSource, i, attribute0, attribute1);
                layer.addRenderable(r);
            }
            layer.setName(iSource.getName() + " (" + uuid.toString() + ")");
            layers.add(layer);
        } else {
            log.warn("Extrude Polyline Visualization does not support shape "
                    + "type {}.", iSource.getGeometryType());
        }

        setProgress(95);

        // 4 - Create legends for visualization (if available)
        List<Layer> legends = createLegends(iSource.getName());
        layers.addAll(legends);

        setProgress(100);

        log.debug("Finished {}", this.getClass().getSimpleName());
        return layers;
    }

    /**
     * Creates an extruded surface for the polyline feature {@code featureId}
     * using the {@code attribute0} for the color of the surface and
     * {@code attribute1} for the extrusion.
     *
     * @param source the {@link ISource} to use
     * @param featureId the feature of the {@link ISource}
     * @param attribute0 the attribute to use for the color
     * @param attribute1 the attribute to use for the height
     * @return the created {@link Renderable}
     */
    private Renderable createExtrudedPolyline(ISource source, int featureId,
            String attribute0, String attribute1) {

        //
        // Use the transfer function for parameter COLOR
        //
        Object object0 = source.getValue(featureId, attribute0);
        ITransferFunction function0 = parColor.getTransferFunction();
        Color c = (Color) function0.calc(object0);
        if (c == null) {
            c = DEFAULT_COLOR;
        }

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
        Object object1 = source.getValue(featureId, attribute1);
        ITransferFunction function1 = parHeight.getTransferFunction();
        Number value1 = (Number) function1.calc(object1);
        if (value1 == null) {
            value1 = DEFAULT_HEIGHT;
        }

        List<Position> positionList = new ArrayList<>();
        List<List<double[]>> list = source.getPoints(featureId);
        for (int i = 0; i < list.size(); i++) {
            for (double[] point : list.get(i)) {
                // ... swap geo positions ???! why
                positionList.add(Position.fromDegrees(point[1], point[0], value1.doubleValue()));
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
