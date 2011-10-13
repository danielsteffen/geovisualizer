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
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwindx.examples.util.ShapefileLoader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
    public static final String ESRI_SHAPE_FILE_EXTENSION = ".shp";
    public static final String GEOTIFF_FILE_EXTENSION = ".tiff";
    public static final String ARC_GRID_FILE_EXTENSION = ".arc";
    public static final String DEM_FILE_EXTENSION = ".dem";
    public static final String ZIP_FILE_EXTENSION = ".zip";
    private static final String FAKE_BUILDING = "buildings.zip";
    private static final String BUILDINGS_SHAPEFILE = "Buildings.shp";
    private static final String FAKE_STREET_LEVEL = "streetlevel.zip";
    private static final String STREET_LEVEL_SHAPEFILE = "Air Quality Street Level.shp";

    /**
     * 
     * @param file
     * @return 
     */
    public static List<Layer> createLayersFromFile(File file) {
        if (log.isDebugEnabled()) {
            log.debug("createLayersFromFile()");
        }

        if (file == null) {
            if (log.isWarnEnabled()) {
                log.warn("Could not create Layer. Parameter 'file' is null.");
            }
            throw new IllegalArgumentException("Could not create Layer. "
                    + "Parameter 'file' is null.");
        }

        String filename = file.getName();
        List<Layer> layerList = new ArrayList<Layer>();
        if (filename.endsWith(BUILDINGS_SHAPEFILE)) {
            layerList.addAll(createBuidlingLayerFromShapefile(file));
        } else if (filename.endsWith(STREET_LEVEL_SHAPEFILE)) {
            layerList.addAll(createStreetLevelLayersFromShapefile(file));
        } else if (filename.endsWith(ESRI_SHAPE_FILE_EXTENSION)) {
            layerList.addAll(createLayersFromShapefile(file));
        } else if (filename.endsWith(GEOTIFF_FILE_EXTENSION)) {
            layerList.addAll(createSurfaceImageLayer(file));
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
        return layerList;
    }

    /**
     * 
     * @param url
     * @return 
     */
    public static List<Layer> createLayersFromURL(URL url) {
        if (log.isDebugEnabled()) {
            log.debug("createLayersFromURL()");
        }

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

            in = new BufferedInputStream(url.openStream());
            File file = null;
            if (url.toString().endsWith(DEM_FILE_EXTENSION)) {
                file = downloadToTempFile(url, DEM_FILE_EXTENSION);
            } else if (url.toString().endsWith(ESRI_SHAPE_FILE_EXTENSION)) {
                file = downloadToTempFile(url, ESRI_SHAPE_FILE_EXTENSION);
            } else if (url.toString().endsWith(GEOTIFF_FILE_EXTENSION)) {
                file = downloadToTempFile(url, GEOTIFF_FILE_EXTENSION);
            } else if (url.toString().endsWith(ZIP_FILE_EXTENSION)) {
                File tmpFile = downloadToTempFile(url, ZIP_FILE_EXTENSION);
                unzip(tmpFile);
                // Finally, do the fake.
                // TODO <steffen>: Remove the fake.
                file = fake(url);
            } else {
                throw new IllegalArgumentException("Could not create layer. "
                        + "File type is not supported.");
            }

            layerList.addAll(createLayersFromFile(file));
        } catch (IOException ex) {
            if (log.isErrorEnabled()) {
                log.error("Could not create Layer from URL: {}", ex.toString());
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                if (log.isErrorEnabled()) {
                    log.error("Could not close InputStream: {}", ex);
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
        if (log.isDebugEnabled()) {
            log.debug("createLayersFromURI()");
        }
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
    private static List<Layer> createLayersFromShapefile(final File file) {
        if (log.isDebugEnabled()) {
            log.debug("createLayersFromShapefile(): {}", 
                    file.getAbsolutePath());
        }
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
        if (log.isDebugEnabled()) {
            log.debug("createSurfaceImageLayer(): {}", 
                    file.getAbsolutePath());
        }
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


    /**
     * 
     * @param file
     * @return 
     */
    private static List<Layer> createBuidlingLayerFromShapefile(final File file) {
        if (log.isDebugEnabled()) {
            log.debug("createBuildingLayersFromShapefile(): {}", 
                    file.getAbsolutePath());
        }

        ShapefileLoader shpLoader = new BuildingShapefileLoader();
        Shapefile shpFile = new Shapefile(file);
        List<Layer> layerList = shpLoader.createLayersFromShapefile(shpFile);
        int i = 0;
        for (Layer layer : layerList) {
            layer.setName("Buildings Södermalm (L " + i + ")");
            i++;
        }
        return layerList;
    }

    /**
     * 
     * @param file
     * @return 
     */
    private static List<Layer> createStreetLevelLayersFromShapefile(final File file) {
        if (log.isDebugEnabled()) {
            log.debug("createStreetLevelLayersFromShapefile(): {}",
                    file.getAbsolutePath());
        }

        ShapefileLoader shpLoader = new StreetLevelShapefileLoader();
//        ShapefileLoader shpLoader = new ShapefileLoader();
        Shapefile shpFile = new Shapefile(file);
        List<Layer> layerList = shpLoader.createLayersFromShapefile(shpFile);
        int i = 0;
        for (Layer layer : layerList) {
            layer.setName("Street level results (" + i + ")");
            i++;
        }
        return layerList;
    }

    /**
     * 
     * @param url
     * @return
     * @throws IOException 
     */
    private static File downloadToTempFile(URL url, String ext) throws IOException {
        BufferedInputStream in = new BufferedInputStream(url.openStream());
        File tmpFile = File.createTempFile("sudplan3D-", ext);
        if (log.isDebugEnabled()) {
            log.debug("Downloading from {} to {}", url.toString(), tmpFile.getAbsolutePath());
        }
        FileOutputStream out = new FileOutputStream(tmpFile);
        byte[] data = new byte[1024];
        int count;
        while ((count = in.read(data, 0, 1024)) != -1) {
            out.write(data, 0, count);
        }
        in.close();
        out.close();

        if (log.isDebugEnabled()) {
            log.debug("Download finished.");
        }
        return tmpFile;
    }

    /**
     * 
     * @param tmpFile
     * @throws IOException 
     */
    private static void unzip(File tmpFile) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Unzipping ... {}", tmpFile.getName());
        }

        // Unpack files into directory.
        ZipFile zipFile = new ZipFile(tmpFile);
        Enumeration zipEntries = zipFile.entries();
        String seperator = System.getProperty("file.separator");
        while (zipEntries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipEntries.nextElement();
            if (log.isDebugEnabled()) {
                log.debug("Unzipping entry {}", entry.getName());
            }

            if (entry.isDirectory()) {
                // Assume directories are stored parents first then children.
                // This is not robust, just for demonstration purposes.
                (new File(SUDPLAN_3D_USER_HOME + seperator + entry.getName())).mkdir();
                continue;
            }

            copyInputStream(zipFile.getInputStream(entry), 
                    new BufferedOutputStream(new FileOutputStream(
                            SUDPLAN_3D_USER_HOME + seperator + entry.getName())));
        }

        if (log.isDebugEnabled()) {
            log.debug("Unzipping finished.");
        }

    }

    /**
     * 
     * @param in
     * @param out
     * @throws IOException 
     */
    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }

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
