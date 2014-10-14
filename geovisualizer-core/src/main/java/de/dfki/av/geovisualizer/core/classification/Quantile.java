/*
 * Quantile.java
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
 * Quantile classification algorithm.
 *
 * Returns class breaks for the quantile classification.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class Quantile implements IClassification {

    /*
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(Quantile.class);

    @Override
    public List<IClass> classify(ISource data, String attribute, int numClasses) {

        LOG.debug("Setting up classes.");
        List intervals = new LinkedList();
        NumberInterval interval;
        ArrayList<Double> list = new ArrayList<>();

        for (int i = 0; i < data.getFeatureCount(); i++) {
            Object o = data.getValue(i, attribute);
            if (o != null) {
                if (o instanceof Number) {
                    Number number = (Number) o;
                    double value = number.doubleValue();
                    Double d = Double.valueOf(value);
                    list.add(d);
                } else {
                    LOG.warn("Value for feature {} attribute {} is not of type Number.", i, attribute);
                }
            } else {
                LOG.warn("Value for feature {} attribute {} is null.", i, attribute);
            }
        }

        Collections.sort(list, new DoubleComp());

        LOG.debug("Number of classes: {}", numClasses);
        LOG.debug("Number of features: {}", data.getFeatureCount());

        double last;
        double next = Double.MAX_VALUE;
        int numberOfDifferentValues = 0;

        for (int j = 1; j <= list.size(); j++) {
            last = ((Double) list.get(j - 1)).doubleValue();
            if (last <= Double.MAX_VALUE) {
                if ((last - next) > .00001) {
                    numberOfDifferentValues = numberOfDifferentValues + 1;
                    next = last;
                }
            }
        }
        double min = data.min(attribute);
        double max = data.max(attribute);
        LOG.debug("Minimum value: {}", min);
        LOG.debug("Maximum value: {}", max);

        if (numClasses >= numberOfDifferentValues) {
            LOG.info("Not enough values for {} caterogies.", numClasses);
            for (int i = 1; i <= numClasses; i++) {
                last = ((Double) list.get(i - 1)).doubleValue();
                if (last != next) {
                    intervals.add(new NumberInterval(last, last));
                }
                next = last;
            }
        } else {
            int leftOver = data.getFeatureCount() - numClasses * (data.getFeatureCount() / numClasses);
            last = min;
            int quanta = data.getFeatureCount() / numClasses;
            LOG.debug("Quanta: {}", quanta);
            for (int i = 1; i <= numClasses; i++) {
                if (i == numClasses) {
                    interval = new NumberInterval(last, max + 0.0000001);
                    LOG.debug("Last Interval {} to {}",
                            interval.getMinValue(), interval.getMaxValue());
                } else {
                    if (leftOver > 0) {
                        next = ((Double) list.get(i * quanta)).doubleValue();
                        leftOver = leftOver - 1;
                    } else {
                        next = ((Double) list.get(i * quanta)).doubleValue();
                    }
                    interval = new NumberInterval(last, next);
                    LOG.debug("Interval {} to {}",
                            interval.getMinValue(), interval.getMaxValue());
                }

                last = next;

                intervals.add(interval);
            }
        }
        return intervals;
    }

    @Override
    public String getName() {
        return "Quantile";
    }
}
