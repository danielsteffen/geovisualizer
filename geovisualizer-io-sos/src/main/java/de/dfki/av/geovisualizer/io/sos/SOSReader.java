/*
 *  SOSReader.java 
 *
 *  Created by DFKI AV on 01.02.2013.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.io.sos;

import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.ISourceReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ISourceReader} to read a SOS source.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class SOSReader implements ISourceReader {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(SOSReader.class);

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
                log.debug("Input is no SOS source: ", ex);
                return false;
            }
        } else if (input instanceof URL) {
            URL uri = (URL) input;
            url = uri.toExternalForm();
        } else {
            log.debug("Input is no SOS source (No instance of String / URL / URI).");
            return false;
        }
        if (url != null && url.contains("/sos/pox")) {
            if ((url.contains("&sensor="))
                    || url.contains("&getAll")
                    || url.contains("&device="))  {
                String[] parts = url.split("/sos/pox");
                if (parts.length == 2 && parts[0].startsWith("http")) {
                    return true;
                } else {
                    log.debug("Input is no SOS source.");
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public ISource read(Object input) {
        return new SOS(input);
    }
}
