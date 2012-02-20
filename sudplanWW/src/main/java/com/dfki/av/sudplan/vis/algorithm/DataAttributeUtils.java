package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
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
     * Auto classification of the parameter <cod>attribute<code> of the data source
     * <code>shpfile</code>. 
     *
     * @param shpfile the data source.
     * @param attribute the attribute to classify.
     * @param numClasses the number of classes to produce.
     * @return array of boundaries of the classes to return
     */
    public static double[] AutoClassificationOfNumberAttribute(Shapefile shpfile, String attribute, int numClasses) {

        log.debug("Auto classification of attribute <{}>.", attribute);
        log.debug("Searching min and max value for attribute <{}>.", attribute);
        double minValue = Shapefile.Min(shpfile, attribute);
        double maxValue = Shapefile.Max(shpfile, attribute);
        log.debug("Attribute {} min value is {}.", attribute, minValue);
        log.debug("Attribute {} man value is {}.", attribute, maxValue);

        double size = (maxValue - minValue) / numClasses;
        double[] bounds = new double[numClasses + 1];

        for (int i = 0; i < bounds.length; i++) {
            bounds[i] = minValue + i * size;
        }

        log.debug("Class boundaries: {}", bounds);

        return bounds;
    }
    
    public static void AutoClassificationOfStringAttribute(Shapefile shpfile, String attribute){
        
    }
    
}
