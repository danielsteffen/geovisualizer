/*
 * SensorMapPluginProvider.java
 *
 * Created by wearHEALTH on 20.04.2015.
 * Copyright (c) 2015 TU Kaiserslautern, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.uni.kl.cs.geovisualizer.plugin.sensormap;

import de.dfki.av.geovisualizer.app.plugins.IPlugin;
import de.dfki.av.geovisualizer.app.plugins.IPluginProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class collects and provides all IPlugin implementations of this library.
 */
public class SensorMapPluginProvider implements IPluginProvider {

    @Override
    public List<String> getPluginNames() {
        List<String> pluginList = new ArrayList<>();
        pluginList.add(SensorMapPlugin.class.getName());

        return Collections.unmodifiableList(pluginList);
    }

    @Override
    public IPlugin get(String name) {
        IPlugin plugin = null;
        if (SensorMapPlugin.class.getName().equalsIgnoreCase(name)) {
            plugin = new SensorMapPlugin();
        }
        return plugin;
    }
}
