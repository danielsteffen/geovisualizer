/**
 * JSONSourceReaderTest.java
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
package net.graphicsmedia.geovisualizer.io.m2b.test;

import de.dfki.av.geovisualizer.core.ISource;
import net.graphicsmedia.geovisualizer.io.m2b.JSONSourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for the {@link JSONSourceReader}.
 *
 * @author Daniel Steffen
 */
public class JSONSourceReaderTest {

    /*
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JSONSourceReaderTest.class);

    public static final String SOURCE_1 = "http://192.168.59.103:8080/com.ccg.analyticsservice.rest/api/v1/events/list";
    public static final String SOURCE_2 = "http://192.168.59.103:8080/com.ccg.analyticsservice.rest/api/v1/events/list";
    public static final String SOURCE_3 = "http://192.168.59.103:8080/com.ccg.analyticsservice.rest/api/v1/events/list";

    /**
     * Main test method.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        JSONSourceReader reader = new JSONSourceReader();
        ISource json1 = reader.read(SOURCE_1);
        ISource json2 = reader.read(SOURCE_2);
        ISource json3 = reader.read(SOURCE_3);

        LOG.debug("Attributes (" + SOURCE_1 + "):");
        for (String attribute : json1.getAttributes().keySet()) {
            LOG.debug("Name: " + attribute + " Type: " + json1.getAttributes().get(attribute));
        }

        LOG.debug("");
        LOG.debug("-------------------------------");
        LOG.debug("");

        LOG.debug("Points (" + SOURCE_1 + "):");
        for (double[] latLon : json1.getPoints(0).get(0)) {
            LOG.debug("Lon: " + latLon[0] + " Lat: " + latLon[1]);
        }

        LOG.debug("");
        LOG.debug("-------------------------------");
        LOG.debug("");

        LOG.debug("Attributes (" + SOURCE_2 + "):");
        for (String attribute : json2.getAttributes().keySet()) {
            LOG.debug("Name: " + attribute + " Type: " + json2.getAttributes().get(attribute));
        }

        LOG.debug("");
        LOG.debug("-------------------------------");
        LOG.debug("");

        LOG.debug("Attributes (" + SOURCE_3 + "):");
        for (String attribute : json3.getAttributes().keySet()) {
            LOG.debug("Name: " + attribute + " Type: " + json3.getAttributes().get(attribute));
        }

        LOG.debug("Points (" + SOURCE_3 + "):");
        for (double[] latLon : json3.getPoints(0).get(0)) {
            LOG.debug("Lon: " + latLon[0] + " Lat: " + latLon[1]);
        }

        LOG.debug("");
        LOG.debug("-------------------------------");
        LOG.debug("");

        LOG.debug("Parsed Attributes (" + SOURCE_1 + "): " + json1.getFeatureCount());
        LOG.debug("Parsed points (" + SOURCE_1 + "): " + json1.getPoints(0).get(0).size());

        LOG.debug("");
        LOG.debug("-------------------------------");
        LOG.debug("");

        LOG.debug("Parsed Attributes (" + SOURCE_2 + "): " + json2.getFeatureCount());

        LOG.debug("");
        LOG.debug("-------------------------------");
        LOG.debug("");

        LOG.debug("Parsed Attributes (" + SOURCE_3 + "): " + json3.getFeatureCount());
        LOG.debug("Parsed points (" + SOURCE_3 + "): " + json3.getPoints(0).get(0).size());

    }
}
