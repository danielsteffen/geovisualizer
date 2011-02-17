/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public abstract class AbstractLoader implements Loader {

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:credits for loaderbase
//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:why is basepath & url differently treated. Code from Java 3D ObjectFile
    private final static Logger logger = LoggerFactory.getLogger(AbstractLoader.class);
    private boolean fromUrl = false;
    /** Stores the baseUrl for data files associated with the URL
     * passed into load(URL).*/
    protected URL baseUrl = null;
    /** Stores the basePath for data files associated with the file
     * passed into load(String).*/
    protected String basePath = null;
    protected BufferedReader reader = null;
    protected File file=null;

    // Constructors
    /**
     * Constructs a Loader with default values for all variables.
     */
    public AbstractLoader() {
    }

    // Variable get/set methods
    /**
     * This method sets the base URL name for data files associated with
     * the file.  The baseUrl should be null by default, which is an indicator
     * to the loader that it should look for any associated files starting
     * from the same place as the URL passed into the load(URL) method.
     * Note: Users of setBaseUrl() would then use load(URL)
     * as opposed to load(String).
     */
    public void setBaseUrl(URL url) {
        baseUrl = url;
    }

    /**
     * This method sets the base path name for data files associated with
     * the file.  The basePath should be null by default, which is an indicator
     * to the loader that it should look for any associated files starting
     * from the same directory as the file passed into the load(String)
     * method.
     * Note: Users of setBasePath() would then use load(String)
     * as opposed to load(URL).
     */
    public void setBasePath(String pathName) {
        basePath = pathName;
    }

    /**
     * Returns the current base URL setting.
     */
    public URL getBaseUrl() {
        return baseUrl;
    }

    /**
     * Returns the current base path setting.
     */
    public String getBasePath() {
        return basePath;
    }

    public Scene load(final String fileName) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading scene from string: " + fileName + " ...");
        }                
        return load(new File(fileName));
    }
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:totally inconsistent --> refactor everything

    public Scene load(final URL url) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading scene from url: " + url + " ...");
        }
//        if (baseUrl == null) {
//            setBaseUrlFromUrl(url);
//        }
//        try {
//            fromUrl = true;
//            reader = new BufferedReader(new InputStreamReader(url.openStream()));
//            return load();
//        } catch (IOException e) {
//            throw new FileNotFoundException(e.getMessage());
//        }
        return load(url.getFile());
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:problem if it should be loaded from a url. Just switch everything build on the url
    public Scene load(final File file) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        this.file = file;
        setBasePathFromFilename(file.getAbsolutePath());
        reader = new BufferedReader(new FileReader(file));
        return load();
    }

    private void setBaseUrlFromUrl(final URL url) throws FileNotFoundException {
        final String u = url.toString();
        String s;
        if (u.lastIndexOf('/') == -1) {
            s = url.getProtocol() + ":";
        } else {
            s = u.substring(0, u.lastIndexOf('/') + 1);
        }
        try {
            baseUrl = new URL(s);
        } catch (final MalformedURLException e) {
            throw new FileNotFoundException(e.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Setting base url from url: " + getBaseUrl());
        }
    } // End of setBaseUrlFromUrl

    /**
     * Set the path where files associated with this .asc file are
     * located.
     * Only needs to be called to set it to a different directory
     * from that containing the .obj file.
     */
    private void setBasePathFromFilename(final String fileName) {
        if (fileName.lastIndexOf(java.io.File.separator) == -1) {
            // No path given - current directory
            setBasePath("." + java.io.File.separator);
        } else {
            setBasePath(
                    fileName.substring(0, fileName.lastIndexOf(java.io.File.separator)));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Setting base path from filename: " + getBasePath());
        }
    } // End of setBasePathFromFilename

    @Override
    public Scene load(Object source) throws LoadingNotPossibleException {
        if (source == null) {
            final String message = "source object is null.";
            if (logger.isErrorEnabled()) {
                logger.error(message);
            }
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:better capsulate in LoadingNotPossibleException
            throw new IllegalArgumentException(message);
        }
        try {
            if (source instanceof String) {
                return load((String) source);
            } else if (source instanceof File) {
                return load((File) source);
            } else if (source instanceof URL) {
                return load((URL) source);
            } else {
                throw new LoadingNotPossibleException("Unrecognised object class: " + source.getClass() + " not possible to load from this source.");
            }
        } catch (Exception ex) {
            final String message = "Error while loading scene.";
            if (logger.isErrorEnabled()) {
                logger.error(message);
            }
            throw new LoadingNotPossibleException(message, ex);
        }
    }

    public abstract Scene load() throws FileNotFoundException,
            IncorrectFormatException,
            ParsingErrorException;
}
