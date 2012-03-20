/*
 *  RedGreenColorrampClassification.java 
 *
 *  Created by DFKI AV on 29.02.2012.
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
public class RedGreenColorrampClassification extends ColorClassification {

    /**
     * The number {@link IClass} objects to create for this classification.
     */
    private int numClasses;
    /**
     * The classification algorithm used for this transferfunction.
     */
    private IClassification classification;

    /**
     * Creates an object of {@link RedGreenColorrampClassification}.
     */
    public RedGreenColorrampClassification() {
        super();

        addClassification(new NumberInterval(), Color.RED);
        this.numClasses = colorList.size();

        this.classification = ClassificationFactory.get(EqualIntervals.class.getSimpleName());
    }

    @Override
    public String getName() {
        return "Red-Green Color ramp (uniformly distributed; 5 classes)";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
        log.debug("Preprocessing ...");
        clear();
        log.debug("Setting up colors.");
        List<Color> colors = ColorUtils.CreateRedGreenColorGradientAttributes(numClasses);

        log.debug("Setting up classes.");
        List<IClass> classes = classification.classify(data, attribute, getNumClasses());

        log.debug("Setting up classification map.");
        for (int i = 0; i < getNumClasses(); i++) {
            IClass m = classes.get(i);
            Color c = colors.get(i);
            addClassification(m, c);
        }
        log.debug("Preprocessing finished.");
    }

    /**
     * Returns the number of used {@link IClass}es.
     *
     * @return the number of {@link IClass}es to return;
     */
    public int getNumClasses() {
        return numClasses;
    }

    /**
     * Sets the number of {@link IClass} objects to create.
     *
     * @param num the number of classes to set.
     * @throws a {@link IllegalArgumentException} if {@code num < 0}.
     */
    public void setNumClasses(int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("No valid argument. "
                    + "'numCategories' has to be greater 0.");
        }
        this.numClasses = num;
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
