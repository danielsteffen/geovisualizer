package com.dfki.av.sudplan.vis.classification;

import com.dfki.av.sudplan.vis.core.IClassification;
import com.dfki.av.sudplan.vis.spi.IClassificationProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author steffen
 */
public class BasicClassificationProvider implements IClassificationProvider {

    @Override
    public List<String> getClassifications() {
        List<String> classifications = new ArrayList<String>();
        classifications.add(EqualIntervals.class.getName());

        return Collections.unmodifiableList(classifications);
    }

    @Override
    public IClassification get(String name) {
        if (name.equalsIgnoreCase(EqualIntervals.class.getName())) {
            return new EqualIntervals();
        }
        return null;
    }
}
