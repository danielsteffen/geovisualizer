/*
 *  VisSOS.java 
 *
 *  Created by DFKI AV on 01.02.2013.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.sos.vis;

import de.dfki.av.geovisualizer.core.*;
import de.dfki.av.geovisualizer.core.functions.*;
import de.dfki.av.geovisualizer.io.sos.SOS;
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
 * {@link VisAlgorithmAbstract} class to create a {@link SOSAnimation}
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class VisSOS extends VisAlgorithmAbstract {

    /**
     * The description for the {@link VisSOS} algorithm.
     */
    public static final String DESCRIPTION = "SOS Animation";
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
    public VisSOS() {
        super("SOS Animation",
                VisSOS.DESCRIPTION,
                new ImageIcon(VisSOS.class.getClassLoader().
                getResource("icons/VisSOS.png")));

        parHeight = new NumberParameter("Height of Bars [m]");
        parHeight.addTransferFunction(AffineTransformation.class.getName());
        addVisParameter(this.parHeight);

        parColor = new ColorParameter("Color of Bar Faces");
        parColor.addTransferFunction(VisSOSFunction.class.getName());
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
        List<Layer> legends = createLegends(iSource.getName());
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
        SOSAnimation bars = new SOSAnimation(layer);

        for (int i = 0; i < source.getFeatureCount(); i++) {
            List<List<double[]>> list = source.getPoints(i);
            Object object0 = source.getValue(i, attributes[0]);
            Object object1 = source.getValue(i, attributes[1]);
            Object object2 = source.getValue(i, attributes[2]);

            Number height = (Number) tfHeight.calc(object0);
            if (height == null) {
                log.debug("Using default height.");
                height = new Double(1.0);
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
            initAnimation(source, layer, bars, tfHeight, tfColor, tfTop,
                    attributes);
            log.debug("Adding {} points.", bars.size());
            layer.addRenderable(bars);
        } else {
            log.debug("No points added. Point list is empty.");
            initAnimation(source, layer, bars, tfHeight, tfColor, tfTop,
                    attributes);
            log.debug("Adding {} points.", bars.size());
            layer.addRenderable(bars);
        }

        return layer;
    }

    /**
     * Initialize animation.
     */
    private void initAnimation(final ISource source,
            final RenderableLayer layer,
            final SOSAnimation sosAnimation, final ITransferFunction tfHeight,
            final ITransferFunction tfColor, final ITransferFunction tfTop,
            final String[] attributes) {

        Thread updater = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (source instanceof SOS) {
                        if (((SOS) source).update()) {
                            log.debug("Update values.");
                            double[] point = (double[]) source
                                    .getValue(-1, "position");
                            if (point != null) {
                                Object object0 = source.getValue(-1, attributes[0]);
                                Object object1 = source.getValue(-1, attributes[1]);
                                Object object2 = source.getValue(-1, attributes[2]);
                                Number height = (Number) tfHeight.calc(object0);

                                Color colorFaces = (Color) tfColor.calc(object1);
                                if (colorFaces == null) {
                                    log.debug("Using default color.");
                                    colorFaces = new Color(1.0f, 1.0f, 1.0f, 0.0f);
                                }
                                Number stress = (Number) tfTop.calc(object2);
                                Position pos = Position.fromDegrees(point[1], point[0],
                                        height.doubleValue());
                                sosAnimation.add(pos,
                                        String.valueOf(source.getValue(-1, XMLUtils.NAME_TIME)),
                                        height.floatValue(), stress, colorFaces);
                                log.debug("Added point");
                            }
                        }
                    }

                    layer.firePropertyChange(AVKey.LAYER, null, null);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        log.error("", ex);
                    }
                }
            }
        });
        updater.start();
    }
}
