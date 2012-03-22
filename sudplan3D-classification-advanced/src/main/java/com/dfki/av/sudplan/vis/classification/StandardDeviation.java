/*
 *  StandardDeviation.java 
 *
 *  Created by DFKI AV on 20.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.classification;

import com.dfki.av.sudplan.vis.classification.utils.DoubleComp;
import com.dfki.av.sudplan.vis.core.IClass;
import com.dfki.av.sudplan.vis.core.IClassification;
import com.dfki.av.sudplan.vis.core.ISource;
import com.dfki.av.sudplan.vis.functions.NumberInterval;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standart Deviation classication algorithm.
 *
 * Returns class breaks for the standart deviation classifaction.
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

    private static final Logger log = LoggerFactory.getLogger(StandardDeviation.class);

    @Override
    public List<IClass> classify(ISource data, String attribute, int numClasses) {
        List intervals = new LinkedList();
        ArrayList list = new ArrayList();
        double number = 0.0;
        double value;
        double min = data.min(attribute);
        double max = data.max(attribute);

        log.debug("Number of categories: {}", numClasses);
        log.debug("Number of features: {}", data.getFeatureCount());
        log.debug("Minimum value: {}", min);
        log.debug("Maximum value: {}", max);

        for (int i = 0; i < data.getFeatureCount(); i++) {
            value = data.getFeature(i).GetFieldAsDouble(attribute);
            list.add(value);
            number += value;
        }
        Collections.sort(list, new DoubleComp());

        // calculate standart deviation
        double mean = number / (double) data.getFeatureCount();
        double deviation = 0.0;
        for (int i = 0; i < list.size(); i++) {
            deviation += Math.pow(((Double) list.get(i)).doubleValue() - mean, 2);
        }
        deviation = Math.sqrt((1.0 / (double) list.size()) * deviation);
        log.debug("Standart deviation: {}", deviation);

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
            log.debug("Interval {} to {}", last, next);
            last = next;
        }

        log.debug("Number of categories: {}", numClasses);
        log.debug("Number of features: {}", data.getFeatureCount());

        return intervals;
    }

    @Override
    public String getName() {
        return "Standard Deviation";
    }
}
