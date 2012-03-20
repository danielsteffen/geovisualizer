  /*
 *  ColorrampClassification.java 
 *
 *  Created by DFKI AV on 09.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.vis.core.ClassificationFactory;
import com.dfki.av.sudplan.vis.core.IClass;
import com.dfki.av.sudplan.vis.core.IClassification;
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
     * The classification algorithm used for this transferfunction.
     */
    private IClassification classification;

    /**
     * Constructor for this {@link ColorrampClassification}.
     */
    public ColorrampClassification() {
        super();

        addClassification(new NumberInterval(), Color.GRAY);
        this.numClasses = colorList.size();
        this.startColor = Color.GREEN;
        this.endColor = Color.RED;

        this.classification = ClassificationFactory.get(EqualIntervals.class.getSimpleName());
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
        List<Color> colors = ColorUtils.CreateLinearHSVColorGradient(getStartColor(), getEndColor(), getNumClasses());

        log.debug("Setting up classes.");
        List<IClass> classes = getClassification().classify(data, attribute, getNumClasses());

        log.debug("Setting up classification map.");
        for (int i = 0; i < getNumClasses(); i++) {
            IClass m = classes.get(i);
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
     * Returns the {@link #startColor}.
     *
     * @return the startColor to return.
     */
    public Color getStartColor() {
        return startColor;
    }

    /**
     * Returns the {@link #startColor} for the transfer function.
     *
     * @param startColor the {@link Color} to set.
     * @throws IllegalArgumentException if {@code startColor} is {@code null}.
     */
    public void setStartColor(Color startColor) {
        if (startColor == null) {
            throw new IllegalArgumentException("Color parameter is null.");
        }
        this.startColor = startColor;
    }

    /**
     * Returns the {@link #endColor}.
     *
     * @return the endColor to return.
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

    /**
     * Returns the {@link IClassification} algorithm for this transfer function.
     *
     * @return the {@link IClassification} to return.
     */
    public IClassification getClassification() {
        return classification;
    }

    /**
     * Sets the {@link #classification} for this transfer function.
     *
     * @param classification the {@link IClassification} to set.
     * @throws IllegalArgumentException if {@code classification} is {@code null}.
     */
    public void setClassification(IClassification classification) {
        if (classification == null) {
            throw new IllegalArgumentException("Classification can not be set to null.");
        }
        this.classification = classification;
    }
}
