/*
 * ColorUtils.java 
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.color;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ColorUtils {

    /**
     * Returns an color array of size {@code numColors}. {@link Color#GREEN} is
     * the first color and {@link Color#RED} the last color in the array.
     *
     * @param numColors the number of colors to return
     * @return the list of colors
     */
    public static List<Color> createRedGreenColorGradientAttributes(int numColors) {
        // Color interpolation from Red (0° degree) to Green (120°)
        List<Color> colors = new ArrayList<>();
        float green = 120.0f;
        float upperLimit = green;
        for (int i = 0; i < numColors; i++) {
            float hue = upperLimit * (1 - (i / (float) (numColors - 1)));
            colors.add(ColorUtils.HSVtoRGB(hue, 1.0f, 1.0f));
        }
        return colors;
    }

    /**
     * Returns a {@link List} of {@code numColors} HSV {@link Color} values. The
     * first color is {@code c1}. Interpolates between the the colors {@code c1}
     * and {@code c2}.
     *
     * @param c1 the start {@link Color}
     * @param c2 the end {@link Color}
     * @param numColors the number of colors to create.
     * @return the {@link List} of {@link Color} values to return
     */
    public static List<Color> createLinearHSVColorGradient(Color c1, Color c2, int numColors) {
        List<Color> colors = new ArrayList<>();
        double[] hsvColor1 = RGBtoHSV(c1.getRed() / 255.0, c1.getGreen() / 255.0, c1.getBlue() / 255.0);
        double[] hsvColor2 = RGBtoHSV(c2.getRed() / 255.0, c2.getGreen() / 255.0, c2.getBlue() / 255.0);

        for (int i = 0; i < numColors; i++) {
            double hue = hsvColor1[0] + (hsvColor2[0] - hsvColor1[0]) / (double) (numColors - 1) * i;
            double s = hsvColor1[1] + (hsvColor2[1] - hsvColor1[1]) / (double) (numColors - 1) * i;
            double v = hsvColor1[2] + (hsvColor2[2] - hsvColor1[2]) / (double) (numColors - 1) * i;
            colors.add(ColorUtils.HSVtoRGB((float) hue, (float) s, (float) v));
        }

        return colors;
    }

    /**
     * Returns a {@link List} of {@code numColors} RGB {@link Color} values. The
     * first color is {@code c1}. Interpolates between the the colors {@code c1}
     * and {@code c2}.
     *
     * @param c1 the start {@link Color}
     * @param c2 the end {@link Color}
     * @param numColors the number of colors to create.
     * @return the {@link List} of {@link Color} values to return
     */
    public static Color[] createLinearRGBColorGradient(Color c1, Color c2, int numColors) {

        Color[] colors = new Color[numColors];
        for (int i = 0; i < colors.length; i++) {
            float ratio = (float) i / (float) colors.length;
            int red = (int) (c2.getRed() * ratio + c1.getRed() * (1 - ratio));
            int green = (int) (c2.getGreen() * ratio
                    + c1.getGreen() * (1 - ratio));
            int blue = (int) (c2.getBlue() * ratio
                    + c1.getBlue() * (1 - ratio));
            colors[i] = new Color(red, green, blue);
        }
        return colors;
    }

    /**
     * Calculates the HSV color value for a given RGB value. The return values
     * are h = [0,360], s = [0,1], v = [0,1]. If s == 0, then h = -1 (undefined)
     *
     * @param r value is from 0 to 1
     * @param g value is from 0 to 1
     * @param b value is from 0 to 1
     * @return the calculated hsv value to return.
     */
    public static double[] RGBtoHSV(double r, double g, double b) {

        double h, s, v;

        double min, max, delta;

        min = Math.min(Math.min(r, g), b);
        max = Math.max(Math.max(r, g), b);

        // V
        v = max;

        delta = max - min;

        // S
        if (max != 0) {
            s = delta / max;
        } else {
            s = 0;
            h = -1;
            return new double[]{h, s, v};
        }

        // H
        if ((r - max) < .0000001) {
            h = (g - b) / delta; // between yellow & magenta
        } else if (g == max) {
            h = 2 + (b - r) / delta; // between cyan & yellow
        } else {
            h = 4 + (r - g) / delta; // between magenta & cyan
        }
        h *= 60;    // degrees

        if (h < 0) {
            h += 360;
        }

        return new double[]{h, s, v};
    }

    /**
     * r,g,b values are from 0 to 1 h = [0,360], s = [0,1], v = [0,1] if s == 0,
     * then h = -1 (undefined)
     *
     * @param h the hue value
     * @param s the saturation value
     * @param v the value
     * @return the RGB {@link Color} to return
     */
    public static Color HSVtoRGB(float h, float s, float v) {
        int i;
        float f, p, q, t;
        float r, g, b;
        if (s == 0) {
            // achromatic (grey)
            r = g = b = v;
            return new Color(r, g, b);
        }

        h /= 60;			// sector 0 to 5
        i = (int) Math.floor(h);
        f = h - i;			// factorial part of h
        p = v * (1 - s);
        q = v * (1 - s * f);
        t = v * (1 - s * (1 - f));

        switch (i) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            default:		// case 5:
                r = v;
                g = p;
                b = q;
                break;
        }
        return new Color(r, g, b);
    }
}
