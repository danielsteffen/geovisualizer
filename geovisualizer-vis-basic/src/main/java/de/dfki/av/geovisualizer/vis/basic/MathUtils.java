/*
 * MathUtils.java
 *
 * Created by AG wearHEALTH on 05.05.2015.
 * Copyright (c) 2015 University of Kaiserslautern, Kaiserslautern. 
 * All rights reserved. Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.vis.basic;

import java.util.List;

/**
 *
 * @author Daniel Steffen
 */
public class MathUtils {

    /**
     * Calculates the arithmetic mean of the {@code points}.
     *
     * @param points the list of points.
     * @return the arithmetic mean to calculate.
     */
    public static double[] calcArithmeticMean(List<double[]> points) {

        if (points.size() < 1) {
            String msg = "Size of points < 1.";
            throw new RuntimeException(msg);
        }

        double[] mean = new double[2];
        mean[1] = 0.0;
        mean[0] = 0.0;

        for (int t = 0; t < points.size(); t++) {
            double point[] = points.get(t);
            mean[0] += point[0];
            mean[1] += point[1];
        }
        mean[0] = mean[0] / points.size();
        mean[1] = mean[1] / points.size();

        return mean;
    }

    /**
     * Calculates the geometric mean of the {@code points}.
     *
     * @param points the list of points.
     * @return the arithmetic mean to calculate.
     */
    public static double[] calcGeometricMean(List<double[]> points) {

        if (points.size() < 1) {
            String msg = "Size of points < 1.";
            throw new RuntimeException(msg);
        }

        double[] mean = new double[2];
        mean[1] = 1.0;
        mean[0] = 1.0;

        for (int t = 0; t < points.size(); t++) {
            double point[] = points.get(t);
            double x_t = point[0] + 180;
            double y_t = point[1] + 90;
            mean[0] *= x_t;
            mean[1] *= y_t;
        }

        double root = 1.0 / (double) points.size();
        mean[0] = Math.pow(mean[0], root);
        mean[1] = Math.pow(mean[1], root);

        mean[0] -= 180;
        mean[1] -= 90;

        return mean;
    }

}
