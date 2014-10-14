/*
 * StandardDeviation.java
 *
 * Created by DFKI AV on 20.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.classification;

import de.dfki.av.geovisualizer.core.IClass;
import de.dfki.av.geovisualizer.core.IClassification;
import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.classification.utils.DoubleComp;
import de.dfki.av.geovisualizer.core.functions.NumberInterval;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard Deviation classification algorithm.
 *
 * Returns class breaks for the standard deviation classification.
 *
 * <pre>
 *                          ----------------------------
 *                          |  1   ---
 * Standard Deviation =     | ___  \   ( x - mean ) ^ 2
 *                         \|  N   /__
 * </pre>
 *
 * N = amount of values x = value
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class StandardDeviation implements IClassification {

    /*
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(StandardDeviation.class);

    @Override
    public List<IClass> classify(ISource data, String attribute, int numClasses) {

        LOG.debug("Setting up classes.");
        List intervals = new LinkedList();
        ArrayList list = new ArrayList();
        double number = 0.0;
        double value;
        double min = data.min(attribute);
        double max = data.max(attribute);

        LOG.debug("Number of classes: {}", numClasses);
        LOG.debug("Number of features: {}", data.getFeatureCount());
        LOG.debug("Minimum value: {}", min);
        LOG.debug("Maximum value: {}", max);

        for (int i = 0; i < data.getFeatureCount(); i++) {
            Object object = data.getValue(i, attribute);
            if (object != null) {
                if (object instanceof Number) {
                    value = ((Number) object).doubleValue();
                    list.add(value);
                    number += value;
                } else {
                    LOG.warn("Value for feature {} attribute {} is not of type Number.", i, attribute);
                }
            } else {
                LOG.warn("Value for feature {} attribute {} is null.", i, attribute);
            }
        }
        Collections.sort(list, new DoubleComp());

        // calculate standart deviation
        double mean = number / (double) data.getFeatureCount();
        double deviation = 0.0;
        for (int i = 0; i < list.size(); i++) {
            deviation += Math.pow(((Double) list.get(i)).doubleValue() - mean, 2);
        }
        deviation = Math.sqrt((1.0 / (double) list.size()) * deviation);
        LOG.debug("Standart deviation: {}", deviation);

        // calculate classes
        double last = min;
        double next;
        double bound = 0.0;
        for (int i = 0; i < numClasses; i++) {
            if (i == 0) {
                last = min;
            }
            if (i == numClasses - 1) {
                next = max;
            } else {
                next = mean + (i * deviation);
            }
            intervals.add(new NumberInterval(last, next));
            LOG.debug("Interval {} to {}", last, next);
            last = next;
        }

        LOG.debug("Number of categories: {}", numClasses);
        LOG.debug("Number of features: {}", data.getFeatureCount());

        return intervals;
    }

    @Override
    public String getName() {
        return "Standard Deviation";
    }
}
