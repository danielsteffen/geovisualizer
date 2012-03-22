/*
 *  Rasterize.java 
 *
 *  Created by DFKI AV on 01.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.rasterize.utils;

import com.dfki.av.sudplan.vis.core.ITransferFunction;
import com.dfki.av.sudplan.vis.io.shapefile.Shapefile;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.gdal.ogr.Feature;
import org.gdal.ogr.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class Rasterizer {

    /**
     * Logger
     */
    static Logger log = LoggerFactory.getLogger(Rasterizer.class);
    /**
     * Default color value
     */
    private static final Color transparent = new Color(255, 255, 255, 0);
    private static final int transparentInt = transparent.getRGB();
    private static int DEFAULT_VALUE = transparentInt;
    /**
     * Feature count
     */
    private static int FEATURE_COUNT;

    /**
     *
     * @param feature
     * @param extend
     * @param w
     * @param h
     * @return
     */
    private static int[] raster(Geometry geometry, double[] extend, int w, int h) {

        double xMin = extend[0];
        double yMin = extend[2];
        double xMax = extend[1];
        double yMax = extend[3];
        int[] coord = new int[2];

        if (geometry.IsEmpty()) {
            log.error("Empty geometry");
            return null;
        }

        double x = geometry.GetX();
        double y = geometry.GetY();

        //Translation
        x = x - xMin;
        y = y - yMin;
        //Normalisation
        x = x / (xMax - xMin);
        y = y / (yMax - yMin);
        if (x > 1) {
            x = 1;
        }
        if (x < 0) {
            x = 0;
        }
        if (y > 1) {
            y = 1;
        }
        if (y < 0) {
            y = 0;
        }
        //Upscaling
        x = x * (w - 1);
        y = y * (h - 1);
        //Convert to Integer
        coord[0] = (int) Math.round(x);
        coord[1] = (int) Math.round(y);

        return coord;
    }

    /**
     *
     * @param shapefile
     * @param w
     * @param h
     * @return
     */
    private static List<int[]> raster(Shapefile shapefile, int w, int h) {
        List<int[]> list = new ArrayList<int[]>();
        Geometry geometry;
        for (int i = 0; i < shapefile.getFeatureCount(); i++) {
            geometry = shapefile.getGeometry(i);
            if (geometry.IsEmpty()) {
                log.warn("Replace empty geometry for ID {}", shapefile.getFeature(i).GetFID());
                int[] coord = new int[2];
                coord[0] = 0;
                coord[1] = 0;
                list.add(coord);
            } else {
                list.add(raster(geometry, shapefile.getExtent(), w, h));
            }
        }
        return list;
    }

    /**
     *
     * @param data
     * @param w
     * @param h
     * @param list
     * @param upscaleFactor
     * @param defaultValue
     * @return
     */
    private static int[][] fill(int[][] data, int w, int h, List<int[]> list, int defaultValue) {
        int x;
        int y;
        int value;
        int upscaleFactor = (int) Math.round((w * h) / list.size());
        upscaleFactor = upscaleFactor / 4;
        upscaleFactor++;
        //Fill empty spaces
        for (int i = 1; i < upscaleFactor; i++) {
            for (int j = 0; j < list.size(); j++) {
                x = list.get(j)[0];
                y = list.get(j)[1];
                value = data[x][y];
                for (int k = 0; k < i; k++) {
                    for (int l = 0; l < i; l++) {
                        if (i > k && i > l) {
                            if (x > k && y > l - 1) {
                                if (data[x - k][y - l] == defaultValue) {
                                    data[x - k][y - l] = value;
                                }
                            }
                            if (x > l - 1 && y > k - 1) {
                                if (data[x - l][y - k] == defaultValue) {
                                    data[x - l][y - k] = value;
                                }
                            }
                            if (x < w - k && y < h - l) {
                                if (data[x + k][y + l] == defaultValue) {
                                    data[x + k][y + l] = value;
                                }
                            }
                            if (x < w - l && y < h - k) {
                                if (data[x + l][y + k] == defaultValue) {
                                    data[x + l][y + k] = value;
                                }
                            }
                            if (x < h - k && y > l - 1) {
                                if (data[x + k][y - l] == defaultValue) {
                                    data[x + k][y - l] = value;
                                }
                            }
                            if (x < h - l && y > k - 1) {
                                if (data[x + l][y - k] == defaultValue) {
                                    data[x + l][y - k] = value;
                                }
                            }
                            if (x > k - 1 && y < w - l) {
                                if (data[x - k][y + l] == defaultValue) {
                                    data[x - k][y + l] = value;
                                }
                            }
                            if (x > i - 1 && y < w - k) {
                                if (data[x - i][y + k] == defaultValue) {
                                    data[x - i][y + k] = value;
                                }
                            }
                        }
                    }
                }
            }
        }
        return data;
    }

    /**
     *
     * @param shapefile
     * @param attribute
     * @param function
     * @param w
     * @param h
     * @return
     */
    public static int[][] raster(Shapefile shapefile, String attribute, ITransferFunction function, int w, int h) {
        FEATURE_COUNT = shapefile.getFeatureCount();
        int[][] data = new int[w][h];
        List<int[]> list = raster(shapefile, w, h);


        //Fill raster with transparent Color
        int col = 0;
        for (int row = 0; row < data.length; row++) {
            while (col < data[row].length) {
                data[row][col] = DEFAULT_VALUE;
                col++;
            }
            col = 0;
        }

        //Fill raster with colors
        Color color;
        if (!shapefile.getAttributes().containsKey(attribute)) {
            String value = shapefile.getAttributes().keySet().iterator().next();
            log.warn("Invalid attribute ({}) parameter for this shapfile. Setting attribute to {}", attribute, value);
            attribute = value;
        }
        for (int i = 0; i < FEATURE_COUNT; i++) {
            Feature feature = shapefile.getFeature(i);
            color = (Color) function.calc(feature.GetFieldAsDouble(attribute));
            data[list.get(i)[0]][list.get(i)[1]] = color.getRGB();
        }


        //Return raster
        return fill(data, w, h, list, DEFAULT_VALUE);
    }
}
