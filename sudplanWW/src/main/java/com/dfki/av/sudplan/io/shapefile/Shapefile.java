package com.dfki.av.sudplan.io.shapefile;

import com.dfki.av.sudplan.io.DataInput;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gdal.ogr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class Shapefile implements DataInput{

    static {
        ogr.RegisterAll();
    }
    private static final Logger log = LoggerFactory.getLogger(Shapefile.class);
    private DataSource data;
    public static final int PolylineType = ogrConstants.wkbLineString;
    public static final int PolygonType = ogrConstants.wkbPolygon;

    /**
     *
     * @param type
     * @return
     */
    public static boolean isPolylineType(int type) {
        return type == PolylineType;
    }

    public static boolean isPolygonType(int type) {
        return type == PolygonType;
    }

    /**
     *
     * @param filename
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
     *
     * @return
     */
    public String getLayerName() {
        if (data == null || data.GetLayerCount() == 0) {
            throw new RuntimeException("Shapefile has no layers.");
        }
        return data.GetLayer(0).GetName();
    }

    /**
     *
     * @param id
     * @return
     */
    public Feature getFeature(int id) {
        if (data == null || data.GetLayerCount() == 0) {
            throw new RuntimeException("Shapefile has no layers.");
        }
        if (id < 0 || id > data.GetLayer(0).GetFeatureCount()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return data.GetLayer(0).GetFeature(id);
    }

    /**
     *
     * @param i
     * @param attribute
     * @return
     */
    public Object getAttributeOfFeature(int i, String attribute) {
        if (data == null || data.GetLayerCount() == 0) {
            log.error("Shapefile has no layers.");
            throw new RuntimeException("Shapefile has no layers.");
        }
        return getAttributeOfFeatureOfLayer(0, i, attribute);
    }

    /**
     *
     * @param layerId
     * @param featureId
     * @param attribute
     * @return
     */
    private Object getAttributeOfFeatureOfLayer(int layerId, int featureId, String attribute) {
        if (layerId < 0 || layerId >= data.GetLayerCount()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        if (featureId >= getFeatureCount()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Object ret;
        Feature feature = data.GetLayer(layerId).GetFeature(featureId);
        switch (feature.GetFieldType(attribute)) {
            case ogrConstants.OFTInteger: {
                int a = feature.GetFieldAsInteger(attribute);
                ret = new Integer(a);
                break;
            }
            case ogrConstants.OFTReal: {
                double a = feature.GetFieldAsDouble(attribute);
                ret = new Double(a);
                break;
            }
            default: {
                ret = feature.GetFieldAsString(attribute);
                break;
            }
        }

        return ret;
    }

    /**
     *
     * @return
     */
    public int getFeatureCount() {
        if (data == null || data.GetLayerCount() == 0) {
            log.error("Shapefile has no layers.");
            throw new RuntimeException("Shapefile has no layers.");
        }
        return getFeatureCountOfLayer(0);
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

    /**
     *
     * @return
     */
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
        List<Geometry> list = new ArrayList<Geometry>();
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

    /**
     * 
     * @return 
     */
    public Map<String, Object> getAttributes() {

        if (data == null || data.GetLayerCount() == 0) {
            log.error("Shapefile has no layers.");
            throw new RuntimeException("Shapefile has no layers.");
        }
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        FeatureDefn fdef = data.GetLayer(0).GetLayerDefn();
       
        for (int i = 0; i < fdef.GetFieldCount(); i++) {
            FieldDefn fielddef = fdef.GetFieldDefn(i);
            attributes.put(fielddef.GetName(), fielddef.GetTypeName());
        }
        
        return attributes;
    }

    @Override
    public String toString() {

        String tmp = "";
        if (data != null) {
            tmp += "Name: " + data.GetName() + "\n";
            tmp += "Layer count: " + data.GetLayerCount() + "\n";
            for (int i = 0; i < data.GetLayerCount(); i++) {
                org.gdal.ogr.Layer layer = data.GetLayer(i);
                tmp += "Layer name " + i + ": " + layer.GetName() + "\n";
                double[] extent = layer.GetExtent();
                tmp += "Layer extend: " + extent.toString() + "\n";
                tmp += "Feature count: " + layer.GetFeatureCount() + "\n";
                tmp += "Field count: " + layer.GetLayerDefn().GetFieldCount() + "\n";
            }
        } else {
            log.error("Parameter 'DataSource' is null.");
            throw new IllegalArgumentException("Parameter not valid.");
        }

        return tmp;
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
}
