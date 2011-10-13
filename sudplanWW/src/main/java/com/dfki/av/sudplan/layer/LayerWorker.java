/*
 *  LayerWorker.java 
 *
 *  Created by DFKI AV on 07.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.layer;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class LayerWorker extends SwingWorker<List<Layer>, Void> {

    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());
    private Object object;
    private WorldWindowGLCanvas wwd;

    public LayerWorker(Object obj, WorldWindowGLCanvas canvas) {
        this.object = obj;
        this.wwd = canvas;
    }

    @Override
    protected List<Layer> doInBackground() throws Exception {
        List<Layer> layerList = new ArrayList<Layer>();
        if (object instanceof File) {
            File file = (File) object;
            layerList = LayerFactory.createLayersFromFile(file);
        } else if (object instanceof URL) {
            URL url = (URL) object;
            layerList = LayerFactory.createLayersFromURL(url);
        } else if (object instanceof URI) {
            URI uri = (URI) object;
            layerList = LayerFactory.createLayersFromURI(uri);
        } else {
            throw new IllegalArgumentException("Parameter 'object' is not a "
                    + "valid input for method 'addLayer()'. Must be of type "
                    + "File, URL, or URI.");
        }
        return layerList;
    }

    @Override
    protected void done() {
        try {
            List<Layer> layerlist = get();
            if (!layerlist.isEmpty()) {
                LayerList layers = wwd.getModel().getLayers();
                layers.addAllAbsent(layerlist);
                wwd.repaint();
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("Parameter 'layerlist' is empty.");
                }
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("{}", ex.toString());
            }
        }
    }
}
