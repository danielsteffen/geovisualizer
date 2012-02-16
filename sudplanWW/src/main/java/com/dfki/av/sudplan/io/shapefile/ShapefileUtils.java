package com.dfki.av.sudplan.io.shapefile;

import org.gdal.ogr.DataSource;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.ogr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class ShapefileUtils {
    /*
     * Logger.
     */

    private static final Logger log = LoggerFactory.getLogger(ShapefileUtils.class);

    static {
        ogr.RegisterAll();
    }

    /**
     *
     * @param shp
     * @param attribute
     * @return
     */
    public static double Min(Shapefile shp, String attribute) {
        double minValue = Double.MAX_VALUE;
        for (int i = 0; i < shp.getFeatureCount(); i++) {
            Object object = shp.getAttributeOfFeature(i, attribute);
            if (object instanceof Number) {
                double value = ((Number) object).doubleValue();
                if (value < minValue) {
                    minValue = value;
                }
            }
        }
        return minValue;
    }

    /**
     *
     * @param shp
     * @param attribute
     * @return
     */
    public static double Max(Shapefile shp, String attribute) {
        double maxValue = Double.MIN_VALUE;

        for (int i = 0; i < shp.getFeatureCount(); i++) {
            Object object = shp.getAttributeOfFeature(i, attribute);
            if (object instanceof Number) {
                double value = ((Number) object).doubleValue();
                if (value > maxValue) {
                    maxValue = value;
                }
            }
        }
        return maxValue;
    }

    /**
     * Auto classification of the parameter attribute. Size of classes is equal.
     *
     * @param shpfile the data source.
     * @param attribute the attribute to classify.
     * @param numClasses the number of classes to produce.
     * @return array of boundaries of the classes to return
     */
    public static double[] AutoClassificationOfNumberAttribute(Shapefile shpfile, String attribute, int numClasses) {

        log.debug("Auto classification of attribute <{}>.", attribute);
        log.debug("Searching min and max value for attribute <{]>.", attribute);
        double minValue = ShapefileUtils.Min(shpfile, attribute);
        double maxValue = ShapefileUtils.Max(shpfile, attribute);
        log.debug("Attribute {} min value is {}.", attribute, minValue);
        log.debug("Attribute {} man value is {}.", attribute, maxValue);

        double size = (maxValue - minValue) / numClasses;
        double[] bounds = new double[numClasses + 1];

        for (int i = 0; i < bounds.length; i++) {
            bounds[i] = minValue + i * size;
        }

        log.debug("Class boundaries: {}", bounds);

        return bounds;
    }

    public static void PrintShapefileInfo(String filename) {

        DataSource data = ogr.Open(filename);
        if (data != null) {
            log.debug("Data source name: {}", data.GetName());
            for (int i = 0; i < data.GetLayerCount(); i++) {
                log.debug("Layer {} name: {}", i, data.GetLayer(i).GetName());
                org.gdal.ogr.Layer layer = data.GetLayer(i);
                double[] extent = layer.GetExtent();
                log.debug("Layer extend {}", extent);
                log.debug("Layer definition: {}", layer.GetLayerDefn().GetName());
                log.debug("Feature count: {}", layer.GetFeatureCount());
                log.debug("Field count: {}", layer.GetLayerDefn().GetFieldCount());
                for (int k = 0; k < layer.GetLayerDefn().GetFieldCount(); k++) {
                    FieldDefn fieldDefn = layer.GetLayerDefn().GetFieldDefn(k);
                    log.debug("Field {} of type {}", fieldDefn.GetName(), fieldDefn.GetFieldType());
                }
//                for (int j = 0; j < layer.GetFeatureCount(); j++) {
//                    Feature feature = layer.GetFeature(j);
//                    double perc98d = feature.GetFieldAsDouble("Perc98d");
//                    log.debug("Feature {} with perc98d={}", j, perc98d);
////                    if (feature.GetGeometryRef().GetGeometryType() == 3) {
////                        log.debug("Feature {}: {}", j, feature.);
////                    }
//                }
            }
        } else {
            log.warn("Parameter 'DataSource' is null.");
            throw new IllegalArgumentException("Parameter not valid.");
        }
    }

    public static void PrintShapefileInfo2(String filename) {

        Shapefile shapefile = new Shapefile(filename);
        DataSource data = ogr.Open(filename);
        if (data != null) {
            log.debug("Data source name: {}", data.GetName());
            for (int i = 0; i < data.GetLayerCount(); i++) {
                log.debug("Layer {} name: {}", i, data.GetLayer(i).GetName());
                org.gdal.ogr.Layer layer = data.GetLayer(i);
                double[] extent = layer.GetExtent();
                log.debug("Layer extend {}", extent);
                log.debug("Layer definition: {}", layer.GetLayerDefn().GetName());
                log.debug("Feature count: {}", layer.GetFeatureCount());
                log.debug("Field count: {}", layer.GetLayerDefn().GetFieldCount());
                for (int k = 0; k < layer.GetLayerDefn().GetFieldCount(); k++) {
                    FieldDefn fieldDefn = layer.GetLayerDefn().GetFieldDefn(k);
                    log.debug("Field {} of type {}", fieldDefn.GetName(), fieldDefn.GetFieldType());
                }
//                for (int j = 0; j < layer.GetFeatureCount(); j++) {
//                    Feature feature = layer.GetFeature(j);
//                    double perc98d = feature.GetFieldAsDouble("Perc98d");
//                    log.debug("Feature {} with perc98d={}", j, perc98d);
////                    if (feature.GetGeometryRef().GetGeometryType() == 3) {
////                        log.debug("Feature {}: {}", j, feature.);
////                    }
//                }
            }
        } else {
            log.warn("Parameter 'DataSource' is null.");
            throw new IllegalArgumentException("Parameter not valid.");
        }
    }
}
