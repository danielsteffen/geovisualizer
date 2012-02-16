/*
 *  VisWorker.java 
 *
 *  Created by DFKI AV on 07.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
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
     * User home directory of SUDPLAN 3D component.
     */
    private static final String SUDPLAN_3D_USER_HOME;

    static {
        String seperator = System.getProperty("file.separator");
        String userHome = System.getProperty("user.home");
        SUDPLAN_3D_USER_HOME = userHome + seperator + ".sudplan3D";
        File sudplanDirectory = new File(SUDPLAN_3D_USER_HOME);
        if (sudplanDirectory.exists()) {
            log.debug("Directory already existing.");
        } else {
            if (sudplanDirectory.mkdir()) {
                log.debug("Directory: {} created.", SUDPLAN_3D_USER_HOME);
            } else {
                log.debug("Could not create directory {}.", SUDPLAN_3D_USER_HOME);
            }
        }
    }
    
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
    public VisWorker(Object data, IVisAlgorithm vis, final Object[] attributes, WorldWindowGLCanvas canvas) {
        if (data == null) {
            log.error("Parameter 'data' is null.");
            throw new IllegalArgumentException("Parameter 'data' is null.");
        }
        if (vis == null) {
            log.error("Parameter 'vis' is null.");
            throw new IllegalArgumentException("Parameter 'vis' is null.");
        }
        if (canvas == null) {
            log.error("Parameter 'canvas' is null.");
            throw new IllegalArgumentException("Parameter 'canvas' is null.");
        }
        if (attributes == null){
            
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
            tmpFile = AVUtils.DownloadToFile(url);
        } else if (dataSource instanceof URI) {
            URI uri = (URI) dataSource;
            tmpFile = AVUtils.DownloadToFile(uri.toURL());
        } else if (dataSource instanceof Layer) {
            layerList.add((Layer) dataSource);
            return layerList;
        } else {
            log.error("No valid data source for LayerWorker. "
                    + "Must be of type File, URL, or URI.");
            throw new IllegalArgumentException("No valid data source for LayerWorker. "
                    + "Must be of type File, URL, or URI.");
        }

        String fileName = tmpFile.getName();
        File file = null;        
        if (fileName.endsWith(".zip")) {
            AVUtils.Unzip(tmpFile, SUDPLAN_3D_USER_HOME);
            // Here, we assume that the name of the shape file equals
            // the name of the zip and vice versa.
            String shpFileName = fileName.replace(".zip", ".shp");
            file = new File(SUDPLAN_3D_USER_HOME + File.separator + shpFileName);
            log.debug("Source file: {}", file.getAbsolutePath());
        } else if (fileName.endsWith(".shp")) {
            file = tmpFile;
        } else {
            log.debug("Data type not supported yet.");
            return layerList;
        }

        String path = file.getAbsolutePath().replace(File.separator, "/");
        log.debug("Path to shapefile: {}", path);
        Shapefile shapefile = new Shapefile(path);
        layerList = algo.createLayersFromData(shapefile, attributes);

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
