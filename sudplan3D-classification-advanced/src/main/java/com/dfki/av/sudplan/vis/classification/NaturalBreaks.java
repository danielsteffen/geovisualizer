/*
 *  NaturalBreaks.java 
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
 * Jenks Natural Breaks algorithm
 *
 * Based on a JAVA and Fortran code available here:
 * https://stat.ethz.ch/pipermail/r-sig-geo/2006-March/000811.html
 *
 * Returns class breaks such that classes are internally homogeneous while
 * assuring heterogeneity among classes.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class NaturalBreaks implements IClassification {

    private static final Logger log = LoggerFactory.getLogger(NaturalBreaks.class);

    /**
     *
     * @param list
     * @param numCategories
     * @return
     */
    private int[] getJenksBreaks(ArrayList list, int numCategories) {
        int numValues = list.size();

        double[][] mat1 = new double[numValues + 1][numCategories + 1];
        double[][] mat2 = new double[numValues + 1][numCategories + 1];


        for (int i = 1; i <= numCategories; i++) {
            mat1[1][i] = 1;
            mat2[1][i] = 0;
            for (int j = 2; j <= numValues; j++) {
                mat2[j][i] = Double.MAX_VALUE;
            }
        }

        double v = 0;
        for (int l = 2; l <= numValues; l++) {
            double s1 = 0;
            double s2 = 0;
            double w = 0;
            for (int m = 1; m <= l; m++) {
                int i3 = l - m + 1;
                double val = ((Double) list.get(i3 - 1)).doubleValue();

                s2 += val * val;
                s1 += val;

                w++;
                v = s2 - (s1 * s1) / w;
                int i4 = i3 - 1;
                if (i4 != 0) {
                    for (int j = 2; j <= numCategories; j++) {
                        if (mat2[l][j] >= (v + mat2[i4][j - 1])) {
                            mat1[l][j] = i3;
                            mat2[l][j] = v + mat2[i4][j - 1];
                        }
                    }
                }
            }
            mat1[l][1] = 1;
            mat2[l][1] = v;
        }
        int k = numValues;


        int[] classes = new int[numCategories];


        classes[numCategories - 1] = list.size() - 1;


        for (int j = numCategories; j >= 2; j--) {
            log.debug("rank = " + mat1[k][j]);
            int id = (int) (mat1[k][j]) - 2;
            log.debug("val = " + list.get(id));

            classes[j - 2] = id;

            k = (int) mat1[k][j] - 1;
        }
        return classes;
    }

    @Override
    public List<IClass> classify(ISource data, String attribute, int numClasses) {
        List intervals = new LinkedList();
        double next;
        double last;

        ArrayList list = new ArrayList();

        for (int i = 1; i < data.getFeatureCount(); i++) {
            list.add(data.getFeature(i).GetFieldAsDouble(attribute));
        }
        Collections.sort(list, new DoubleComp());

        int[] classes = getJenksBreaks(list, numClasses);

        last = ((Double) list.get(0)).doubleValue();

        NumberInterval interval;

        for (int i = 1; i <= numClasses; i++) {
            int active;
            if (i == numClasses) {
                active = classes[numClasses - 1];
                next = ((Double) list.get(active)).doubleValue();
                interval = new NumberInterval(last, next + 0.0000001);
            } else {
                active = classes[i - 1];
                next = ((Double) list.get(active)).doubleValue();
                interval = new NumberInterval(last, next);
            }
            log.debug("Interval {} to {}",
                    interval.getMinValue(), interval.getMaxValue());
            last = next;
            intervals.add(interval);
        }
        return intervals;
    }

    @Override
    public String getName() {
        return "Natural Breaks (Jenks)";
    }
}
