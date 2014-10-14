/*
 * SourceReaderFactory.java
 *
 * Created by DFKI AV on 07.09.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.spi;

import de.dfki.av.geovisualizer.core.ISourceReader;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SourceReaderFactory} collects all implementations of the
 * {@link ISourceReaderProvider} interface using the {@link ServiceLoader}
 * technique.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class SourceReaderFactory {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SourceReaderFactory.class);
    /**
     * The {@link List} of registered {@link ISourceReader}.
     */
    private static final List<String> NAME_LIST = new ArrayList<>();
    /**
     * The {@link List} of registered {@link ISourceReaderProvider}.
     */
    private static final List<ISourceReaderProvider> PROVIDER_LIST = new ArrayList<>();

    /**
     * Initialization. Register all available {@link ISourceReader}.
     */
    static {
        ServiceLoader<ISourceReaderProvider> service = ServiceLoader.load(ISourceReaderProvider.class);
        for (Iterator<ISourceReaderProvider> providers = service.iterator(); providers.hasNext();) {
            ISourceReaderProvider provider = providers.next();
            LOG.info("Found plugin: {}", provider.getClass().getName());
            NAME_LIST.addAll(provider.getSourceReader());
            PROVIDER_LIST.add(provider);
        }
    }

    /**
     * Returns the {@link String} {@link List} of registered
     * {@link ISourceReader}.
     *
     * @return the {@link String} {@link List} to return
     */
    public static List<String> getNames() {
        return Collections.unmodifiableList(NAME_LIST);
    }

    /**
     * Returns an instance of an {@link ISourceReader} or {@code null} for the
     * given {@code name}
     *
     * @param name the name of the {@link ISourceReader} implementation.
     * @return an instance of a {@link ISourceReader} or {@code null}
     */
    public static ISourceReader newInstance(String name) {
        ISourceReader sourceReader = null;
        for (ISourceReaderProvider provider : PROVIDER_LIST) {
            sourceReader = provider.get(name);
            if (sourceReader != null) {
                break;
            }
        }
        return sourceReader;
    }
}
