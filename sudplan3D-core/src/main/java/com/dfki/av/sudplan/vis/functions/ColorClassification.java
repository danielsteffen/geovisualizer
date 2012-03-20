/*
 *  ColorClassification.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.vis.core.IClass;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author steffen
 */
public abstract class ColorClassification extends ColorTransferFunction {

    /**
     * The list of classes defined for this color classification.
     */
    protected List<IClass> classes;

    /**
     * Constructor of <code>ColorClassification</code>.
     */
    public ColorClassification() {
        super();
        this.classes = new ArrayList<IClass>();
    }

    /**
     * Adds an {@link IClass} object and the its {@link Color} to the classification.
     * 
     * @param c the {@link IClass} to set.
     * @param color the {@link Color} to set.
     */
    public void addClassification(IClass c, Color color) {
        this.classes.add(c);
        this.colorList.add(color);
    }

    /**
     * Clears the {@link #classes} and the {@link #colorList} {@link List}.
     */
    public void clear() {
        this.classes.clear();
        this.colorList.clear();
    }

    @Override
    public Object calc(Object o) {
        if (o == null) {
            log.error("Argument set to null.");
            return Color.GRAY;
        }

        if (o instanceof Number) {
            double arg = ((Number) o).doubleValue();
            for (int i = 0; i < classes.size(); i++) {
                IClass c = classes.get(i);
                if (c.contains(arg)) {
                    return colorList.get(i);
                }
            }
            log.error("Should not reach this part.");
        } else {
            log.error("Data type {} not supported", o.getClass().getSimpleName());
            return Color.GRAY;
        }
        return Color.GRAY;
    }
}
