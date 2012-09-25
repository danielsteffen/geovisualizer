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
     * Initialize the
     */
    private static XMLConfiguration initConfig() {
        log.debug("Init sudplan3D user home...");
        String userHomeDir = VisSettings.USER_HOME_DIR;

        log.info("Init sudplan3D working directory...");
        File workingDir = new File(userHomeDir);

        log.info("Init sudplan3D configuration...");
        // Check whether the sudplan3D config file already exists.
        File userConfigFile = new File(userHomeDir + "/config/sudplan3D.xml");
        if (!userConfigFile.exists()) {
            log.debug("Setting up {}...", userConfigFile.getAbsoluteFile());
            try {
                ClassLoader loader = Configuration.class.getClassLoader();
                URL url = loader.getResource("config/sudplan3D.xml");
                XMLConfiguration xmlInitialConfig = new XMLConfiguration(url);
                xmlInitialConfig.addProperty("sudplan3D.user.dir", userHomeDir);
                xmlInitialConfig.addProperty("sudplan3D.working.dir", workingDir.getAbsolutePath());
                xmlInitialConfig.save(userConfigFile);
            } catch (ConfigurationException ex) {
                log.error(ex.toString());
            }
        }

        log.debug("{} exists.", userConfigFile.getAbsoluteFile());
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
     * Return the {@link XMLConfiguration}.
     *
     * @return the {@link XMLConfiguration} to return.
     */
    public static XMLConfiguration getXMLConfiguration() {
        return XML_CONFIG;
    }
}
