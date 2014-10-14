/*
 * Shapefile.java
 *
 * Created by DFKI AV on 20.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.io.shapefile;

import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.IVisAlgorithm;
import de.dfki.av.geovisualizer.core.io.GeometryType;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gdal.ogr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A reader / parser for data sources of type shapefile. Using gdal library to
 * access all information.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class Shapefile implements ISource {

    // TODO add comments to methods.
    static {
        ogr.RegisterAll();
    }
    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(Shapefile.class);
    /**
     * {@link Integer} which represents the polyline geometry type.
     */
    public static final int PolylineType = ogrConstants.wkbLineString;
    /**
     * {@link Integer} which represents the polygon geometry type.
     */
    public static final int PolygonType = ogrConstants.wkbPolygon;
    /**
     * {@link Integer} which represents the point geometry type.
     */
    public static final int PointType = ogrConstants.wkbPoint;
    /**
     * {@link Integer} representation of real data field.
     */
    public static final int RealType = ogrConstants.OFTReal;
    /**
     * {@link Integer} representation of date data field.
     */
    public static final int DateType = ogrConstants.OFTDate;
    /**
     * {@link Integer} representation of binary data field.
     */
    public static final int BinaryType = ogrConstants.OFTBinary;
    /**
     * {@link Integer} representation of string data field.
     */
    public static final int StringType = ogrConstants.OFTString;
    /**
     * {@link Integer} representation of integer data field.
     */
    public static final int IntegerType = ogrConstants.OFTInteger;
    /**
     * {@link Integer} representation of time data field.
     */
    public static final int TimeType = ogrConstants.OFTTime;
    /**
     * {@link Integer} representation of string list data field.
     */
    public static final int StringListType = ogrConstants.OFTStringList;
    /**
     * {@link Integer} representation of integer list data field.
     */
    public static final int IntegerListType = ogrConstants.OFTIntegerList;
    /**
     * {@link Integer} representation of real list data field.
     */
    public static final int RealListType = ogrConstants.OFTRealList;
    /**
     * {@link Integer} representation of wide string data field.
     */
    public static final int WideStringType = ogrConstants.OFTWideString;
    /**
     * {@link Integer} representation of wide string list data field.
     */
    public static final int WideStringListType = ogrConstants.OFTWideStringList;
    /**
     * ogr {@link DataSource}
     */
    private org.gdal.ogr.DataSource data;

    /**
     * Constructor.
     *
     * @param filename the file name of the shapefile.
     * @throws {@link IllegalArgumentException} or {@link RuntimeException}
     */
    public Shapefile(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("No valid filename for shapefile.");
        }
        String path = filename.replace(File.separator, "/");
        data = ogr.OpenShared(path, 1);
        if (data == null) {
            throw new RuntimeException("Could not open shapefile.");
        }
    }

    /**
     * Returns an {@link Object} which represents the attribute data of the
     * feature at index {@code i}.
     *
     * @param i feature index
     * @param attribute {@link String} representation of attribute
     * @return {@link Object}
     */
    public Object getAttributeOfFeature(int i, String attribute) {
        if (data == null || data.GetLayerCount() == 0) {
            log.error("Shapefile has no layers.");
            throw new RuntimeException("Shapefile has no layers.");
        }
        return getValue(i, attribute);
    }

    /**
     * Returns true if the geometry is from type polyline.
     *
     * @param type {@link Integer} representation of the geometry
     * @return true if geometry is from type polyline
     */
    public static boolean isPolylineType(int type) {
        return type == PolylineType;
    }

    /**
     * Returns true if the geometry is from type polygon.
     *
     * @param type {@link Integer} representation of the geometry
     * @return true if geometry is from type polygon
     */
    public static boolean isPolygonType(int type) {
        return type == PolygonType;
    }

    /**
     * Returns the name of layer {@code 0}.
     *
     * @return the name of the layer to return.
     * @throws {@link RuntimeException} if layer count is zero.
     */
    public String getLayerName() {
        if (data == null || data.GetLayerCount() == 0) {
            throw new RuntimeException("Shapefile has no layers.");
        }
        return data.GetLayer(0).GetName();
    }

    /**
     * Returns true if the shapefile has an attribute called
     * <code>attribute</code>.
     *
     * @param attribute the attribute to check.
     * @return true if the shapefile has an attribute called
     * <code>attribute</code>.
     */
    private boolean hasAttribute(String attribute) {
        FeatureDefn fdef = data.GetLayer(0).GetLayerDefn();

        for (int i = 0; i < fdef.GetFieldCount(); i++) {
            FieldDefn fielddef = fdef.GetFieldDefn(i);
            if (fielddef.GetName().equalsIgnoreCase(attribute)) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param layerId
     * @return
     */
    private int getFeatureCountOfLayer(int layerId) {
        if (layerId >= data.GetLayerCount()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Layer layer = data.GetLayer(layerId);
        return layer.GetFeatureCount();
    }

    public int getShapeType() {
        if (data == null
                || data.GetLayerCount() == 0
                || data.GetLayer(0).GetFeatureCount() == 0) {
            log.error("Shapefile has no layers.");
            throw new RuntimeException("Shapefile has no layers.");
        }
        return data.GetLayer(0).GetFeature(0).GetGeometryRef().GetGeometryType();
    }

    /**
     *
     * @return
     */
    @Override
    public GeometryType getGeometryType() {
        if (data == null
                || data.GetLayerCount() == 0
                || data.GetLayer(0).GetFeatureCount() == 0) {
            log.error("Shapefile has no layers.");
            throw new RuntimeException("Shapefile has no layers.");
        }
        int type = data.GetLayer(0).GetFeature(0).GetGeometryRef()
                .GetGeometryType();
        if (isPolygonType(type)) {
            return GeometryType.POLYGON;
        } else if (isPolylineType(type)) {
            return GeometryType.POLYLINE;
        } else if (PointType == type) {
            return GeometryType.POINT;
        }
        return GeometryType.UNDEFINED;
    }

    /**
     *
     * @param featureId
     * @return
     */
    public Geometry getGeometry(int featureId) {
        if (data == null || data.GetLayerCount() == 0) {
            log.error("Shapefile has no layers.");
            throw new RuntimeException("Shapefile has no layers.");
        }
        return data.GetLayer(0).GetFeature(featureId).GetGeometryRef();
    }

    /**
     *
     * @param featureId
     * @return
     */
    public List<Geometry> getGeometryList(int featureId) {
        List<Geometry> list = new ArrayList<>();
        Geometry g = getGeometry(featureId);
        recAddGeometry(list, g);
        return list;
    }

    private void recAddGeometry(List<Geometry> list, Geometry g) {
        if (g.GetGeometryCount() > 0) {
            for (int i = 0; i < g.GetGeometryCount(); i++) {
                recAddGeometry(list, g.GetGeometryRef(i));
            }
        } else if (g.GetGeometryCount() == 0) {
            if (g.GetPointCount() > 0) {
                list.add(g);
            }
        } else {
            log.error("Shapefile has negative geometry count.");
        }
    }

    /**
     *
     * @return
     */
    public double[] getExtent() {
        if (data == null || data.GetLayerCount() == 0) {
            log.error("Shapefile has no layers.");
            throw new RuntimeException("Shapefile has no layers.");
        }
        return data.GetLayer(0).GetExtent();
    }

    @Override
    public double[] getBoundingBox() {
        return getExtent();
    }

    @Override
    public Map<String, Object> getAttributes() {

        if (data == null || data.GetLayerCount() == 0) {
            log.error("Shapefile has no layers.");
            throw new RuntimeException("Shapefile has no layers.");
        }
        HashMap<String, Object> attributes = new HashMap<>();
        FeatureDefn fdef = data.GetLayer(0).GetLayerDefn();

        for (int i = 0; i < fdef.GetFieldCount(); i++) {
            FieldDefn fielddef = fdef.GetFieldDefn(i);
            attributes.put(fielddef.GetName(), fielddef.GetTypeName());
        }

        return attributes;
    }

    /**
     *
     * @param attribute
     * @return
     */
    public int getTypeOfAttribute(String attribute) {
        if (data == null || data.GetLayerCount() == 0) {
            log.error("Shapefile has no layers.");
            throw new RuntimeException("Shapefile has no layers.");
        }

        FeatureDefn fdef = data.GetLayer(0).GetLayerDefn();
        for (int i = 0; i < fdef.GetFieldCount(); i++) {
            FieldDefn fielddef = fdef.GetFieldDefn(i);
            if (fielddef.GetName().equalsIgnoreCase(attribute)) {
                return fielddef.GetFieldType();
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (data != null) {
            buf.append("Name: ").append(data.GetName()).append("\n");
            buf.append("Layer count: ").append(data.GetLayerCount())
                    .append("\n");
            for (int i = 0; i < data.GetLayerCount(); i++) {
                org.gdal.ogr.Layer layer = data.GetLayer(i);
                buf.append("Layer name ").append(i).append(": ")
                        .append(layer.GetName()).append("\n");
                double[] extent = layer.GetExtent();
                buf.append("Layer extend: ").append(Arrays.toString(extent))
                        .append("\n");
                buf.append("Feature count: ").append(layer.GetFeatureCount())
                        .append("\n");
                buf.append("Field count: ").append(layer.GetLayerDefn()
                        .GetFieldCount()).append("\n");
            }
        } else {
            log.error("Parameter 'DataSource' is null.");
            throw new IllegalArgumentException("Parameter not valid.");
        }

        return buf.toString();
    }

    /**
     * Returns an {@link Object} which represents the attribute data of the
     * feature at index {@code featureId} and attribute {@code attributeName}.
     *
     * @param featureId feature index
     * @param attributeName {@link String} representation of attribute
     * @return {@link Object}
     */
    @Override
    public Object getValue(int featureId, String attributeName) {
        if (attributeName.equals(IVisAlgorithm.NO_ATTRIBUTE)) {
            return null;
        }
        if (!hasAttribute(attributeName)) {
            log.error("Attribute {} doesn't exist", attributeName);
            return null;
        }
        if (data == null || data.GetLayerCount() == 0) {
            throw new RuntimeException("Shapefile has no layers.");
        }
        if (featureId < 0 || featureId > data.GetLayer(0).GetFeatureCount()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Feature feature = data.GetLayer(0).GetFeature(featureId);
        int type = feature.GetFieldType(attributeName);
        int id = feature.GetFieldIndex(attributeName);
        switch (type) {
            case StringType:
                return feature.GetFieldAsString(id);
            case StringListType:
                return feature.GetFieldAsStringList(id);
            case IntegerType:
                return feature.GetFieldAsInteger(id);
            case IntegerListType:
                return feature.GetFieldAsIntegerList(id);
            case RealType:
                return feature.GetFieldAsDouble(id);
            case RealListType:
                return feature.GetFieldAsDoubleList(id);
            case DateType:
                return feature.GetFieldAsString(id);
            case TimeType:
                return feature.GetFieldAsString(id);
            case WideStringType:
                return feature.GetFieldAsString(id);
            case WideStringListType:
                return feature.GetFieldAsString(id);
            case BinaryType:
                return feature.GetFieldAsString(id);
            default:
                return feature.GetFieldAsString(id);
        }
    }

    @Override
    public int getFeatureCount() {
        if (data == null || data.GetLayerCount() == 0) {
            log.error("Shapefile has no layers.");
            throw new RuntimeException("Shapefile has no layers.");
        }
        return getFeatureCountOfLayer(0);
    }

    @Override
    public String getName() {
        return getLayerName();
    }

    @Override
    public List<List<double[]>> getPoints(int featureId) {
        List<Geometry> list = getGeometryList(featureId);
        List<List<double[]>> geometry = new ArrayList<>();
        for (int j = 0; j < list.size(); j++) {
            List<double[]> points = new ArrayList<>();
            Geometry g = list.get(j);
            for (int u = 0; u < g.GetPointCount(); u++) {
                double[] point = g.GetPoint_2D(u);
                points.add(point);
            }
            g.delete();
            geometry.add(points);
        }
        return geometry;
    }

    @Override
    public double min(String attribute) {
        double minValue = Double.MAX_VALUE;
        String query = "SELECT MIN(" + attribute + ") FROM " + getLayerName();
        Layer layer = data.ExecuteSQL(query);
        if (layer.GetFeatureCount() > 0) {
            minValue = layer.GetFeature(0).GetFieldAsDouble(0);
        }
        data.ReleaseResultSet(layer);
        return minValue;
    }

    @Override
    public double max(String attribute) {
        double maxValue = Double.MIN_VALUE;
        String query = "SELECT MAX(" + attribute + ") FROM " + getLayerName();
        Layer layer = data.ExecuteSQL(query);
        if (layer.GetFeatureCount() > 0) {
            maxValue = layer.GetFeature(0).GetFieldAsDouble(0);
        }
        data.ReleaseResultSet(layer);
        return maxValue;
    }

    /**
     * Calculates the minimum value for the attribute {@code attribute} of the
     * shapefile {@code shp}.
     *
     * @param shp the {@link Shapefile}.
     * @param attribute the {@code attribute} name
     * @return the minimum value to return.
     */
    public static double MIN(Shapefile shp, String attribute) {
        return shp.min(attribute);
    }

    /**
     * Calculates the maximum value for the attribute {@code attribute} of the
     * shapefile {@code shp}.
     *
     * @param shp the {@link Shapefile}.
     * @param attribute the {@code attribute} name
     * @return the maximum value to return.
     */
    public static double MAX(Shapefile shp, String attribute) {
        return shp.max(attribute);
    }
}
