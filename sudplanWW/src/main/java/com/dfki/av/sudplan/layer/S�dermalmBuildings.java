/*
 *  SödermalmBuildings.java 
 *
 *  Created by DFKI AV on 11.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.layer;


import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.render.SurfaceShape;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurface;
import java.util.ArrayList;
import java.util.List;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class SödermalmBuildings extends RenderableLayer {

    static {
        ogr.RegisterAll();
    }
    private final static String shpFileName = "test.shp";
    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

//    public SödermalmBuildings() {
//
//        SwingWorker<gov.nasa.worldwind.layers.Layer, Void> worker = new SwingWorker<gov.nasa.worldwind.layers.Layer, Void>() {
//
//            @Override
//            protected gov.nasa.worldwind.layers.Layer doInBackground() throws Exception {
//                RenderableLayer renderableLayer = new RenderableLayer();
//                DataSource dataSource = ogr.Open(shpFileName);
//                if (dataSource != null) {
//                    if (log.isDebugEnabled()) {
//                        log.debug("Data source name: {}", dataSource.GetName());
//                        for (int i = 0; i < dataSource.GetLayerCount(); i++) {
//                            log.debug("Layer name: {}", dataSource.GetLayer(i).GetName());
//                            Layer layer = dataSource.GetLayer(0);
//                            log.debug("Layer definition: {}", layer.GetLayerDefn().GetName());
//                            log.debug("Feature count: {}", layer.GetFeatureCount());
//                            log.debug("Field count: {}", layer.GetLayerDefn().GetFieldCount());
//                            for (int k = 0; k < layer.GetLayerDefn().GetFieldCount(); k++) {
//                                FieldDefn fieldDefn = layer.GetLayerDefn().GetFieldDefn(k);
//                                log.debug("Field name: {}", fieldDefn.GetName());
//                            }
//
//                            for (int j = 0; j < layer.GetFeatureCount()-100; j++) {
//                                Feature feature = layer.GetFeature(j);
//
//                                if (feature.GetGeometryRef().GetGeometryType() == 3) {
//                                    Polygon polygon = createPolygon(feature);
//                                    polygon.setAttributes(new BasicShapeAttributes());
//                                    renderableLayer.addRenderable(polygon);
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    if (log.isWarnEnabled()) {
//                        log.warn("Parameter 'DataSource' is null.");
//                    }
//                }
//                return renderableLayer;
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    gov.nasa.worldwind.layers.Layer layer = get();
//                    RenderableLayer rlayer = (RenderableLayer) layer;
//                    addRenderables(rlayer.getRenderables());
//                } catch (Exception ex) {
//                    if (log.isErrorEnabled()) {
//                        log.error("{}", ex.toString());
//                    }
//                }
//            }
//
//            private Polygon createPolygon(Feature feature) {
//
//                List<Position> positionList = new ArrayList<Position>();
//                Geometry geometry = feature.GetGeometryRef();
//                log.debug("Geometry type: {}", geometry.GetGeometryType());
//                log.debug("Geometry name: {}", geometry.GetGeometryName());
//                for (int t = 0; t < geometry.GetGeometryCount(); t++) {
//                    Geometry ref = geometry.GetGeometryRef(t);
//                    log.debug("Ref point count: {}", ref.GetPointCount());
//                    for (int s = 0; s < ref.GetPointCount(); s++) {
//                        double[] point = ref.GetPoint_2D(s);
//                        log.debug("lat: {}, lon: {}", point[0], point[1]);
//                        positionList.add(Position.fromDegrees(point[0], point[1]));
//                    }
//                }
//                Polygon p = new Polygon(positionList);
//                return p;
//            }
//        };
//        worker.execute();
//    }
    public SödermalmBuildings() {

        this.setName("Buildings (Södermalm)");
        this.setPickEnabled(false);
        this.setEnabled(false);

        DataSource dataSource = ogr.Open(shpFileName);
        if (dataSource != null) {
            if (log.isDebugEnabled()) {
                log.debug("Data source name: {}", dataSource.GetName());
                for (int i = 0; i < dataSource.GetLayerCount(); i++) {
                    log.debug("Layer name: {}", dataSource.GetLayer(i).GetName());
                    Layer layer = dataSource.GetLayer(0);
                    log.debug("Layer definition: {}", layer.GetLayerDefn().GetName());
                    log.debug("Feature count: {}", layer.GetFeatureCount());
                    log.debug("Field count: {}", layer.GetLayerDefn().GetFieldCount());
                    for (int k = 0; k < layer.GetLayerDefn().GetFieldCount(); k++) {
                        FieldDefn fieldDefn = layer.GetLayerDefn().GetFieldDefn(k);
                        log.debug("Field name: {}", fieldDefn.GetName());
                    }

                    for (int j = 0; j < layer.GetFeatureCount() - 100; j++) {
                        Feature feature = layer.GetFeature(j);

                        if (feature.GetGeometryRef().GetGeometryType() == 3) {
                            SurfaceShape shape = createPolygon(feature);
                            addRenderable(shape);
                        }
                    }
                }
            }
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Parameter 'DataSource' is null.");
            }
        }

    }

    private SurfaceShape createPolygon(Feature feature) {

        List<Position> positionList = new ArrayList<Position>();
        List<LatLon> latLonList = new ArrayList<LatLon>();
        Geometry geometry = feature.GetGeometryRef();
        log.debug("Geometry type: {}", geometry.GetGeometryType());
        log.debug("Geometry name: {}", geometry.GetGeometryName());
        for (int t = 0; t < geometry.GetGeometryCount(); t++) {
            Geometry ref = geometry.GetGeometryRef(t);
            log.debug("Ref point count: {}", ref.GetPointCount());
            for (int s = 0; s < ref.GetPointCount(); s++) {
                double[] point = ref.GetPoint_2D(s);
                log.debug("lat: {}, lon: {}", point[0], point[1]);
//                positionList.add(Position.fromDegrees(point[0], point[1]));                
                latLonList.add(LatLon.fromRadians(point[0], point[1]));
            }
        }
//        SurfaceShape shape = new SurfacePolygon(positionList);
        SurfaceShape shape = new SurfacePolygon(latLonList);
        AnalyticSurface as = new AnalyticSurface();
        BasicShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setInteriorMaterial(Material.GRAY);
        attrs.setOutlineMaterial(Material.WHITE);
        attrs.setInteriorOpacity(0.5);
        attrs.setOutlineOpacity(0.8);
        attrs.setOutlineWidth(3);
        attrs.setImageScale(0.5);
        shape.setAttributes(attrs);
        return shape;
    }
}
