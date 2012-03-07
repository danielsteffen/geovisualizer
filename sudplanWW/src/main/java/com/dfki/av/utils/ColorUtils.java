/*
 *  ColorUtils.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.utils;

import java.awt.Color;

/**
 *
 * @author steffen
 */
public class ColorUtils {

    /**
     * Returns an color array of size {@link #numColors}. {@link Color#GREEN} is
     * the first color and {@link Color#RED} the last color in the array.
     *
     * @param numColors the number of colors to return
     * @return the array of colors
     */
    public static Color[] CreateRedGreenColorGradientAttributes(int numColors) {
        // Color interpolation from Red (0° degree) to Green (120°)
        Color[] colors = new Color[numColors];
        float green = 120.0f;
        float upperLimit = green;
        for (int i = 0; i < colors.length; i++) {
            float hue = upperLimit * (1 - (i / (float) (numColors - 1)));
            colors[i] = ColorUtils.HSVtoRGB(hue, 1.0f, 1.0f);
        }
        return colors;
    }

    public static Color[] CreateLinearColorGradient(Color c1, Color c2, int numColors) {

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
     * r,g,b values are from 0 to 1 h = [0,360], s = [0,1], v = [0,1] if s == 0,
     * then h = -1 (undefined)
     *
     * @param h
     * @param s
     * @param v
     * @return
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
