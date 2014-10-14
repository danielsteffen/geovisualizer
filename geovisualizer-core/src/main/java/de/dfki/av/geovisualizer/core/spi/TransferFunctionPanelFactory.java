/*
 * TransferFunctionPanelFactory.java
 *
 * Created by DFKI AV on 26.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.spi;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.ITransferFunction;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory that collects - facilitating Service Provider Interface (SPI) - all
 * available implementations of {@link ITransferFunctionPanelProvider}. The
 * implementations of {@link ITransferFunctionPanelProvider} provide the
 * {@link AbstractTransferFunctionPanel} implementations.
 *
 * @see ITransferFunctionPanelProvider
 * @see AbstractTransferFunctionPanel
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class TransferFunctionPanelFactory {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TransferFunctionPanelFactory.class);
    /**
     * The {@link List} of full-qualified class names of
     * {@link AbstractTransferFunctionPanel}.
     */
    private static final List<String> NAME_LIST = new ArrayList<>();
    /**
     * The {@link List} of {@link ITransferFunctionPanelProvider}.
     */
    private static final List<ITransferFunctionPanelProvider> PROVIDER_LIST = new ArrayList<>();

    /**
     * Initialization.
     */
    static {
        ServiceLoader<ITransferFunctionPanelProvider> service = ServiceLoader.load(ITransferFunctionPanelProvider.class);
        for (Iterator<ITransferFunctionPanelProvider> providers = service.iterator(); providers.hasNext();) {
            ITransferFunctionPanelProvider provider = providers.next();
            LOG.info("Found plugin: {}", provider.getClass().getName());
            NAME_LIST.addAll(provider.getTFPanels());
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
     * {@code function}.
     *
     * @param function the full qualified class name
     * @return a new instance of the class {@code function}
     */
    public static AbstractTransferFunctionPanel newInstance(ITransferFunction function) {
        AbstractTransferFunctionPanel panel = null;
        for (ITransferFunctionPanelProvider provider : PROVIDER_LIST) {
            panel = provider.get(function);
            if (panel != null) {
                break;
            }
        }
        return panel;
    }
}
