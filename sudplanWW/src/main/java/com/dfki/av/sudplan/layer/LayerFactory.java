/*
 *  LayerFactory.java 
 *
 *  Created by DFKI AV on 06.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.layer;

import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwindx.examples.util.ShapefileLoader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class LayerFactory {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(LayerFactory.class);
    public static final String ESRI_SHAPE_FILE_EXTENSION = ".shp";
    public static final String GEOTIFF_FILE_EXTENSION = ".tiff";
    public static final String ARC_GRID_FILE_EXTENSION = ".arc";
    public static final String DEM_FILE_EXTENSION = ".dem";

    /**
     * 
     * @param file
     * @return 
     */
    public static List<Layer> createLayersFromFile(File file) {
        
        if (file == null) {
            if (log.isWarnEnabled()) {
                log.warn("Could not create Layer. Parameter 'file' is null.");
            }
            throw new IllegalArgumentException("Could not create Layer. "
                    + "Parameter 'file' is null.");
        }
        
        String filename = file.getName();
        List<Layer> layerList = new ArrayList<Layer>();
        if (filename.endsWith(ESRI_SHAPE_FILE_EXTENSION)) {
            layerList.addAll(createLayersFromShapefile(file));
        } else if (filename.endsWith(ARC_GRID_FILE_EXTENSION)) {
            throw new IllegalArgumentException("The file could not be added. "
                    + "The file type " + ARC_GRID_FILE_EXTENSION
                    + "is not supported.");
        } else if (filename.endsWith(DEM_FILE_EXTENSION)) {
            throw new IllegalArgumentException("The file could not be added. "
                    + "The file type " + DEM_FILE_EXTENSION
                    + "is not supported.");
        } else if (filename.endsWith(GEOTIFF_FILE_EXTENSION)) {
            layerList.addAll(createSurfaceImageLayer(file));
        } else {
            throw new IllegalArgumentException("The file could not be added. "
                    + "The file type is not supported.");
        }
        return layerList;
    }

    /**
     * 
     * @param url
     * @return 
     */
    public static List<Layer> createLayersFromURL(URL url) {
        List<Layer> layerList = new ArrayList<Layer>();
        BufferedInputStream in = null;
        try {
            if (url == null) {
                if (log.isWarnEnabled()) {
                    log.warn("Could not create layer. Parameter 'url' is null.");
                }
                throw new IllegalArgumentException("Could not create layer. "
                        + "Parameter 'url' is null.");
            }
            
            if (log.isDebugEnabled()) {
                log.debug("Downloading file ...");
            }
            
            in = new BufferedInputStream(url.openStream());
            File file = null;
            if (url.toString().endsWith(DEM_FILE_EXTENSION)) {
                file = File.createTempFile("s3D-", DEM_FILE_EXTENSION);
            } else if (url.toString().endsWith(ESRI_SHAPE_FILE_EXTENSION)) {
                file = File.createTempFile("s3D-", ESRI_SHAPE_FILE_EXTENSION);
            } else if (url.toString().endsWith(GEOTIFF_FILE_EXTENSION)) {
                file = File.createTempFile("s3D-", GEOTIFF_FILE_EXTENSION);
            } else {
                throw new IllegalArgumentException("Could not create layer. "
                        + "File type is not supported.");
            }
            FileOutputStream out = new FileOutputStream(file);
            byte[] data = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);
            }
            in.close();
            out.close();
            layerList.addAll(createLayersFromFile(file));
        } catch (IOException ex) {
            if (log.isErrorEnabled()) {
                log.error("{}", ex);
            }
        } finally {
            try {
                if(in != null){
                    in.close();
                }
            } catch (IOException ex) {
                if (log.isErrorEnabled()) {
                    log.error("{}", ex);
                }
            }
        }
        return layerList;
    }

    /**
     * 
     * @param uri
     * @return 
     */
    public static List<Layer> createLayersFromURI(URI uri) {
        if (uri == null) {
            if (log.isWarnEnabled()) {
                log.warn("Could not create Layer. Parameter 'uri' is null.");
            }
            throw new IllegalArgumentException("Could not create Layer. "
                    + "Parameter 'uri' is null.");
        }
        
        List<Layer> layerList = new ArrayList<Layer>();
        layerList.addAll(createLayersFromFile(new File(uri)));
        return layerList;
    }

    /**
     * 
     * @param file
     * @return 
     */
    private static List<Layer> createSurfaceImageLayer(final File file) {
        SurfaceImageLayer sul = new SurfaceImageLayer();
        sul.setOpacity(0.8);
        sul.setPickEnabled(false);
        sul.setName(file.getName());
        try {
            sul.addImage(file.getAbsolutePath());
        } catch (IOException ex) {
            if (log.isErrorEnabled()) {
                log.error("{}", ex);
            }
            sul = null;
        }
        List<Layer> layerList = new ArrayList<Layer>();
        layerList.add(sul);
        return layerList;
    }

    /**
     * 
     * @param file
     * @return 
     */
    private static List<Layer> createLayersFromShapefile(final File file) {
        ShapefileLoader shpLoader = new ShapefileLoader();
        Shapefile shpFile = new Shapefile(file);
        List<Layer> layerList = shpLoader.createLayersFromShapefile(shpFile);
        int i = 0;
        for (Layer layer : layerList) {
            layer.setName(file.getName() + "_" + i);
            i++;
        }
        return layerList;
    }

    /**
     * 
     * @param file
     * @return 
     */
    private static Layer createExtrudedLayerFromShapefile(final File file) {
        ShapeAttributes sideAttributes = new BasicShapeAttributes();
        sideAttributes.setInteriorMaterial(Material.LIGHT_GRAY);
        sideAttributes.setOutlineMaterial(Material.DARK_GRAY);
        
        ShapeAttributes capAttributes = new BasicShapeAttributes(sideAttributes);
        capAttributes.setInteriorMaterial(Material.GRAY);
        RenderableLayer layer = new RenderableLayer();
        layer.setName(file.getName());
        
        Shapefile shpFile = new Shapefile(file);
        while (shpFile.hasNext()) {
            ShapefileRecord shpRecord = shpFile.nextRecord();
            ExtrudedPolygon ePolygon = new ExtrudedPolygon(shpRecord.getCompoundPointBuffer().getPositions());
            ePolygon.setHeight(40.0);
            ePolygon.setSideAttributes(sideAttributes);
            ePolygon.setCapAttributes(capAttributes);
            layer.addRenderable(ePolygon);
        }
        
        return layer;
    }
}
