/*
 *  XMLUtils.java 
 *
 *  Created by DFKI AV on 01.02.2013.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.io.sos.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class which holds util methods for SOS XML handeling.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class XMLUtils {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(XMLUtils.class);

    public static final String TAG_VALUES = "swe:values";
    public static final String TOKEN_SEPARATOR = ",";
    public static final String BLOCK_SEPARATOR = ";";
    public static final String DEF_POSITION = "urn:ogc:def:phenomenon:ogc:1.0.30:position";
    public static final String DEF_TIME = "http://www.opengis.net/def/property/OGC/0/PhenomenonTime";
    public static final String NAME_POSITION = "position";
    public static final String NAME_TIME = "phenomenonTime";
    public static final String TAG_END_POSITION = "gml:endPosition";
    public static final String TAG_BEGIN_POSITION = "gml:beginPosition";
    public static final String TAG_RESPONSE_FORMAT = "sos:responseFormat";
    public static final String TAG_SOS_PROCEDURE = "sos:procedure";
    public static final String TAG_SWES_PROCEDURE = "swes:procedure";
    public static final String TAG_SWE_FIELD = "swe:field";

    public static final String DEVICE_ID = "DeviceID";
    public static final String SENSOR_UID = "SensorUID";
    public static final String SESSION_ID = "SessionID";

    /**
     * Parses a {@link List} of times which are represented as {@link Object}
     *
     * @param document sos XML document
     * @return {@link List} of {@link Object}
     */
    private static List<Object> parseDates(Element document) {
        if (document != null && document
                .getElementsByTagName(TAG_VALUES) != null && document
                .getElementsByTagName(TAG_VALUES).item(0) != null) {
            NodeList nl = document.getElementsByTagName(TAG_VALUES);
            String[] values = new String[nl.getLength()];
            for (int i = 0; i < nl.getLength(); i++) {
                values[i] = nl.item(i).getTextContent();
            }
            List<Object> parsedValues = new ArrayList<>();
            for (int j = 0; j < values.length; j++) {
                String[] v = values[j].split(BLOCK_SEPARATOR);
                for (int i = 0; i < v.length; i++) {
                    String timestamp = v[i].split(TOKEN_SEPARATOR)[0];
                    Date date = SOSUtils.sosString2Calendar(timestamp);
                    parsedValues.add(SOSUtils.calendar2String(date));
                }
            }
            return parsedValues;
        }
        return null;
    }

    /**
     * Parses a {@link List} of times which are represented as {@link Object}
     *
     * @param document sos xml document
     * @return {@link List} of {@link Object}
     */
    private static List<Object> parseDeviceIDs(Element document) {
        if (document != null && document
                .getElementsByTagName(TAG_VALUES) != null && document
                .getElementsByTagName(TAG_VALUES).item(0) != null) {
            NodeList nl = document.getElementsByTagName("om:procedure");
            String[] identifiers = new String[nl.getLength()];
            for (int i = 0; i < nl.getLength(); i++) {
                identifiers[i] = nl.item(i).getAttributes().item(0)
                        .getTextContent().split(":")[0];
            }
            nl = document.getElementsByTagName(TAG_VALUES);
            String[] values = new String[nl.getLength()];
            for (int i = 0; i < nl.getLength(); i++) {
                values[i] = nl.item(i).getTextContent();
            }
            List<Object> parsedValues = new ArrayList<>();
            for (int j = 0; j < values.length; j++) {
                String[] v = values[j].split(BLOCK_SEPARATOR);
                for (int i = 0; i < v.length; i++) {
                    parsedValues.add(identifiers[j]);
                }
            }
            return parsedValues;
        }
        return null;
    }

    static List<String> retreiveSensorUIDs(String deviceID, Document document) {
        if (document != null && document
                .getElementsByTagName("sos:procedure") != null && document
                .getElementsByTagName("sos:procedure").item(0) != null) {
            NodeList nl = document.getElementsByTagName("sos:procedure");
            List<String> identifiers = new ArrayList<>();
            for (int i = 0; i < nl.getLength(); i++) {
                String uid = nl.item(i).getAttributes().item(0)
                        .getTextContent();
                if (uid.split(":")[0].equals(deviceID)) {
                    identifiers.add(uid);
                }
            }
            return identifiers;
        }
        return null;
    }

    /**
     * Parses a {@link List} of times which are represented as {@link Object}
     *
     * @param document sos XML document
     * @return {@link List} of {@link Object}
     */
    private static List<Object> parseSensorUIDs(Element document) {
        if (document != null && document
                .getElementsByTagName(TAG_VALUES) != null && document
                .getElementsByTagName(TAG_VALUES).item(0) != null) {
            NodeList nl = document.getElementsByTagName("om:procedure");
            String[] identifiers = new String[nl.getLength()];
            for (int i = 0; i < nl.getLength(); i++) {
                identifiers[i] = nl.item(i).getAttributes().item(0)
                        .getTextContent();
            }
            nl = document.getElementsByTagName(TAG_VALUES);
            String[] values = new String[nl.getLength()];
            for (int i = 0; i < nl.getLength(); i++) {
                values[i] = nl.item(i).getTextContent();
            }
            List<Object> parsedValues = new ArrayList<>();
            for (int j = 0; j < values.length; j++) {
                String[] v = values[j].split(BLOCK_SEPARATOR);
                for (int i = 0; i < v.length; i++) {
                    parsedValues.add(identifiers[j]);
                }
            }
            return parsedValues;
        }
        return null;
    }

    private static List<Object> parseValue(int output, Element document) {
        List<Object> valueList = new ArrayList<>();
        if (document != null && document
                .getElementsByTagName(TAG_VALUES) != null && document
                .getElementsByTagName(TAG_VALUES).item(0) != null) {
            NodeList nl = document.getElementsByTagName(TAG_VALUES);
            String[] values = new String[nl.getLength()];
            for (int i = 0; i < nl.getLength(); i++) {
                values[i] = nl.item(i).getTextContent();
            }
            for (String value : values) {
                String[] v = value.split(BLOCK_SEPARATOR);
                for (String entry : v) {
                    String[] parts = entry.split(TOKEN_SEPARATOR);
                    try {
                        valueList.add(Double.parseDouble(parts[output]));
                    } catch (NumberFormatException ex) {
                        valueList.add(parts[output]);
                    } catch (IndexOutOfBoundsException ex){
                        valueList.add(0);
                    }
                }
            }
        }
        return valueList;
    }

    /**
     * Parses longitude and latitude values in degrees and return the values in
     * an double[2] array.
     *
     * @param document {@link Document}
     * @return Array of {@link Double}
     */
    public static List<Object> parseLonLatFromValue(Element document) {
        List<Object> positions = new ArrayList<>();
        if (document != null && document
                .getElementsByTagName(TAG_VALUES) != null && document
                .getElementsByTagName(TAG_VALUES).item(0) != null) {
            NodeList nl = document.getElementsByTagName(TAG_VALUES);
            String[] values = new String[nl.getLength()];
            for (int i = 0; i < nl.getLength(); i++) {
                values[i] = nl.item(i).getTextContent();
            }
            for (String value : values) {
                String[] v = value.split(BLOCK_SEPARATOR);
                int i = 0;
                for (String entry : v) {
                    String[] parts = entry.split(TOKEN_SEPARATOR);
                    for (String part : parts) {
                        try {
                            if (value.split("Point\\(").length > 1) {
                                String position = value.split("Point\\(")[1];
                                position = position.split("\\)#")[0];
                                position = position.replaceAll("\\(", "");
                                position = position.replaceAll("\\)", "");
                                double lat = Double.valueOf(position.split(" ")[0]);
                                double lon = Double.valueOf(position.split(" ")[1]);
                                double[] lonLat = new double[2];
                                lonLat[0] = lon;
                                lonLat[1] = lat;
                                positions.add(lonLat);
                            }
                        } catch (NumberFormatException ex) {
                            log.debug("Parsing [{}] - No numeric value [{}], skipping.",
                                    XMLUtils.NAME_POSITION, part);
                        }
                    }
                }
            }
        }
        if (positions.isEmpty()) {
            log.warn("No longitude and latititude values found.");
        }
        return positions;
    }

    /**
     * Parses a {@link List} of times which are represented as {@link Object}
     *
     * @param document sos XML document
     * @return {@link List} of {@link Object}
     */
    private static List<Object> parseSessionIDs(Element document) {
        if (document != null && document
                .getElementsByTagName("om:featureOfInterest") != null && document
                .getElementsByTagName("om:featureOfInterest").item(0) != null) {
            NodeList nl = document.getElementsByTagName("om:featureOfInterest");
            String[] identifiers = new String[nl.getLength()];
            for (int i = 0; i < nl.getLength(); i++) {
                identifiers[i] = nl.item(i).getAttributes().item(1)
                        .getTextContent();
            }
            nl = document.getElementsByTagName(TAG_VALUES);
            String[] values = new String[nl.getLength()];
            for (int i = 0; i < nl.getLength(); i++) {
                values[i] = nl.item(i).getTextContent();
            }
            List<Object> parsedValues = new ArrayList<>();
            for (int j = 0; j < values.length; j++) {
                String[] v = values[j].split(BLOCK_SEPARATOR);
                for (int i = 0; i < v.length; i++) {
                    parsedValues.add(identifiers[j]);
                }
            }
            return parsedValues;
        }
        return null;
    }

    /**
     * Transforms a {@link Document} in a {@link String} representation.
     *
     * @param doc xml {@link Document}
     * @return {@link String}
     */
    public static String xmlToString(Document doc) {
        try {
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            String xmlString = sw.toString();
            return xmlString;
        } catch (TransformerException ex) {
            log.error("" + ex);
        }
        return null;
    }

    /**
     * Parses the {@link List} of sensor outputs from a XML element.
     *
     * @param document XML {@link Element}
     * @return {@link HashMap}
     */
    public static List<String> parseOutputList(Element document) {
        List<String> values = new ArrayList<>();
        if (document != null && document
                .getElementsByTagName(TAG_SWE_FIELD) != null) {
            NodeList nl = document
                    .getElementsByTagName(TAG_SWE_FIELD);
            values.add(XMLUtils.DEF_TIME);
            values.add("FOI");
            values.add(XMLUtils.DEF_POSITION);
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                String definition = node.getFirstChild().getNextSibling().getAttributes().item(0).getTextContent();
                if (!definition.equals(DEF_POSITION) && !definition.equals(DEF_TIME)) {
                    values.add(definition);
                }
            }
        }
        return values;
    }

    /**
     * Parses the {@link HashMap} of sensor outputs from a XML element.
     *
     * @param document XML {@link Element}
     * @return {@link HashMap}
     */
    public static Map<String, String> parseSensorDescription(Element document) {
        Map<String, String> values = new HashMap<>();
        if (document != null && document
                .getElementsByTagName(TAG_SWE_FIELD) != null) {
            NodeList nl = document
                    .getElementsByTagName(TAG_SWE_FIELD);
            values.put(XMLUtils.NAME_TIME, XMLUtils.DEF_TIME);
            values.put("FOI", "FOI");
            values.put(XMLUtils.NAME_POSITION, XMLUtils.DEF_POSITION);
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                String name = node.getAttributes().item(0).getTextContent();
                String definition = node.getFirstChild().getNextSibling().getAttributes().item(0).getTextContent();
                if (!name.equals(DEF_POSITION) && !name.equals(DEF_TIME)) {
                    values.put(name, definition);
                }
            }
        }
        return values;
    }

    /**
     * Parses all values for the given observed property in the given document.
     *
     * @param sensor {@link HashMap}
     * @param document {@link Document}
     * @return {@link List} of {@link Object}
     */
    public static Map<String, List<Object>> parseValues(List<String> sensor, Element document) {
        Map<String, List<Object>> parsedValues = new HashMap<>();
        if (document != null
                && document.getElementsByTagName(TAG_VALUES) != null
                && document.getElementsByTagName(TAG_VALUES).item(0) != null) {
            for (String output : sensor) {
                switch (output) {
                    case SESSION_ID: {
                        parsedValues.put(output, XMLUtils.parseSessionIDs(document));
                        return parsedValues;
                    }
                    case DEF_POSITION: {
                        parsedValues.put(output, XMLUtils.parseLonLatFromValue(document));
                        break;
                    }
                    case SENSOR_UID: {
                        parsedValues.put(output, XMLUtils.parseSensorUIDs(document));
                        return parsedValues;
                    }
                    case DEVICE_ID: {
                        parsedValues.put(output, XMLUtils.parseDeviceIDs(document));
                        return parsedValues;
                    }
                    case NAME_TIME: {
                        parsedValues.put(output, XMLUtils.parseDates(document));
                        return parsedValues;
                    }
                    default: {
                        parsedValues.put(output, XMLUtils.parseValue(sensor.indexOf(output), document));
                    }
                }
            }
        }
        return parsedValues;
    }

}
