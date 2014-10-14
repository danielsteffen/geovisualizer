/*
 * VisPins.java
 *
 * Created by DFKI AV on 09.09.2013.
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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.media.opengl.GL;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 * The pins visualization. Useful for large data sets. Additionally, to the
 * point cloud visualization the height of the points the distance to the ground
 * is drawn as a solid line. The {@link RenderableLayer} uses
 * {@link GL#GL_POINTS} and {@link GL#GL_LINES} for the rendering.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisPins extends VisAlgorithmAbstract {

    /**
     * The description for the {@link VisPointCloudNew} algorithm.
     */
    public static final String DESCRIPTION = "The pins visualization"
            + " for visualizing large set of data points. The "
            + "height as well as the color of the individual points can be "
            + "configured. The distance from point to the ground is "
            + "drawn as a solid line.\n"
            + "In case the geometry of a feature is not a point the "
            + "arithmetic mean of all points of that geometry is being"
            + "calculated, e.g. for a triangle the average of three points.";
    /**
     * Default height if something goes wrong.
     */
    private static final Double DEFAULT_HEIGHT = new Double(100.0);
    /**
     * Default color if something goes wrong.
     */
    private static final Color DEFAULT_COLOR = Color.RED;
    /**
     * Calculation of arithmetic mean is enabled / disabled. This attribute is
     * used in case the geometry of a feature is not a point.
     */
    private static final boolean ARITHMETIC_MEAN_ENABLED = true;
    /**
     * The {@link NumberParameter} to change the height of the spheres.
     */
    private NumberParameter parHeight;
    /**
     * The {@link ColorParameter} to change the color of the spheres.
     */
    private ColorParameter parColor;
    /**
     * A random unique identifier for an instance of {@link VisPins}.
     */
    private final UUID uuid;

    /**
     * Constructor
     */
    protected VisPins() {
        super("Pins visualization", VisPins.DESCRIPTION,
                new ImageIcon(VisExtrudePolygon.class.getClassLoader().
                getResource("icons/VisPins.png")));

        uuid = UUID.randomUUID();

        parHeight = new NumberParameter("Height of Pin [m]");
        parHeight.addTransferFunction(ConstantNumber.class.getName());
        parHeight.addTransferFunction(IdentityFunction.class.getName());
        parHeight.addTransferFunction(ScalarMultiplication.class.getName());
        addVisParameter(this.parHeight);

        parColor = new ColorParameter("Color of Pin Head");
        parColor.addTransferFunction(ConstantColor.class.getName());
        parColor.addTransferFunction(ColorrampClassification.class.getName());
        parColor.addTransferFunction(ColorrampCategorization.class.getName());
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
        RenderableLayer layer = createPinsLayer(iSource, attribute0, attribute1);
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
     * Creates a {@link RenderableLayer} with all available pins from the
     * {@link ISource}. The method produces the pins using {@link GL#GL_POINTS}
     * and {@link GL#GL_LINES}.
     *
     * @param iSource the {@link ISource} as data source
     * @param attributes the attributes to use for the visualization
     * @return the {@link RenderableLayer} with the points.
     */
    private RenderableLayer createPinsLayer(ISource iSource, String... attributes) {

        RenderableLayer layer = new RenderableLayer();

        if (iSource != null) {
            layer.setName(iSource.getName() + " (" + uuid.toString() + ")");
        } else {
            layer.setName("Error - " + iSource.getName());
            log.error("iSource == null");
            return layer;
        }

        ITransferFunction tfHeight = parHeight.getTransferFunction();
        if (tfHeight == null) {
            String msg = "tfHeight == null";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        ITransferFunction tfColor = parColor.getTransferFunction();
        if (tfHeight == null) {
            String msg = "tfColor == null";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        Pins pins = new Pins();

        for (int i = 0; i < iSource.getFeatureCount(); i++) {

            setProgress((int) (100 * i / (float) iSource.getFeatureCount()));

            List<List<double[]>> list = iSource.getPoints(i);
            if (list == null) {
                String msg = "list == null";
                log.error(msg);
                throw new RuntimeException(msg);
            }

            Object object0 = iSource.getValue(i, attributes[0]);
            Number height = (Number) tfHeight.calc(object0);
            if (height == null) {
                log.warn("height == null; using default");
                height = DEFAULT_HEIGHT;
            }

            Object object1 = iSource.getValue(i, attributes[1]);
            Color color = (Color) tfColor.calc(object1);
            if (color == null) {
                log.warn("color == null; using default");
                color = DEFAULT_COLOR;
            }

            log.debug("Number of geometries per feature: {}", list.size());



            for (int j = 0; j < list.size(); j++) {

                List<double[]> featurePoints = list.get(j);
                log.debug("# feature points: {}", featurePoints.size());

                if (ARITHMETIC_MEAN_ENABLED) {

                    // Add one one pin per feature. The position of the pin is
                    // calculate with the arithmetic mean.
                    double[] mean = calcGeometricMean(featurePoints);
                    Position pos = Position.fromDegrees(mean[1], mean[0],
                            height.doubleValue());
                    pins.add(pos, color);
                } else {
                    for (int t = 0; t < featurePoints.size(); t++) {
                        double point[] = featurePoints.get(t);
                        Position pos = Position.fromDegrees(point[1], point[0],
                                height.doubleValue());
                        pins.add(pos, color);
                    }
                }
            }
        }

        if (pins.size() > 0) {
            log.debug("Adding {} pins.", pins.size());
            initAnimation(layer, pins);
            layer.addRenderable(pins);
        } else {
            log.debug("No pins added. Point list is empty.");
        }

        return layer;
    }

    /**
     * Calculates the arithmetic mean of the {@code points}.
     *
     * @param points the list of points.
     * @return the arithmetic mean to calculate.
     */
    private double[] calcArithmeticMean(List<double[]> points) {

        if (points.size() < 1) {
            String msg = "Size of points < 1.";
            throw new RuntimeException(msg);
        }

        double[] mean = new double[2];
        mean[1] = 0.0;
        mean[0] = 0.0;

        for (int t = 0; t < points.size(); t++) {
            double point[] = points.get(t);
            mean[0] += point[0];
            mean[1] += point[1];
        }
        mean[0] = mean[0] / points.size();
        mean[1] = mean[1] / points.size();

        return mean;
    }

    /**
     * Calculates the geometric mean of the {@code points}.
     *
     * @param points the list of points.
     * @return the arithmetic mean to calculate.
     */
    private double[] calcGeometricMean(List<double[]> points) {

        if (points.size() < 1) {
            String msg = "Size of points < 1.";
            throw new RuntimeException(msg);
        }

        double[] mean = new double[2];
        mean[1] = 1.0;
        mean[0] = 1.0;

        for (int t = 0; t < points.size(); t++) {
            double point[] = points.get(t);
            double x_t = point[0] + 180;
            double y_t = point[1] + 90;
            mean[0] *= x_t;
            mean[1] *= y_t;
        }

        double root = 1.0 / (double) points.size();
        mean[0] = Math.pow(mean[0], root);
        mean[1] = Math.pow(mean[1], root);

        mean[0] -= 180;
        mean[1] -= 90;

        return mean;
    }

    /**
     * Initialize animation.
     */
    private void initAnimation(final RenderableLayer layer, final Pins pins) {
        Timer timer;
        timer = new Timer(10000, new AnimationListener(layer, pins));
        timer.start();
    }

    /**
     * {@link ActionListener} for the VisPoleAnimation.
     */
    static class AnimationListener implements ActionListener {

        final RenderableLayer layer;
        final Pins pins;

        public AnimationListener(final RenderableLayer layer, final Pins pins) {
            this.layer = layer;
            this.pins = pins;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            pins.update();
            layer.firePropertyChange(AVKey.LAYER, null, null);
        }
    }
}
