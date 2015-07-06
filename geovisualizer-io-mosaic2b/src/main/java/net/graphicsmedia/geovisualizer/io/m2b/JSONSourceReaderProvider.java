/**
 * JSONSourceReaderProvider.java
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

import de.dfki.av.geovisualizer.core.ISourceReader;
import de.dfki.av.geovisualizer.core.spi.ISourceReaderProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link ISourceReaderProvider} to provide the {@link ISourceReader} for JSON
 * sources.
 *
 * @author Daniel Steffen
 */
public class JSONSourceReaderProvider implements ISourceReaderProvider {

    @Override
    public List<String> getSourceReader() {
        List<String> providers = new ArrayList<>();
        providers.add(JSONSourceReader.class.getName());
        return Collections.unmodifiableList(providers);
    }

    @Override
    public ISourceReader get(String name) {
        if (name.equalsIgnoreCase(JSONSourceReader.class.getName())) {
            return new JSONSourceReader();
        }
        return null;
    }
}
