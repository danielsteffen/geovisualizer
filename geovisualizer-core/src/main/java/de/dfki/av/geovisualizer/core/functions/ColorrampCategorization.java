/*
 * ColorrampCategorization.java
 *
 * Created by DFKI AV on 09.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.color.ColorUtils;
import de.dfki.av.geovisualizer.core.functions.ui.TFPColorrampCategorization;
import de.dfki.av.geovisualizer.core.render.Legend;
import java.awt.Color;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ColorrampCategorization extends ColorCategorization {

    /**
     * The UI for the transfer function.
     */
    private AbstractTransferFunctionPanel panel;
    /**
     * The start {@link Color} for the color gradient.
     */
    private Color startColor;
    /**
     * The end {@link Color} for the color gradient.
     */
    private Color endColor;

    /**
     * Constructor.
     */
    public ColorrampCategorization() {
        super();

        this.startColor = Color.GREEN;
        this.endColor = Color.RED;

        this.categories.add(new StringCategory(""));
        this.colorList = ColorUtils.createLinearHSVColorGradient(startColor, endColor, 1);
    }

    @Override
    public String getName() {
        return "Color ramp categorization";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
        log.debug("Preprocessing ...");
        log.debug("Auto classification of attribute <{}>.", attribute);
        categories.clear();
        for (int i = 0; i < data.getFeatureCount(); i++) {
            Object o = data.getValue(i, attribute);
            if (o instanceof String) {
                if (!categories.contains(new StringCategory((String) o))) {
                    categories.add(new StringCategory((String) o));
                }
            } else if (o instanceof Number) {
                if (!categories.contains(new NumberCategory((Number) o))) {
                    categories.add(new NumberCategory((Number) o));
                }
            }
        }
        log.debug("Generated {} categories.", categories.size());
        log.debug("Categories: {}", categories.toString());
        this.colorList = ColorUtils.createLinearHSVColorGradient(startColor, endColor, categories.size());
        log.debug("Generate legend.");
        setLegend(Legend.newInstance(colorList, categories));
        log.debug("Preprocessing finished.");
    }

    /**
     * Return the start {@link Color}.
     *
     * @return the startColor to return.
     */
    public Color getStartColor() {
        return startColor;
    }

    /**
     * Set the start {@link Color} for the color gradient.
     *
     * @param startColor the startColor to set
     * @throws IllegalArgumentException if {@code startColor} is {@code null}.
     */
    public void setStartColor(Color startColor) {
        if (startColor == null) {
            throw new IllegalArgumentException("Color parameter is null.");
        }
        this.startColor = startColor;
    }

    /**
     * Return the end {@link Color}.
     *
     * @return the endColor to return
     */
    public Color getEndColor() {
        return endColor;
    }

    /**
     * Set the end {@link Color} for the color gradient.
     *
     * @param endColor the endColor to set
     * @throws IllegalArgumentException if {@code endColor} is {@code null}.
     */
    public void setEndColor(Color endColor) {
        if (endColor == null) {
            throw new IllegalArgumentException("Color parameter is null.");
        }
        this.endColor = endColor;
    }

    @Override
    public AbstractTransferFunctionPanel getPanel() {
        if(this.panel == null){
            this.panel = new TFPColorrampCategorization(this);
        }
        return this.panel;
    }
}
