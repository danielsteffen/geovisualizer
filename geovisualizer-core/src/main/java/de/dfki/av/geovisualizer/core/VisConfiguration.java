/*
 * VisConfiguration.java
 *
 * Created by DFKI AV on 19.09.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import de.dfki.av.geovisualizer.core.io.IOUtils;
import gov.nasa.worldwind.layers.Layer;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container for {@link IVisAlgorithm}, {@code data}, and the selected
 * {@code attributes}.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisConfiguration {

    /*
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(VisConfiguration.class);
    /**
     * The name of the {@link VisConfiguration}.
     */
    private String name;
    /**
     * The {@link IVisAlgorithm} to use / used.
     */
    private IVisAlgorithm iVisAlgorithm;
    /**
     * The data used for the visualization.
     */
    private Object data;
    /**
     * The attributes of the data used for the visualization.
     */
    private Object[] attributes;
    /**
     * The {@link List} of {@link Layer} produced by the {@link IVisAlgorithm}.
     */
    private List<Layer> layers;
    /**
     * The {@link UUID} for this {@link VisConfiguration}.
     */
    private final UUID uuid;

    /**
     * Constructor.
     *
     * @param algo the {@link IVisAlgorithm}
     * @param data the data to use.
     * @param attributes the attributes of the data to use.
     * @throws IllegalArgumentException if algo == null or if data == null
     */
    public VisConfiguration(final IVisAlgorithm algo, final Object data, final Object[] attributes) {
        if (algo == null) {
            String msg = "algo == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (data == null) {
            String msg = "data == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (attributes == null) {
            String msg = "attributes == null";
            LOG.warn(msg);
        }

        this.iVisAlgorithm = algo;
        this.data = data;
        this.attributes = attributes;
        this.layers = null;
        this.uuid = UUID.randomUUID();
    }

    /**
     * Executes the {@link IVisAlgorithm} and returns the generated {@link List}
     * of {@link Layer}.
     *
     * @return the {@link #layers} to return. Maybe {@code null}.
     */
    public List<Layer> execute() {
        ISource source = IOUtils.read(data);
        layers = iVisAlgorithm.createLayersFromData(source, attributes);
        return layers;
    }

    /**
     * Returns the {@link IVisAlgorithm} for this configuration.
     *
     * @return the {@link IVisAlgorithm} to return.
     */
    public IVisAlgorithm getIVisAlgorithm() {
        return iVisAlgorithm;
    }

    /**
     * Returns the data.
     *
     * @return the data to return.
     */
    public Object getData() {
        return data;
    }

    /**
     * Returns the attributes.
     *
     * @return the attributes to return.
     */
    public Object[] getAttributes() {
        return attributes;
    }

    /**
     * Returns the name of the {@link VisConfiguration}.
     *
     * @return the name to return.
     */
    public String getName() {
        return name;
    }

    /**
     * Print the information of this {@link VisConfiguration} using
     * {@link #LOG}.
     */
    public void printDebugInfo() {
        LOG.debug("VisConfiguration");
        LOG.debug("- {}", iVisAlgorithm.getClass().getName());
        List<IVisParameter> paramters = iVisAlgorithm.getVisParameters();
        for (IVisParameter iVisParameter : paramters) {
            LOG.debug("-- {}", iVisParameter.getClass().getName());
            LOG.debug("--- {}", iVisParameter.getTransferFunction().getClass().getName());
        }
    }

    /**
     * Returns the {@link UUID} for the {@link VisConfiguration}.
     *
     * @return the {@link UUID} to return
     */
    public UUID getUuid() {
        return uuid;
    }
}
