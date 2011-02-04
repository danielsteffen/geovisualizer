/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.LoaderBase;
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
public abstract class AbstractLoader extends LoaderBase {

//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:why is basepath & url differently treated. Code from Java 3D ObjectFile
  private final static Logger logger = LoggerFactory.getLogger(AbstractLoader.class);
  private boolean fromUrl = false;
  
  @Override
  public Scene load(final String fileName) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
    if (logger.isDebugEnabled()) {
      logger.debug("Loading scene from file: " + fileName + " ...");
    }
    setBasePathFromFilename(fileName);
    final Reader reader = new BufferedReader(new FileReader(fileName));
    return load(reader);
  }

  @Override
  public Scene load(final URL url) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
    if (logger.isDebugEnabled()) {
      logger.debug("Loading scene from url: " + url + " ...");
    }
    if (baseUrl == null) {
      setBaseUrlFromUrl(url);
    }
    try {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      fromUrl = true;
      return load(reader);
    } catch (IOException e) {
      throw new FileNotFoundException(e.getMessage());
    }
  }

  public Scene load(final File file) throws FileNotFoundException,IncorrectFormatException, ParsingErrorException{
    setBasePathFromFilename(file.getAbsolutePath());
    return load(new BufferedReader(new FileReader(file)));
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
}
