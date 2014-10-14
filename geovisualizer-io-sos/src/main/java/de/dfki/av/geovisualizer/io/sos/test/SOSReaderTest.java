/*
 *  SOSReaderTest.java 
 *
 *  Created by DFKI AV on 01.02.2013.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.io.sos.test;

import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.io.sos.SOSReader;
import de.dfki.av.geovisualizer.io.sos.util.SOSUtils;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for the {@link SOSReader}.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class SOSReaderTest {
    
    /**
     * URLs to SOS servers
     */
    private static final String[] serverList = {
        "http://192.168.21.187:8080/52n-sos-webapp/sos/", 
        "http://localhost:8080/52n-sos-webapp/sos/", 
        "http://serv-2107:8080/52n-sos-webapp/sos/"};

    /**
     * SOS requests
     */
    private static final String[] requestList = {
        "pox&getAll", 
        "pox&getAll&date_begin=2014-09-01&date_end=2014-10-30",
        "pox&sensor=45e5f23bf4bf18c3:0001020304050607460842454344",
        "pox&sensor=1520c1220b7e6046:0001020304050607460842454344", 
        "pox&sensor=1520c1220b7e6046:0001020304050607460842454344&date_begin=2014-01-01&date_end=2014-10-30", 
        "pox&sensor=80ffc4f0941ffc2e:00010207460842454344",
        "pox&sensor=DeviceID", 
        "pox&device=dcbbbbf38aa3e33e&date_begin=2013-01-01&date_end=2013-07-30"};
    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(SOSReaderTest.class);

    /**
     * Main test method.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        SOSReader sosReader = new SOSReader();
        String url = serverList[0]+requestList[3];
        if (sosReader.canRead(url)) {
            log.debug("URL: " + url);
            ISource source = sosReader.read(url);
            log.debug("Number of features: {}", source.getFeatureCount());
            log.debug("Number of attributes: {}", source.getAttributes()
                    .size());
            for (String att : source.getAttributes().keySet()) {
                log.debug("Attribute: {}", att);
                log.debug("Values: {}", source.getValue(0, att));
            }
            double[] bbox = source.getBoundingBox();
            log.debug("Bounding box:");
            log.debug("lower corner: lat: {}, lon: {}", bbox[1], bbox[0]);
            log.debug("upper corner: lat: {}, lon: {}", bbox[3], bbox[2]);
            int limit = 25;
            if (source.getFeatureCount() < limit) {
                limit = source.getFeatureCount();
            }
            log.debug("First {} features:", limit);
            for (String attribute : source.getAttributes().keySet()) {
                log.debug(attribute);
                for (int i = 0; i < limit; i++) {
                    Object value = source.getValue(i, attribute);
                    if (value instanceof Number) {
                        log.debug(((Number) value).toString());
                    } else if (value instanceof double[]) {
                        double[] lonlat = (double[]) value;
                        log.debug("Lat: {}, Lon: {}", lonlat[1], lonlat[0]);
                    } else if (value instanceof String) {
                        log.debug((String) value);
                        try {
                            SOSUtils.calendar2SOSString(
                                    SOSUtils.string2Calendar((String) value));
                        } catch (ParseException ex) {
                        }
                    }
                }
            }
            log.debug("Retreive features:");
            for (String attribute : source.getAttributes().keySet()) {
                log.debug(attribute);
                Map<String, Integer> occurence = new HashMap<>();
                for (int i = 0; i < source.getFeatureCount(); i++) {
                    String valueString = null;
                    Object value;
                    try {
                        value = source.getValue(i, attribute);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        break;
                    }
                    if (value instanceof Number) {
                        valueString = ((Number) value).toString();
                    } else if (value instanceof double[]) {
                        double[] lonlat = (double[]) value;
                        valueString = "Lat: " + lonlat[1] + " Lon: " + lonlat[0];
                    } else if (value instanceof String) {
                        valueString = ((String) value);
                    }
                    if ((valueString != null) && !occurence.containsKey(valueString)) {
                        occurence.put(valueString, 1);
                    }
                }
                for (String att : occurence.keySet()) {
                    log.debug(att);
                }
            }
            log.debug("Retreive Points:");
            int i = 0;
            while (source.getPoints(i) != null) {
                for (List<double[]> points : source.getPoints(0)) {
                    for (double[] point : points) {
                        log.debug("Lat: {}, Lon: {}", point[1], point[0]);
                    }
                }
                i++;
            }
        } else {
            log.debug("Can't read from source");
        }
    }
}
