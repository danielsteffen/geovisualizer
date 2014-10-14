/*
 * TransferFunctionFactory.java
 *
 * Created by DFKI AV on 21.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.spi;

import de.dfki.av.geovisualizer.core.ITransferFunction;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory that collects - facilitating Service Provider Interface (SPI) - all
 * available implementations of {@link ITransferFunctionProvider}. The
 * implementations of {@link ITransferFunctionProvider} provide the
 * {@link ITransferFunction} implementations.
 *
 * @see ITransferFunctionProvider
 * @see ITransferFunction
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class TransferFunctionFactory {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TransferFunctionFactory.class);
    /**
     * The {@link List} of full-qualified class names of
     * {@link ITransferFunction}.
     */
    private static final List<String> NAME_LIST = new ArrayList<>();
    /**
     * The {@link List} of {@link ITransferFunctionProvider}.
     */
    private static final List<ITransferFunctionProvider> PROVIDER_LIST = new ArrayList<>();

    /**
     * Initialization.
     */
    static {
        ServiceLoader<ITransferFunctionProvider> service = ServiceLoader.load(ITransferFunctionProvider.class);
        for (Iterator<ITransferFunctionProvider> providers = service.iterator(); providers.hasNext();) {
            ITransferFunctionProvider provider = providers.next();
            LOG.info("Found plugin: {}", provider.getClass().getName());
            NAME_LIST.addAll(provider.getTransferFunctions());
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
     * @return a new instance of the class {@code function}
     */
    public static ITransferFunction newInstance(String name) {
        ITransferFunction transferFunction = null;
        for (ITransferFunctionProvider provider : PROVIDER_LIST) {
            transferFunction = provider.get(name);
            if (transferFunction != null) {
                break;
            }
        }
        return transferFunction;
    }
}
