/*
 *  LayerInfo.java 
 *
 *  Created by DFKI AV on 15.04.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Renderable;
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
 * Utility class for the generation of elevated WMS Layers
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class WMSHeightUtils {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(WMSHeightUtils.class);
    /**
     * Image resolution
     */
    private static final int RESOLUTION = 512;

    /**
     * Devides
     * <code>Sector</code> from the WMS data in subsectors and fill the
     * <code>SurfaceImageLayer</code> with the images retrieved for this
     * subsectors, in an elevation specified with the parameter
     * <code>elevation</code> and an opacity specified by the parameter
     * <code>opacity</code>.
     *
     * The amount of subsectors is decided in dependency of the length of the
     * diagonal (in kilometers) of the
     * <code>Sector</code>:
     *
     * <code>sector.subdivide(4 + (int) Math.sqrt(distance))</code> Which
     * results in (4+squrt(distance))^2 subsectors
     *
     * The images for the subsectors are retrieved with a hardcoded resolution
     * of 128px x 128px (
     * <code>private static final int RESOLUTION = 128;</code>)
     *
     * The level parameter for the WMSTiledImageLayer.composeImageForSector is
     * hardcoded to -1 which retrieves all levels with content for the image. (
     * <code>int level = -1;</code>)
     *
     * The scale parameter for the WMSTiledImageLayer.composeImageForSector is
     * hardcoded tp 1. because we don't want to change the scale of the image.
     *
     * @param caps
     * <code>WMSCapabiilities</code> of the Layer</code>
     * @param lcaps
     * <code>WMSLayerCapabilities</code> of the
     * <code>Layer</code>
     * @param params
     * <code>Parameters</code> for the
     * <code>Layer</code>
     * @param elevation Elevation for the layer (in meter)
     * @param opacity Opacity for the layer (100 is transparent, 0 is
     * intransparent)
     */
    protected static void addWMSDataToLayer(SurfaceImageLayer sul, WMSCapabilities caps, WMSLayerCapabilities lcaps, AVList params, double elevation, double opacity) {
        // Copy AVList to insulate changes from the caller.
        AVList configParams = params.copy();
        String image_format = "";
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
        Sector[] sectors = sector.subdivide(4 + (int) Math.sqrt(distance));
//        Sector[] sectors = sector.subdivide(1);

        int width = RESOLUTION;
        int height = RESOLUTION;
        double scale = 1.0;
        int level = -1;
        BufferedImage[] imgs = new BufferedImage[sectors.length];
        for (int i = 0; i < sectors.length; i++) {
            imgs[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        int i = -1;
        ElevatedSurfaceImage last = null;
        ElevatedSurfaceImage current = null;
        ElevatedSurfaceImage next;
        for (Sector sec : sectors) {
            i++;
            try {
                tmp.composeImageForSector(sec, width, height, scale,
                        level, image_format, false, imgs[i], 40000);
                next = addImageToLayer(sul, params.getStringValue(AVKey.DISPLAY_NAME) + "_" + elevation, imgs[i], sec, elevation, opacity);
                if (last != null) {
                    last.refresh();
                }
                if (current != null) {
                    current.refresh();
                }
                last = current;
                current = next;
            } catch (Exception e) {
                log.error("" + e);
            }
        }
        for (Renderable r : sul.getRenderables()) {
            ((ElevatedSurfaceImage) r).refresh();
        }
    }

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

    public static double getMaxElevationOfSector(Sector sector, Globe globe) {
        return globe.getMinAndMaxElevations(sector)[1];
    }

    /**
     *
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
     * @param opacity the Opacity for the image (1.0 is fully transparent)
     * @return
     */
    private static void addImageToLayer(SurfaceImageLayer sul, String name, BufferedImage image, Sector sector, double opacity) {
        sul.addImage(name, image, sector);
        sul.setOpacity(opacity);
        sul.setPickEnabled(false);

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
    private static ElevatedSurfaceImage addImageToLayer(SurfaceImageLayer sul, String name, BufferedImage image, Sector sector, double elevation, double opacity) {
        sul.setName(name);
        if (elevation == 0) {
            addImageToLayer(sul, name, image, sector, opacity);
            return null;
        }
        ElevatedSurfaceImage tl = new ElevatedSurfaceImage(image, sector);
        tl.setElevation(elevation);
        tl.setOpacity(opacity);
        tl.setFloating(true);
        tl.needsUpdate = true;
        sul.addRenderable(tl);
        return tl;
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
