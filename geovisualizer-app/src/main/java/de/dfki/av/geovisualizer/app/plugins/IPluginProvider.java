/*
 * IPluginProvider.java
 *
 * Created by DFKI AV on 12.03.2014.
 * Copyright (c) 2011-2014 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.plugins;


import java.util.List;

/**
 * The interface for the SPI provider handling the {@link IPlugin}.
 */
public interface IPluginProvider {

    /**
     * Returns the {@link List} of plug-ins. The name is the name of
     * the class ({@code getClass.getName()} implementing the
     * {@link IPlugin}, {@code null} or an empty list.
     *
     * @return the {@link List} of {@link String} names
     */
    List<String> getPluginNames();

    /**
     * Returns the {@link IPlugin} with class name {@code name}.
     *
     * @param name the name of the {@link IPlugin}
     * @return the {@link IPlugin} to return or null
     */
    IPlugin get(String name);
}
