/*
 * RedGreenPalette.java
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
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class RedGreenPalette implements IColorPalette {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(RedGreenPalette.class);
    /**
     * The name of the color palette.
     */
    private static final String NAME = "reg-green-gradient";
    /**
     * Colors for a red-green color palette.
     */
    private List<Color> colors;
    private ImageIcon icon;

    /**
     * Create an instance of the color palette with initial {@code numColors}
     * colors.
     *
     * @param numColors the initial number of colors.
     * @throws IllegalArgumentException if numColors < 2
     */
    public RedGreenPalette() {
        icon = null;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getMinNumColors() {
        return 2;
    }

    @Override
    public int getMaxNumColors() {
        return 100;
    }

    @Override
    public List<Color> getColors() {
        return getColors(getMaxNumColors());
    }

    @Override
    public List<Color> getColors(int numColors) {
        if (numColors < getMinNumColors()) {
            throw new IllegalArgumentException("numColors < 2");
        }
        if (numColors > getMaxNumColors()) {
            throw new IllegalArgumentException("numColors > 100");
        }
        this.colors = RedGreenPalette.calculate(numColors);
        return Collections.unmodifiableList(colors);
    }

    /**
     * Calculate the reg to greed color gradient for {@code numColors} steps.
     *
     * @param numColors the number of {@link Color} elements to calculate.
     * @return
     */
    private static List<Color> calculate(int numColors) {
        ArrayList<Color> colors = new ArrayList<>();
        float green = 120.0f;
        float upperLimit = green;
        for (int i = 0; i < numColors; i++) {
            float hue = upperLimit * (1 - (i / (float) (numColors - 1)));
            colors.add(ColorUtils.HSVtoRGB(hue, 1.0f, 1.0f));
        }
        return colors;
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
