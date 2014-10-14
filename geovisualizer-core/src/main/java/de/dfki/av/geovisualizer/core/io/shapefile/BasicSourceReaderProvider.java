/*
 * BasicSourceReaderProvider.java
 *
 * Created by DFKI AV on 07.09.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.io.shapefile;

import de.dfki.av.geovisualizer.core.ISourceReader;
import de.dfki.av.geovisualizer.core.spi.ISourceReaderProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class collects the basic {@link ISourceReader} and provides them using
 * Java's SPI technique. Currently, only one {@link ISourceReader} is provided
 * namely the {@link ShapefileReader} to support loading of shapefiles.
 *
 * @see ISourceReader
 * @see ISourceReaderProvider
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class BasicSourceReaderProvider implements ISourceReaderProvider {

    /**
     * Returns the {@link String} {@link List} of {@link ISourceReader}
     * implementations.
     *
     * @return a {@link List} of names ({@code getName()}) of the
     * {@link ISourceReader} implementation.
     */
    @Override
    public List<String> getSourceReader() {
        List<String> providers = new ArrayList<>();
        providers.add(ShapefileReader.class.getName());
        return Collections.unmodifiableList(providers);
    }

    /**
     * Returns an instance of the passed {@code name} class. The instance is of
     * type {@link ISourceReader}
     *
     * @param name the {@code name} of the {@link ISourceReader} implementation.
     * @return the instance of the {@code name} class or null if the given
     * {@code name} is not supported by this provider
     */
    @Override
    public ISourceReader get(String name) {
        if (name.equalsIgnoreCase(ShapefileReader.class.getName())) {
            return new ShapefileReader();
        }
        return null;
    }
}
