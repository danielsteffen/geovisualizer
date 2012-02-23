/*
 *  DataAttributeUtils.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class DataAttributeUtils {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(DataAttributeUtils.class);
    /**
     *
     */
    private static final int DEFAULT_NUM_CLASSES = 5;

    /*
     *
     */
    public static List<Category> AutoClassificationOfAttribute(Shapefile shpfile, String attribute) {
        log.debug("Auto classification of attribute <{}>.", attribute);
        return AutoClassificationOfAttribute(shpfile, attribute, DEFAULT_NUM_CLASSES);
    }

    /**
     * Auto classification of the parameter <cod>attribute<code> of the data source
     * <code>shpfile</code>.
     *
     * @param shpfile the data source.
     * @param attribute the attribute to classify.
     * @param numClasses the number of classes to produce.
     * @return array of boundaries of the classes to return
     */
    public static List<Category> AutoClassificationOfAttribute(Shapefile shpfile, String attribute, int numClasses) {

        Object o = shpfile.getAttributeOfFeature(numClasses, attribute);
        if (o instanceof Number) {
            log.debug("Type of attribute {} is Number.", attribute);
            return AutoClassificationOfNumberAttribute(shpfile, attribute, numClasses);
        } else if (o instanceof String) {
            log.debug("Type of attribute {} is String.", attribute);
            return AutoClassificationOfStringAttribute(shpfile, attribute);
        } else {
            log.error("Type of attribute {} not supported for classification.", attribute);
        }
        // Should not reach this.
        return null;
    }

    /**
     * Auto classification of the parameter <cod>attribute<code> of the data source
     * <code>shpfile</code>.
     *
     * @param shpfile the data source.
     * @param attribute the attribute to classify.
     * @param numClasses the number of classes to produce.
     * @return array of boundaries of the classes to return
     */
    public static List<Category> AutoClassificationOfNumberAttribute(Shapefile shpfile, String attribute, int numClasses) {

        log.debug("Searching min and max value for attribute <{}>.", attribute);
        double totalMinValue = Shapefile.Min(shpfile, attribute);
        double totalMaxValue = Shapefile.Max(shpfile, attribute);
        log.debug("Attribute {} min value is {}.", attribute, totalMinValue);
        log.debug("Attribute {} man value is {}.", attribute, totalMaxValue);
        double intervallSize = (totalMaxValue - totalMinValue) / numClasses;
        ArrayList<Category> categories = new ArrayList<Category>();
        for (int i = 0; i < numClasses; i++) {
            double minValue = totalMinValue + i * intervallSize;
            double maxValue = totalMinValue + (i + 1) * intervallSize;
            NumberCategory n = new NumberCategory(minValue, maxValue);
            categories.add(n);
        }
        log.debug("Generated {} categories.", categories.size());
        return categories;
    }

    /**
     *
     * @param shpfile
     * @param attribute
     * @return
     */
    public static List<Category> AutoClassificationOfStringAttribute(Shapefile shpfile, String attribute) {
        log.debug("Auto classification of attribute <{}>.", attribute);
        ArrayList<Category> categories = new ArrayList<Category>();
        for (int i = 0; i < shpfile.getFeatureCount(); i++) {
            Object o = shpfile.getAttributeOfFeature(i, attribute);
            // Todo attention here ..... potential error.
            if (o instanceof String) {
                if (!categories.contains(new StringCategory((String) o))) {
                    categories.add(new StringCategory((String) o));
                }
            }
        }
        log.debug("Generated {} categories.", categories.size());
        return categories;
    }
}
