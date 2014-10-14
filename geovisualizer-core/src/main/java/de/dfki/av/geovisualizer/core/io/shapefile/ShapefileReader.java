/*
 * ShapefileReader.java
 *
 * Created by DFKI AV on 07.09.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.io.shapefile;

import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.ISourceReader;
import de.dfki.av.geovisualizer.core.VisSettings;
import de.dfki.av.geovisualizer.core.io.IOUtils;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ISourceReader} for shapefiles as well as shapezip files.
 *
 * Note that in case of a {@link File} input a shapefile as well as a shapezip
 * are supported.
 *
 * Note that in case the input is of type {@link URL}, {@link URI} or
 * {@link InputStream} the usage of only a shapefile is not supported. Thus, you
 * have to use the shapezip.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ShapefileReader implements ISourceReader {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ShapefileReader.class);

    /**
     * Returns whether or not this reader is able to read, parse and handle the
     * input data.
     *
     * @param input the {@code input} to read
     * @return {@code true} if {@link ShapefileReader} is able to read.
     * Otherwise {@code false}.
     * @throws IllegalArgumentException if input == null or input is not of type
     * {@link File}, {@link URL}, {@link URI} or {@link InputStream}.
     */
    @Override
    public boolean canRead(Object input) {
        boolean ret = false;
        if (input == null) {
            String msg = "input == null.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        InputStream is = null;
        try {
            if (input instanceof File) {
                File file = (File) input;
                is = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                String extension = IOUtils.getFileExtension(bis);
                bis.close();
                if (extension.equalsIgnoreCase(IOUtils.SHP)) {
                    log.debug("Input instance of shapefile.");
                    return true;
                }
                is = new FileInputStream(file);
                log.debug("Input instance of File.");
            } else if (input instanceof URL) {
                URL url = (URL) input;
                is = url.openStream();
                log.debug("Input instance of URL.");
            } else if (input instanceof URI) {
                URI uri = (URI) input;
                URL url = uri.toURL();
                is = url.openStream();
                log.debug("Input instance of URI.");
            } else if (input instanceof InputStream) {
                is = (InputStream) input;
                log.debug("Input instance of InputStream.");
            } else {
                String message = "Datatype not supported. Use URL, URI, File, "
                        + "or Inputstream.";
                log.warn(message);
                throw new IllegalArgumentException(message);
            }

            BufferedInputStream bis = new BufferedInputStream(is);
            ret = ShapefileReader.isShapezip(bis);
            bis.close();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }

        return ret;
    }

    /**
     * Reads the input data and converts it to an object of type
     * {@link ISource}.
     *
     * In case of shapezip files the content of the zip is being extracted to
     * {@link VisSettings#USER_HOME_DIR}.
     *
     * Note that a single shapefile is only supported if the input data is of
     * type {@link File}.
     *
     * @param input the {@code input} to read
     * @return the {@link ISource} object to return
     * @throws IllegalArgumentException if input == null or input is not of type
     * {@link File}, {@link URL}, {@link URI} or {@link InputStream}.
     */
    @Override
    public ISource read(Object input) {
        if (input == null) {
            String msg = "input == null.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        ISource source = null;
        if (input instanceof File) {
            log.debug("Input instance of File.");
            File file = (File) input;
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                String ext = IOUtils.getFileExtension(bis);
                switch (ext) {
                    case IOUtils.SHP:
                        // Here we assume a local file of type shape.
                        source = new Shapefile(file.getAbsolutePath());
                        break;
                    case IOUtils.ZIP:
                        List<Shapefile> shapefiles = ShapefileReader.unzipShapezip(bis);
                        if (shapefiles.isEmpty()) {
                            String msg = "Shapezip is empty.";
                            log.error(msg);
                            throw new RuntimeException(msg);
                        }
                        if (shapefiles.size() > 1) {
                            String msg = "Only considering first shapefile in zip.";
                            log.info(msg);
                        }
                        source = shapefiles.get(0);
                        break;
                    default:
                        break;
                }
                try {
                    fis.close();
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
            } catch (FileNotFoundException ex) {
                log.error(ex.getMessage());
            }
        } else if (input instanceof URL) {
            log.debug("Input instance of URL.");
            URL url = (URL) input;
            InputStream is;
            try {
                is = url.openStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                String ext = IOUtils.getFileExtension(bis);
                switch (ext) {
                    case IOUtils.SHP:
                        log.warn("No support for shp from URL. Use zipped shapefile.");
                        break;
                    case IOUtils.ZIP:
                        List<Shapefile> shapefiles = ShapefileReader.unzipShapezip(bis);
                        if (shapefiles.isEmpty()) {
                            String msg = "Shapezip is empty.";
                            log.error(msg);
                            throw new RuntimeException(msg);
                        }
                        if (shapefiles.size() > 1) {
                            String msg = "Only considering first occurance of type "
                                    + "shp in zip file.";
                            log.info(msg);
                        }
                        source = shapefiles.get(0);
                        break;
                    default:
                        break;
                }
                try {
                    is.close();
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        } else if (input instanceof URI) {
            log.debug("Input instance of URI.");
            URI uri = (URI) input;
            InputStream is;
            try {
                URL url = uri.toURL();
                is = url.openStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                String ext = IOUtils.getFileExtension(bis);
                switch (ext) {
                    case IOUtils.SHP:
                        log.warn("No support for shp from URL. Use zipped shapefile.");
                        break;
                    case IOUtils.ZIP:
                        List<Shapefile> shapefiles = ShapefileReader.unzipShapezip(bis);
                        if (shapefiles.isEmpty()) {
                            String msg = "Shapezip is empty.";
                            log.error(msg);
                            throw new RuntimeException(msg);
                        }
                        if (shapefiles.size() > 1) {
                            String msg = "Only considering first occurance of type "
                                    + "shp in zip file.";
                            log.info(msg);
                        }
                        source = shapefiles.get(0);
                        break;
                    default:
                        break;
                }
                try {
                    is.close();
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }

        } else if (input instanceof InputStream) {
            log.debug("Input instance of InputStream.");
            InputStream is = (InputStream) input;
            BufferedInputStream bis = new BufferedInputStream(is);
            String ext = IOUtils.getFileExtension(bis);
            switch (ext) {
                case IOUtils.SHP:
                    log.warn("No support for shp from URL. Use zipped shapefile.");
                    break;
                case IOUtils.ZIP:
                    List<Shapefile> shapefiles = ShapefileReader.unzipShapezip(bis);
                    if (shapefiles.isEmpty()) {
                        String msg = "Shapezip is empty.";
                        log.error(msg);
                        throw new RuntimeException(msg);
                    }
                    if (shapefiles.size() > 1) {
                        String msg = "Only considering first occurance of type "
                                + "shp in zip file.";
                        log.info(msg);
                    }
                    source = shapefiles.get(0);
                    break;
            }
        } else {
            String message = "Datatype not supported. Use URL, URI, File, or Inputstream.";
            log.warn(message);
            throw new IllegalArgumentException(message);
        }
        return source;
    }

    /**
     * Checks whether the passed {@link InputStream} - that is converted into
     * {@link ZipInputStream} - contains a shapefile.
     *
     * Note that only the file extension is checked to be equal to
     * {@link IOUtils#SHP}.
     *
     * @param is the {@link InputStream} to be checked
     * @return {@code true} if the stream contains at least one file with the
     * file extension {@link IOUtils#SHP}. Otherwise {@code false}.
     */
    private static boolean isShapezip(InputStream is) {

        try {
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(IOUtils.SHP)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }

        return false;
    }

    /**
     * Unzip the passed {@link InputStream} into the
     * {@link VisSettings#USER_HOME_DIR} directory and return a {@link List} of
     * {@link Shapefile} objects that are in the zip.
     *
     * @param input the {@link InputStream} to unzip
     * @return the {@link List} of {@link Shapefile} objects from the zip
     * stream.
     */
    private static List<Shapefile> unzipShapezip(InputStream input) {
        ArrayList<Shapefile> shapefiles = new ArrayList<>();
        try {
            List<String> entries = IOUtils.unzip(input, VisSettings.USER_HOME_DIR);

            for (Iterator<String> it = entries.iterator(); it.hasNext();) {
                String path = it.next();
                if (path.endsWith(IOUtils.SHP)) {
                    Shapefile s = new Shapefile(path);
                    shapefiles.add(s);
                }
            }
            return shapefiles;
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return shapefiles;
    }
}
