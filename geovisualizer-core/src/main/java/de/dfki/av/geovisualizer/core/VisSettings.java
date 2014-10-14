/*
 * VisSettings.java
 *
 * Created by DFKI AV on 01.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisSettings {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(VisSettings.class);
    /**
     * User home directory of GeoVisualizer application.
     */
    public static final String USER_HOME_DIR = initialize();

    /**
     * Return the String representing the path to the GeoVisualizer user home.
     *
     * @return the path to the GeoVisualizer user home.
     */
    private static String initialize() {
        String seperator = System.getProperty("file.separator");
        String userHome = System.getProperty("user.home", ".");
        String geovisualizerUserHome = userHome + seperator + ".geovisualizer";
        File geovisualizerDir = new File(geovisualizerUserHome);
        if (geovisualizerDir.exists()) {
            LOG.debug("Directory already existing.");
        } else {
            if (geovisualizerDir.mkdir()) {
                LOG.debug("Directory: {} created.", geovisualizerUserHome);
            } else {
                LOG.warn("Could not create directory: {}.", geovisualizerUserHome);
            }
        }
        return geovisualizerUserHome;
    }
}
