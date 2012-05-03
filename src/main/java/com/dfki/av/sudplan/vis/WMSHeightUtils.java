/*
 *  LayerInfo.java 
 *
 *  Created by DFKI AV on 15.04.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class WMSHeightUtils {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(WMSHeightUtils.class);

    /**
     *
     * @param caps
     * @param lcaps
     * @param params
     * @param elevation
     * @param opacity
     */
    protected static void addComponent(SurfaceImageLayer sul, WMSCapabilities caps, WMSLayerCapabilities lcaps, AVList params, double elevation, double opacity) {
        AVList configParams = params.copy(); // Copy to insulate changes from the caller.
        String image_format = "";
        for (String s : caps.getImageFormats()) {
            log.debug(s);
        }
        for (String s : caps.getImageFormats()) {
            if (s.endsWith("gif") || s.endsWith("png")) {
                image_format = s;
                break;
            }
        }
        if (image_format.equals("")) {
            log.warn("No supported image format available.");
        }
        WMSTiledImageLayer tmp = new WMSTiledImageLayer(caps, params);

        // Some wms servers are slow, so increase the timeouts and limits used by world wind's retrievers.
        configParams.setValue(AVKey.URL_CONNECT_TIMEOUT, 30000);
        configParams.setValue(AVKey.URL_READ_TIMEOUT, 30000);
        configParams.setValue(AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT, 60000);

        if (lcaps == null) {
            log.error("LayerCaps == null");
        }
        Sector sector = lcaps.getGeographicBoundingBox();
        double distance = distance(sector.getMinLatitude().degrees, sector.getMinLongitude().degrees, sector.getMaxLatitude().degrees, sector.getMaxLongitude().degrees, "K");
        int parts = 2 + (int) (distance / 3);
        Sector[] sectors = sector.subdivide(parts);

        int width = 512;
        int height = 512;
        double scale = 1.0;
        int level = -1;

        LatLon location = getReferenceLocation(sector);
        if (elevation > 0) {
            WorldWindowGLCanvas ww = new WorldWindowGLCanvas();
            Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
            ww.setModel(m);
            Globe globe = ww.getModel().getGlobe();
            elevation += globe.getMinAndMaxElevations(sector)[1];
            log.debug("Elevation: " + elevation);
            log.debug("Reference Location: " + location + " Ele: " + globe.getElevation(location.getLatitude(), location.getLongitude()));
            double fix = globe.getElevation(location.getLatitude(), location.getLongitude());
            if (fix < 0) {
//                elevation += fix;
            } else {
                elevation -= fix;
            }
            log.debug("Elevation Fix: " + elevation);
        }
        for (Sector sec : sectors) {
            try {
                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                tmp.composeImageForSector(sec, width, height, scale,
                        level, image_format, false, img, 40000);
                sul = createLayer(sul, location, params.getStringValue(AVKey.DISPLAY_NAME) + "_" + elevation, img, sec, elevation, opacity);
            } catch (Exception e) {
                log.error("" + e);
            }
        }
    }

    /**
     *
     * @param name
     * @param image
     * @param sector
     * @return
     */
    private static SurfaceImageLayer createLayer(SurfaceImageLayer sul, String name, BufferedImage image, Sector sector, double opacity) {
        sul.addImage(name, image, sector);
        sul.setOpacity(opacity);
        sul.setPickEnabled(false);
        return sul;
    }

    private static LatLon getReferenceLocation(Sector sector) {
        ArrayList<LatLon> pathLocations = new ArrayList<LatLon>();
        Iterator<LatLon> iterator = sector.iterator();
        while (iterator.hasNext()) {
            pathLocations.add(iterator.next());
        }
        ExtrudedPolygon pgon = new ExtrudedPolygon(pathLocations, 1.0);
        return pgon.getReferenceLocation();
    }

    /**
     *
     * @param name
     * @param image
     * @param sector
     * @param height
     * @return
     */
    private static SurfaceImageLayer createLayer(SurfaceImageLayer sul, LatLon ref, String name, BufferedImage image, Sector sector, double elevation, double opacity) {
        if (elevation == 0) {
            return createLayer(sul, name, image, sector, opacity);
        }
        if (elevation < 0) {
            log.warn("Invalid elevation value. Set elevation = 0.");
            return createLayer(sul, name, image, sector, opacity);
        }
        ArrayList<LatLon> pathLocations = new ArrayList<LatLon>();
        Iterator<LatLon> iterator = sector.iterator();
        while (iterator.hasNext()) {
            pathLocations.add(iterator.next());
        }
        ExtrudedPolygon pgon = new ExtrudedPolygon(pathLocations, elevation);
        if (ref != null) {
            pgon.setReferenceLocation(ref);
        }

        float[] corners = new float[8];
        corners[0] = 0.0f;
        corners[1] = 0.0f;
        corners[2] = 1.0f;
        corners[3] = 0.0f;
        corners[4] = 1.0f;
        corners[5] = 1.0f;
        corners[6] = 0.0f;
        corners[7] = 1.0f;

        pgon.setCapImageSource(image, corners, 4);

        ShapeAttributes capAttributes = new BasicShapeAttributes();
        capAttributes.setImageScale(1.0);
        capAttributes.setDrawInterior(true);
        capAttributes.setEnableLighting(true);
        capAttributes.setEnableAntialiasing(true);
        capAttributes.setDrawOutline(false);
        capAttributes.setInteriorOpacity(opacity);


        pgon.setEnableSides(false);
        pgon.setCapAttributes(capAttributes);
        sul.addRenderable(pgon);
        return sul;
    }

    /**
     * lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees) lat2,
     * lon2 = Latitude and Longitude of point 2 (in decimal degrees) unit = the
     * unit you desire for results where: 'M' is statute miles 'K' is kilometers
     * (default) 'N' is nautical miles
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @param unit
     * @return
     */
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit.equals("K")) {
            dist = dist * 1.609344;
        } else if (unit.equals("N")) {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /**
     * Converts decimal degrees to radians
     *
     * @param deg
     * @return
     */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * Converts radians to decimal degrees
     *
     * @param rad
     * @return
     */
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /**
     *
     * @param serverURI
     * @return
     */
    protected static List<LayerInfo> getLayerInfos(URI serverURI) {
        WMSCapabilities caps;
        final ArrayList<LayerInfo> layerInfos = new ArrayList<LayerInfo>();
        try {
            caps = WMSCapabilities.retrieve(serverURI);
            caps.parse();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

        // Gather up all the named layers and make a world wind layer for each.
        final List<WMSLayerCapabilities> namedLayerCaps = caps.getNamedLayers();
        if (namedLayerCaps == null) {
            log.debug("No named layers available for server: {}.", serverURI);
            return null;
        }

        try {
            for (WMSLayerCapabilities lc : namedLayerCaps) {
                Set<WMSLayerStyle> styles = lc.getStyles();
                if (styles == null || styles.isEmpty()) {
                    LayerInfo layerInfo = LayerInfo.create(caps, lc, null);
                    layerInfos.add(layerInfo);
                } else {
                    for (WMSLayerStyle style : styles) {
                        LayerInfo layerInfo = LayerInfo.create(caps, lc, style);
                        layerInfos.add(layerInfo);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return layerInfos;
    }
}
