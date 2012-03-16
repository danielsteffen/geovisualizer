/*
 *  ColorrampClassification.java 
 *
 *  Created by DFKI AV on 09.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.vis.core.IClass;
import com.dfki.av.sudplan.vis.core.DataSource;
import com.dfki.av.utils.ColorUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class ColorrampClassification extends ColorClassification {
    /*
     *
     */

    private static final Logger log = LoggerFactory.getLogger(ColorrampClassification.class);
    /**
     *
     */
    private int numClasses;
    /**
     *
     */
    private List<IClass> classes;
    /**
     *
     */
    private Color[] colorramp;
    /**
     *
     */
    private Color startColor;
    /**
     *
     */
    private Color endColor;

    /**
     *
     */
    public ColorrampClassification() {
        this.numClasses = 1;
        this.startColor = Color.GREEN;
        this.endColor = Color.RED;
        this.colorramp = ColorUtils.CreateLinearHSVColorGradient(startColor, endColor, numClasses);
        this.classes = new ArrayList<IClass>();
        this.classes.add(new NumberInterval());
    }

    @Override
    public Object calc(Object o) {
        if (o == null) {
            log.error("Argument set to null.");
            return Color.GRAY;
        }

        if (o instanceof Number) {
            double arg = ((Number) o).doubleValue();
            for(int i = 0; i < classes.size(); i++){
                IClass c = classes.get(i);
                if(c.contains(arg)){
                    return colorramp[i];
                }
            }
            log.error("Should not reach this part.");
        } else {
            log.error("Data type {} not supported", o.getClass().getSimpleName());
            return Color.GRAY;
        }
        return Color.GRAY;
    }

    @Override
    public String getName() {
        return "Color ramp";
    }

    @Override
    public void preprocess(DataSource data, String attribute) {
        log.debug("Preprocessing ...");
        this.colorramp = ColorUtils.CreateLinearHSVColorGradient(startColor, endColor, numClasses);
        double min = data.min(attribute);
        double max = data.max(attribute);
        log.debug("Minimum for attribute {} is {}.", attribute, min);
        log.debug("Maximum for attribute {} is {}.", attribute, max);

        log.debug("Setting up classes.");
        classes.clear();
        double intervalSize = (max - min) / (double) this.getNumCategories();
        for (int i = 0; i < getNumCategories(); i++) {
            double t0 = min + i * intervalSize;
            double t1 = min + (i + 1) * intervalSize;
            NumberInterval m = new NumberInterval(t0, t1);
            classes.add(m);
        }
        log.debug("Preprocessing finished.");
    }

    /**
     * @return the numCategories
     */
    public int getNumCategories() {
        return numClasses;
    }

    /**
     * @param numCategories the numCategories to set
     */
    public void setNumCategories(int numCategories) {
        if (numCategories <= 0) {
            throw new IllegalArgumentException("No valid argument. "
                    + "'numCategories' has to be greater 0.");
        }
        this.numClasses = numCategories;
    }

    /**
     * @return the startColor
     */
    public Color getStartColor() {
        return startColor;
    }

    /**
     * @param startColor the startColor to set
     */
    public void setStartColor(Color startColor) {
        if (startColor == null) {
            throw new IllegalArgumentException("Color parameter is null.");
        }
        this.startColor = startColor;
    }

    /**
     * @return the endColor
     */
    public Color getEndColor() {
        return endColor;
    }

    /**
     * @param endColor the endColor to set
     */
    public void setEndColor(Color endColor) {
        if (endColor == null) {
            throw new IllegalArgumentException("Color parameter is null.");
        }
        this.endColor = endColor;
    }
}
