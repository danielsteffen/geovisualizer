/*
 *  WMSHeightUtils.java 
 *
 *  Created by DFKI AV on 15.04.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for the generation of elevated WMS Layers
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class WMSHeightUtils {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(WMSHeightUtils.class);
    private static String image_format = "image/png";

    /**
     * Retrieves the reference location of an
     * <code>ExtrudedPolygon</code> with is created on the
     * <code>Sector</code>
     * <code>sector</code>
     *
     * @param sector
     * <code>Sector</code> in which the reference location for the
     * <code>ExtrudedPolygon</code> should be calculated.
     * @return
     */
    public static LatLon getReferenceLocation(Sector sector) {
        ArrayList<LatLon> pathLocations = new ArrayList<LatLon>();
        Iterator<LatLon> iterator = sector.iterator();
        while (iterator.hasNext()) {
            pathLocations.add(iterator.next());
        }
        ExtrudedPolygon pgon = new ExtrudedPolygon(pathLocations, 1.0);
        return pgon.getReferenceLocation();
    }

    public static String getImageFormat() {
        return image_format;
    }

    public static double getMaxElevationOfSector(Sector sector, Globe globe) {
        return globe.getMinAndMaxElevations(sector)[1];
    }

    /**
     * Adds an image to the
     * <code>SurfaceImageLayer</code>
     * <code>sul</code> mapped to the ground terrain.
     *
     * @param sul the
     * <code>SurfaceLayer</code>
     * @param name
     * @param image
     * <code>BufferedImage</code> in which the image is stored
     * @param sector
     * <code>Sector</code> in which the image should be mapped
     * @param elevation the elevation for the image (in meter)
     * @param opacity the Opacity for the image (1.0 is fully transparent)
     * @return
     */
    public static void addImageToLayer(ElevatedSurfaceLayer sul, BufferedImage image, Sector sector, double elevation, double opacity) {
        if (elevation == 0) {
            sul.addImage(sul.getName()+sector.toString(), image, sector);
            sul.setOpacity(opacity);
            sul.setPickEnabled(false);
            return;
        }
        ElevatedSurfaceImage tl = new ElevatedSurfaceImage(image, sector, -1);
        tl.setElevation(elevation);
        tl.setOpacity(opacity);
        tl.setFloating(true);
        sul.addRenderable(tl);
//        sul.refresh();
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

    public static double distance(Sector s) {
        return distance(s.getMinLatitude().degrees, s.getMinLongitude().degrees, s.getMaxLatitude().degrees, s.getMaxLongitude().degrees, "K");
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

    public static LayerInfo parseWMSRequest(String request) throws Exception {
        URI url = new URI(request.trim());
        String[] parts = request.split("&");
        String layerName = "";
        for (String part : parts) {
            if (part.startsWith("layers=")) {
                layerName = part.replaceFirst("layers=", "");
            }
        }
        for (LayerInfo li : getLayerInfos(url)) {
            if (li.getTitle().equals(layerName)) {
                return li;
            }
        }
        return null;
    }

    /**
     * Retrieve a list with
     * <code>LayerInfo</code> for each layer from the wms server specified with
     * the parameter
     * <code>serverURI</code>.
     *
     * @param serverURI
     * @return
     */
    public static List<LayerInfo> getLayerInfos(URI serverURI) {
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
