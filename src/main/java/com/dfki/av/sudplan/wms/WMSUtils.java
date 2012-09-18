/*
 *  WMSUtils.java 
 *
 *  Created by DFKI AV on 15.04.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for the generation of elevated WMS data handeling
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class WMSUtils {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(WMSUtils.class);

    /**
     * Retrieves the reference location of an
     * <code>ExtrudedPolygon</code> with is created on the
     * <code>Sector</code>
     * <code>sector</code>
     *
     * @param sector
     * <code>Sector</code> in which the reference location for the
     * <code>ExtrudedPolygon</code> should be calculated.
     * @return reference location of the given sector
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

    /**
     * Returns maximum elevation of the given sector on the given globe
     *
     * @param sector
     * @param globe
     * @return maximum elevation
     */
    public static double getMaxElevationOfSector(Sector sector, Globe globe) {
        return globe.getMinAndMaxElevations(sector)[1];
    }

    /**
     * lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees) lat2,
     * lon2 = Latitude and Longitude of point 2 (in decimal degrees) unit = the
     * unit you desire for results where: 'M' is statute miles 'K' is kilometers
     * (default) 'N' is nautical miles
     *
     * @param lat1 Latitude value in decimal degrees of the first point
     * @param lon1 Longitude value in decimal degrees of the first point
     * @param lat2 Latitude value in decimal degrees of the second point
     * @param lon2 Longitude value in decimal degrees of the second point
     * @param unit unit for the distance calculation 'M' is statute miles, 'K'
     * is kilometers, (default) 'N' is nautical miles
     * @return distance
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
     * Calculates the greatest distance between the corner points of the given
     * bounding box
     *
     * @param s bounding box for which the distance should be calculated
     * @return distance as double (in kilometers)
     */
    public static double distance(Sector s) {
        return distance(s.getMinLatitude().degrees, s.getMinLongitude().degrees, s.getMaxLatitude().degrees, s.getMaxLongitude().degrees, "K");
    }

    /**
     * Calculates the area of the {@link Sector}
     *
     * @param s bounding box for which the distance should be calculated
     * @return area as double (in kilometers)
     */
    public static double area(Sector s) {
        Double[] verticies = verticies(s);
        if (verticies[0] == 0 || verticies[1] == 0) {
            return verticies[2] * verticies[3];
        } else {
            return verticies[0] * verticies[1];
        }
    }

    /**
     * Calculates the verticies of the {@link Sector}
     *
     * @param s bounding box for which the distance should be calculated
     * @return verticies as array of {@link Double} (in kilometers)
     */
    public static Double[] verticies(Sector s) {
        Angle a = s.getMinLatitude();
        Angle b = s.getMinLongitude();
        Angle c = s.getMaxLatitude();
        Angle d = s.getMaxLongitude();
        Double[] vericies = new Double[4];
        vericies[0] = distance(a.degrees, b.degrees, a.degrees, d.degrees, "K");
        vericies[1] = distance(a.degrees, d.degrees, c.degrees, d.degrees, "K");
        vericies[2] = distance(c.degrees, d.degrees, c.degrees, b.degrees, "K");
        vericies[3] = distance(c.degrees, b.degrees, a.degrees, b.degrees, "K");
        return vericies;
    }

    /**
     * Converts decimal degrees to radians
     *
     * @param deg degree value
     * @return radian value
     */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * Retreives a {@link List} of {@link LayerInfo} which are represented
     * with the {@link WMSCapabilities}.
     * 
     * @param namedLayerCaps {@link List} of {@link WMSLayerCapabilities}
     * @param caps {@link WMSCapabilities} which represents the WMS data
     * @return {@link List} of {@link LayerInfo}
     */
    private static List<LayerInfo> getLayerInfos(final List<WMSLayerCapabilities> namedLayerCaps, WMSCapabilities caps) {
        List<LayerInfo> layerInfos = new ArrayList<LayerInfo>();
        for (WMSLayerCapabilities lc : namedLayerCaps) {
            Set<WMSLayerStyle> styles = lc.getStyles();
            if (styles == null || styles.isEmpty()) {
                LayerInfo layerInfo = new LayerInfo(caps, lc, null);
                layerInfos.add(layerInfo);
            } else {
                for (WMSLayerStyle style : styles) {
                    LayerInfo layerInfo = new LayerInfo(caps, lc, style);
                    layerInfos.add(layerInfo);
                }
            }
        }
        return layerInfos;
    }

    /**
     * Retreives a {@link List} of {@link LayerInfo} which are represented
     * with the {@link URI}.
     * 
     * @param uri WMS server {@link URI}
     * @param lf Top layer of the time series.
     * @return {@link List} of {@link LayerInfo}
     */
    private static List<LayerInfo> getTimeSeriesLayerInfos(List<WMSLayerCapabilities> namedLayerCaps, WMSCapabilities caps, LayerInfo lf) {
        List<LayerInfo> layerInfos = new ArrayList<LayerInfo>();
        String layerName;
        String[] parts;
        boolean isStartPosition = false;
        List<LayerInfo> layers = getLayerInfos(namedLayerCaps, caps);
        for (int i = 0; i < layers.size(); i++) {
            LayerInfo li = layers.get(i);
            if (isStartPosition) {
                layerInfos.add(li);
                layerName = li.getTitle();
                parts = layerName.split(" ");
                if (parts.length > 1) {
                    String identifier = parts[0];
                    for (int j = (i + 1); j < layers.size(); j++) {
                        li = layers.get(j);
                        layerName = li.getTitle();
                        parts = layerName.split(" ");
                        if (parts.length > 1) {
                            String prefix = parts[0];
                            if (prefix.equals(identifier)) {
                                layerInfos.add(li);
                            } else {
                                return layerInfos;
                            }
                        } else {
                            return layerInfos;
                        }
                    }
                }
            }
            if (li.getTitle().equals(lf.getTitle())) {
                isStartPosition = true;
            }
        }
        return layerInfos;
    }

    /**
     * Converts radians to decimal degrees
     *
     * @param rad radian value
     * @return degree value
     */
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /**
     * Parses the data reveived from the wms server to {@link LayerInfo}
     *
     * @param request request url
     * @return parsed data as {@link LayerInfo}
     * @throws Exception
     */
    public static LayerInfo parseWMSRequest(String request) throws Exception {
        URI url = new URI(request.trim());
        String[] parts = request.split("&");
        String layerName = "";
        for (String part : parts) {
            if (part.startsWith("layers=")) {
                layerName = part.replaceFirst("layers=", "");
            } else {
                return null;
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
     * Note that the method will return all elements of a time series if a
     * request {@link URI} is given which contains a layer which ends with '[]'
     *
     *
     * @param uri
     * @return parsed data as list of {@link LayerInfo}
     *
     */
    public static List<LayerInfo> getLayerInfos(URI uri) {
        WMSCapabilities caps;
        try {
            caps = WMSCapabilities.retrieve(uri);
            caps.parse();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

        // Gather up all the named layers and make a world wind layer for each.
        final List<WMSLayerCapabilities> namedLayerCaps = caps.getNamedLayers();
        if (namedLayerCaps == null) {
            log.debug("No named layers available for server: {}.", uri);
            return null;
        }
        LayerInfo lf = null;
        boolean isTimeSeries = false;
        try {
            lf = parseWMSRequest(uri.toString());
            if (lf != null && lf.getTitle().contains("[]")) {
                isTimeSeries = true;
            } else {
                isTimeSeries = false;
            }
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        try {
            if (isTimeSeries) {
                return getTimeSeriesLayerInfos(namedLayerCaps, caps, lf);
            } else {
                return getLayerInfos(namedLayerCaps, caps);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
