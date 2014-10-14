/*
 *  SOSRetreiver.java 
 *
 *  Created by DFKI AV on 01.02.2013.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.io.sos.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class which holds methods to retrieve data from a sos source.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class SOSRetreiver {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory
            .getLogger(SOSRetreiver.class);
    private final String serviceUrl;
    private final String sensorUID;

    private Map<String, String> sensor;
    private Map<String, List<Object>> cachedValues;
    private final String foi;
    private List<String> uids;
    private final Date start;
    private Date end;
    private Date date;

    /**
     * Creates a {@link SOSRetreiver}, for receiving data from an sos source
     *
     * @param serviceURL String representation of the sos service url (e.g.
     * http://www.example.com/sos)
     * @param sensorUID String representation of the sensor UUID (e.g.
     * urn:ogc:object:feature:Sensor:example)
     * @param foi
     * @param start
     * @param end
     */
    public SOSRetreiver(String serviceURL, String sensorUID, String foi,
            Date start, Date end) {
        this.serviceUrl = serviceURL;
        this.sensorUID = sensorUID;
        this.foi = foi;
        this.cachedValues = new HashMap<>();
        this.start = start;
        this.end = end;
        updateValues();
    }

    private List<String> getUIDs() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is;
        is = cl.getResourceAsStream("GetContents.xml");
        Document doc = SOSUtils.parseDoc(is);
        String xmlString = XMLUtils.xmlToString(doc);
        if (xmlString != null) {
            HttpURLConnection connection = getConnection(serviceUrl);
            OutputStream reqStream;
            try {
                reqStream = connection.getOutputStream();
                if (reqStream != null) {
                    reqStream.write(xmlString.getBytes("UTF-8"));
                    reqStream.flush();
                    reqStream.close();
                }
            } catch (IOException ex) {
                log.error("getUIDs: " + ex);
            }
            InputStream resStream;
            try {
                resStream = connection.getInputStream();
                doc = SOSUtils.parseDoc(resStream);
                List<String> ids = XMLUtils.retreiveSensorUIDs(sensorUID, doc);
                log.debug("Found " + ids.size() + " corresponding UIDs.");
                return ids;
            } catch (IOException e) {
                log.error("getUIDs: " + e);
            }
        }
        return new ArrayList<>();
    }

    /**
     * Method for retrieving a XML {@link Document} which contains all
     * observations or the last observation for a given observedProperty
     *
     * @param lastObservation if true only the last observation will be returned
     * @param start
     * @param end
     *
     * @return {@link Document}
     */
    private Document getObservations(boolean lastObservation, Date start,
            Date end) {
        if (start != null && end != null) {
            if (end.before(start)) {
                Date newStart = end;
                end = start;
                start = newStart;
            }
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is;
        if (lastObservation) {
            is = cl.getResourceAsStream("GetLastObservation.xml");
        } else if (start != null || end != null) {
            is = cl.getResourceAsStream("GetObservation_timePeriod.xml");
        } else if (sensorUID.equals("none") && foi.equals("none")) {
            is = cl.getResourceAsStream("GetAllObservations.xml");
        } else if (!sensorUID.contains(":") && foi.equals("DeviceID")) {
            is = cl.getResourceAsStream("GetAllObservations.xml");
        } else {
            is = cl.getResourceAsStream("GetObservations_basic.xml");
        }
        Document doc = SOSUtils.parseDoc(is);
        if (lastObservation) {
            try {
                doc.getFirstChild().removeChild(doc.getElementsByTagName(XMLUtils.TAG_SOS_PROCEDURE).item(0));
            } catch (NullPointerException ex) {
            }
        }
        if (sensorUID == null || sensorUID.equals("none")) {
            try {
                doc.getFirstChild().removeChild(doc.getElementsByTagName(XMLUtils.TAG_SOS_PROCEDURE).item(0));
            } catch (NullPointerException ex) {
            }
        }
        if (!sensorUID.contains(":") && foi.equals("DeviceID")) {
            try {
                doc.getFirstChild().removeChild(doc.getElementsByTagName(XMLUtils.TAG_SOS_PROCEDURE).item(0));
            } catch (NullPointerException e) {
            }
            Node res = doc.getElementsByTagName(XMLUtils.TAG_RESPONSE_FORMAT).item(0).cloneNode(true);
            doc.getFirstChild().removeChild(doc.getElementsByTagName(XMLUtils.TAG_RESPONSE_FORMAT).item(0));
            if (this.uids == null) {
                this.uids = getUIDs();
            }
            for (String uid : uids) {
                Node node = doc.createElement(XMLUtils.TAG_SOS_PROCEDURE);
                node.setTextContent(uid);
                doc.getFirstChild().appendChild(node);
            }
            doc.getFirstChild().appendChild(res);
        }
        try {
            NodeList nodeList = doc.getDocumentElement().getElementsByTagName(XMLUtils.TAG_SOS_PROCEDURE);
            if (nodeList != null && nodeList.getLength() > 0) {
                nodeList.item(0).setTextContent(sensorUID);
            }
        } catch (DOMException e) {
            log.error("getObservations: " + e);
        }
        if (!lastObservation && (start != null || end != null)) {
            if (start == null) {
                start = new Date(0);
            }
            if (end == null) {
                end = new Date();
            }
            doc.getDocumentElement().getElementsByTagName(XMLUtils.TAG_BEGIN_POSITION)
                    .item(0).setTextContent(SOSUtils.calendar2SOSString(start));
            doc.getDocumentElement().getElementsByTagName(XMLUtils.TAG_END_POSITION)
                    .item(0).setTextContent(SOSUtils.calendar2SOSString(end));
        }
        if (doc != null) {
            String xmlString = XMLUtils.xmlToString(doc);
            if (xmlString != null) {
                HttpURLConnection connection = getConnection(serviceUrl);
                OutputStream reqStream;
                try {
                    reqStream = connection.getOutputStream();
                    if (reqStream != null) {
                        reqStream.write(xmlString.getBytes("UTF-8"));
                        reqStream.flush();
                        reqStream.close();
                    }
                } catch (IOException ex) {
                    log.error("getObservations: " + ex);
                }
                InputStream resStream;
                try {
                    resStream = connection.getInputStream();
                    Document resDoc = SOSUtils.parseDoc(resStream);
                    return resDoc;
                } catch (IOException ex) {
                    log.error("getObservations: " + ex);
                } finally {
                    connection.disconnect();
                }
            }
        }
        return null;
    }

    /**
     * Opens a {@link HttpURLConnection} to the sos source with the given
     * service url
     *
     * @param serviceURL {@link String} representation of the sos service url
     * @return {@link HttpURLConnection}
     */
    private HttpURLConnection getConnection(String serviceURL) {
        try {
            URL oURL = new URL(serviceURL);
            HttpURLConnection con = (HttpURLConnection) oURL.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-type", "text/xml; charset=utf-8");

            con.setDoOutput(true);
            con.setDoInput(true);
            return con;
        } catch (IOException ex) {
            log.error("getConnection: " + ex);
        }
        return null;
    }

    /**
     * Returns the amount of observations which are recorded on the sos service
     * for the given sensor.
     *
     * @return {@link Integer}
     */
    public int size() {
        if (cachedValues == null || cachedValues.isEmpty()) {
            updateValues();
        }
        if (cachedValues != null) {
            int size = Integer.MAX_VALUE;
            int count = 0;
            for (String observedProperty : cachedValues.keySet()) {
                if (cachedValues.get(observedProperty) != null) {
                    count = cachedValues.get(observedProperty).size();
                    if (count < size) {
                        size = count;
                    }
                }
            }
            if (count == 0) {
                return 0;
            } else {
                return size;
            }
        }
        return 0;
    }

    /**
     * Returns the service URL of the SOS service.
     *
     * @return {@link String} representation of the SOS service URL
     */
    public String getServiceURL() {
        return serviceUrl;
    }

    /**
     * Retrieves the values for the given observed property.
     *
     * @param observedProperty observed property as {@link String}
     * @param lastObservation if true only the last observation will be
     * returned.
     * @param start
     * @param end
     * @return {@link List} of {@link Object}
     */
    public List<Object> getValues(String observedProperty,
            boolean lastObservation, Date start,
            Date end) {
        if (sensor != null && sensor.containsKey(observedProperty)) {
            observedProperty = sensor.get(observedProperty);
        } else {
            observedProperty = XMLUtils.DEF_POSITION;
        }
        if (!cachedValues.containsKey(observedProperty) || cachedValues.get(observedProperty) == null) {
            switch (observedProperty) {
                case "<<NO_ATTRIBUTE>>": {
                    List<Object> list = new ArrayList<>();
                    int limit = size();
                    for (int i = 0; i < limit; i++) {
                        list.add(0.0d);
                    }
                    cachedValues.put(observedProperty, list);
                    break;
                }
                default: {
                    updateValues(lastObservation, start, end);
                    break;
                }
            }
        }
        return cachedValues.get(observedProperty);
    }

    public boolean updateValues(boolean lastObservation, Date start1, Date end1) {
        Document resultDoc = getObservations(lastObservation, start1, end1);
        if (resultDoc != null) {
            List<String> outputList = XMLUtils.parseOutputList(resultDoc.getDocumentElement());
            this.sensor = XMLUtils.parseSensorDescription(resultDoc.getDocumentElement());
            if (outputList != null && !outputList.isEmpty()) {
                Map<String, List<Object>> valueMap = XMLUtils.parseValues(outputList, resultDoc.getDocumentElement());
                if (valueMap != null && !valueMap.isEmpty()) {
                    cachedValues = valueMap;
                }
            } else {
                log.warn("No output list retreived!");
                return true;
            }
        } else {
            log.warn("No result document retreived!");
            return true;
        }
        return false;
    }

    /**
     * Returns the observed properties as array of {link String}.
     *
     * @return the observed properties as array of {link String}
     */
    public Map<String, String> getObservedProperties() {
        return sensor;
    }

    /**
     * Retrieves the bounding box for the given serviceUrl and sensor.
     *
     * @param points {@link List}
     *
     * @return array of {@link Double} values which represents the bounding box
     * of the observation data.
     */
    public double[] getBoundingBox(List<List<double[]>> points) {
        double[] bbox = new double[4];
        bbox[0] = 180.0d;
        bbox[1] = 90.0d;
        bbox[2] = -180.0d;
        bbox[3] = -90.0d;
        if (points != null) {
            for (List<double[]> pointList : points) {
                for (double[] point : pointList) {
                    if (point[0] < bbox[0]) {
                        bbox[0] = point[0];
                    }
                    if (point[1] < bbox[1]) {
                        bbox[1] = point[1];
                    }
                    if (point[0] > bbox[2]) {
                        bbox[2] = point[0];
                    }
                    if (point[1] > bbox[3]) {
                        bbox[3] = point[1];
                    }
                }
            }
        } else {
            bbox[0] = -180.0d;
            bbox[1] = -90.0d;
            bbox[2] = 180.0d;
            bbox[3] = 90.0d;
        }
        return bbox;
    }

    /**
     * Returns the observation count.
     *
     * @return observation count as {@link Integer}
     */
    public int getObservationCount() {
        if (sensor != null) {
            return getValues(sensor.keySet().iterator().next(), false, start, end)
                    .size();
        } else {
            return getValues(XMLUtils.NAME_POSITION, false, start, end).size();
        }
    }

    /**
     * Retrieves the value for a given id and observed property.
     *
     * @param observationId observation id
     * @param observedProperty observed property
     * @return {@link Object}
     */
    public Object getValue(int observationId, String observedProperty) {
        if (!cachedValues.containsKey(observedProperty)
                || cachedValues.get(observedProperty) == null
                || !(observationId < cachedValues.get(observedProperty)
                .size())) {
            cachedValues.put(observedProperty, getValues(observedProperty,
                    false, start, end));
        }
        if (cachedValues.get(observedProperty) != null && !(observationId < cachedValues.get(observedProperty).size())) {
            updateValues();
            if (!(observationId < cachedValues.get(observedProperty).size())) {
                return 0.0;
            }
        }
        if (cachedValues.get(observedProperty) != null) {
            return cachedValues.get(observedProperty).get(observationId);
        }
        return null;
    }

    private Date getLastDate() {
        Date last;
        Document resultDoc = getObservations(true, new Date(0), new Date());
        if (resultDoc != null) {
            List<String> outputList = XMLUtils.parseOutputList(resultDoc.getDocumentElement());
            if (outputList != null && !outputList.isEmpty()) {
                Map<String, List<Object>> valueMap = XMLUtils.parseValues(outputList, resultDoc.getDocumentElement());
                if (valueMap != null) {
                    try {
                        String timestamp = (String) valueMap.get(sensor.get(XMLUtils.NAME_TIME)).get(0);
                        last = SOSUtils.sosString2Calendar(timestamp);
                    } catch (NullPointerException e) {
                        log.warn("getLastDate: Nullpointer Exception!");
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                log.warn("No output list retreived!");
                return null;
            }
        } else {
            log.warn("No result document retreived!");
            return null;
        }
        return last;
    }

    private Object getLastEntry(String property) {
        Object last = null;
        Document resultDoc;
        if (property.equalsIgnoreCase(XMLUtils.NAME_TIME)) {
            resultDoc = getObservations(true, new Date(0), new Date());
        } else {
            resultDoc = getObservations(true, new Date(0), new Date());
        }
        if (resultDoc != null) {
            List<String> outputList = XMLUtils.parseOutputList(resultDoc.getDocumentElement());
            if (outputList != null && !outputList.isEmpty()) {
                Map<String, List<Object>> valueMap = XMLUtils.parseValues(outputList, resultDoc.getDocumentElement());
                if (valueMap != null) {
                    try{
                        last = valueMap.get(property).get(0);
                        return last;
                    }catch (NullPointerException e){
                        
                    }
                }
            } else {
                log.warn("No output list retreived!");
            }
        } else {
            log.warn("No result document retreived!");
        }
        return last;
    }

    /**
     * Checks if an update of the cached values is needed. If an update is
     * required the cached updates will be updated with the data on the sos
     * service.
     *
     * @return true if an update was proceeded.
     */
    public final boolean updateValues() {
        boolean updated = false;
        if (sensor != null) {
            if (cachedValues != null && !cachedValues.isEmpty()
                    && cachedValues.get(XMLUtils.NAME_TIME) != null) {
                boolean before = false;
                if (!cachedValues.get(XMLUtils.NAME_TIME).isEmpty()) {
                    if (date != null) {
                        end = getLastDate();
                        if (date.before(end)) {
                            before = true;
                        }
                    } else {
                        before = false;
                        date = getLastDate();
                    }
                } else {
                    before = true;
                    date = new Date(0);
                }
                if (before) {
                    for (String observedProperty : sensor.values()) {
                        if (cachedValues.containsKey(observedProperty)
                                && !cachedValues.get(observedProperty).isEmpty()) {
                            Object last = getLastEntry(observedProperty);
                            cachedValues.get(observedProperty).add(last);
                        } else {
                            cachedValues.put(observedProperty,
                                    getValues(observedProperty, false, date, end));
                        }
                    }
                    date = end;
                    updated = true;
                }
            } else {
                for (String output : sensor.keySet()) {
                    cachedValues.put(output, getValues(output, false, start,
                            end));
                }
                updated = true;
            }
        } else if (sensorUID.equals("none") && foi.equals("none")) {
            cachedValues.put("DeviceID", getValues("DeviceID", false, start, end));
            updated = true;
        } else {
            updateValues(false, start, end);
            updated = true;
        }
        if (!cachedValues.isEmpty()) {
            if (!cachedValues.containsKey("<<NO_ATTRIBUTE>>")) {
                cachedValues.put("<<NO_ATTRIBUTE>>", getValues("<<NO_ATTRIBUTE>>",
                        false, start, end));
            }
        }
        return updated;
    }

}
