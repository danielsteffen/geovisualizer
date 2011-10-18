/*
 *  LayerFactory.java 
 *
 *  Created by DFKI AV on 06.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.layer;

import com.dfki.av.sudplan.layer.fake.BuildingShapefileLoader;
import com.dfki.av.sudplan.layer.fake.StreetLevelShapefileLoader;
import com.dfki.av.utils.AVUtils;
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwindx.examples.util.ShapefileLoader;
import java.io.File;
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
    private static final String SUDPLAN_3D_USER_HOME;
    private static final String FAKE_BUILDING = "buildings.zip";
    private static final String BUILDINGS_SHAPEFILE = "Buildings.shp";
    private static final String FAKE_STREET_LEVEL = "streetlevel.zip";
    private static final String STREET_LEVEL_SHAPEFILE = "Air Quality Street Level.shp";
    public static final String ESRI_SHAPE_FILE_EXTENSION = ".shp";
    public static final String GEOTIFF_FILE_EXTENSION = ".tiff";
    public static final String ARC_GRID_FILE_EXTENSION = ".arc";
    public static final String DEM_FILE_EXTENSION = ".dem";
    public static final String ZIP_FILE_EXTENSION = ".zip";

    static {
        String seperator = System.getProperty("file.separator");
        String userHome = System.getProperty("user.home");
        SUDPLAN_3D_USER_HOME = userHome + seperator + ".sudplan3D";
        // TODO <steffen>: Check if directory already exists.
        boolean success = new File(SUDPLAN_3D_USER_HOME).mkdir();
        if (success) {
            if (log.isDebugEnabled()) {
                log.debug("Directory: {} created.", SUDPLAN_3D_USER_HOME);
            }
        }
    }

    /**
     * Creates a list of {@link Layer} from a {@link File}. 
     * 
     * @param file the source file.
     * @return the list of created {@link Layer}s to return.
     * @throws IllegalArgumentException If <code>file</code> is null.
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
        if (filename.endsWith(BUILDINGS_SHAPEFILE)) {
            return createBuidlingLayerFromShapefile(file);
        } else if (filename.endsWith(STREET_LEVEL_SHAPEFILE)) {
            return createStreetLevelLayersFromShapefile(file);
        } else if (filename.endsWith(ESRI_SHAPE_FILE_EXTENSION)) {
            return createLayersFromShapefile(file);
        } else if (filename.endsWith(GEOTIFF_FILE_EXTENSION)) {
            return createSurfaceImageLayer(file);
        } else if (filename.endsWith(ARC_GRID_FILE_EXTENSION)) {
            throw new IllegalArgumentException("The file could not be added. "
                    + "The file type " + ARC_GRID_FILE_EXTENSION
                    + "is not supported.");
        } else if (filename.endsWith(DEM_FILE_EXTENSION)) {
            throw new IllegalArgumentException("The file could not be added. "
                    + "The file type " + DEM_FILE_EXTENSION
                    + "is not supported.");
        } else {
            throw new IllegalArgumentException("The file could not be added. "
                    + "The file type is not supported.");
        }
    }

    /**
     * Creates a list of {@link Layer}s from a {@link URL}. 
     * 
     * @param url the source url.
     * @return the list of created {@link Layer}s to return.
     * @throws IllegalArgumentException If <code>url</code> is null.
     */
    public static List<Layer> createLayersFromURL(URL url) {

        if (url == null) {
            if (log.isWarnEnabled()) {
                log.warn("Could not create layer. Parameter 'url' is null.");
            }
            throw new IllegalArgumentException("Could not create layer. "
                    + "Parameter 'url' is null.");
        }

        try {
            File file = null;
            if (url.toString().endsWith(DEM_FILE_EXTENSION)) {
                file = AVUtils.DownloadToTempFile(url, "Sudplan3D-", DEM_FILE_EXTENSION);
            } else if (url.toString().endsWith(ESRI_SHAPE_FILE_EXTENSION)) {
                file = AVUtils.DownloadToTempFile(url, "Sudplan3D-", ESRI_SHAPE_FILE_EXTENSION);
            } else if (url.toString().endsWith(GEOTIFF_FILE_EXTENSION)) {
                file = AVUtils.DownloadToTempFile(url, "Sudplan3D-", GEOTIFF_FILE_EXTENSION);
            } else if (url.toString().endsWith(ZIP_FILE_EXTENSION)) {
                File tmpFile = AVUtils.DownloadToTempFile(url, "Sudplan3D-", ZIP_FILE_EXTENSION);
                AVUtils.Unzip(tmpFile, SUDPLAN_3D_USER_HOME);
                // TODO <steffen>: Remove the fake.
                file = fake(url);
            } else {
                throw new IllegalArgumentException("Could not create layer. "
                        + "URL type is not supported.");
            }

            return createLayersFromFile(file);
        } catch (IOException ex) {
            if (log.isErrorEnabled()) {
                log.error("Could not create layer from URL. {}", ex.toString());
            }
        }
        return null;
    }

    /**
     * Creates a list of {@link Layer}s from a {@link URI}. 
     * 
     * @param uri the source uri.
     * @return the list of created {@link Layer}s to return.
     * @throws IllegalArgumentException If <code>uri</code> is null.
     */
    public static List<Layer> createLayersFromURI(URI uri) {

        if (uri == null) {
            if (log.isWarnEnabled()) {
                log.warn("Could not create Layer. Parameter 'uri' is null.");
            }
            throw new IllegalArgumentException("Could not create layer. "
                    + "Parameter 'uri' is null.");
        }

        return createLayersFromFile(new File(uri));
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
    private static List<Layer> createSurfaceImageLayer(final File file) {
        SurfaceImageLayer sul = new SurfaceImageLayer();
        sul.setOpacity(0.8);
        sul.setPickEnabled(false);
        sul.setName(file.getName());
        try {
            sul.addImage(file.getAbsolutePath());
        } catch (IOException ex) {
            if (log.isErrorEnabled()) {
                log.error("Could not add image {}", ex.toString());
            }
            sul = null;
        }
        List<Layer> layerList = new ArrayList<Layer>();
        layerList.add(sul);
        return layerList;
    }

    // TODO <steffen>: remove fake
    private static List<Layer> createBuidlingLayerFromShapefile(final File file) {
        ShapefileLoader shpLoader = new BuildingShapefileLoader("Elevation");
        Shapefile shpFile = new Shapefile(file);
        List<Layer> layerList = shpLoader.createLayersFromShapefile(shpFile);
        int i = 0;
        for (Layer layer : layerList) {
            layer.setName("Buildings Södermalm (L " + i + ")");
            layer.setEnabled(true);
            i++;
        }
        return layerList;
    }

    // TODO <steffen>: remove fake
    private static List<Layer> createStreetLevelLayersFromShapefile(final File file) {
        ShapefileLoader shpLoader = new StreetLevelShapefileLoader("Perc98d");
        Shapefile shpFile = new Shapefile(file);
        List<Layer> layerList = shpLoader.createLayersFromShapefile(shpFile);
        int i = 0;
        for (Layer layer : layerList) {
            layer.setName("Street level results (" + i + ")");
            i++;
        }
        return layerList;
    }

    // TODO <steffen>: remove fake
    private static File fake(URL url) {
        String seperator = System.getProperty("file.separator");
        File file = null;
        if (url.toString().endsWith(FAKE_BUILDING)) {
            file = new File(SUDPLAN_3D_USER_HOME
                    + seperator
                    + BUILDINGS_SHAPEFILE);
        } else if (url.toString().endsWith(FAKE_STREET_LEVEL)) {
            file = new File(SUDPLAN_3D_USER_HOME
                    + seperator
                    + STREET_LEVEL_SHAPEFILE);
        }

        return file;
    }
}
