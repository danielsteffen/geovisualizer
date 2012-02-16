package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
import com.dfki.av.sudplan.io.shapefile.ShapefileUtils;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.util.BufferFactory;
import gov.nasa.worldwind.util.BufferWrapper;
import gov.nasa.worldwind.util.WWMath;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurface;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurfaceAttributes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import org.gdal.ogr.Feature;

/**
 *
 * @author steffen
 */
public class VisTimeseries extends VisAlgorithm {

    private String[] attributes;

    public VisTimeseries(String[] timesteps) {
        super("Timeseries Visualization");
        if(timesteps.length != 2){
            throw new IllegalArgumentException();
        }
        this.attributes = timesteps;
    }

    @Override
    public List<Layer> createLayersFromData(Object data) {

        List<Layer> layers = new ArrayList<Layer>();
        if (data instanceof Shapefile) {
            Shapefile shpfile = (Shapefile) data;
            RenderableLayer layer = new RenderableLayer();

            createTimeSeriesSurface(shpfile, layer);

            layer.setPickEnabled(false);
            layer.setName(shpfile.getLayerName());
            layers.add(layer);
        }
        return layers;
    }

    /**
     *
     * @param shpfile
     * @param layer
     */
    private void createTimeSeriesSurface(Shapefile shpfile, RenderableLayer layer) {
        double minHue = 240d / 360d;
        double maxHue = 0d / 360d;
        int width = 140;
        int height = 140;

        double minValue = -200e3;
        double maxValue = 200e3;

        AnalyticSurface surface = new AnalyticSurface();
        log.debug("Bounding box: {}", shpfile.getExtent());
        double[] extend = shpfile.getExtent();
        surface.setSector(Sector.fromDegrees(extend[2], extend[3], extend[0], extend[1]));
        surface.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        surface.setDimensions(width, height);
        surface.setClientLayer(layer);
        layer.addRenderable(surface);

        List<Double> firstValues = GridValues(shpfile, attributes[0]);
        List<Double> secondValues = GridValues(shpfile, attributes[1]);
        mixValuesOverTime(2000L, firstValues, secondValues, minValue, maxValue, minHue, maxHue, surface);

        AnalyticSurfaceAttributes attr = new AnalyticSurfaceAttributes();
        attr.setDrawShadow(false);
        attr.setInteriorOpacity(0.6);
        attr.setOutlineWidth(0);
        surface.setSurfaceAttributes(attr);
    }

    protected void mixValuesOverTime(
            final long timeToMix,
            final List<Double> firstBuffer, final List<Double> secondBuffer,
            final double minValue, final double maxValue, final double minHue, final double maxHue,
            final AnalyticSurface surface) {

        Timer timer = new Timer(20, new ActionListener() {

            protected long startTime = -1;

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

                surface.setValues(createMixedColorGradientGridValues(
                        a, firstBuffer, secondBuffer, minValue, maxValue, minHue, maxHue));

                if (surface.getClientLayer() != null) {
                    surface.getClientLayer().firePropertyChange(AVKey.LAYER, null, surface.getClientLayer());
                }
            }
        });
        timer.start();
    }

    public Iterable<? extends AnalyticSurface.GridPointAttributes> createMixedColorGradientGridValues(double a,
            List<Double> firstBuffer, List<Double> secondBuffer, double minValue, double maxValue,
            double minHue, double maxHue) {
        ArrayList<AnalyticSurface.GridPointAttributes> attributesList = new ArrayList<AnalyticSurface.GridPointAttributes>();

        long length = Math.min(firstBuffer.size(), secondBuffer.size());
        for (int i = 0; i < length; i++) {
            double value = WWMath.mixSmooth(a, firstBuffer.get(i).doubleValue(), secondBuffer.get(i).doubleValue());
            attributesList.add(
                    AnalyticSurface.createColorGradientAttributes(value, minValue, maxValue, minHue, maxHue));
        }

        return attributesList;
    }

    public static List<Double> GridValues(Shapefile shpfile, String timestep) {
        int width = 140;
        int height = 140;
        double min = -200e3;
        double max = 200e3;
        int numIterations = 1000;
        double smoothness = 0.2d;
        int numValues = 140 * 140;

        double[] values = new double[shpfile.getFeatureCount()];
        for (int i = 0; i < shpfile.getFeatureCount(); i++) {
            Feature f = shpfile.getFeature(i);
            values[i] = f.GetFieldAsDouble(timestep);
        }

//        smoothValues(width, height, values, smoothness);
        scaleValues(values, numValues, min, max);
        List list = new ArrayList<Double>(numValues);
        for (int i = 0; i < numValues; i++) {
            list.add(new Double(values[i]));
        }
        return list;
    }

    protected static void scaleValues(double[] values, int count, double minValue, double maxValue) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < count; i++) {
            if (min > values[i]) {
                min = values[i];
            }
            if (max < values[i]) {
                max = values[i];
            }
        }

        for (int i = 0; i < count; i++) {
            values[i] = (values[i] - min) / (max - min);
            values[i] = minValue + values[i] * (maxValue - minValue);
        }
    }
}
