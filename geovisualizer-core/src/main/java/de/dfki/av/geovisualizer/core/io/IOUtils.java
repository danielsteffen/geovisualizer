/*
 * IOUtils.java
 *
 * Created by DFKI AV on 07.10.2011.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.io;

import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.ISourceReader;
import de.dfki.av.geovisualizer.core.VisSettings;
import de.dfki.av.geovisualizer.core.spi.ISourceReaderProvider;
import de.dfki.av.geovisualizer.core.spi.SourceReaderFactory;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class offers the possibility to read and decode a data source into an
 * {@link ISource} object using an {@link ISourceReader} implementation.
 *
 * @see "http://www.garykessler.net/library/file_sigs.html"
 * @see ISourceReader
 * @see ISourceReaderProvider
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class IOUtils {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(IOUtils.class);
    /**
     * File extension for gzip data types.
     */
    public static final String GZIP = ".gzip";
    /**
     * File extension for zip data types.
     */
    public static final String ZIP = ".zip";
    /**
     * File extension for shp data types.
     */
    public static final String SHP = ".shp";
    /**
     * File extension for tif data types.
     */
    public static final String TIF = ".tif";

    /**
     * Returns a {@link ISource} as the result of decoding a supplied
     * <code>input</code> object with an {@link ISourceReader} chosen
     * automatically from among those currently registered.
     *
     * @param input the input object to decode.
     * @return the {@link ISource} object.
     * @throws RuntimeException if no {@link ISourceReader} can handle data.
     * @throws IllegalArgumentException if input == null
     */
    public static ISource read(Object input) {
        if (input == null) {
            String msg = "input == null.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        List<String> sourceReaders = SourceReaderFactory.getNames();
        ISourceReader reader;
        // The following part is currently a hack / fake
        // Need to save the inputstream and then pass it to the IOUtils
        // Inside the IOUtils the BufferedInputReader is used to determine
        // the type of the InputStream. However, it seems that some bytes
        // are getting lost if the method IOUtils.GetFileExtension is
        // called twice on the InputStream...
        Object data = null;
        if (input instanceof InputStream) {
            InputStream is = null;
            try {
                is = (InputStream) input;
                File file = IOUtils.downloadToTempFile(is);
                file.deleteOnExit();
                data = file.toURI();
            } catch (IOException ex) {
                log.error(ex.toString());
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    log.error(ex.toString());
                }
            }
        } else {
            data = input;
        }
        // ...check if the hack worked...
        if (data == null) {
            String msg = "data == null. Download of InputStream failed.";
            log.error(msg);
            return null;
        }
        // ... and here ends the fake.

        // Find reader for input
        for (Iterator<String> it = sourceReaders.iterator(); it.hasNext();) {
            String string = it.next();
            reader = SourceReaderFactory.newInstance(string);

            if (reader.canRead(data)) {
                String readerName = reader.getClass().getSimpleName();
                log.debug("Found {} to handle data.", readerName);
                return reader.read(data);
            }
        }
        String message = "No reader of type " + ISourceReader.class.getName()
                + " found to handle data.";
        log.error(message);
        throw new IllegalArgumentException(message);
    }

    /**
     * Checks the {@link BufferedInputStream} for its file signature (aka "magic
     * numbers") and returns the particular file extension if supported.
     *
     * @param input the {@link BufferedInputStream} to check.
     * @throws IllegalArgumentException if input is {@code null}
     */
    public static String getFileExtension(final BufferedInputStream input) {
        if (input == null) {
            String msg = "input == null!";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try {
            byte[] buffer = new byte[4];
            input.mark(0);
            input.read(buffer);
            input.reset();

            //check if matches standard gzip maguc number
            if (isGZip(buffer)) {
                log.debug("Found gzip input stream");
                return IOUtils.GZIP;
            } else if (isZip(buffer)) {
                log.debug("Found zip input stream");
                return IOUtils.ZIP;
            } else if (isShp(buffer)) {
                log.debug("Found shp input stream");
                return IOUtils.SHP;
            } else if (isTiff(buffer)) {
                log.debug("Found tif input stream.");
                return IOUtils.TIF;
            }
        } catch (IOException ex) {
            log.error(ex.toString());
        }
        String msg = "Could not check type of inputstream";
        log.debug(msg);
        throw new RuntimeException(msg);
    }

    /**
     * Returns whether the this byte buffer is the magic number for a zip file.
     * The magic number is {@code 0x50 0x4B 0x03 0x04}.
     *
     * @param buffer the byte header to check
     * @return {@code true} if zip file otherwise {@code false}
     */
    private static boolean isZip(byte[] buffer) {
        return (buffer[0] == (byte) 0x50) && (buffer[1] == (byte) 0x4b)
                && (buffer[2] == (byte) 0x03) && (buffer[3] == (byte) 0x04);
    }

    /**
     * Returns whether the this byte buffer is the magic number for a gzip file.
     * The magic number is {@code 0x1F 0x8B 0x08}.
     *
     * @param buffer the byte header to check
     * @return {@code true} if gzip file otherwise {@code false}
     */
    private static boolean isGZip(byte[] buffer) {
        return (buffer[0] == (byte) 0x1f) && (buffer[1] == (byte) 0x8b)
                && (buffer[2] == (byte) 0x08);
    }

    /**
     * Returns whether the this byte buffer is the magic number for a shp file.
     *
     * @param buffer the byte header to check
     * @return {@code true} if shp file otherwise {@code false}
     */
    private static boolean isShp(byte[] buffer) {
        return (buffer[0] == (byte) 0x00) && (buffer[1] == (byte) 0x00)
                && (buffer[2] == (byte) 0x27) && (buffer[3] == (byte) 0x0A);
    }

    /**
     * Returns whether the this byte buffer is the magic number for a tiff file.
     * The magic number is {@code 0x49 0x20 0x49} and
     * {@code 0x49 0x49 0x2A 0x00}.
     *
     * @param buffer the byte header to check
     * @return {@code true} if tiff file otherwise {@code false}
     */
    private static boolean isTiff(byte[] buffer) {
        if ((buffer[0] == (byte) 0x49) && (buffer[1] == (byte) 0x20)
                && (buffer[2] == (byte) 0x49)) {
            // 49 20 49	 	I I
            // TIF, TIFF	Tagged Image File Format file
            return true;
        } else if ((buffer[0] == (byte) 0x49) && (buffer[1] == (byte) 0x49)
                && (buffer[2] == (byte) 0x2A) && (buffer[3] == (byte) 0x00)) {
            //49 49 2A 00	 	II*.
            //TIF, TIFF	 	Tagged Image File Format file (little
            //                  endian, i.e., LSB first in the byte; Intel)
            return true;
        }
        return false;
    }

    /**
     * Downloads the content of the {@link URL} to a temporary file and returns
     * the {@link File}. The file is saved with prefix "geovisualizer-" and a
     * suffix ".tmp".
     *
     * @param url the {@link URL} to download from
     * @return the temporary {@link File}
     * @throws IOException
     * @throws IllegalArgumentException if url == null
     * @see IOUtils#downloadToTempFile(java.net.URI)
     * @see IOUtils#downloadToTempFile(java.io.InputStream)
     * @see File#createTempFile(java.lang.String, java.lang.String)
     */
    public static File downloadToTempFile(URL url) throws IOException {
        if (url == null) {
            String msg = "url == null.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        InputStream input = url.openStream();
        return IOUtils.downloadToTempFile(input);

    }

    /**
     * Downloads the content of the {@link URI} to a temporary file and returns
     * the {@link File}. The file is saved with prefix "geovisualizer-" and a
     * suffix ".tmp".
     *
     * @param uri the {@link URI} to download from
     * @return the temporary {@link File}
     * @throws IOException
     * @throws IllegalArgumentException if uri == null
     * @see IOUtils#downloadToTempFile(java.net.URL)
     * @see IOUtils#downloadToTempFile(java.io.InputStream)
     * @see File#createTempFile(java.lang.String, java.lang.String)
     */
    public static File downloadToTempFile(URI uri) throws IOException {
        if (uri == null) {
            String msg = "uri == null.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return IOUtils.downloadToTempFile(uri.toURL());
    }

    /**
     * Downloads the content of the {@link InputStream} to a temporary file and
     * returns the {@link File}. The file is saved with prefix "geovisualizer-"
     * and a suffix ".tmp".
     *
     * @param input the {@link InputStream} to download
     * @return the temporary {@link File}
     * @throws IOException
     * @throws IllegalArgumentException if input == null
     * @see IOUtils#downloadToTempFile(java.net.URL)
     * @see IOUtils#downloadToTempFile(java.net.URI)
     * @see File#createTempFile(java.lang.String, java.lang.String)
     */
    public static File downloadToTempFile(InputStream input) throws IOException {
        if (input == null) {
            String msg = "input == null.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        File file;
        try {
            file = File.createTempFile("geovisualizer-", ".tmp");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = input.read(buffer)) >= 0) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw ex;
        }

        return file;
    }

    /**
     * Unzip the {@link File} to the directory {@code directory}. In case no
     * valid argument is passed for the {@code directory} the directory
     * {@link VisSettings#USER_HOME_DIR} is used.
     *
     * @param file the {@link File} to unzip
     * @param dir the directory to put the content of the zip file
     * @return a {@link List} of the entries
     * @throws IOException
     * @throws IllegalArgumentException if file == null or directory == null or
     * {@link File#exists()} returns {@code false}
     */
    public static List<String> unzip(File file, String dir) throws IOException {
        if (file == null) {
            String msg = "file == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else if (!file.exists()) {
            String msg = "file not existing.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (dir == null) {
            String msg = "directory == null.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else if (dir.isEmpty()) {
            dir = VisSettings.USER_HOME_DIR;
        }

        log.debug("Unzipping {} to {}", file.getName(), dir);

        ArrayList<String> entries = new ArrayList<>();
        String seperator = System.getProperty("file.separator");
        ZipFile zipFile = new ZipFile(file);
        Enumeration zipEntries = zipFile.entries();

        while (zipEntries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipEntries.nextElement();
            log.debug("Unzipping entry {}", entry.getName());

            if (entry.isDirectory()) {
                // Assume directories are stored parents first then children.
                // This is not robust, just for demonstration purposes.
                String path = dir + seperator + entry.getName();
                (new File(path)).mkdir();
                continue;
            }

            File tmpfile;
            try (InputStream is = zipFile.getInputStream(entry)) {
                tmpfile = new File(dir + seperator + entry.getName());
                FileOutputStream fos = new FileOutputStream(tmpfile);
                try (BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                    IOUtils.CopyInputStream(is, bos);
                    bos.flush();
                }
            }

            entries.add(tmpfile.getAbsolutePath());
        }
        log.debug("Unzipping finished.");

        return entries;
    }

    /**
     * Unzip the {@link InputStream} to the directory {@code directory}. In case
     * no valid argument is passed for the {@code directory} the directory
     * {@link VisSettings#USER_HOME_DIR} is used.
     *
     * @param input the {@link File} to unzip
     * @param dir the directory to put the content of the zip file
     * @return a {@link List} of the entries
     * @throws IOException
     * @throws IllegalArgumentException if input == null or directory == null or
     * {@link File#exists()} returns {@code false}
     */
    public static List<String> unzip(InputStream input, String dir) throws IOException {

        if (input == null) {
            String msg = "input == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (dir == null) {
            String msg = "directory == null.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else if (dir.isEmpty()) {
            dir = VisSettings.USER_HOME_DIR;
        }

        ArrayList<String> entries = new ArrayList<>();

        log.debug("Unzipping shapezip.");
        BufferedInputStream bis = new BufferedInputStream(input);
        ZipInputStream zis = new ZipInputStream(bis);

        String seperator = System.getProperty("file.separator");
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {

            log.debug("Unzipping entry {}", entry.getName());

            String entryName = dir + seperator + entry.getName();
            if (entry.isDirectory()) {
                // Assume directories are stored parents first then children.
                // This is not robust, just for demonstration purposes.
                (new File(entryName)).mkdir();
                continue;
            }

            File file = new File(entryName);
            FileOutputStream fos = new FileOutputStream(file);
            try (BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                IOUtils.CopyInputStream(zis, bos);
                bos.flush();
            }
            entries.add(file.getAbsolutePath());
        }
        log.debug("Unzipping finished.");

        return entries;
    }

    /**
     * Copy the {@link InputStream} to the {@link OutputStream}.
     *
     * @param in the {@link InputStream}
     * @param out the {@link OutputStream}
     * @throws IOException
     */
    private static void CopyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
    }
}
