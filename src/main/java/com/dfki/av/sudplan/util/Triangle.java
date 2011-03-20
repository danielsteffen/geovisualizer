/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.util;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:modfied from wwjdk
public class Triangle {

    private final static Logger logger = LoggerFactory.getLogger(Triangle.class);
    // TODO: arg checking
    // TODO: account for degenerate quads
    protected Vector3f a;
    protected Vector3f b;
    protected Vector3f c;
    public Point3f pointA;
    public Point3f pointB;
    public Point3f pointC;
//    protected double c;
//    protected double a;
//    protected double area;
//    protected double h;
    protected Vector3f baryCenter = new Vector3f();
    protected double[] baryCenterCoords;
    protected Vector3f q1 = new Vector3f();
    protected Vector3f q3 = new Vector3f();

//    public Triangle(final Vector3f p00, final Vector3f p10, final Vector3f p01) {
//        this.p00 = p00;
//        this.p10 = p10;
//        this.p01 = p01;
//
//        q1.sub(p10, p00);
//        q3.sub(p01, p00);
//
//    }
//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:duplicated code
//    public Triangle(final Point3f[] triangle,double c,double a,double h) throws IllegalArgumentException {
    public Triangle(final Point3f[] triangle) throws IllegalArgumentException {
        if (triangle == null) {
            throw new IllegalArgumentException("Array must not be null.");
        }
        if (triangle.length < 3) {
            throw new IllegalArgumentException("Array must contain at least 3 points");
        }
        pointA = triangle[0];
        pointB = triangle[1];
        pointC = triangle[2];
        this.a = new Vector3f(pointA);
        this.b = new Vector3f(pointB);
        this.c = new Vector3f(pointC);
        q1.sub(b, a);
        q3.sub(c, a);

        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:not object oriented;
//        baryCenterCoords = getBarycentricCoords(baryCenter);
//        if (logger.isDebugEnabled()) {
//            logger.debug("BaryCenterCoords: " + baryCenterCoords[0] + " " + baryCenterCoords[1] + " " + baryCenterCoords[2]);
//        }
    }

    private static Vector3f getAbs(final Vector3f vector) {
        if (vector == null) {
            return null;
        }
        final Vector3f absVector = new Vector3f(vector);
        absVector.x = Math.abs(absVector.x);
        absVector.y = Math.abs(absVector.y);
        absVector.z = Math.abs(absVector.z);
        return absVector;
    }

//    ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:unperformant --> this does not have to be calculated every time
//    public double[] getBarycentricCoords(Vector3f p) {
//        Vector3f n = new Vector3f();
//        n.cross(q1, q3);
//        final Vector3f na = getAbs(n);
//        Vector3f q2 = new Vector3f();
//        q2.sub(p, a);
//
//        double a, b;
//
//        // Choose equations providing best numerical accuracy
//        if (na.x >= na.y && na.x >= na.z) {
//            a = (q2.y * q3.z - q2.z * q3.y) / n.x;
//            b = (q1.y * q2.z - q1.z * q2.y) / n.y;
//        } else if (na.y >= na.x && na.y >= na.z) {
//            a = (q2.z * q3.x - q2.x * q3.z) / n.y;
//            b = (q1.z * q2.x - q1.x * q2.z) / n.y;
//        } else {
//            a = (q2.x * q3.y - q2.y * q3.x) / n.z;
//            b = (q1.x * q2.y - q1.y * q2.x) / n.z;
//        }
//        return new double[]{1 - a - b, a, b};
//    }
    public double[] getBaryCentricCoords(final Point3f p) {
        final double triangleArea = getArea();
//        if (logger.isDebugEnabled()) {
//            logger.debug("Triangle Area: " + triangleArea);
//        }
        final double alphaArea = getTriangleArea(p, pointB, pointC);
//        if (logger.isDebugEnabled()) {
//            logger.debug("alpha Area: " + alphaArea);
//        }
        final double betaArea = getTriangleArea(pointA, p, pointC);
//        if (logger.isDebugEnabled()) {
//            logger.debug("beta Area: " + betaArea);
//        }
        final double alpha = alphaArea / triangleArea;
        final double beta = betaArea / triangleArea;
        final double gamma = 1 - alpha - beta;
//        if (logger.isDebugEnabled()) {
//            logger.debug("Bary coords: " + alpha + "," + beta + "," + gamma);
//        }
        return new double[]{alpha, beta, gamma};
    }

