package com.dfki.av.sudplanX.test;

import com.dfki.av.sudplan.io.DataInput;
import com.dfki.av.sudplan.io.shapefile.Shapefile;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class CategorizationAuto implements Categorization {

    /*
     * 
     */
    private static final Logger log = LoggerFactory.getLogger(CategorizationAuto.class);
    /**
     * 
     */
    private int numCategories;
    /**
     * 
     */
    public CategorizationAuto(int num){
        this.numCategories = num;
    }
    
    @Override
    public List<Category> execute(DataInput data, String attribute) {

        if (data instanceof Shapefile) {
            Shapefile shpfile = (Shapefile) data;
            Object o = shpfile.getAttributeOfFeature(0, attribute);
            if (o instanceof Number) {
                log.debug("Type of attribute {} is Number.", attribute);
                return computeCategoriesForNumberAttribute(shpfile, attribute, numCategories);
            } else if (o instanceof String) {
                log.debug("Type of attribute {} is String.", attribute);
                return computeCategoriesForStringAttribute(shpfile, attribute);
            } else {
                log.error("Type of attribute {} not supported for classification.", attribute);
            }
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
    private List<Category> computeCategoriesForNumberAttribute(Shapefile shpfile, String attribute, int numClasses) {

        log.debug("Searching min and max value for attribute <{}>.", attribute);
        double totalMinValue = Shapefile.MIN(shpfile, attribute);
        double totalMaxValue = Shapefile.MAX(shpfile, attribute);
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
    private List<Category> computeCategoriesForStringAttribute(Shapefile shpfile, String attribute) {
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
