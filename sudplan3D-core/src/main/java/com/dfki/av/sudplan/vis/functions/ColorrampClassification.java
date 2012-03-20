/*
 *  ColorrampClassification.java 
 *
 *  Created by DFKI AV on 09.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.vis.core.IClass;
import com.dfki.av.sudplan.vis.core.ISource;
import com.dfki.av.sudplan.vis.utils.ColorUtils;
import java.awt.Color;
import java.util.List;

/**
 *
 * @author steffen
 */
public class ColorrampClassification extends ColorClassification {

    /**
     * The number of {@link IClass}es to be used by this transfer function.
     */
    private int numClasses;
    /**
     * The {@link Color} to be mapped to the first {@link IClass}.
     */
    private Color startColor;
    /**
     * The {@link Color} to be mapped to the last {@link IClass}.
     */
    private Color endColor;

    /**
     * Constructor for this {@link ColorrampClassification}.
     */
    public ColorrampClassification() {
        super();

        addClassification(new NumberInterval(), Color.GRAY);
        this.numClasses = colorList.size();
        this.startColor = Color.GREEN;
        this.endColor = Color.RED;
    }

    @Override
    public String getName() {
        return "Color ramp";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
        log.debug("Preprocessing ...");
        clear();

        log.debug("Setting up colors.");
        List<Color> colors = ColorUtils.CreateLinearHSVColorGradient(startColor, endColor, numClasses);

        log.debug("Setting up classes.");
        double min = data.min(attribute);
        double max = data.max(attribute);
        double intervalSize = (max - min) / (double) this.getNumClasses();

        for (int i = 0; i < getNumClasses(); i++) {
            double t0 = min + i * intervalSize;
            double t1 = min + (i + 1) * intervalSize;
            NumberInterval m = new NumberInterval(t0, t1);
            Color c = colors.get(i);
            addClassification(m, c);
        }
        log.debug("Preprocessing finished.");
    }

    /**
     * @return The number of classes to return.
     */
    public int getNumClasses() {
        return numClasses;
    }

    /**
     * @param num The number of classes to set.
     */
    public void setNumClasses(int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("No valid argument. "
                    + "'numCategories' has to be greater 0.");
        }
        this.numClasses = num;
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