    public double getInterpolatedHeight(final Point3f p) {
        final double[] baryCoords = getBaryCentricCoords(p);
        return baryCoords[0] * pointA.z + baryCoords[1] * pointB.z + baryCoords[2] * pointC.z;
    }

    public Point3f interpolateHeight(final Point3f p) {
        p.z = (float) getInterpolatedHeight(p);
//        if (logger.isDebugEnabled()) {
//            logger.debug("Interpolated Height: " + p.z);
//        }
        return p;
    }

    public Point3f interpolateHeight(final Point2f p) {
        return interpolateHeight(new Point3f(p.x, p.y, 0));
    }

    public double getArea() {
        return getTriangleArea(pointA, pointB, pointC);
    }

    public static double getTriangleArea(final Point3f pointA, final Point3f pointB, final Point3f pointC) {
        Vector3f vectorA = new Vector3f(pointA.x, pointA.y, 0);
        Vector3f vectorB = new Vector3f(pointB.x, pointB.y, 0);
        Vector3f vectorC = new Vector3f(pointC.x, pointC.y, 0);
//        if (logger.isDebugEnabled()) {
//            logger.debug("point A: " + pointA);
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("point B: " + pointB);
//        }
//
//        if (logger.isDebugEnabled()) {
//            logger.debug("point c: " + pointC);
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("vector A: " + vectorA);
//        }

        Vector3f b = new Vector3f();
        b.sub(vectorB, vectorA);
//        if (logger.isDebugEnabled()) {
//            logger.debug("vector b: " + b);
//        }
        Vector3f c = new Vector3f();
        c.sub(vectorC, vectorA);
//        if (logger.isDebugEnabled()) {
//            logger.debug("vector c: " + c);
//        }
        Vector3f n = new Vector3f();
        n.cross(b, c);
//        if (logger.isDebugEnabled()) {
//            logger.debug("cross: " + n);
//        }
        Vector3f na = getAbs(n);
        return 0.5 * na.length();
    }
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:unperformant --> this does not have to be calculated every time
//    public double[] getBarycentricCoords(Vector3f p) {
//        Vector3f n = new Vector3f();
//        n.cross(q1, q3);
////        final Vector3f na = getAbs(n);
//        Vector3f q2 = new Vector3f();
//        q2.sub(p, a);
//
//        final Vector3f naTemp1 = new Vector3f();
//        final Vector3f naaTemp2 = new Vector3f();
//
//        final Vector3f nbTemp1 = new Vector3f();
//        nbTemp1.sub(a,c);
//        final Vector3f nbTemp2 = new Vector3f();
//        nbTemp1.sub(a,c);
//
//        final Vector3f ncTemp1 = new Vector3f();
//        final Vector3f ncTemp2 = new Vector3f();
//
//        double a, b;
//
////        // Choose equations providing best numerical accuracy
////        if (na.x >= na.y && na.x >= na.z) {
////            a = (q2.y * q3.z - q2.z * q3.y) / n.x;
////            b = (q1.y * q2.z - q1.z * q2.y) / n.y;
////        } else if (na.y >= na.x && na.y >= na.z) {
////            a = (q2.z * q3.x - q2.x * q3.z) / n.y;
////            b = (q1.z * q2.x - q1.x * q2.z) / n.y;
////        } else {
////            a = (q2.x * q3.y - q2.y * q3.x) / n.z;
////            b = (q1.x * q2.y - q1.y * q2.x) / n.z;
////        }
//        return new double[]{1 - a - b, a, b};
//    }
//     public double[] getBarycentricCoords(Vector3f p) {
//       hs
//    }
    // xS = (xA + xB + xC)/3
    //yS = (yA + yB + yC)/3
    //zS = (zA + zB + zC)/3    
}
