package com.dfki.av.sudplan.vis.core;

import com.dfki.av.sudplan.vis.functions.EqualIntervals;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author steffen
 */
public class ClassificationFactory {

    /**
     *
     * @return
     */
    public static List<String> getAvailableClassifications() {
        List<String> classifications = new ArrayList<String>();
        classifications.add(EqualIntervals.class.getSimpleName());
        return Collections.unmodifiableList(classifications);
    }

    /**
     *
     * @param name
     * @return
     */
    public static IClassification get(String name) {
        IClassification c = null;
        if (name.equalsIgnoreCase(EqualIntervals.class.getSimpleName())) {
            c = new EqualIntervals();
        }
        return c;
    }
}
