/*
 * VisAlgorithmFactory.java
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.spi;

import de.dfki.av.geovisualizer.core.IVisAlgorithm;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory that collects - facilitating Service Provider Interface (SPI) - all
 * available implementations of {@link IVisAlgorithmProvider}. The
 * implementations of {@link IVisAlgorithmProvider} provide the
 * {@link IVisAlgorithm} implementations.
 *
 * @see IVisAlgorithmProvider
 * @see IVisAlgorithm
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisAlgorithmFactory {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(VisAlgorithmFactory.class);
    /**
     * The {@link List} of full-qualified class names of {@link IVisAlgorithm}.
     */
    private static final List<String> NAME_LIST = new ArrayList<>();
    /**
     * The {@link List} of {@link IVisAlgorithmProvider}.
     */
    private static final List<IVisAlgorithmProvider> PROVIDER_LIST = new ArrayList<>();

    /**
     * Initialization of static class members.
     */
    static {
        ServiceLoader<IVisAlgorithmProvider> service = ServiceLoader.load(IVisAlgorithmProvider.class);
        for (Iterator<IVisAlgorithmProvider> providers = service.iterator(); providers.hasNext();) {
            IVisAlgorithmProvider provider = providers.next();
            LOG.info("Found plugin: {}", provider.getClass().getName());
            NAME_LIST.addAll(provider.getVisualizationNames());
            PROVIDER_LIST.add(provider);
        }
    }

    /**
     * Returns the {@link List} of full qualified class names.
     *
     * @return the {@link List} of full qualified class names to return.
     */
    public static List<String> getNames() {
        return Collections.unmodifiableList(NAME_LIST);
    }

    /**
     * Create a new instance of the class with the full qualified name
     * {@code name}.
     *
     * @param name the full qualified class name
     * @return a new instance of the class {@code name}
     */
    public static IVisAlgorithm newInstance(String name) {
        IVisAlgorithm algo = null;
        for (IVisAlgorithmProvider provider : PROVIDER_LIST) {
            algo = provider.get(name);
            if (algo != null) {
                break;
            }
        }
        return algo;
    }
}
