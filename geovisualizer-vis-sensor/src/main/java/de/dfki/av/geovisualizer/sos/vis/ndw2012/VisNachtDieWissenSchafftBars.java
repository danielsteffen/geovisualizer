/*
 *  VisNachtDieWissenSchafft.java 
 *
 *  Created by DFKI AV on 30.08.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.sos.vis.ndw2012;

import de.dfki.av.geovisualizer.core.*;
import de.dfki.av.geovisualizer.core.functions.*;
import de.dfki.av.geovisualizer.io.sos.util.XMLUtils;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.opengl.GL;
import javax.swing.ImageIcon;

/**
 * Visualization for the event "Nacht, die Wissen schafft 2012".
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisNachtDieWissenSchafftBars extends VisAlgorithmAbstract {

    /**
     * The description for the {@link VisNachtDieWissenSchafft} algorithm.
     */
    public static final String DESCRIPTION = "Nacht die Wissenschaft Animated "
            + "Scrolling 3D Bar Visualization. "
            + "Data Visualization for static positions.";
    /**
     * The {@link NumberParameter} to change the height of the spheres.
     */
    private NumberParameter parHeight;
    /**
     * The {@link ColorParameter} to change the color of the spheres.
     */
    private ColorParameter parColor;
    private NumberParameter parMerge;

    /**
     * Constructor
     */
    public VisNachtDieWissenSchafftBars() {
        super("Nacht die Wissenschaft 2012 - Animated Scrolling Bars",
                VisNachtDieWissenSchafftBars.DESCRIPTION,
                new ImageIcon(VisNachtDieWissenSchafftBars.class.getClassLoader().
                getResource("icons/VisNachtDieWissenSchafftBars.png")));

        parHeight = new NumberParameter("Height of Bars [m]");
        parHeight.addTransferFunction(UnitIntervalMapping.class.getName());
        parHeight.addTransferFunction(UnitIntervalMappingSpecial.class.getName());
        parHeight.addTransferFunction(AffineTransformation.class.getName());
        parHeight.addTransferFunction(IdentityFunction.class.getName());
        addVisParameter(this.parHeight);

        parColor = new ColorParameter("Color of Bar Faces");
        parColor.addTransferFunction(ColorrampClassification.class.getName());
        addVisParameter(this.parColor);

        parMerge = new NumberParameter("Amount of values to merge");
        parMerge.addTransferFunction(ConstantNumber.class.getName());
        addVisParameter(this.parMerge);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {
        log.debug("Running {}", this.getClass().getSimpleName());

        List<Layer> layers = new ArrayList<>();
        String attribute0 = IVisAlgorithm.NO_ATTRIBUTE;
        String attribute1 = IVisAlgorithm.NO_ATTRIBUTE;
        String attribute2 = IVisAlgorithm.NO_ATTRIBUTE;
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
        } else if (attributes.length == 2) {
            log.warn("Only using two attributes at the moment.");
            attribute0 = checkAttribute(attributes[0]);
            attribute1 = checkAttribute(attributes[1]);
        } else if (attributes.length >= 3) {
            log.warn("Using three attributes at the moment.");
            attribute0 = checkAttribute(attributes[0]);
            attribute1 = checkAttribute(attributes[1]);
            attribute2 = checkAttribute(attributes[2]);
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

        ITransferFunction function2 = parMerge.getTransferFunction();
        function2.preprocess(iSource, attribute2);
        setProgress(40);

        // 3 - Create visualization
        RenderableLayer layer = createPointLayer(iSource, attribute0,
                attribute1, attribute2);
        layers.add(layer);
        setProgress(95);

//        // 4 - Create legends for visualization (if available)
//        List<Layer> legends = createLegends(shapefile.getLayerName());
//        layers.addAll(legends);
        setProgress(100);

        log.debug("Finished {}", this.getClass().getSimpleName());

        return layers;
    }

    /**
     * Creates a {@link RenderableLayer} with all available points from the
     * {@link ISource}. The method produces the points using
     * {@link GL#GL_POINTS}.
     *
     * @param source the {@link ISource} as data source
     * @param attributes the attributes to use for the visualization
     * @return the {@link RenderableLayer} with the points.
     */
    private RenderableLayer createPointLayer(ISource source, String... attributes) {

        RenderableLayer layer = new RenderableLayer();
        layer.setName(source.getName());

        ITransferFunction tfHeight = parHeight.getTransferFunction();
        ITransferFunction tfColor = parColor.getTransferFunction();
        ITransferFunction tfMerge = parMerge.getTransferFunction();
        AnimatedBars bars = new AnimatedBars(layer);
        Object object2 = source.getValue(0, attributes[2]);
        Number merge = (Number) tfMerge.calc(object2);
        if (merge == null) {
            log.debug("Using default merge value.");
            merge = 1;
        }
        if (merge.intValue() > source.getFeatureCount()) {
            log.debug("Using default merge value.");
            merge = 1;
        }
        int count = source.getFeatureCount() / merge.intValue();
        int[] indices = new int[count];
        String name = source.getName();
        setProgress(45);
        Map<Integer, String> eventList = new HashMap<>();
        if (name.equals("Stadion_Ben")) {
            eventList.put(0, "Spielbegin");
            eventList.put(1460, "Tor (Kaiserslautern)");
            eventList.put(2641, "Abpfiff 1. Halbzeit");
            eventList.put(3421, "Anpfiff 2. Halbzeit");
            eventList.put(5254, "Ausgleich (Braunschweig)");
            eventList.put(6121, "Rote Karte (Kaiserslautern)");
            eventList.put(6241, "Abpfiff (1:1)");
        }
        for (int i = 0; i < count; i++) {
            double maxValue = 0.0d;
            int start = i * merge.intValue();
            int end = start + merge.intValue();
            if (!(end < source.getFeatureCount())) {
                end = source.getFeatureCount();
            }
            for (int j = start; j < end; j++) {
                Object object0 = source.getValue(j, attributes[0]);
                Number height = (Number) tfHeight.calc(object0);
                if (height == null) {
                    log.debug("Using default height.");
                    height = 1.0d;
                }
                if (height.doubleValue() > maxValue) {
                    maxValue = height.doubleValue();
                    indices[i] = j;
                }
            }
            setProgress(45 + (int) ((35.f / (float) count) * i));
        }
        for (int i : indices) {
            StringBuilder event = new StringBuilder("");
            for (int eventIndex : eventList.keySet()) {
                if (i < eventIndex + 90 && i > eventIndex - 90) {
                    if (event.length() > 0) {
                        event.append(" & ");
                    }
                    event.append(eventList.get(eventIndex));
                }
            }
            List<List<double[]>> list = source.getPoints(i);
            Object object0 = source.getValue(i, attributes[0]);
            Object object1 = source.getValue(i, attributes[1]);

            Number height = (Number) tfHeight.calc(object0);
            if (height == null) {
                log.debug("Using default height.");
                height = 1.0d;
            }
            Color colorFaces = (Color) tfColor.calc(object1);
            if (colorFaces == null) {
                log.debug("Using default color.");
                colorFaces = new Color(1.0f, 1.0f, 1.0f, 0.0f);
            }
            for (int j = 0; j < list.size(); j++) {
                setProgress((int) (100 * j / (float) list.size()));
                for (double[] point : list.get(j)) {
                    Position pos = Position.fromDegrees(point[1], point[0], height.doubleValue());
                    bars.add(pos,
                            String.valueOf(source.getValue(i, XMLUtils.NAME_TIME)),
                            event.toString(), height.floatValue(), colorFaces);
                }
            }
        }
        if (bars.size() > 0) {
            if (name.equals("Stadion_Ben")) {
                // Annotations over the "West-Tribuehne"
                LatLon latLon = new LatLon(Angle.fromDegrees(49.4346d), Angle.fromDegrees(7.77535d));
                double elevation = 55;
                bars.setAnnotationPosition(new Position(latLon, elevation));
                // Move bars origin
                bars.setBarsOrigin(new LatLon(Angle.fromDegrees(49.43415d),
                        Angle.fromDegrees(7.7760d)));
                bars.setTransparency(false);
            } else {
                bars.setTransparency(false);
            }
            initAnimation(layer, bars);
            log.debug("Adding {} points.", bars.size());
            layer.addRenderable(bars);
        } else {
            log.debug("No points added. Point list is empty.");
        }

        return layer;
    }

    /**
     * Initialize animation.
     */
    private void initAnimation(final RenderableLayer layer, final AnimatedBars bars) {
        Thread updater = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (bars.animationCompleted()) {
                        bars.resetAnimation();
                    }
                    layer.firePropertyChange(AVKey.LAYER, null, null);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        log.error("", ex);
                    }
                }
            }
        });
        updater.start();
    }
}
