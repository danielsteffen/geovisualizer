/*
 * Legend.java
 *
 * Created by DFKI AV on 01.04.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.render;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ScreenImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class Legend implements Renderable {

    /*
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Legend.class);
    /**
     * The default font to use.
     */
    protected static final Font DEFAULT_FONT = Font.decode("Arial-PLAIN-12");
    /**
     * The default font for the legend title.
     */
    protected static final Font DEFAULT_TITLE_FONT = Font.decode("Arial-PLAIN-14");
    /**
     * The default color for the font.
     */
    protected static final Color DEFAULT_COLOR = Color.WHITE;
    /**
     * The default width of the color rectangle.
     */
    protected static final int DEFAULT_WIDTH = 25;
    /**
     * The default height of the color rectangle.
     */
    protected static final int DEFAULT_HEIGHT = 25;
    /**
     * The distance between the color rectangles.
     */
    protected static final int DEFAULT_SEPERATOR = 2;
    /**
     * The {@link ScreenImage} used.
     */
    private ScreenImage screenImage;
    /**
     * The labels for the color rectangles.
     */
    private Iterable<? extends Renderable> labels;

    /**
     * The default constructor.
     */
    protected Legend() {
    }

    /**
     * Returns the opacity of the {@link #screenImage}.
     *
     * @return the opacity to return.
     */
    public double getOpacity() {
        return this.screenImage.getOpacity();
    }

    /**
     * Set the opacity of the {@link #screenImage}.
     *
     * @param opacity the opacity to set.
     * @throws IllegalArgumentException if
     * {@code opacity < 0} or {@code opacity > 1}
     */
    public void setOpacity(double opacity) {
        if (opacity < 0d || opacity > 1d) {
            String msg = "opacity out of range";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.screenImage.setOpacity(opacity);
    }

    /**
     * Return the screen location of the {@link Legend}.
     *
     * @param dc the {@link DrawContext}.
     * @return the screen location as {@link Point} to return.
     */
    protected Point getScreenLocation(DrawContext dc) {
        return this.screenImage.getScreenLocation(dc);
    }

    /**
     * Sets the {@link Legend} to the screen location {@link Point}.
     *
     * @param point the point to set the screen location to.
     * @throws IllegalArgumentException if {@code point == null}.
     */
    protected void setScreenLocation(Point point) {
        if (point == null) {
            String msg = "point == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.screenImage.setScreenLocation(point);
    }

    /**
     * Returns the width of the {@link #screenImage}.
     *
     * @param dc the {@link DrawContext}.
     * @return the width to return.
     * @throws IllegalArgumentException if {@code dc == null}.
     */
    public int getWidth(DrawContext dc) {
        if (dc == null) {
            String msg = "drawcontext == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return this.screenImage.getImageWidth(dc);
    }

    /**
     * Returns the height of the {@link #screenImage}.
     *
     * @param dc the {@link DrawContext}
     * @return the height to return.
     * @throws IllegalArgumentException if {@code dc == null}
     */
    public int getHeight(DrawContext dc) {
        if (dc == null) {
            String msg = "drawcontext == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return this.screenImage.getImageHeight(dc);
    }

    @Override
    public void render(DrawContext dc) {
        if (dc == null) {
            String msg = "drawcontext == null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        int w = getWidth(dc);
        int h = getHeight(dc);
        this.setScreenLocation(new Point(w / 2 + 10, h / 2 + 40));
        this.screenImage.render(dc);

        if (!dc.isPickingMode() && this.labels != null) {
            for (Renderable renderable : this.labels) {
                if (renderable != null) {
                    renderable.render(dc);
                }
            }
        }
    }

    /**
     * Returns a {@link BufferedImage} for the {@link List} of {@code colors}.
     *
     * @param colors the {@link List} of colors for the legend.
     * @return a {@link BufferedImage} with all colors to return.
     */
    private BufferedImage createLegendImage(final java.util.List<? extends Color> colors) {

        int totalh = DEFAULT_HEIGHT * colors.size() + DEFAULT_SEPERATOR * (colors.size() - 1);
        BufferedImage image = new BufferedImage(DEFAULT_WIDTH, totalh, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = image.createGraphics();

        try {
            for (int i = 0, y = 0; i < colors.size(); i++, y += DEFAULT_HEIGHT + DEFAULT_SEPERATOR) {
                g2d.setColor(colors.get(i));
                g2d.fillRect(0, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            }
        } finally {
            g2d.dispose();
        }

        return image;
    }

    /**
     * Create the labels for the colors.
     *
     * @param labelAttr the {@link LabelAttributes} for the color labels
     * @param titleAttr the {@link LabelAttributes} for the legend title.
     * @return the labels as {@link Renderable} items.
     */
    private Iterable<? extends Renderable> createLegendLabels(List<? extends LabelAttributes> labelAttr, LabelAttributes titleAttr) {
        ArrayList<Renderable> list = new ArrayList<>();
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        int seperator = DEFAULT_SEPERATOR;
        double y = height / 2.0;

        if (labelAttr != null) {

            for (LabelAttributes attr : labelAttr) {
                if (attr == null) {
                    continue;
                }
                LabelRenderable label = new LabelRenderable(this, attr, width, y, AVKey.LEFT, AVKey.CENTER);
                list.add(label);
                y = y + height + seperator;
            }
        }

        LabelRenderable titleLabel = new LabelRenderable(this, titleAttr, 0, -height / 2.0, AVKey.LEFT, AVKey.BOTTOM);
        list.add(titleLabel);

        return list;
    }

    /**
     * Returns a new instance of a {@link Legend}.
     *
     * @param colors the {@link List} of {@link Color} elements.
     * @param labels the {@link List} of labels for the {@link Legend}.
     * @return the {@link Legend} to return.
     */
    public static Legend newInstance(java.util.List<Color> colors, java.util.List labels) {
        return Legend.newInstance("Legend", colors, labels);
    }

    /**
     * Returns a new instance of a {@link Legend}.
     *
     * @param title the title of the {@link Legend}.
     * @param colors the {@link List} of {@link Color} elements.
     * @param labels the {@link List} of labels for the {@link Legend}.
     * @return the {@link Legend} to return.
     */
    public static Legend newInstance(String title, java.util.List<Color> colors, java.util.List labels) {

        Legend legend = new Legend();
        BufferedImage image = legend.createLegendImage(colors);
        legend.screenImage = new ScreenImage();
        legend.screenImage.setImageSource(image);
        legend.setOpacity(0.9);

        ArrayList<LabelAttributes> listAttributes = new ArrayList<>();
        for (Object o : labels) {
            LabelAttributes labelAttr = new LabelAttributes(o.toString(), Legend.DEFAULT_FONT, Legend.DEFAULT_COLOR, 5d, 0);
            listAttributes.add(labelAttr);
        }

        LabelAttributes titleAttr = new LabelAttributes(title, Legend.DEFAULT_TITLE_FONT, Legend.DEFAULT_COLOR, 5d, 0);
        legend.labels = legend.createLegendLabels(listAttributes, titleAttr);

        return legend;
    }
}
