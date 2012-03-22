/*
 *  BufferedImageGenerator.java 
 *
 *  Created by DFKI AV on 01.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.rasterize.utils;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class BufferedImageGenerator {

    /**
     * Logger
     */
    static Logger log = LoggerFactory.getLogger(BufferedImageGenerator.class);

    /**
     *
     * @param pixels
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage argbToImage(int[] pixels, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 0, width, height, pixels, 0, width);
        AffineTransform tx = new AffineTransform();
        tx.rotate((Math.PI / 2) * 3, img.getWidth() / 2, img.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        img = op.filter(img, null);
        return img;
    }

    /**
     *
     * @param data
     * @param w
     * @param h
     * @return
     */
    public static BufferedImage argbToImage(int[][] data, int w, int h) {
        int[] d = new int[w * h];
        int z = 0;
        int j = -1;
        for (int i = 0; i < d.length; i++) {
            if (j < data[z].length - 1) {
                j++;
            } else if (z < data.length - 1) {
                j = 0;
                z++;
            }
            d[i] = data[z][j];
        }
        return argbToImage(d, w, h);
    }
}
