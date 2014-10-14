/*
 * ColorPalette.java
 *
 * Created by DFKI AV on 11.02.2013.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.color;

import de.dfki.av.geovisualizer.core.classification.utils.ImageIconUtils;
import de.dfki.av.geovisualizer.core.color.brewer.BrewerPalette;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Color palette to be defined by the user or any other util.
 *
 * @author Daniel Steffen <Daniel.Steffen@dfki.de>
 */
public class ColorPalette implements IColorPalette {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ColorPalette.class);
    /**
     * The unique name of the color palette.
     */
    private String name;
    /**
     * The colors managed by this palette.
     */
    private ArrayList<Color> colors;
    private ImageIcon icon;

    /**
     *
     * @param name
     * @param numColors
     */
    public ColorPalette(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name == null");
        }
        this.name = name;
        this.colors = new ArrayList<>();
        this.icon = null;
    }

    /**
     * Add a {@link Color} to the palette.
     *
     * @param color the {@link Color} to add.
     * @throws IllegalArgumentException if color == null
     */
    public void addColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        colors.add(color);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getMinNumColors() {
        return colors.size();
    }

    @Override
    public int getMaxNumColors() {
        return colors.size();
    }

    @Override
    public List<Color> getColors() {
        return Collections.unmodifiableList(colors);
    }

    @Override
    public List<Color> getColors(int numColors) {
        if (numColors != colors.size()) {
            throw new IllegalArgumentException("No valid numColors");
        }
        return getColors();
    }

    @Override
    public String toString() {
        return name + " [" + colors.size() + "] " + colors;
    }

    @Override
    public void initIcon() {
        int colorCount;
        BufferedImage image;
        if (getMaxNumColors() >= 100) {
            colorCount = 100;
        } else {
            colorCount = getMaxNumColors();
        }
        List<Color> cList = getColors(colorCount);
        image = new BufferedImage(cList.size(), 1, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < cList.size(); i++) {
            try {
                image.setRGB(i, 0, cList.get(i).getRGB());
            } catch (ArrayIndexOutOfBoundsException ex) {
                LOG.error("" + ex);
            }
        }
        Image img = ImageIconUtils.getScaledImage(image, 120, 18);
        icon = new ImageIcon(img);
    }

    @Override
    public ImageIcon getIcon() {
        return icon;
    }
}