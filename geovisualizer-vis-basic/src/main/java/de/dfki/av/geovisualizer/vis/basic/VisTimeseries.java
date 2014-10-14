/*
 * VisTimeseries.java
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.vis.basic;

import de.dfki.av.geovisualizer.core.*;
import de.dfki.av.geovisualizer.core.functions.ColorrampClassification;
import de.dfki.av.geovisualizer.core.functions.ConstantNumber;
import de.dfki.av.geovisualizer.core.functions.IdentityFunction;
import de.dfki.av.geovisualizer.core.functions.NO2ColorClassification;
import de.dfki.av.geovisualizer.core.io.GeometryType;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.util.WWMath;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurface;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurfaceAttributes;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 * This {@link IVisAlgorithm} produces an animated visualization for two
 * different time steps. The animation is a linear interpolation between the two
 * defined time steps. The algorithm makes use of the {@link AnalyticSurface}
 * class. Currently, this algorithm only works with the datasets of the
 * Stockholm pilot, i.e. ts_nox_2m, etc.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisTimeseries extends VisAlgorithmAbstract {

    /**
     * The description for the {@link VisTimeseries} algorithm.
     */
    private static final String DESCRIPTION = "The customized timeseries "
            + "visualization is/was a visualization for the Stockholm pilot. "
            + "The visualization can be used for data of type shapefile. "
            + "Two timesteps can be choosen. The animation shows a linear"
            + "interpolation between the choosen timesteps. The animation "
            + "takes three seconds.\n\n"
            + "Note that the features in the shapefile have to be of type "
            + "point and the grid must have a resolution of 140 x 140 points."
            + "\n\nAn example is available at:\n"
            + "http://sudplan.kl.dfki.de/testdata/ts_nox_2m.zip";
    /**
     * The width dimension of the Stockholm datasets.
     */
    private static final int WIDTH = 140;
    /**
     * The height dimension of the Stockholm datasets.
     */
    private static final int HEIGHT = 140;
    /**
     * The animation duration.
     */
    private static final Long ANIMATION_DURATION = 3000L;
    /**
     * The default height used for a grid point of the {@link AnalyticSurface}.
     */
    private static final Double DEFAULT_HEIGHT = 1.0;
    /**
     * The default color used for a grid point of the {@link AnalyticSurface}.
     */
    private static final Color DEFAULT_COLOR = new Color(1.0f, 1.0f, 1.0f, 0.0f);
    /**
     * The first time step of the animation. Beginning of the animation
     */
    private NumberParameter parTimestep0;
    /**
     * The second time step of the animation. End of the animation
     */
    private NumberParameter parTimestep1;
    /**
     * The height above ground to set for the surface.
     */
    private NumberParameter parHeight;
    /**
     * The color parameter to define the color for each grid point of the
     * {@link AnalyticSurface}.
     */
    private ColorParameter parColor;
    /**
     * A random unique identifier for an instance of {@link VisTimeseries}.
     */
    private final UUID uuid;

    /**
     * Constructor.
     */
    protected VisTimeseries() {
        super("Customized Timeseries Visualization",
                VisTimeseries.DESCRIPTION,
                new ImageIcon(VisTimeseries.class.getClassLoader().
                getResource("icons/VisTimeseries.png")));

        uuid = UUID.randomUUID();

        this.parTimestep0 = new NumberParameter("Timestep (t=0)");
        this.parTimestep0.addTransferFunction(IdentityFunction.class.getName());
        addVisParameter(parTimestep0);

        this.parTimestep1 = new NumberParameter("Timestep (t=1)");
        this.parTimestep1.addTransferFunction(IdentityFunction.class.getName());
        addVisParameter(parTimestep1);

        this.parHeight = new NumberParameter("Height of Timeseries [m]");
        this.parHeight.addTransferFunction(ConstantNumber.class.getName());
        addVisParameter(parHeight);

        this.parColor = new ColorParameter("Color definition");
        this.parColor.addTransferFunction(NO2ColorClassification.class.getName());
        this.parColor.addTransferFunction(ColorrampClassification.class.getName());
        addVisParameter(parColor);
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
            throw new IllegalArgumentException(msg);
        }

        if (!(data instanceof ISource)) {
            log.error("Data type {} not supported for {}.",
                    data.getClass().getSimpleName(), this.getName());
            setProgress(100);
            return layers;
        } else {
            iSource = (ISource) data;
        }

        setProgress(2);

        // 0.0 - Check for the customized things.
        if (iSource.getGeometryType() != GeometryType.POINT) {
            log.error("Shapefile != Shapefile.PointType");
            setProgress(100);
            return layers;
        }
        if (iSource.getFeatureCount() != VisTimeseries.WIDTH * VisTimeseries.HEIGHT) {
            log.error("Invalid dimensions.");
            setProgress(100);
            return layers;
        }

        setProgress(2);

        // 1 - Check and set all attributes
        if (attributes == null || attributes.length == 0) {
            log.error("Attributes set to null. Can not create visualization.");
            return layers;
        } else if (attributes.length == 1) {
            log.warn("Using only one attribute. Setting second attribute to first one.");
            attribute0 = checkAttribute(attributes[0]);
            attribute1 = attribute0;
        } else if (attributes.length == 2) {
            log.warn("Using only two attributes. Setting remaining attribute to default.");
            attribute0 = checkAttribute(attributes[0]);
            attribute1 = checkAttribute(attributes[1]);
        } else if (attributes.length > 3) {
            attribute0 = checkAttribute(attributes[0]);
            attribute1 = checkAttribute(attributes[1]);
            attribute2 = checkAttribute(attributes[2]);
        }
        log.debug("Using " + attribute0 + ", " + attribute1
                + ", " + attribute2 + " as attributes.");

        setProgress(5);

        // 2 - Pre-processing data
        ITransferFunction function0 = parTimestep0.getTransferFunction();
        log.debug("Using transfer function {} for attribute 0.", function0.getClass().getSimpleName());
        function0.preprocess(iSource, attribute0);
        setProgress(10);

        ITransferFunction function1 = parTimestep1.getTransferFunction();
        log.debug("Using transfer function {} for attribute 1.", function1.getClass().getSimpleName());
        function1.preprocess(iSource, attribute1);
        setProgress(15);

        ITransferFunction function2 = parHeight.getTransferFunction();
        log.debug("Using transfer function {} for attribute 2.", function2.getClass().getSimpleName());
        function2.preprocess(iSource, attribute2);
        setProgress(20);

        // 3 - Create visualization
        RenderableLayer layer = new RenderableLayer();
        createTimeSeriesSurface(layer, iSource, attribute0, attribute1);

        layer.setPickEnabled(false);
        layer.setName(iSource.getName() + " (" + uuid.toString() + ")");
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
     * Creates the {@link AnalyticSurface} for the time series animation using
     * the time steps {@code t0} and {@code t1}. Calculates a linear
     * interpolation between the timesteps.
     *
     * @param layer the {@link Layer} to add the {@link AnalyticSurface}
     * @param source the source file to be used
     * @param t0 the timestep for the start of the anmiation
     * @param t1 the timestep for the end of the animation
     */
    private void createTimeSeriesSurface(RenderableLayer layer, ISource source, String t0, String t1) {

        AnalyticSurface surface = new AnalyticSurface();
        double[] boundingBox = source.getBoundingBox();
        surface.setSector(Sector.fromDegrees(boundingBox[2], boundingBox[3],
                boundingBox[0], boundingBox[1]));
        surface.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        surface.setDimensions(VisTimeseries.WIDTH, VisTimeseries.HEIGHT);
        surface.setClientLayer(layer);
        layer.addRenderable(surface);

        setProgress(25);

        List<Double> firstValues = computeGridValues(source, t0);

        setProgress(55);

        List<Double> secondValues = computeGridValues(source, t1);

        setProgress(85);

        interpolateValuesOverTime(VisTimeseries.ANIMATION_DURATION, firstValues,
                secondValues, surface);

        AnalyticSurfaceAttributes attr = new AnalyticSurfaceAttributes();
        attr.setDrawShadow(false);
        attr.setInteriorOpacity(0.6);
        attr.setOutlineWidth(0);
        surface.setSurfaceAttributes(attr);
    }

    /**
     * Compute the grid values for the attribute {@code timestep} of the data
     * source {@code shpfile}. Returns the {@link List} of {@link Double}.
     *
     * @param source the data source
     * @param timestep the time step
     * @return the grid values to compute
     */
    private List<Double> computeGridValues(ISource source, String timestep) {
        int numValues = WIDTH * HEIGHT;

        double[] values = new double[source.getFeatureCount()];
        for (int i = 0; i < numValues; i++) {
            int index = (HEIGHT - (i / HEIGHT)) * WIDTH - (WIDTH - (i % WIDTH));
            Object object = source.getValue(index, timestep);
            if (object instanceof Number) {
                values[i] = ((Number) object).doubleValue();
            } else {
                log.debug("Value of attribute is not of type Number.");
            }
        }

        List list = new ArrayList<>(numValues);
        for (int i = 0; i < numValues; i++) {
            list.add(new Double(values[i]));
        }

        return list;
    }

    /**
     * Linear interpolation of the values in the first and second buffer. Starts
     * a {@link Timer} updated every 20 milliseconds.
     *
     * @param duration the duration of the animation
     * @param startValues the values for the first timestep
     * @param endValues the values for the last timestep.
     * @param surface the {@link AnalyticSurface} to be animated.
     */
    private void interpolateValuesOverTime(final long duration,
            final List<Double> startValues, final List<Double> endValues,
            final AnalyticSurface surface) {

        Timer timer = new Timer(20, new ActionListener() {
            protected long startTime = -1;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (this.startTime < 0) {
                    this.startTime = System.currentTimeMillis();
                }

                double t = (double) (e.getWhen() - this.startTime) / (double) duration;
                int ti = (int) Math.floor(t);

                double a = t - ti;
                if ((ti % 2) == 0) {
                    a = 1d - a;
                }

                surface.setValues(createColorGradientGridValues(a, startValues, endValues));

                if (surface.getClientLayer() != null) {
                    surface.getClientLayer().firePropertyChange(AVKey.LAYER, null, surface.getClientLayer());
                }
            }
        });
        timer.start();
    }

    /**
     * Creates a list of {@code AnalyticSurface.GridPointAttributes} from the
     * {@code list0} and {@code list1} at the given {@code time} using linear
     * interpolation.
     *
     * @param time the time elapsed
     * @param list0 the starting values
     * @param list1 the end values
     * @return a {@link List} of {@code AnalyticSurface.GridPointAttributes} to
     * return
     */
    private Iterable<? extends AnalyticSurface.GridPointAttributes> createColorGradientGridValues(
            double time, List<Double> list0, List<Double> list1) {

        ArrayList<AnalyticSurface.GridPointAttributes> attributesList;
        attributesList = new ArrayList<>();

        for (int i = 0; i < list0.size(); i++) {
            double a = list0.get(i).doubleValue();
            double b = list1.get(i).doubleValue();
            double value = WWMath.mixSmooth(time, a, b);

            ITransferFunction function0 = parHeight.getTransferFunction();
            Double height = (Double) function0.calc(null);
            if (height == null) {
                height = VisTimeseries.DEFAULT_HEIGHT;
            }

            ITransferFunction function1 = parColor.getTransferFunction();
            Color color = (Color) function1.calc(value);
            if (color == null) {
                color = VisTimeseries.DEFAULT_COLOR;
            }

            attributesList.add(AnalyticSurface.createGridPointAttributes(height, color));
        }

        return attributesList;
    }
}
