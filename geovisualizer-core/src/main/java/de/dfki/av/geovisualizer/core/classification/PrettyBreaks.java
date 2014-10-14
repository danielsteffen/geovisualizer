/*
 * PrettyBreaks.java
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
 * Pretty Breaks classification algorithm.
 *
 * Returns class breaks for the Pretty Breaks classification.
 *
 * Compute a sequence of about n+1 equally spaced nice values which cover the
 * range of the values in x. The values are chosen so that they are 1, 2 or 5
 * times a power of 10.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class PrettyBreaks implements IClassification {

    /*
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(PrettyBreaks.class);

    @Override
    public final List<IClass> classify(ISource data, String attribute, int numClasses) {
        ArrayList<IClass> classList = new ArrayList<>();
        LOG.debug("Setting up classes.");
        double min = data.min(attribute);
        double max = data.max(attribute);
        double intervalSize = (max - min) / (double) numClasses;
        double prettyBreak = 1.0;

        if (intervalSize > 2.0) {
            prettyBreak = 2.0;
        }
        if (intervalSize < 10.0) {
            prettyBreak = 5.0;
        }
        if (intervalSize >= 10.0) {
            prettyBreak = 10.0;
        }

        double multiplier = 10.0;
        while (intervalSize >= multiplier * 10.0) {
            prettyBreak = multiplier * 10.0;
        }

        double add = prettyBreak;
        while ((prettyBreak + add) * (numClasses - 1) + min < max) {
            prettyBreak = prettyBreak + add;
        }
        intervalSize = prettyBreak;

        double last = 0.0;
        double next;

        if (min < 0) {
            while (last > min) {
                last = -intervalSize;
            }
        } else {
            while (last + intervalSize < min) {
                last = +intervalSize;
            }
        }

        NumberInterval interval;

        for (int i = 0; i < numClasses; i++) {
            if (i == numClasses - 1) {
                next = max;
            } else {
                next = (i + 1) * intervalSize;
            }
            interval = new NumberInterval(last, next);
            LOG.debug("Interval {} to {}", interval.getMinValue(), interval.getMaxValue());
            last = next;
            classList.add(interval);
        }
        return classList;
    }

    @Override
    public String getName() {
        return "Pretty Breaks";
    }
}
