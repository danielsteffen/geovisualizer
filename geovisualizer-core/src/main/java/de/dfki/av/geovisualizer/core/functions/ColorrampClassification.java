/*
 * ColorrampClassification.java
 *
 * Created by DFKI AV on 09.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.IClass;
import de.dfki.av.geovisualizer.core.IClassification;
import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.classification.EqualIntervals;
import de.dfki.av.geovisualizer.core.color.ColorUtils;
import de.dfki.av.geovisualizer.core.functions.ui.TFPColorrampClassification;
import de.dfki.av.geovisualizer.core.spi.ClassificationFactory;
import java.awt.Color;
import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ColorrampClassification extends ColorClassification {

    /**
     * The UI for the transfer function.
     */
    private AbstractTransferFunctionPanel panel;
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
     * The classification algorithm used for this transfer function.
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

        this.classification = ClassificationFactory.newInstance(EqualIntervals.class.getName());
    }

    @Override
    public String getName() {
        return "Color ramp classification";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
        log.debug("Preprocessing ...");
        clear();

        log.debug("Setting up colors.");
        List<Color> colors = ColorUtils.createLinearHSVColorGradient(
                getStartColor(), 
                getEndColor(), 
                getNumClasses());

        log.debug("Setting up {} classes.", getNumClasses());
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
     * The number of {@link IClass} elements to produce.
     *
     * @param num The number of classes to set.
     * @throws IllegalArgumentException if {@code num<1}.
     */
    public void setNumClasses(int num) {
        if (num <= 0) {
            String msg = "num <= 0";
            log.error(msg);
            throw new IllegalArgumentException(msg);
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
            String msg = "startColor == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
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
     * The end {@link Color} for the color gradient.
     *
     * @param endColor the endColor to set
     * @throws IllegalArgumentException if {@code endColor} is {@code null}.
     */
    public void setEndColor(Color endColor) {
        if (endColor == null) {
            String msg = "endColor == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
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
            String msg = "classification == null";
            log.warn(msg);
            throw new IllegalArgumentException(msg);
        }
        this.classification = classification;
    }

    @Override
    public AbstractTransferFunctionPanel getPanel() {
        if (this.panel == null) {
            this.panel = new TFPColorrampClassification(this);
        }
        return this.panel;
    }
}
