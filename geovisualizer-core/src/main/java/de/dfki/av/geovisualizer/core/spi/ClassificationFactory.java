/*
 * ClassificationFactory.java
 *
 * Created by DFKI AV on 21.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.spi;

import de.dfki.av.geovisualizer.core.IClassification;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory that collects - facilitating Service Provider Interface (SPI) - all
 * available implementations of {@link IClassificationProvider}. The
 * implementations of {@link IClassificationProvider} offer the
 * {@link IClassification} implementations.
 *
 * @see IClassificationProvider
 * @see IClassification
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ClassificationFactory {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ClassificationFactory.class);
    /**
     * The {@link List} of full-qualified class names of
     * {@link IClassification}.
     */
    private static final List<String> NAME_LIST = new ArrayList<>();
    /**
     * The {@link List} of {@link IClassificationProvider}.
     */
    private static final List<IClassificationProvider> providerList = new ArrayList<>();

    /**
     * Initialization.
     */
    static {
        ServiceLoader<IClassificationProvider> service = ServiceLoader.load(IClassificationProvider.class);
        for (Iterator<IClassificationProvider> providers = service.iterator(); providers.hasNext();) {
            IClassificationProvider provider = providers.next();
            LOG.info("Found plugin: {}", provider.getClass().getName());
            NAME_LIST.addAll(provider.getClassifications());
            providerList.add(provider);
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
    public static IClassification newInstance(String name) {
        IClassification classificaiton = null;
        for (IClassificationProvider provider : providerList) {
            classificaiton = provider.get(name);
            if (classificaiton != null) {
                break;
            }
        }
        return classificaiton;
    }
}
