/*
 * OrderedLabel.java
 *
 * Created by DFKI AV on 01.04.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.render;

import com.jogamp.opengl.util.awt.TextRenderer;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.OrderedRenderable;
import gov.nasa.worldwind.util.OGLTextRenderer;
import gov.nasa.worldwind.util.WWUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class OrderedLabel implements OrderedRenderable {

    protected final Legend legend;
    protected final LabelAttributes attr;
    protected final double x;
    protected final double y;
    protected final String halign;
    protected final String valign;

    public OrderedLabel(Legend legend, LabelAttributes attr, double x, double y,
            String halign, String valign) {
        this.legend = legend;
        this.attr = attr;
        this.x = x;
        this.y = y;
        this.halign = halign;
        this.valign = valign;
    }

    @Override
    public double getDistanceFromEye() {
        return 0;
    }

    @Override
    public void render(DrawContext dc) {
        drawLabel(dc, this.attr, this.x, this.y, this.halign, this.valign);
    }

    /**
     *
     * @param dc
     * @param attr
     * @param x
     * @param y
     * @param halign
     * @param valign
     */
    protected void drawLabel(DrawContext dc, LabelAttributes attr, double x, double y, String halign, String valign) {
        String text = attr.getText();
        if (WWUtil.isEmpty(text)) {
            return;
        }

        Font font = attr.getFont();
        if (font == null) {
            font = Legend.DEFAULT_FONT;
        }

        Color color = Legend.DEFAULT_COLOR;
        if (attr.getColor() != null) {
            color = attr.getColor();
        }

        Point location = legend.getScreenLocation(dc);
        if (location != null) {
            x += location.getX() - (double) legend.getWidth(dc) / 2.d;
            y += location.getY() - (double) legend.getHeight(dc) / 2.d;
        }

        Point2D offset = attr.getOffset();
        if (offset != null) {
            x += offset.getX();
            y += offset.getY();
        }

        TextRenderer tr = OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(), font);
        if (tr == null) {
            return;
        }

        Rectangle2D bounds = tr.getBounds(text);
        if (bounds != null) {
            if (AVKey.CENTER.equals(halign)) {
                x += -(bounds.getWidth() / 2d);
            }
            if (AVKey.RIGHT.equals(halign)) {
                x += -bounds.getWidth();
            }

            if (AVKey.CENTER.equals(valign)) {
                y += (bounds.getHeight() + bounds.getY());
            }
            if (AVKey.TOP.equals(valign)) {
                y += bounds.getHeight();
            }
        }

        Rectangle viewport = dc.getView().getViewport();
        tr.beginRendering(viewport.width, viewport.height);
        try {
            double yInGLCoords = viewport.height - y - 1;

            // Draw the text outline, in a contrasting color.
            tr.setColor(WWUtil.computeContrastingColor(color));
            tr.draw(text, (int) x - 1, (int) yInGLCoords - 1);
            tr.draw(text, (int) x + 1, (int) yInGLCoords - 1);
            tr.draw(text, (int) x + 1, (int) yInGLCoords + 1);
            tr.draw(text, (int) x - 1, (int) yInGLCoords + 1);

            // Draw the text over its outline, in the specified color.
            tr.setColor(color);
            tr.draw(text, (int) x, (int) yInGLCoords);
        } finally {
            tr.endRendering();
        }
    }

    @Override
    public void pick(DrawContext dc, Point pickPoint) {
    }
}
