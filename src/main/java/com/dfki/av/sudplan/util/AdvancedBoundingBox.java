/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.util;

import java.awt.geom.Point2D;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.Bounds;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class AdvancedBoundingBox extends BoundingBox {

    private final static Logger logger = LoggerFactory.getLogger(AdvancedBoundingBox.class);
    
    public AdvancedBoundingBox(Bounds[] bounds) {
        super(bounds);
    }

    public AdvancedBoundingBox(Bounds boundsObject) {
        super(boundsObject);
    }

    public AdvancedBoundingBox() {
        super();
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: lat/lon sector problem (south/north east/west) coordinate problem see Simple Camera. Fix after ATR
    public AdvancedBoundingBox(Point3d point1, Point3d point2, Point3d point3, Point3d point4) throws IllegalArgumentException {
        super();
        if (point1 == null || point2 == null || point2 == null || point2 == null) {
            throw new IllegalArgumentException("All points must be != null");
        }
        final double minX = Math.min(point4.x, (Math.min(point3.x, Math.min(point1.x, point2.x))));
        final double minY = Math.min(point4.y, (Math.min(point3.y, Math.min(point1.y, point2.y))));
        final double maxX = Math.max(point4.x, (Math.max(point3.x, Math.max(point1.x, point2.x))));
        final double maxY = Math.max(point4.y, (Math.max(point3.y, Math.max(point1.y, point2.y))));
        setLower(new Point3d(minX, minY, 0.0));
        setUpper(new Point3d(maxX, maxY, 0.0));
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:combine with above
    public AdvancedBoundingBox(Point3d point1, Point3d point2) {
        super();
        if (point1 == null || point2 == null) {
            throw new IllegalArgumentException("All points must be != null");
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: overhead
        final double minX = Math.min(point1.x, point2.x);
        final double minY = Math.min(point1.y, point2.y);
        final double maxX = Math.max(point1.x, point2.x);
        final double maxY = Math.max(point1.y, point2.y);
        setLower(new Point3d(minX, minY, 0.0));
        setUpper(new Point3d(maxX, maxY, 0.0));
    }

    public Point3d getLower() {
        final Point3d lower = new Point3d();
        getLower(lower);
        return lower;
    }

    public Point3d getUpper() {
        final Point3d upper = new Point3d();
        getUpper(upper);
        return upper;
    }

    public Point3d getCenter() {
        final Point3d lower = new Point3d();
        final Point3d upper = new Point3d();
        getLower(lower);
        getUpper(upper);
//        if (logger.isDebugEnabled()) {
//            logger.debug("getCenter: "+);
//        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:does it make sense to make this three dimensional ?? boundings should not be dimensional
        final Point3d centerPoint = new Point3d(lower.x + (upper.x - lower.x) / 2, lower.y + (upper.y - lower.y) / 2, lower.z + (upper.z - lower.z) / 2);
        return centerPoint;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:use a datastructure with width / height
    public Point2d getDelta(final AdvancedBoundingBox box) {
        if (box != null) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("this" + this);
//                logger.debug("other" + box);
//            }
            final Point2d result = new Point2d();
            final Point3d center1 = box.getCenter();
            final Point3d center2 = this.getCenter();
//            if (logger.isDebugEnabled()) {
//                logger.debug("centerx this:" + center2.x + "centerx other:" + center1.x);
//                logger.debug("centerx this:" + center2.y + "centerx other:" + center1.y);
//                logger.debug("deltax:" + (center1.x - center2.x) + "deltay:" + (center1.y - center2.y));
//            }
            result.x = (center1.x - center2.x);
            result.y = (center1.y - center2.y);
            return result;
        }
        return null;
    }

    public boolean containsBoundingBox(final AdvancedBoundingBox box) {
        if (box == null) {
            return false;
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: getter unperformand   
        if (getLower().x < box.getLower().x && getLower().y < box.getLower().y && getUpper().x > box.getUpper().x && getUpper().y > box.getUpper().y) {
            return true;
        }
        return false;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:all operations in this class are not save what if lower is null ?
    public double getWidth() {
        return getUpper().x - getLower().x;
    }

    public double getHeight() {
        return getUpper().y - getLower().y;
    }
}
