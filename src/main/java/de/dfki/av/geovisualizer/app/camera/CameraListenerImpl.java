/*
 *  CameraListenerImpl.java 
 *
 *  Created by DFKI AV on 30.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.camera;

import gov.nasa.worldwind.View;
import java.beans.PropertyChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of {@link CameraListener} using {@link org.slf4j.Logger}
 * for logging the data of the {@link PropertyChangeEvent}.
 * 
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class CameraListenerImpl implements CameraListener {

    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (log.isDebugEnabled()) {
            log.debug("Property Name: {}", evt.getPropertyName());
            View view = (View) evt.getNewValue();
            log.debug("New object: {}", evt.getNewValue());            
            log.debug("Position: {}", view.getCurrentEyePosition());
            log.debug("Forward Vector: {}", view.getForwardVector());
            log.debug("Heading: {}", view.getHeading());
            log.debug("Pitch: {}", view.getPitch());
            log.debug("Old object: {}", evt.getOldValue());            
        }
    }
}
