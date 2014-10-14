/*
 * BasicClassificationProvider.java
 *
 * Created by DFKI AV on 01.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.classification;

import de.dfki.av.geovisualizer.core.IClassification;
import de.dfki.av.geovisualizer.core.spi.IClassificationProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class BasicClassificationProvider implements IClassificationProvider {

    @Override
    public List<String> getClassifications() {
        List<String> classifications = new ArrayList<>();
        classifications.add(EqualIntervals.class.getName());
        classifications.add(Quantile.class.getName());
        classifications.add(NaturalBreaks.class.getName());
        classifications.add(StandardDeviation.class.getName());
        classifications.add(PrettyBreaks.class.getName());

        return Collections.unmodifiableList(classifications);
    }

    @Override
    public IClassification get(String name) {
        if (name.equalsIgnoreCase(EqualIntervals.class.getName())) {
            return new EqualIntervals();
        } else if (name.equalsIgnoreCase(Quantile.class.getName())) {
            return new Quantile();
        } else if (name.equalsIgnoreCase(NaturalBreaks.class.getName())) {
            return new NaturalBreaks();
        } else if (name.equalsIgnoreCase(StandardDeviation.class.getName())) {
            return new StandardDeviation();
        } else if (name.equalsIgnoreCase(PrettyBreaks.class.getName())) {
            return new PrettyBreaks();
        }
        return null;
    }
}
