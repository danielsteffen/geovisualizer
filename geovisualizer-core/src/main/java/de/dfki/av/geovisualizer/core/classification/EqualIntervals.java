/*
 * EqualIntervals.java
 *
 * Created by DFKI AV on 20.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.classification;

import de.dfki.av.geovisualizer.core.IClass;
import de.dfki.av.geovisualizer.core.IClassification;
import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.functions.NumberInterval;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class EqualIntervals implements IClassification {

    /*
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(EqualIntervals.class);

    @Override
    public final List<IClass> classify(ISource data, String attribute, int numClasses) {
        ArrayList<IClass> classList = new ArrayList<>();
        LOG.debug("Setting up classes.");
        double min = data.min(attribute);
        double max = data.max(attribute);
        LOG.debug("Minimum for attribute '{}': {}", attribute, min);
        LOG.debug("Maximum for attribute '{}': {}", attribute, max);
        double intervalSize = (max - min) / (double) numClasses;

        for (int i = 0; i < numClasses; i++) {
            double t0 = min + i * intervalSize;
            double t1 = min + (i + 1) * intervalSize;
            NumberInterval m = new NumberInterval(t0, t1);
            classList.add(m);
        }
        return classList;
    }

    @Override
    public String getName() {
        return "Equal Intervals";
    }
}
