/**
 * JSONSource.java
 *
 * Created by <a href="mailto:daniel.steffen@graphicsmedia.net">Daniel
 * Steffen</a> on 01.08.2014.
 *
 * Copyright (c) 2014 MOSAIC 2B Consortium Members. All rights reserved.
 *
 * This software is developed as part of the project MOSAIC 2B
 * (http://www.mobile-empowerment.org) and has received funding from the
 * European Union’s Seventh Framework Programme for research, technological
 * development and demonstration under grant agreement no 611796.
 */
package net.graphicsmedia.geovisualizer.io.m2b;

import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.io.GeometryType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ISource} to read data from an JSONSource source.
 *
 * @author Daniel Steffen <daniel.steffen at graphicsmedia.net>
 */
public class JSONSource implements ISource {

    /**
     *
     */
    public static final String DATE_FORMAT = "yyyy-MM-ss HH:mm:ss.S";
    /**
     *
     */
    public static final String LONGITUDE = "longitude";
    /**
     *
     */
    public static final String LON = "lon";
    /**
     *
     */
    public static final String LATITUDE = "latitude";
    /**
     *
     */
    public static final String LAT = "lat";
    /**
     *
     */
    public static final String LAT_LON = "LatLon";
    /**
     *
     */
    public static final String POSITION = "Position";
    /**
     *
     */
    public static final String DATE = "Date";
    /**
     *
     */
    public static final String NUMERIC = "Numeric";
    /**
     *
     */
    public static final String STRING = "String";
    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JSONSource.class);
    /**
     *
     */
    private JsonParser jp;
    /**
     *
     */
    private HashMap<String, Object> attributes;
    /**
     *
     */
    private List<HashMap<String, Object>> valuesList;
    /**
     *
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    /**
     *
     */
    private List<List<double[]>> pointsList;
    /**
     *
     */
    private HashMap<String, double[]> minMaxMap;

    /**
     * Constructor for the {@link ISource} object of type {@link JSONSource}.
     *
     * @param input the URL as {@link String}, {@link URL}, or {@link URI}
     * object.
     * @throws IllegalArgumentException if {@code input == null}
     * @throws IllegalArgumentException if malformed {@link URI}
     * @throws IllegalArgumentException if {@code input} is not of type
     * {@link String}, {@link URI}, or {@link URL}.
     */
    public JSONSource(final Object input) {
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

        JsonFactory f = new JsonFactory();
        try {
            attributes = new HashMap<>();
            valuesList = new ArrayList<>();
            minMaxMap = new HashMap<>();
            URL resUrl = JSONSource.class.getClassLoader().getResource(url);
            jp = f.createJsonParser(resUrl);
            jp.nextToken();
            double[] latLon = new double[2];
            boolean lat = false;
            boolean lon = false;

            HashMap<String, Object> values = new HashMap<>();
            do {
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String namefield = jp.getCurrentName();
                    if (namefield != null) {
                        switch (namefield) {
                            case LAT:
                            case LATITUDE:
                                try {
                                    latLon[1] = jp.getDoubleValue();
                                    lat = true;
                                    if (lon) {
                                        attributes.put(LAT_LON, POSITION);
                                        values.put(LAT_LON, latLon);
                                    }
                                } catch (JsonParseException ex) {
                                }
                                break;
                            case LON:
                            case LONGITUDE:
                                try {
                                    latLon[0] = jp.getDoubleValue();
                                    lon = true;
                                    if (lat) {
                                        attributes.put(LAT_LON, POSITION);
                                        values.put(LAT_LON, latLon);
                                    }
                                } catch (JsonParseException ex) {
                                }
                                break;
                            default:
                                try {
                                    values.put(namefield, dateFormat.parse(jp.getText()));
                                    attributes.put(namefield, DATE);
                                    continue;
                                } catch (ParseException e) {
                                }
                                try {
                                    String string = jp.getText();
                                    try {
                                        Double value = Double.parseDouble(jp.getText());
                                        values.put(namefield, value);
                                        attributes.put(namefield, NUMERIC);
                                        if (minMaxMap.containsKey(namefield)) {
                                            double[] minMax = minMaxMap.get(namefield);
                                            if (value < minMax[0]) {
                                                minMax[0] = value;
                                            }
                                            if (value > minMax[1]) {
                                                minMax[1] = value;
                                            }
                                        } else {
                                            double[] minMax = new double[2];
                                            minMax[0] = value;
                                            minMax[1] = value;
                                            minMaxMap.put(namefield, minMax);
                                        }
                                        continue;
                                    } catch (NumberFormatException ex) {
                                    }
                                    values.put(namefield, jp.getText());
                                    attributes.put(namefield, STRING);
                                } catch (JsonParseException ex) {
                                }
                        }
                    }
                }
                valuesList.add(values);
            } while (jp.nextToken() == JsonToken.START_OBJECT);
            pointsList = new ArrayList<>();
            if (attributes.containsKey(LAT_LON)) {
                List<double[]> points = new ArrayList<>();
                for (HashMap<String, Object> v : valuesList) {
                    if (v.containsKey(LAT_LON)) {
                        points.add((double[]) values.get(LAT_LON));
                    }
                }
                pointsList.add(points);
            }

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        LOG.debug("JSON Reader initialized");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public int getFeatureCount() {
        return attributes.size();
    }

    @Override
    public Object getValue(int id, String attributeName) {
        return valuesList.get(id).get(attributeName);
    }

    @Override
    public double min(String attributeName) {
        double minValue = Double.NEGATIVE_INFINITY;
        if (minMaxMap.containsKey(attributeName)) {
            minValue = minMaxMap.get(attributeName)[0];
        }
        return minValue;
    }

    @Override
    public double max(String attributeName) {
        double maxValue = Double.POSITIVE_INFINITY;
        if (minMaxMap.containsKey(attributeName)) {
            maxValue = minMaxMap.get(attributeName)[0];
        }
        return maxValue;
    }

    @Override
    public String getName() {
        return "Json";
    }

    @Override
    public GeometryType getGeometryType() {
        return GeometryType.POINT;
    }

    @Override
    public double[] getBoundingBox() {
        double maxLat = Double.NEGATIVE_INFINITY;
        double maxLon = Double.NEGATIVE_INFINITY;
        double minLat = Double.POSITIVE_INFINITY;
        double minLon = Double.POSITIVE_INFINITY;
        for (double[] point : getPoints(0).get(0)) {
            if (point[0] < minLat) {
                minLat = point[0];
            }
            if (point[1] < minLon) {
                minLon = point[1];
            }
            if (point[0] > maxLat) {
                maxLat = point[0];
            }
            if (point[1] > maxLon) {
                maxLon = point[1];
            }
        }
        double[] boundingBox = new double[4];
        boundingBox[1] = minLat;
        boundingBox[0] = minLon;
        boundingBox[3] = maxLat;
        boundingBox[2] = maxLon;
        return boundingBox;
    }

    @Override
    public List<List<double[]>> getPoints(int featureId) {
        return pointsList;
    }
}
