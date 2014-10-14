/*
 *  SOSSourceReaderProvider.java 
 *
 *  Created by DFKI AV on 01.02.2013.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.io.sos;

import de.dfki.av.geovisualizer.core.ISourceReader;
import de.dfki.av.geovisualizer.core.spi.ISourceReaderProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link ISourceReaderProvider} to provide the {@link ISourceReader} for SOS
 * sources.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class SOSSourceReaderProvider implements ISourceReaderProvider {

    @Override
    public List<String> getSourceReader() {
        List<String> providers = new ArrayList<>();
        providers.add(SOSReader.class.getName());
        return Collections.unmodifiableList(providers);
    }

    @Override
    public ISourceReader get(String name) {
        if (name.equalsIgnoreCase(SOSReader.class.getName())) {
            return new SOSReader();
        }
        return null;
    }
}
