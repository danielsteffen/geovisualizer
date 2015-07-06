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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
 * @author Daniel Steffen
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
    public static final String LON_LAT = "LonLat";
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
    private final String USER_AGENT = "Mozilla/5.0";

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
        URL url;
        if (input == null) {
            String msg = "input == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        } else if (input instanceof String) {
            try {
                URI uri = URI.create((String) input);
                url = uri.toURL();
            } catch (MalformedURLException ex) {
                String msg = "No valid input. Input no instance of "
                        + "String, URL, or URI.";
                LOG.error(msg);
                throw new IllegalArgumentException(msg);
            }
        } else if (input instanceof URI) {
            try {
                URI uri = (URI) input;
                url = uri.toURL();
            } catch (MalformedURLException ex) {
                String msg = "No valid input. Input no instance of "
                        + "String, URL, or URI.";
                LOG.error(msg);
                throw new IllegalArgumentException(msg);
            }
        } else if (input instanceof URL) {
            url = (URL) input;
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
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // optional default is GET
            con.setRequestMethod("GET");
            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            jp = f.createJsonParser(in);
            jp.nextToken();
            double tmp = 0.0d;
            boolean lat = false;
            boolean lon = false;

            do {
                HashMap<String, Object> values = new HashMap<>();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String namefield = jp.getCurrentName();
                    if (namefield != null) {
                        switch (namefield) {
                            case LAT:
                            case LATITUDE:
                                try {
                                    if (lon) {
                                        attributes.put(LON_LAT, POSITION);
                                        values.put(LON_LAT, new double[]{tmp, jp.getDoubleValue()});
                                        lat = lon = false;
                                    } else {
                                        tmp = jp.getDoubleValue();
                                        lat = true;
                                    }
                                } catch (JsonParseException ex) {
                                }
                                break;
                            case LON:
                            case LONGITUDE:
                                try {
                                    if (lat) {
                                        attributes.put(LON_LAT, POSITION);
                                        values.put(LON_LAT, new double[]{jp.getDoubleValue(), tmp});
                                        lat = lon = false;
                                    } else {
                                        tmp = jp.getDoubleValue();
                                        lon = true;
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
            if (attributes.containsKey(LON_LAT)) {
                List<double[]> points = new ArrayList<>();
                for (HashMap<String, Object> v : valuesList) {
                    if (v.containsKey(LON_LAT)) {
                        points.add((double[]) v.get(LON_LAT));
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
        LOG.debug("Valu: " + valuesList.get(id).toString());
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
        LOG.debug("Num points " + pointsList.get(0).size());
        LOG.debug("Points " + pointsList.get(0).toString());
        return pointsList;
    }
}
