/*
 *  AVUtils.java 
 *
 *  Created by DFKI AV on 13.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
     * @param prefix
     * @param suffix
     * @return
     * @throws IOException 
     */
    public static File DownloadToTempFile(URL url, String prefix, String suffix) throws IOException {
        BufferedInputStream in = new BufferedInputStream(url.openStream());
        File tmpFile = File.createTempFile(prefix, suffix);
        tmpFile.deleteOnExit();
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
     * @param file
     * @param directory
     * @throws IOException 
     */
    public static void Unzip(File file, String directory) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Unzipping ... {}", file.getName());
        }

        // Unpack files into directory.
        ZipFile zipFile = new ZipFile(file);
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
                (new File(directory + seperator + entry.getName())).mkdir();
                continue;
            }

            copyInputStream(zipFile.getInputStream(entry),
                    new BufferedOutputStream(new FileOutputStream(
                    directory + seperator + entry.getName())));
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
}
