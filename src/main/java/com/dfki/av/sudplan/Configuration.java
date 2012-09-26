/*
 *  Configuration.java 
 *
 *  Created by DFKI AV on 01.08.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan;

import com.dfki.av.sudplan.vis.core.VisSettings;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import javax.imageio.ImageIO;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class responsible for the configuration of the sudplan3D application.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public final class Configuration {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
    /**
     * The sudplan3D image used as window and dialog icons.
     */
    public static final Image SUDPLAN_3D_IMAGE = initImage();
    /**
     * The current version of the configuration file.
     */
    private static final String VERSION = "1.1";
    /**
     * The sudplan3D configuration.
     */
    private static final XMLConfiguration XML_CONFIG = initConfig();

    /**
     * Initialize the {@link #SUDPLAN_3D_IMAGE}.
     *
     * @return the {@link Image} to return or null.
     */
    private static Image initImage() {
        log.info("Init suplan3D icon...");
        Image image = null;
        try {
            ClassLoader loader = Configuration.class.getClassLoader();
            URL iconURL = loader.getResource("icons/sudplan3D.png");
            image = ImageIO.read(iconURL);
        } catch (IOException ex) {
            log.error(ex.toString());
        }

        return image;
    }

    /**
     * Initialize the {@link XMLConfiguration} for the application.
     *
     * @return the {@link #XML_CONFIG} to return.
     */
    private static XMLConfiguration initConfig() {
        log.debug("Init sudplan3D user home...");
        String userHomeDir = VisSettings.USER_HOME_DIR;

        log.info("Init sudplan3D configuration...");
        // Check whether the sudplan3D config file already exists.
        final File userConfigFile = new File(userHomeDir + "/config/sudplan3D.xml");
        if (!userConfigFile.exists()) {
            log.debug("No configuration file existing.");
            installConfigFile(userConfigFile, userHomeDir, userHomeDir);
        } else {
            log.debug("Configuration file existing. Checking version ...");
            if (!isConfigFileSupported(userConfigFile)) {
                log.debug("Configuration file not supported.");
                log.debug("Delete old configuration file.");
                userConfigFile.delete();
                installConfigFile(userConfigFile, userHomeDir, userHomeDir);
            } else {
                log.debug("Configuration file supported.");
            }
        }

        XMLConfiguration xmlConfig = null;
        try {
            xmlConfig = new XMLConfiguration(userConfigFile);
            xmlConfig.setAutoSave(true);
            for (Iterator<String> it = xmlConfig.getKeys(); it.hasNext();) {
                String key = it.next();
                log.debug("{} : {}", key, xmlConfig.getString(key));
            }
        } catch (ConfigurationException ex) {
            log.error(ex.toString());
        }
        return xmlConfig;
    }

    /**
     * Save the configuration file {@code config/sudplan3D.xml} from the
     * resources to the the file {@code file}. Adding the additional properties
     * {@code sudplan3D.user.dir} and {@code sudplan3D.working.dir}.
     *
     * @param file the configuration {@link File}
     * @param userHomeDir the user home directory
     * @param workingDir the working directory
     */
    private static void installConfigFile(File file, String userHomeDir, String workingDir) {
        log.debug("Installing configuration to {}...", file.getAbsoluteFile());
        try {
            ClassLoader loader = Configuration.class.getClassLoader();
            URL url = loader.getResource("config/sudplan3D.xml");
            XMLConfiguration xmlInitialConfig = new XMLConfiguration(url);
            xmlInitialConfig.addProperty("sudplan3D.user.dir", userHomeDir);
            xmlInitialConfig.addProperty("sudplan3D.working.dir", workingDir);
            xmlInitialConfig.save(file);
        } catch (ConfigurationException ex) {
            log.error(ex.toString());
        }
    }

    /**
     * Returns whether the configuration file {@code file} is supported by the
     * current implementation.
     *
     * @param file the configuration {@link File} to check
     * @return {@code true} if a {@code version} tag exists and its value is
     * equal to {@link #VERSION}. Otherwise {@code false}.
     */
    private static boolean isConfigFileSupported(File file) {
        try {
            XMLConfiguration xmlConfig = new XMLConfiguration(file);
            if (!xmlConfig.containsKey("version")) {
                log.debug("No version tag.");
                return false;
            }

            String version = xmlConfig.getString("version");
            if (version.equalsIgnoreCase(VERSION)) {
                return true;
            } else {
                log.debug("Version {} not supported.", version);
                return false;
            }
        } catch (ConfigurationException ex) {
            log.error(ex.toString());
        }
        return false;
    }

    /**
     * Return the {@link XMLConfiguration}.
     *
     * @return the {@link XMLConfiguration} to return.
     */
    public static XMLConfiguration getXMLConfiguration() {
        return XML_CONFIG;
    }
}
