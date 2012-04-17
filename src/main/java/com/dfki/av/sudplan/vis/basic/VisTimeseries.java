/*
 *  VisTimeseries.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.basic;

import com.dfki.av.sudplan.vis.core.*;
import com.dfki.av.sudplan.vis.functions.ColorRuleClassification;
import com.dfki.av.sudplan.vis.functions.ColorTransferFunction;
import com.dfki.av.sudplan.vis.functions.ConstantNumber;
import com.dfki.av.sudplan.vis.functions.IdentityFunction;
import com.dfki.av.sudplan.vis.io.shapefile.Shapefile;
import com.dfki.av.sudplan.vis.render.Legend;
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
import javax.swing.ImageIcon;
import javax.swing.Timer;
import org.gdal.ogr.Feature;

/**
 *
 * @author steffen
 */
public class VisTimeseries extends VisAlgorithmAbstract {

    /*
     *
     */
    private final Double DEFAULT_HEIGHT = 1.0;
    /*
     *
     */
    private final Color DEFAULT_COLOR = new Color(1.0f, 1.0f, 1.0f, 0.0f);
    /**
     *
     */
    private NumberParameter parTimestep0;
    /**
     *
     */
    private NumberParameter parTimestep1;
    /**
     *
     */
    private NumberParameter parHeight;
    /**
     *
     */
    private ColorParameter parColor;

    /**
     *
     */
    protected VisTimeseries() {
        super("Timeseries Visualization",
                "No description available.",
                new ImageIcon(VisTimeseries.class.getClassLoader().
                getResource("icons/VisTimeseries.png")));

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
        this.parColor.addTransferFunction(ColorRuleClassification.class.getName());
        addVisParameter(parColor);
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
                + ", " + attribute2 + /*
                 * ", and " + attribute3 +
                 */ " as attributes.");
        setProgress(10);

        // 2 - Pre-processing data
        ITransferFunction function0 = parTimestep0.getTransferFunction();
        log.debug("Using transfer function {} for attribute 0.", function0.getClass().getSimpleName());
        function0.preprocess(shapefile, attribute0);
        setProgress(15);

        ITransferFunction function1 = parTimestep1.getTransferFunction();
        log.debug("Using transfer function {} for attribute 1.", function1.getClass().getSimpleName());
        function1.preprocess(shapefile, attribute1);
        setProgress(20);

        ITransferFunction function2 = parHeight.getTransferFunction();
        log.debug("Using transfer function {} for attribute 2.", function2.getClass().getSimpleName());
        function2.preprocess(shapefile, attribute2);
        setProgress(25);

        //missing function3

        // 3 - Create visualization            
        RenderableLayer layer = new RenderableLayer();
        createTimeSeriesSurface(shapefile, layer, attribute0, attribute1);

        layer.setPickEnabled(false);
        layer.setName(shapefile.getLayerName());
        layers.add(layer);

        // 4 - Create legends for visualization (if available)
        List<IVisParameter> parameterList = getVisParameters();
        for (IVisParameter iVisParameter : parameterList) {
            ITransferFunction function = iVisParameter.getTransferFunction();
            if (function instanceof ColorTransferFunction) {
                ColorTransferFunction ctf = (ColorTransferFunction) function;
                Legend legend = ctf.getLegend();
                if (legend != null) {
                    RenderableLayer rLayer = new RenderableLayer();
                    rLayer.setName(shapefile.getLayerName() + " - " + iVisParameter.getName());
                    rLayer.addRenderable(legend);
                    rLayer.setEnabled(false);
                    layers.add(rLayer);
                }
            }
        }
        setProgress(100);
        log.debug("Finished {}", this.getClass().getSimpleName());

        return layers;
    }

    /**
     *
     * @param shpfile
     * @param layer
     * @param attribute0
     * @param attribute1
     */
    private void createTimeSeriesSurface(Shapefile shpfile, RenderableLayer layer, String attribute0, String attribute1) {

        int width = 140;
        int height = 140;

        AnalyticSurface surface = new AnalyticSurface();
        double[] extend = shpfile.getExtent();
        surface.setSector(Sector.fromDegrees(extend[2], extend[3], extend[0], extend[1]));
        surface.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        surface.setDimensions(width, height);
        surface.setClientLayer(layer);
        layer.addRenderable(surface);

        List<Double> firstValues = computeGridValues(shpfile, attribute0);
        setProgress(50);
        List<Double> secondValues = computeGridValues(shpfile, attribute1);
        setProgress(75);
        interpolateValuesOverTime(3000L, firstValues, secondValues, surface);

        AnalyticSurfaceAttributes attr = new AnalyticSurfaceAttributes();
        attr.setDrawShadow(false);
        attr.setInteriorOpacity(0.6);
        attr.setOutlineWidth(0);
        surface.setSurfaceAttributes(attr);
    }

    /**
     *
     * @param timeToMix
     * @param firstBuffer
     * @param secondBuffer
     * @param surface
     */
    private void interpolateValuesOverTime(
            final long timeToMix,
            final List<Double> firstBuffer, final List<Double> secondBuffer,
            final AnalyticSurface surface) {

        Timer timer = new Timer(20, new ActionListener() {

            protected long startTime = -1;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (this.startTime < 0) {
                    this.startTime = System.currentTimeMillis();
                }

                double t = (double) (e.getWhen() - this.startTime) / (double) timeToMix;
                int ti = (int) Math.floor(t);

                double a = t - ti;
                if ((ti % 2) == 0) {
                    a = 1d - a;
                }

                surface.setValues(createColorGradientGridValues(
                        a, firstBuffer, secondBuffer));

                if (surface.getClientLayer() != null) {
                    surface.getClientLayer().firePropertyChange(AVKey.LAYER, null, surface.getClientLayer());
                }
            }
        });
        timer.start();
    }

    /**
     *
     * @param a
     * @param firstBuffer
     * @param secondBuffer
     * @return
     */
    private Iterable<? extends AnalyticSurface.GridPointAttributes> createColorGradientGridValues(double a,
            List<Double> firstBuffer, List<Double> secondBuffer) {
        ArrayList<AnalyticSurface.GridPointAttributes> attributesList = new ArrayList<AnalyticSurface.GridPointAttributes>();
        for (int i = 0; i < firstBuffer.size(); i++) {
            double value = WWMath.mixSmooth(a, firstBuffer.get(i).doubleValue(), secondBuffer.get(i).doubleValue());

            ITransferFunction function0 = parHeight.getTransferFunction();
            Double height = (Double) function0.calc(null); // Actually, you don't need an argument!!
            if (height == null) {
                height = DEFAULT_HEIGHT;
            }

            ITransferFunction function1 = parColor.getTransferFunction();
            Color color = (Color) function1.calc(value);
            if (color == null) {
                color = DEFAULT_COLOR;
            }

            attributesList.add(AnalyticSurface.createGridPointAttributes(height, color));
        }

        return attributesList;
    }

    /**
     *
     * @param shpfile
     * @param timestep
     * @return
     */
    private List<Double> computeGridValues(Shapefile shpfile, String timestep) {
        int width = 140;
        int height = 140;
        int numValues = width * height;

        double[] values = new double[shpfile.getFeatureCount()];
        for (int i = 0; i < numValues; i++) {
            int index = (height - (i / height)) * 140 - (width - (i % width));
            Feature f = shpfile.getFeature(index);
            values[i] = f.GetFieldAsDouble(timestep);
        }

        List list = new ArrayList<Double>(numValues);
        for (int i = 0; i < numValues; i++) {
            list.add(new Double(values[i]));
        }

        return list;
    }
}