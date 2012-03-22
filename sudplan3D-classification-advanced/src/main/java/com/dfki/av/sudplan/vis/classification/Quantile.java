/*
 *  Quantile.java 
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
 * Quantile classification algorithm.
 *
 * Returns class breaks for the quantile classifaction.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class Quantile implements IClassification {

    private static final Logger log = LoggerFactory.getLogger(Quantile.class);

    @Override
    public List<IClass> classify(ISource data, String attribute, int numClasses) {
        List intervals = new LinkedList();
        NumberInterval interval;
        ArrayList list = new ArrayList();

        for (int i = 0; i < data.getFeatureCount(); i++) {
            list.add(data.getFeature(i).GetFieldAsDouble(attribute));
        }
        Collections.sort(list, new DoubleComp());


        log.debug("Number of categories: {}", numClasses);
        log.debug("Number of features: {}", data.getFeatureCount());

        double last;
        double next;

        next = Double.MAX_VALUE;
        int numberOfDifferentValues = 0;
        for (int j = 1; j <= list.size(); j++) {
            last = ((Double) list.get(j - 1)).doubleValue();
            if (last != Double.MAX_VALUE) {
                if (last != next) {
                    numberOfDifferentValues = numberOfDifferentValues + 1;
                    next = last;
                }
            }
        }
        double min = data.min(attribute);
        double max = data.max(attribute);
        log.debug("Minimum value: {}", min);
        log.debug("Maximum value: {}", max);

        if (numClasses >= numberOfDifferentValues) {
            log.info("Not enough values for {} caterogies.", numClasses);
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
            log.debug("Quanta: {}", quanta);
            for (int i = 1; i <= numClasses; i++) {
                if (i == numClasses) {
                    interval = new NumberInterval(last, max + 0.0000001);
                    log.debug("Last Interval {} to {}",
                            interval.getMinValue(), interval.getMaxValue());
                } else {
                    if (leftOver > 0) {
                        next = ((Double) list.get(i * quanta)).doubleValue();
                        leftOver = leftOver - 1;
                    } else {
                        next = ((Double) list.get(i * quanta)).doubleValue();
                    }
                    interval = new NumberInterval(last, next);
                    log.debug("Interval {} to {}",
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