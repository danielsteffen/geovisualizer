/*
 * VisWorker.java
 *
 * Created by DFKI AV on 07.10.2011.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link SwingWorker} class to be used to generate the visualization.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisWorker extends SwingWorker<List<Layer>, Void> {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(VisWorker.class);
    /**
     * The {@link WorldWindow} where the layer will be added.
     */
    private WorldWindow worldWindow;
    /**
     * The {@link VisConfiguration} to be used.
     */
    private VisConfiguration configuration;

    /**
     * Constructor.
     *
     * @param config the {@link VisConfiguration}
     * @param worldwindow the {@link WorldWindow}
     * @throws IllegalArgumentException if config == null and if worldwindow ==
     * null
     */
    public VisWorker(final VisConfiguration config, WorldWindow worldwindow) {
        if (config == null) {
            String msg = "config == null.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (worldwindow == null) {
            String msg = "worldwindow == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.configuration = config;
        this.worldWindow = worldwindow;
    }

    @Override
    protected List<Layer> doInBackground() throws Exception {

        List<Layer> layerList = new ArrayList<>();
        Object data = configuration.getData();
        if (data instanceof Layer) {
            log.debug("dataSource is instance of {}", Layer.class.getName());
            layerList.add((Layer) data);
            return layerList;
        }

        layerList = configuration.execute();
        configuration.printDebugInfo();

        return layerList;
    }

    @Override
    protected void done() {
        try {
            List<Layer> layerlist = get();
            if (layerlist != null && !layerlist.isEmpty()) {

                LayerList layers = worldWindow.getModel().getLayers();
                // Insert the layer into the layer list just before the placenames.
                // copied from ApplicationTemplate.
                int position = getIndexOfPlaceNameLayer();
                for (Layer layer : layerlist) {
                    if (!layers.contains(layer)) {
                        layers.add(position, layer);
                    }
                }

                IVisAlgorithm algo = configuration.getIVisAlgorithm();
                SelectListener listener = algo.getSelectListener();
                if (listener != null) {
                    worldWindow.addSelectListener(listener);
                } else {
                    log.debug("No {} available to control visualization {}",
                            SelectListener.class.getSimpleName(),
                            algo.getClass().getSimpleName());
                }

                worldWindow.redraw();
            } else {
                log.warn("Parameter 'layerlist' is empty or null. Nothing to add.");
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.error("{}", ex.toString());
        }
    }

    /**
     * Returns the index of the 'placename' layer. If the place name layer is
     * not in the list of available layers 0 is returned.
     *
     * @return the index of the place name layer to return.
     */
    private int getIndexOfPlaceNameLayer() {
        int placeNamePosition = 0;
        LayerList layers = worldWindow.getModel().getLayers();
        for (Layer l : layers) {
            if (l instanceof PlaceNameLayer) {
                placeNamePosition = layers.indexOf(l);
            }
        }
        return placeNamePosition;
    }
}
