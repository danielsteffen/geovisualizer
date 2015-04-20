/*
 * SensorMapPlugin.java
 *
 * Created by wearHEALTH on 20.04.2015.
 * Copyright (c) 2015 TU Kaiserslautern, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.uni.kl.cs.geovisualizer.plugin.sensormap;

import de.dfki.av.geovisualizer.app.plugins.IPlugin;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class collects and provides all IPlugin implementations of this library.
  */
public class SensorMapPlugin implements IPlugin {

    /*
     * The logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SensorMapPlugin.class);
    /**
     * The {@link AbstractAction} implementation to start this {@link IPlugin}
     * implementation.
     */
    private SensorMapAction action;
    /**
     * The object used for supporting property changes.
     */
    private PropertyChangeSupport propertyChangeSupport;

    /**
     * Constructor.
     */
    public SensorMapPlugin() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);

    }

    @Override
    public String getName() {
        return "SensorMap Plugin";
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            LOG.debug("Adding PropertyChangeListener {}", listener.toString());
            propertyChangeSupport.addPropertyChangeListener(listener);
        } else {
            String msg = "listener == null";
            LOG.warn(msg);
            throw new RuntimeException(msg);
        }
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            LOG.debug("Removing PropertyChangeListener {}", listener.toString());
            propertyChangeSupport.removePropertyChangeListener(listener);
        } else {
            String msg = "listener == null";
            LOG.warn(msg);
            throw new RuntimeException(msg);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt != null) {
            LOG.debug("Received PropertyChangeEvent {}", evt.toString());
        } else {
            String msg = "evt == null";
            LOG.warn(msg);
            throw new RuntimeException(msg);
        }
    }

    @Override
    public AbstractAction getAbstractAction() {
        if (action == null) {
            action = new SensorMapAction();
        }
        return action;
    }

    /**
     * Fire {@link PropertyChangeEvent} to all registered
     * {@link PropertyChangeListener}.
     *
     * @param evt the {@link PropertyChangeEvent} to fire.
     * @throws RuntimeException if {@code evt == null}.
     */
    protected void firePropertyChangeEvents(PropertyChangeEvent evt) {
        if (evt != null) {
            propertyChangeSupport.firePropertyChange(evt);
        } else {
            String msg = "evt == null";
            LOG.warn(msg);
            throw new RuntimeException(msg);
        }
    }
}
