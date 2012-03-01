/*
 *  VisTimeseries.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
import com.dfki.av.sudplan.vis.algorithm.functions.ConstantNumberTansferFunction;
import com.dfki.av.sudplan.vis.algorithm.functions.ITransferFunction;
import com.dfki.av.sudplan.vis.algorithm.functions.IdentityFunction;
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
    public VisTimeseries() {
        super("Timeseries Visualization",
                "No description available.",
                new ImageIcon(VisTimeseries.class.getClassLoader().
                getResource("icons/VisTimeseries.png")));

        this.parTimestep0 = new NumberParameter("Timestep (t=0)");
        this.parTimestep0.addTransferFunction(new IdentityFunction());
        addVisParameter(parTimestep0);

        this.parTimestep1 = new NumberParameter("Timestep (t=1)");
        this.parTimestep1.addTransferFunction(new IdentityFunction());
        addVisParameter(parTimestep1);

        this.parHeight = new NumberParameter("Height of Timeseries [m]");
        this.parHeight.addTransferFunction(new ConstantNumberTansferFunction());
        addVisParameter(parHeight);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {

        log.debug("Running {}", this.getClass().getSimpleName());
        
        if (attributes == null || attributes.length < 2) {
            throw new IllegalArgumentException("Not engough attributes for "
                    + "timeseries visualization.");
        }

        List<Layer> layers = new ArrayList<Layer>();
        if (data instanceof Shapefile
                && attributes[0] instanceof String
                && attributes[1] instanceof String) {
            Shapefile shpfile = (Shapefile) data;
            String attribute0 = (String) attributes[0];
            String attribute1 = (String) attributes[1];
            
            // 1 - Pre-processing data
            ITransferFunction function0 = parTimestep0.getSelectedTransferFunction();
            log.debug("Using transfer function {} for attribute 0.", function0.getClass().getSimpleName());
            function0.preprocess(shpfile, attribute0);
            
            ITransferFunction function1 = parTimestep1.getSelectedTransferFunction();
            log.debug("Using transfer function {} for attribute 1.", function1.getClass().getSimpleName());
            function1.preprocess(shpfile, attribute1);
            
            ITransferFunction function2 = parHeight.getSelectedTransferFunction();
            log.debug("Using transfer function {} for attribute 2.", function2.getClass().getSimpleName());
            function2.preprocess(shpfile, attribute1);

            // 2 - Create visualization            
            RenderableLayer layer = new RenderableLayer();
            createTimeSeriesSurface(shpfile, layer, attribute0, attribute1);

            layer.setPickEnabled(false);
            layer.setName(shpfile.getLayerName());
            layers.add(layer);

        }
        
        log.debug("Running {}", this.getClass().getSimpleName());
        
        return layers;
    }

    /**
     *
     * @param shpfile
     * @param layer
     */
    private void createTimeSeriesSurface(Shapefile shpfile, RenderableLayer layer, String attribute0, String attribute1) {
        double minHue = 240d / 360d;
        double maxHue = 0.d / 360d;
        int width = 140;
        int height = 140;

        double minValue = -200e3;
        double maxValue = 200e3;

        AnalyticSurface surface = new AnalyticSurface();
        log.debug("Bounding box: {}", shpfile.getExtent());
        double[] extend = shpfile.getExtent();
        surface.setSector(Sector.fromDegrees(extend[2], extend[3], extend[0], extend[1]));
        surface.setAltitudeMode(WorldWind.ABSOLUTE);
        surface.setDimensions(width, height);
        surface.setClientLayer(layer);
        layer.addRenderable(surface);

        List<Double> firstValues = computeGridValues(shpfile, attribute0);
        List<Double> secondValues = computeGridValues(shpfile, attribute1);

        interpolateValuesOverTime(3000L, firstValues, secondValues, minValue, maxValue, minHue, maxHue, surface);

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
     * @param minValue
     * @param maxValue
     * @param minHue
     * @param maxHue
     * @param surface
     */
    private void interpolateValuesOverTime(
            final long timeToMix,
            final List<Double> firstBuffer, final List<Double> secondBuffer,
            final double minValue, final double maxValue, final double minHue, final double maxHue,
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
                        a, firstBuffer, secondBuffer, minValue, maxValue, minHue, maxHue));

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
     * @param minValue
     * @param maxValue
     * @param minHue
     * @param maxHue
     * @return
     */
    private Iterable<? extends AnalyticSurface.GridPointAttributes> createColorGradientGridValues(double a,
            List<Double> firstBuffer, List<Double> secondBuffer, double minValue, double maxValue,
            double minHue, double maxHue) {
        ArrayList<AnalyticSurface.GridPointAttributes> attributesList = new ArrayList<AnalyticSurface.GridPointAttributes>();
        for (int i = 0; i < firstBuffer.size(); i++) {
            double value = WWMath.mixSmooth(a, firstBuffer.get(i).doubleValue(), secondBuffer.get(i).doubleValue());

            ITransferFunction function2 = parHeight.getSelectedTransferFunction();
            Double height = (Double) function2.calc(value); // Actually, you don't need an argument!!
            
            Color color;
            if (value < 5.0) {
                color = Color.GREEN;
            } else if (value < 6.0) {
                color = Color.YELLOW;
            } else {
                color = Color.RED;
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
