/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.util;

import javax.vecmath.Point3f;
//import org.deegree.cs.CRS;
//import org.deegree.cs.exceptions.TransformationException;
//import org.deegree.cs.exceptions.UnknownCRSException;
//import org.deegree.geometry.GeometryTransformer;
//import org.deegree.geometry.standard.primitive.DefaultPoint;
//import org.deegree.cs.CRS;
//import org.deegree.geometry.GeometryTransformer;
//import org.deegree.geometry.standard.primitive.DefaultPoint;
//import org.geotools.referencing.CRS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class CoordinateTransformer {

//    public static final int WGS84_EPGS = 4326;
//    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: does not work with geotools the resulting coordinates are deegree/rads
////    public static final int PLATE_CARREE_EPGS = 32662;
//    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:bad design investigate further must be a way not to do the conversion by hand
//    //this is wrong by the idea
//    public static final int PLATE_CARREE_EPGS = 4326;
//    public static final int INTERNAL_EPSG = PLATE_CARREE_EPGS;
//    private static final Logger logger = LoggerFactory.getLogger(CoordinateTransformer.class);
//
//// GeoTools problem with 4124
//    public static Point3f transformPoint(final CRS source, final CRS target, final Point3f point) throws
//            TransformationException {
//        if (point == null) {
//            throw new IllegalArgumentException("Transformation not possible point is null.");
//        }
//        try {
//            final Point3f transformedPoint = new Point3f();
//            final GeometryTransformer transformer = new GeometryTransformer(target);
//            final DefaultPoint sourcePoint = new DefaultPoint(
//                    null, source, null, new double[]{point.x, point.y, point.z});
//            DefaultPoint deegreeTransformedPoint =
//                    (DefaultPoint) transformer.transform(sourcePoint, source.getWrappedCRS());
//            transformedPoint.x = (float) deegreeTransformedPoint.get0();
//            transformedPoint.y = (float) deegreeTransformedPoint.get1();
//            return transformedPoint;
//        } catch (Exception ex) {
//            final String message = "Error while transforming point.";
//            if (logger.isErrorEnabled()) {
//                logger.error(message, ex);
//            }
//            throw new TransformationException(message, ex);
//        }
//    }
//
//    public static Point3f transformPoint(final int source, final int target, final Point3f point) throws
//            TransformationException {
//        final CRS sourceCRS = new CRS("epsg:" + source);
//        final CRS targetCRS = new CRS("epsg:" + target);
//        return transformPoint(sourceCRS, targetCRS, point);
//    }
////    public static Point3f transformPoint(CoordinateReferenceSystem source, CoordinateReferenceSystem target, Point3f point) throws TransformationException {
////        if (point == null) {
////            throw new IllegalArgumentException("Transformation not possible point is null.");
////        }
////        try {
////            final Point3f transformedPoint = new Point3f();
////            MathTransform transformation = CRS.findMathTransform(source, target);
////            final float[] transformedCoordinates = new float[3];
////            point.get(transformedCoordinates);
////            transformation.transform(transformedCoordinates, 0, transformedCoordinates, 0, 1);
////            transformedPoint.set(transformedCoordinates);
////            return transformedPoint;
////        } catch (Exception ex) {
////            final String message = "Error while transforming point.";
////            if (logger.isErrorEnabled()) {
////                logger.error(message, ex);
////            }
////            throw new TransformationException(message, ex);
////        }
////    }
////    public static Point3f transformPoint(int source, int target, Point3f point) throws TransformationException {
////
////        try {
////            final CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:" + source);
////            final CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:" + target);
////            return transformPoint(sourceCRS, targetCRS, point);
////        } catch (Exception ex) {
////            final String message = "Error while transforming point.";
////            if (logger.isErrorEnabled()) {
////                logger.error(message, ex);
////            }
////            throw new TransformationException(message, ex);
////        }
////
////
////    }
//
//    public static Point3f transformPointToInternalCoordinateSystem(final int source, final Point3f point)
//            throws TransformationException {
////        if (logger.isDebugEnabled()) {
////            logger.debug("source point: "+point);
////        }
//        final Point3f transformedPoint = transformPoint(source, INTERNAL_EPSG, point);
//        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: see above don't want to do this by hand. Must be a good way
////        if (logger.isDebugEnabled()) {
////            logger.debug("transformed point: "+transformedPoint);
////        }
//        if (INTERNAL_EPSG == PLATE_CARREE_EPGS) {
//            transformedPoint.x =
//                    (float) (EarthFlat.deegreeToRadians(transformedPoint.x) * EarthFlat.WGS84_EARTH_EQUATORIAL_RADIUS);
//            transformedPoint.y =
//                    (float) (EarthFlat.deegreeToRadians(transformedPoint.y) * EarthFlat.WGS84_EARTH_EQUATORIAL_RADIUS);
//        }
//        return transformedPoint;
//    }
}
