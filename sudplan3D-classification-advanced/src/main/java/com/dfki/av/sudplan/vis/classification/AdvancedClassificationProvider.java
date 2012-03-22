/*
 *  AdvancedClassificationProvider.java 
 *
 *  Created by DFKI AV on 20.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.classification;

import com.dfki.av.sudplan.vis.core.IClassification;
import com.dfki.av.sudplan.vis.spi.IClassificationProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class AdvancedClassificationProvider implements IClassificationProvider {

    @Override
    public List<String> getClassifications() {
        List<String> classifications = new ArrayList<String>();
        classifications.add(Quantile.class.getName());
        classifications.add(NaturalBreaks.class.getName());
        classifications.add(StandardDeviation.class.getName());
        classifications.add(PrettyBreaks.class.getName());

        return Collections.unmodifiableList(classifications);
    }

    @Override
    public IClassification get(String name) {
        if (name.equalsIgnoreCase(Quantile.class.getName())) {
            return new Quantile();
        }
        if (name.equalsIgnoreCase(NaturalBreaks.class.getName())) {
            return new NaturalBreaks();
        }
        if (name.equalsIgnoreCase(StandardDeviation.class.getName())) {
            return new StandardDeviation();
        }
        if (name.equalsIgnoreCase(PrettyBreaks.class.getName())) {
            return new PrettyBreaks();
        }
        return null;
    }
}
