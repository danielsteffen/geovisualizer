/*
 * PluginFactory.java
 *
 * Created by DFKI AV on 12.03.2014.
 * Copyright (c) 2011-2014 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory that collects - facilitating Service Provider Interface (SPI) - all
 * available implementations of {@link IPluginProvider}. The
 * implementations of {@link IPluginProvider} provide the
 * {@link IPlugin} implementations.
 *
 * @see IPluginProvider
 * @see IPlugin
 */
public class PluginFactory {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PluginFactory.class);
    /**
     * The {@link List} of full-qualified class names of {@link IPlugin}.
     */
    private static final List<String> NAME_LIST = new ArrayList<>();
    /**
     * The {@link List} of {@link IPluginProvider}.
     */
    private static final List<IPluginProvider> PLUGIN_LIST = new ArrayList<>();

    /**
     * Initialization of static class members.
     */
    static {
        ServiceLoader<IPluginProvider> service = ServiceLoader.load(IPluginProvider.class);
        for (Iterator<IPluginProvider> providers = service.iterator(); providers.hasNext();) {
            IPluginProvider provider = providers.next();
            LOG.info("Found plugin: {}", provider.getClass().getName());
            NAME_LIST.addAll(provider.getPluginNames());
            PLUGIN_LIST.add(provider);
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
    public static IPlugin newInstance(String name) {
        IPlugin plugin = null;
        for (IPluginProvider provider : PLUGIN_LIST) {
            plugin = provider.get(name);
            if (plugin != null) {
                break;
            }
        }
        return plugin;
    }
}