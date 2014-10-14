/*
 * VisPointCloudNew.java
 *
 * Created by DFKI AV on 30.08.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.vis.basic;

import de.dfki.av.geovisualizer.core.*;
import de.dfki.av.geovisualizer.core.functions.*;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.media.opengl.GL;
import javax.swing.ImageIcon;

/**
 * The new point cloud visualization. Useful for large data sets. Additionally,
 * to the old point cloud visualization the height of the points as well as the
 * color of the points can be configured. The {@link RenderableLayer} uses
 * {@link GL#GL_POINTS} for the rendering.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisPointCloudNew extends VisAlgorithmAbstract {

    /**
     * The description for the {@link VisPointCloudNew} algorithm.
     */
    public static final String DESCRIPTION = "The new point cloud visualization"
            + " for visualizing large set of data points. Additionally, the "
            + "height as well as the color of the individual points can be "
            + "configured.";
    /**
     * The {@link NumberParameter} to change the height of the spheres.
     */
    private NumberParameter parHeight;
    /**
     * The {@link ColorParameter} to change the color of the spheres.
     */
    private ColorParameter parColor;
    /**
     * A random unique identifier for an instance of {@link VisPointCloudNew}.
     */
    private final UUID uuid;

    /**
     * Constructor
     */
    protected VisPointCloudNew() {
        super("Point cloud visualization (new)",
                VisPointCloudNew.DESCRIPTION,
                new ImageIcon(VisPointCloudNew.class.getClassLoader().
                getResource("icons/VisPointCloudNew.png")));

        uuid = UUID.randomUUID();

        parHeight = new NumberParameter("Height of Points [m]");
        parHeight.addTransferFunction(IdentityFunction.class.getName());
        parHeight.addTransferFunction(ScalarMultiplication.class.getName());
        parHeight.addTransferFunction(ConstantNumber.class.getName());
        addVisParameter(this.parHeight);

        parColor = new ColorParameter("Color of Points");
        parColor.addTransferFunction(ColorrampClassification.class.getName());
        parColor.addTransferFunction(ColorRuleClassification.class.getName());
        parColor.addTransferFunction(ConstantColor.class.getName());

        addVisParameter(this.parColor);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {
        log.debug("Running {}", this.getClass().getSimpleName());

        List<Layer> layers = new ArrayList<>();
        String attribute0 = IVisAlgorithm.NO_ATTRIBUTE;
        String attribute1 = IVisAlgorithm.NO_ATTRIBUTE;
        ISource iSource;
        setProgress(0);

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
            log.warn("Only one attribute.");
            attribute0 = checkAttribute(attributes[0]);
        } else if (attributes.length >= 2) {
            log.warn("Only using two attributes at the moment.");
            attribute0 = checkAttribute(attributes[0]);
            attribute1 = checkAttribute(attributes[1]);
        }

        log.debug("Using attribute: {}", attributes);
        setProgress(10);

        // 2 - Pre-processing data
        ITransferFunction function0 = parHeight.getTransferFunction();
        function0.preprocess(iSource, attribute0);
        setProgress(20);

        ITransferFunction function1 = parColor.getTransferFunction();
        function1.preprocess(iSource, attribute1);
        setProgress(30);

        // 3 - Create visualization
        RenderableLayer layer = createPointLayer(iSource, attribute0, attribute1);
        layers.add(layer);
        setProgress(95);

        // 4 - Create legends for visualization (if available)
        List<Layer> legends = createLegends(iSource.getName() + " (" + uuid.toString() + ")");
        layers.addAll(legends);
        setProgress(100);

        log.debug("Finished {}", this.getClass().getSimpleName());

        return layers;
    }

    /**
     * Creates a {@link RenderableLayer} with all available points from the
     * {@link ISource}. The method produces the points using
     * {@link GL#GL_POINTS}.
     *
     * @param iSource the {@link ISource} as data source
     * @param attributes the attributes to use for the visualization
     * @return the {@link RenderableLayer} with the points.
     */
    private RenderableLayer createPointLayer(ISource iSource, String... attributes) {

        RenderableLayer layer = new RenderableLayer();
        layer.setName(iSource.getName() + " (" + uuid.toString() + ")");

        ITransferFunction tfHeight = parHeight.getTransferFunction();
        ITransferFunction tfColor = parColor.getTransferFunction();
        Points points = new Points();

        for (int i = 0; i < iSource.getFeatureCount(); i++) {

            List<List<double[]>> list = iSource.getPoints(i);
            Object object0 = iSource.getValue(i, attributes[0]);
            Number height = (Number) tfHeight.calc(object0);
            if (height == null) {
                log.debug("Using default height.");
                height = new Double(1.0);
            }

            Object object1 = iSource.getValue(i, attributes[1]);
            Color color = (Color) tfColor.calc(object1);
            if (color == null) {
                log.debug("Using default color.");
                color = new Color(1.0f, 1.0f, 1.0f, 0.0f);
            }

            for (int j = 0; j < list.size(); j++) {
                setProgress((int) (100 * j / (float) list.size()));
                for (double[] point : list.get(j)) {
                    Position pos = Position.fromDegrees(point[1], point[0], height.doubleValue());
                    points.add(pos, color);
                }
            }
        }

        if (points.size() > 0) {
            log.debug("Adding {} points.", points.size());
            layer.addRenderable(points);
        } else {
            log.debug("No points added. Point list is empty.");
        }

        return layer;
    }
}
