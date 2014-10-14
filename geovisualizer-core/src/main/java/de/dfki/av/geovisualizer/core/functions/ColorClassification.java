/*
 * ColorClassification.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.IClass;
import de.dfki.av.geovisualizer.core.render.Legend;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public abstract class ColorClassification extends ColorTransferFunction {

    /**
     * The list of classList defined for this color classification.
     */
    protected List<IClass> classList;

    /**
     * Constructor of
     * <code>ColorClassification</code>.
     */
    public ColorClassification() {
        super();
        this.classList = new ArrayList<>();
    }

    /**
     * Adds an {@link IClass} object and the its {@link Color} to the
     * classification.
     *
     * @param c the {@link IClass} to set.
     * @param color the {@link Color} to set.
     */
    public void addClassification(IClass c, Color color) {
        log.debug("Class {}", c.toString());
        this.classList.add(c);
        this.colorList.add(color);
        setLegend(Legend.newInstance(colorList, classList));
    }

    /**
     * Clears the {@link #classList} and the {@link #colorList} {@link List}.
     */
    public void clear() {
        this.classList.clear();
        this.colorList.clear();
        setLegend(null);
    }

    @Override
    public Object calc(Object o) {
        if (o == null) {
            log.error("Argument set to null.");
            return null;
        }

        if (o instanceof Number) {
            double arg = ((Number) o).doubleValue();
            for (int i = 0; i < classList.size(); i++) {
                IClass c = classList.get(i);
                if (c.contains(arg)) {
                    return colorList.get(i);
                }
            }
            log.debug("Value {} not element of class list.", arg);
        } else {
            log.error("Data type {} not supported", o.getClass().getSimpleName());
            return null;
        }
        return null;
    }

    @Override
    public Legend getLegend(String title) {
        setLegend(Legend.newInstance(title, colorList, classList));
        return getLegend();
    }
}
