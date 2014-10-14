/*
 * ISourceReaderProvider.java
 *
 * Created by DFKI AV on 07.09.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.spi;

import de.dfki.av.geovisualizer.core.ISourceReader;
import java.util.List;

/**
 * An implementation of the {@link ISourceReaderProvider} collects a list of
 * available {@link ISourceReader}.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface ISourceReaderProvider {

    /**
     * Returns a {@link List} of names of the {@link ISourceReader}
     * implementations that the provider supports.
     *
     * @return the {@link List} of names.
     */
    List<String> getSourceReader();

    /**
     * Returns an instance for the passed name of an {@link ISourceReader}
     * implementation.
     *
     * @param name the name of the {@link ISourceReader} implementation
     * @return an instance of the {@link ISourceReader} implementation
     */
    ISourceReader get(String name);
}
