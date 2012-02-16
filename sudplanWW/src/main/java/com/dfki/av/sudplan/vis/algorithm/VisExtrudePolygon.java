/*
 *  VisExtrudePolygon.java 
 *
 *  Created by DFKI AV on 26.01.2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.util.WWMath;
import gov.nasa.worldwind.util.WWUtil;
import java.util.ArrayList;
import java.util.List;
import org.gdal.ogr.Geometry;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisExtrudePolygon extends VisAlgorithmAbstract {

    private static final BasicShapeAttributes bsa;
    static {
        bsa = new BasicShapeAttributes();
        bsa.setDrawOutline(false);
        bsa.setInteriorOpacity(1.0);
        bsa.setInteriorMaterial(Material.GRAY);
    }
            
    /**
     *
     */
    private final int numPolygonsPerLayer = 10000;

    /**
     *
     * @param attribute
     */
    public VisExtrudePolygon() {
        super("Extrude Polygon Visualization");
        VisParameter parameter = new VisParameter("Height");
        parameters.add(parameter);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {

        if (attributes == null || attributes.length != 1) {
            throw new IllegalArgumentException("Need 2 attributes "
                    + "to define visualization parameters.");
        }

        List<Layer> layers = new ArrayList<Layer>();
        if (data instanceof Shapefile
                && attributes[0] instanceof String) {
            Shapefile shapefile = (Shapefile) data;
            String attribute = (String) attributes[0];
            // Pre-processing data
            // .. here the preporcessing of the data should take place, or?
            // Create visualization
            addRenderablesForPolygons(shapefile, attribute, layers);
        } else {
            log.warn("Data type or attribute types not supported for Extrude Polygon Visualization.");
        }
        return layers;
    }

    /**
     * Creates renderables for all the polygons in the shapefile. Polygon is the
     * only shape that potentially returns a more than one layer, which it does
     * when the polygons per layer limit is exceeded.
     *
     * @param shp the shapefile to read
     * @param layers a list in which to place the layers created. May not be
     * null.
     */
    private void addRenderablesForPolygons(Shapefile shp, String attribute, List<Layer> layers) {
        RenderableLayer layer = new RenderableLayer();
        int numLayers = 0;
        layer.setName(shp.getLayerName() + "-" + numLayers);
        layers.add(layer);


        for (int i = 0; i < shp.getFeatureCount(); i++) {

            createExtrudedPolygon(shp, i, attribute, layer);

            if (layer.getNumRenderables() > this.numPolygonsPerLayer) {
                layer = new RenderableLayer();
                layer.setName(shp.getLayerName() + "-" + ++numLayers);
                layer.setEnabled(false);
                layers.add(layer);
            }
        }
    }

    /**
     *
     * @param shpfile
     * @param featureId
     * @param attribute
     * @param layer
     */
    private void createExtrudedPolygon(Shapefile shpfile, int featureId, String attribute, RenderableLayer layer) {
        Object object = shpfile.getAttributeOfFeature(featureId, attribute);
        Double value = null;
        if (object instanceof Number) {
            value = ((Number) object).doubleValue();
        }

        if (object instanceof String) {
            value = WWUtil.convertStringToDouble(object.toString());
        }

        if (value != null) {
            if (value < 0) {
                log.error("The input value for ExtrudedPolygon is less than 0.");
                return;
            } else if (value == 0) {
                log.warn("The input value for ExtrudedPolygon is equal to 0.");
                value += 0.01;
            }

            ExtrudedPolygon ep = new ExtrudedPolygon();
            ep.setHeight(value);
            ep.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            ep.setAttributes(bsa);
            layer.addRenderable(ep);

            List<Geometry> list = shpfile.getGeometryList(featureId);
            for (int i = 0; i < list.size(); i++) {

                Geometry g = list.get(i);
                List<Position> positionList = new ArrayList<Position>();

                for (int j = 0; j < g.GetPointCount(); j++) {
                    double[] point = g.GetPoint_2D(j);
                    // ... swap geo positions
                    positionList.add(Position.fromDegrees(point[1], point[0], value));
                }

                if (WWMath.computeWindingOrderOfLocations(positionList).equals(AVKey.CLOCKWISE)) {
                    if (!ep.getOuterBoundary().iterator().hasNext()) // has no outer boundary yet
                    {
                        ep.setOuterBoundary(positionList);
                    } else {
                        ep = new ExtrudedPolygon();
                        ep.setAttributes(bsa);
                        ep.setOuterBoundary(positionList);
                        layer.addRenderable(ep);
                    }
                } else {
                    ep.addInnerBoundary(positionList);
                }
            }
        }
    }
}
