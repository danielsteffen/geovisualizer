/*
 * BrewerPalette.java
 *
 * Created by DFKI AV on 11.02.2013.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.color.brewer;

import de.dfki.av.geovisualizer.core.classification.utils.ImageIconUtils;
import de.dfki.av.geovisualizer.core.color.IColorPalette;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen@dfki.de>
 */
public class BrewerPalette implements IColorPalette {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(BrewerPalette.class);
    /**
     * The static map of already created color palette.
     */
    private HashMap<Integer, List<Color>> paletteMap;
    /**
     * The name of the brewer palette.
     */
    private String name;
    /**
     * Icon for the palette which can be initialized with initIcon().
     */
    private ImageIcon icon;

    /**
     * The constructor of the brewer palette.
     *
     * @param name the name of a palette.
     */
    public BrewerPalette(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name == null or name is empty");
        }
        this.name = name;
        this.paletteMap = new HashMap<>();
    }

    /**
     * Add the color to the category {@code i}.
     *
     * @param color the {@link Color} to add
     * @param i the category to add the {@code color} to.
     * @throws IllegalArgumentException if color == null
     */
    public void addColor(Color color, int i) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }

        if (!paletteMap.containsKey(i)) {
            ArrayList<Color> colors = new ArrayList<>();
            paletteMap.put(i, colors);
        }
        List<Color> colors = paletteMap.get(i);
        colors.add(color);
    }

    @Override
    public String getName() {
        return this.name;
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
        List<Color> colors = getColors(colorCount);
        image = new BufferedImage(colors.size(), 1, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < colors.size(); i++) {
            try {
                image.setRGB(i, 0, colors.get(i).getRGB());
            } catch (ArrayIndexOutOfBoundsException ex) {
                LOG.error("" + ex);
            }
        }
        Image img = ImageIconUtils.getScaledImage(image, 120, 18);
        icon = new ImageIcon(img);
    }

    @Override
    public int getMinNumColors() {
        Set<Integer> keys = paletteMap.keySet();
        Integer min = Collections.min(keys);
        return min.intValue();
    }

    @Override
    public int getMaxNumColors() {
        Set<Integer> keys = paletteMap.keySet();
        Integer max = Collections.max(keys);
        return max.intValue();
    }

    @Override
    public List<Color> getColors() {
        return getColors(getMaxNumColors());
    }

    @Override
    public List<Color> getColors(int numColors) {
        if (!(numColors >= getMinNumColors() && numColors <= getMaxNumColors())) {
            throw new IllegalArgumentException("numColors not valid");
        }
        return Collections.unmodifiableList(paletteMap.get(numColors));
    }

    @Override
    public String toString() {
        return "BrewerPalette{ name=" + name + ", min="
                + getMinNumColors() + ", max=" + getMaxNumColors() + "\npaletteMap=" + paletteMap + "'}'";
    }

    @Override
    public ImageIcon getIcon() {
        return icon;
    }
}
