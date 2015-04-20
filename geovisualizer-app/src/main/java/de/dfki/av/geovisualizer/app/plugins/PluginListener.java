/*
 * PluginListener.java
 *
 * Created by DFKI AV on 12.03.2014.
 * Copyright (c) 2011-2014 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.plugins;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link PropertyChangeListener} for {@link PropertyChangeEvent} from
 * the {@link IPlugin} implementations.
 */
public class PluginListener implements PropertyChangeListener {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PluginListener.class);

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        LOG.debug("Received event: {}", evt.toString());
    }
}
