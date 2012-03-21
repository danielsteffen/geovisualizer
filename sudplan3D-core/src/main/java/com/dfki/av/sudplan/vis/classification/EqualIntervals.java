/*
 *  EqualIntervals.java 
 *
 *  Created by DFKI AV on 20.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.classification;

import com.dfki.av.sudplan.vis.core.IClass;
import com.dfki.av.sudplan.vis.core.IClassification;
import com.dfki.av.sudplan.vis.core.ISource;
import com.dfki.av.sudplan.vis.functions.NumberInterval;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class EqualIntervals implements IClassification {

    /*
     * The logger for the all <code>ColorTransferFunction</code>.
     */
    private static final Logger log = LoggerFactory.getLogger(EqualIntervals.class);

    @Override
    public final List<IClass> classify(ISource data, String attribute, int numClasses) {
        ArrayList<IClass> classList = new ArrayList<IClass>();
        log.debug("Setting up classes.");
        double min = data.min(attribute);
        double max = data.max(attribute);
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
