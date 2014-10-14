/*
 *  SOS.java
 *
 *  Created by DFKI AV on 01.02.2013.
 *  Copyright (c) 2011-2014 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.io.sos;

import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.io.GeometryType;
import de.dfki.av.geovisualizer.io.sos.util.SOSRetreiver;
import de.dfki.av.geovisualizer.io.sos.util.SOSUtils;
import de.dfki.av.geovisualizer.io.sos.util.XMLUtils;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ISource} to read data from an SOS source.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class SOS implements ISource {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SOS.class);
    /**
     * The {@link SOSRetreiver}.
     */
    private final SOSRetreiver sosRetreiver;
    /**
     * The start date as {@link Calendar} object.
     */
    private Date start;
    /**
     * The end date as {@link Calendar} object.
     */
    private Date end;

    /**
     * Constructor for the {@link ISource} object of type {@link SOS}.
     *
     * @param input the URL as {@link String}, {@link URL}, or {@link URI}
     * object.
     * @throws IllegalArgumentException if {@code input == null}
     * @throws IllegalArgumentException if malformed {@link URI}
     * @throws IllegalArgumentException if {@code input} is not of type
     * {@link String}, {@link URI}, or {@link URL}.
     */
    public SOS(final Object input) {
        String url;
        if (input == null) {
            String msg = "input == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        } else if (input instanceof String) {
            url = (String) input;
        } else if (input instanceof URI) {
            URI uri = (URI) input;
            try {
                url = uri.toURL().toExternalForm();
            } catch (MalformedURLException ex) {
                LOG.error(ex.toString());
                throw new IllegalArgumentException(ex.toString());
            }
        } else if (input instanceof URL) {
            URL uri = (URL) input;
            url = uri.toExternalForm();
        } else {
            String msg = "No valid input. Input no instance of "
                    + "String, URL, or URI.";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }

        String[] parts = url.split("/sos/pox");
        String serviceURL = parts[0] + "/sos/pox";
        parts = parts[1].split("&");
        String sensorId = "none";
        String foi = "none";
        if (url.contains("&sensor=")) {
            for (String part : parts) {
                if (part.startsWith("sensor=")) {
                    sensorId = part.replaceFirst("sensor=", "");
                } else if (part.startsWith("foi=")) {
                    foi = part.replaceFirst("foi=", "");
                }
            }
        } else if (url.contains("&getAll")) {
            sensorId = "none";
            foi = "none";
        } else if (url.contains("&device=")) {
            for (String part : parts) {
                if (part.startsWith("device=")) {
                    sensorId = part.replaceFirst("device=", "");
                }
            }
            foi = "DeviceID";
        }
        if (url.contains("&date_begin=")) {
            for (String part : parts) {
                if (part.startsWith("date_begin=")) {
                    String dateString = part.replaceFirst("date_begin=", "");
                    start = SOSUtils.urlString2Calendar(dateString);
                }
            }
        }
        if (url.contains("&date_end=")) {
            for (String part : parts) {
                if (part.startsWith("date_end=")) {
                    String dateString = part.replaceFirst("date_end=", "");
                    end = SOSUtils.urlString2Calendar(dateString);
                }
            }
        }
        sosRetreiver = new SOSRetreiver(serviceURL, sensorId, foi, start, end);
        LOG.debug("SOS Reader initialized");
    }

    @Override
    public Map<String, Object> getAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();
        Map<String, String> outputs = sosRetreiver.getObservedProperties();
        if (outputs != null) {
            for (String output : outputs.keySet()) {
                switch (output) {
                    case XMLUtils.NAME_POSITION:
                        attributes.put(output, "Position");
                        break;
                    case "MAC":
                        attributes.put(output, "String");
                        break;
                    case "SensorUID":
                        attributes.put(output, "String");
                        break;
                    case "SessionID":
                        attributes.put(output, "String");
                        break;
                    case "DeviceID":
                        attributes.put(output, "String");
                        break;
                    case "nuubo_stress":
                        attributes.put(output, "String");
                        break;
                    case XMLUtils.NAME_TIME:
                        attributes.put(output, "String");
                        break;
                    default:
                        if (output instanceof String) {
                            attributes.put(output, "Number");
                        }
                        break;
                }
            }
            LOG.debug(outputs.keySet().size() + " attributes retrieved");
        } else {
            LOG.error("Failed to retrieve attributes!");
        }
        return attributes;
    }

    @Override
    public int getFeatureCount() {
        return sosRetreiver.size();
    }

    /**
     * Calls the {@link #sosRetreiver} to update the values.
     *
     * @return true if an update of the values was proceeded.
     */
    public boolean update() {
        return sosRetreiver.updateValues();
    }

    @Override
    public Object getValue(int id, String attributeName) {
        if (id == -1) {
            List<Object> list = sosRetreiver.getValues(attributeName, true, start, end);
            if (list != null) {
                return list.get(list.size() - 1);
            } else {
                return null;
            }
        }
        return sosRetreiver.getValue(id, attributeName);
    }

    @Override
    public double min(String attributeName) {
        double minValue = Double.POSITIVE_INFINITY;

        for (int i = 0; i < getFeatureCount(); i++) {
            Object obj = sosRetreiver.getValue(i, attributeName);
            if (obj != null) {
                if (obj instanceof Number) {
                    Number num = (Number) obj;
                    if (num.doubleValue() < minValue) {
                        minValue = num.doubleValue();
                    }
                } else {
                    LOG.warn("Value {} of attribute {} not of type 'Number'.", i, attributeName);
                }
            }
        }
        LOG.debug("My min value = {}.", minValue);

        return minValue;
    }

    @Override
    public double max(String attributeName) {
        double maxValue = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < getFeatureCount(); i++) {
            Object obj = sosRetreiver.getValue(i, attributeName);
            if (obj != null) {
                if (obj instanceof Number) {
                    Number number = (Number) obj;
                    if (number.doubleValue() > maxValue) {
                        maxValue = number.doubleValue();
                    }
                } else {
                    LOG.warn("Value {} of attribute {} not of type 'Number'.", i, attributeName);
                }
            }
        }
        LOG.debug("My max value = {}.", maxValue);

        return maxValue;
    }

    @Override
    public String getName() {
        return sosRetreiver.getServiceURL();
    }

    @Override
    public GeometryType getGeometryType() {
        return GeometryType.POINT;
    }

    @Override
    public double[] getBoundingBox() {
        return sosRetreiver.getBoundingBox(getPoints(0));
    }

    @Override
    public List<List<double[]>> getPoints(int featureId) {
        Object value = getValue(featureId, XMLUtils.DEF_POSITION);
        if (value != null && value instanceof double[]) {
            double[] obs = (double[]) value;
            List<List<double[]>> points = new ArrayList<>();
            List<double[]> pointlist = new ArrayList<>();
            double[] point = new double[2];
            point[0] = obs[0];
            point[1] = obs[1];
            pointlist.add(point);
            points.add(pointlist);
            return points;
        }
        return null;
    }
}
