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
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import javax.swing.ImageIcon;

/**
 * Visualization for the event "Nacht, die Wissen schafft 2012".
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisNachtDieWissenSchafft extends VisAlgorithmAbstract {

    /**
     * The description for the {@link VisNachtDieWissenSchafft} algorithm.
     */
    public static final String DESCRIPTION = "Nacht die Wissenschaft Animated "
            + "3D Graph Visualization. "
            + "Visualizes data with dynamical positions.";
    /**
     * The {@link NumberParameter} to change the height of the spheres.
     */
    private NumberParameter parHeight;
    /**
     * The {@link ColorParameter} to change the color of the spheres.
     */
    private ColorParameter parColor;
    /**
     * The {@link NumberParameter} used for the stress factor.
     */
    private final NumberParameter parTop;

    /**
     * Constructor
     */
    public VisNachtDieWissenSchafft() {
        super("Nacht die Wissenschaft 2012 - Animated Bars",
                VisNachtDieWissenSchafft.DESCRIPTION,
                new ImageIcon(VisNachtDieWissenSchafft.class.getClassLoader().
                getResource("icons/VisNachtDieWissenSchafft.png")));

        parHeight = new NumberParameter("Height of Bars [m]");
        parHeight.addTransferFunction(UnitIntervalMappingSpecial.class.getName());
        parHeight.addTransferFunction(AffineTransformation.class.getName());
        parHeight.addTransferFunction(UnitIntervalMapping.class.getName());
        parHeight.addTransferFunction(IdentityFunction.class.getName());
        addVisParameter(this.parHeight);

        parColor = new ColorParameter("Color of Bar Faces");
        parColor.addTransferFunction(ColorrampClassification.class.getName());
        addVisParameter(this.parColor);

        parTop = new NumberParameter("Stress");
        parTop.addTransferFunction(IdentityFunction.class.getName());
        addVisParameter(this.parTop);
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
        } else if (attributes.length == 1) {
            log.warn("Only using two attributes at the moment.");
            attribute0 = checkAttribute(attributes[0]);
            attribute1 = checkAttribute(attributes[1]);
        } else if (attributes.length >= 2) {
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

        ITransferFunction function2 = parTop.getTransferFunction();
        function2.preprocess(iSource, attribute2);
        setProgress(40);

        // 3 - Create visualization
        RenderableLayer layer = createPointLayer(iSource, attribute0,
                attribute1, attribute2);
        layers.add(layer);
        setProgress(95);

        // 4 - Create legends for visualization (if available)
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
        ITransferFunction tfTop = parTop.getTransferFunction();
        AnimatedPolygons bars = new AnimatedPolygons(layer);

        for (int i = 0; i < source.getFeatureCount(); i++) {
            List<List<double[]>> list = source.getPoints(i);
            Object object0 = source.getValue(i, attributes[0]);
            Object object1 = source.getValue(i, attributes[1]);
            Object object2 = source.getValue(i, attributes[2]);

            Number height = (Number) tfHeight.calc(object0);
            if (height == null) {
                log.debug("Using default height.");
                height = Double.valueOf(1.0d);
            }
            Color colorFaces = (Color) tfColor.calc(object1);
            if (colorFaces == null) {
                log.debug("Using default color.");
                colorFaces = new Color(1.0f, 1.0f, 1.0f, 0.0f);
            }
            Number stress = (Number) tfTop.calc(object2);
            if (stress == null) {
                log.debug("Using default Stress idendicator.");
                stress = 0;
            }

            for (int j = 0; j < list.size(); j++) {
                setProgress((int) (100 * j / (float) list.size()));
                for (double[] point : list.get(j)) {
                    Position pos = Position.fromDegrees(point[1], point[0], height.doubleValue());
                    bars.add(pos, String.valueOf(source.getValue(i, XMLUtils.NAME_TIME)), height.floatValue(), stress, colorFaces);
                }
            }
        }
        if (bars.size() > 0) {
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
    private void initAnimation(final RenderableLayer layer, final AnimatedPolygons points) {

        Thread updater = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (points.animationCompleted()) {
                        points.resetAnimation();
                    }
                    layer.firePropertyChange(AVKey.LAYER, null, null);
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ex) {
                        log.error("", ex);
                    }
                }
            }
        });
        updater.start();
    }
}
