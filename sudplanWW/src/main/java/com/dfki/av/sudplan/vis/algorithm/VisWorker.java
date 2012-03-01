/*
 *  VisWorker.java 
 *
 *  Created by DFKI AV on 07.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
import com.dfki.av.sudplan.vis.Settings;
import com.dfki.av.utils.AVUtils;
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
public class VisWorker extends SwingWorker<List<Layer>, Void> {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(VisWorker.class);
    /**
     * Data source for the layer to be produced.
     */
    private Object dataSource;
    /**
     * The canvas where the layer will be added.
     */
    private WorldWindowGLCanvas wwd;
    /**
     * The visualization algorithm used to create the layer.
     */
    private IVisAlgorithm algo;
    /**
     * The attributes / settings for the visualization technique to consider.
     */
    private Object[] attributes;

    /**
     *
     * @param data
     * @param vis
     * @param canvas
     */
    public VisWorker(final Object data, final IVisAlgorithm vis, final Object[] attributes, WorldWindowGLCanvas canvas) {
        if (data == null) {
            log.error("Parameter 'data' is null.");
            throw new IllegalArgumentException("Parameter 'data' is null.");
        }
        if (canvas == null) {
            log.error("Parameter 'canvas' is null.");
            throw new IllegalArgumentException("Parameter 'canvas' is null.");
        }
        if (vis == null) {
            log.error("Parameter 'vis' is null.");
            throw new IllegalArgumentException("Parameter 'vis' is null.");
        }
        if (attributes == null) {
            log.warn("Parameter 'attributes' is null.");
        }
        this.dataSource = data;
        this.algo = vis;
        this.attributes = attributes;
        this.wwd = canvas;
    }

    @Override
    protected List<Layer> doInBackground() throws Exception {
        List<Layer> layerList = new ArrayList<Layer>();
        // If necessary donwload the selected object source. Currently, only
        // object sources of type File, URL, URI and Layer are being 
        // supported.
        File tmpFile = null;

        if (dataSource instanceof File) {
            tmpFile = (File) dataSource;
        } else if (dataSource instanceof URL) {
            URL url = (URL) dataSource;
            tmpFile = AVUtils.DownloadFileToDirectory(url, Settings.SUDPLAN_3D_USER_HOME);
        } else if (dataSource instanceof URI) {
            URI uri = (URI) dataSource;
            tmpFile = AVUtils.DownloadFileToDirectory(uri.toURL(), Settings.SUDPLAN_3D_USER_HOME);
        } else if (dataSource instanceof Layer) {
            layerList.add((Layer) dataSource);
            return layerList;
        } else {
            log.error("No valid data source. "
                    + "Must be of type File, URL, or URI.");
            throw new IllegalArgumentException("No valid data source for LayerWorker. "
                    + "Must be of type File, URL, or URI.");
        }

        String fileName = tmpFile.getName();
        Object data = null;
        if (fileName.endsWith(".zip")) {
            AVUtils.Unzip(tmpFile, Settings.SUDPLAN_3D_USER_HOME);
            // Here, we assume that the name of the shapefile contained in the
            // zip file equals the name of the zip and vice versa.
            String shpFileName = fileName.replace(".zip", ".shp");
            File tmp = new File(Settings.SUDPLAN_3D_USER_HOME + File.separator + shpFileName);
            data = new Shapefile(tmp.getAbsolutePath());
        } else if (fileName.endsWith(".shp")) {
            data = new Shapefile(tmpFile.getAbsolutePath());
        } else if (fileName.endsWith(".tif")
                || fileName.endsWith(".tiff")) {
            data = tmpFile;
        } else {
            log.debug("Data type not supported yet.");
            return layerList;
        }

        layerList = algo.createLayersFromData(data, attributes);

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
                log.warn("Parameter 'layerlist' is empty. Nothing to add.");
            }
        } catch (Exception ex) {
            log.error("{}", ex.toString());
        }
    }
}
