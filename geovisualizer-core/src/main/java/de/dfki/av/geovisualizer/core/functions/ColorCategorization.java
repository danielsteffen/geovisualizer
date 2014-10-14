/*
 * ColorCategorization.java
 *
 * Created by DFKI AV on 09.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.ICategory;
import de.dfki.av.geovisualizer.core.render.Legend;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public abstract class ColorCategorization extends ColorTransferFunction {

    /**
     * The {@link List} of {@link ICategory} elements.
     */
    protected List<ICategory> categories;

    /**
     * Constructor.
     */
    public ColorCategorization() {
        super();
        this.categories = new ArrayList<>();
    }

    @Override
    public Object calc(Object o) {
        if (o == null) {
            log.error("Argument set to null.");
            return null;
        }

        for (int i = 0; i < categories.size(); i++) {
            ICategory c = categories.get(i);
            if (c.contains(o)) {
                return colorList.get(i);
            }
        }
        log.error("Should not reach this part.");
        return null;
    }

    @Override
    public Legend getLegend(String title) {
        setLegend(Legend.newInstance(title, colorList, categories));
        return getLegend();
    }
}
