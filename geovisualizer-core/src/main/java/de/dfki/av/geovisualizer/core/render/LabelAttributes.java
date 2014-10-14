/*
 * LabelAttributes.java
 *
 * Created by DFKI AV on 01.04.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class LabelAttributes {

    private final String text;
    private final Font font;
    private final Color color;
    private final double xOffset;
    private final double yOffset;

    public LabelAttributes(String s, Font f, Color c, double x, double y) {
        this.text = s;
        this.font = f;
        this.color = c;
        this.xOffset = x;
        this.yOffset = y;
    }

    public String getText() {
        return this.text;
    }

    public Font getFont() {
        return this.font;
    }

    public Color getColor() {
        return this.color;
    }

    public Point2D getOffset() {
        return new Point2D.Double(xOffset, yOffset);
    }
}
