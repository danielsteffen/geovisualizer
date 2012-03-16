/*
 *  AVUtils.java 
 *
 *  Created by DFKI AV on 13.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.utils;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class AVUtils {
    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(AVUtils.class);

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static File DownloadFileToDirectory(URL url, String directory) throws IOException {
        // Get filename form url.
        String urlFile = url.getFile();
        log.debug("URL file: {}", urlFile);
        String urlPath = url.getPath();
        log.debug("URL path: {}", urlFile);
        String fileName = urlPath.substring(urlFile.lastIndexOf('/') + 1, urlPath.length());
        fileName = directory + File.separator + fileName;
        log.debug("Creating file with filename: {}", fileName);
        File tmpFile = new File(fileName);

        log.info("Downloading from {} to {}", url.toString(), tmpFile.getAbsolutePath());
        FileOutputStream out = new FileOutputStream(tmpFile);
        BufferedInputStream in = new BufferedInputStream(url.openStream());
        copyInputStream(in, out);
        log.info("Download finished.");

        return tmpFile;
    }

    /**
     *
     * @param url
     * @param prefix
     * @param suffix
     * @return
     * @throws IOException
     */
    public static File DownloadToTempFile(URL url, String prefix, String suffix) throws IOException {
        BufferedInputStream in = new BufferedInputStream(url.openStream());
        File tmpFile = File.createTempFile(prefix, suffix);
        tmpFile.deleteOnExit();
        log.debug("Downloading from {} to {}", url.toString(), tmpFile.getAbsolutePath());
        FileOutputStream out = new FileOutputStream(tmpFile);
        copyInputStream(in, out);
        log.debug("Download finished.");
        return tmpFile;
    }

    /**
     *
     * @param file
     * @param directory
     * @throws IOException
     */
    public static void Unzip(File file, String directory) throws IOException {
        log.debug("Unzipping {}", file.getName());
        ZipFile zipFile = new ZipFile(file);
        Enumeration zipEntries = zipFile.entries();
        String seperator = System.getProperty("file.separator");
        while (zipEntries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipEntries.nextElement();
            log.debug("Unzipping entry {}", entry.getName());

            if (entry.isDirectory()) {
                // Assume directories are stored parents first then children.
                // This is not robust, just for demonstration purposes.
                (new File(directory + seperator + entry.getName())).mkdir();
                continue;
            }

            copyInputStream(zipFile.getInputStream(entry),
                    new BufferedOutputStream(new FileOutputStream(
                    directory + seperator + entry.getName())));
        }
        log.debug("Unzipping finished.");
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
}
