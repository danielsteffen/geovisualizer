/**
 * JSONSourceReader.java
 *
 * Created by <a href="mailto:daniel.steffen@graphicsmedia.net">Daniel
 * Steffen</a> on 01.08.2014.
 *
 * Copyright (c) 2014 MOSAIC 2B Consortium Members. All rights reserved.
 *
 * This software is developed as part of the project MOSAIC 2B
 * (http://www.mobile-empowerment.org) and has received funding from the
 * European Union’s Seventh Framework Programme for research, technological
 * development and demonstration under grant agreement no 611796.
 */
package net.graphicsmedia.geovisualizer.io.m2b;

import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.ISourceReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ISourceReader} to read a JSONSource source.
 *
 * @author Daniel Steffen <daniel.steffen at graphicsmedia.net>
 */
public class JSONSourceReader implements ISourceReader {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JSONSourceReader.class);

    @Override
    public boolean canRead(Object input) {
        String url;
        if (input instanceof String) {
            url = (String) input;
        } else if (input instanceof URI) {
            URI uri = (URI) input;
            try {
                url = uri.toURL().toExternalForm();
            } catch (MalformedURLException ex) {
                LOG.debug("Input is no JSONSource source: ", ex);
                return false;
            }
        } else if (input instanceof URL) {
            URL uri = (URL) input;
            url = uri.toExternalForm();
        } else {
            LOG.debug("Input is no JSONSource source (No instance of String / URL / URI).");
            return false;
        }
        return url != null && url.contains("rest");
    }

    @Override
    public ISource read(Object input) {
        return new JSONSource(input);
    }
}
