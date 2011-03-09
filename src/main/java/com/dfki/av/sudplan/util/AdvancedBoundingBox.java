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

    public AdvancedBoundingBox(Point3d lower, Point3d upper) {
        super(lower, upper);
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
        final Point3d centerPoint = new Point3d(lower.x+(upper.x - lower.x)/2, lower.y +(upper.y - lower.y)/2,lower.z +( upper.z - lower.z)/2);
        return centerPoint;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:use a datastructure with width / height
    public Point2d getDelta(final AdvancedBoundingBox box) {
        if (box != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("this"+this);
                logger.debug("other"+box);
            }
            final Point2d result = new Point2d();
            final Point3d center1 = box.getCenter();
            final Point3d center2 = this.getCenter();
            if (logger.isDebugEnabled()) {
                logger.debug("centerx this:"+center2.x+"centerx other:"+center1.x);
                logger.debug("deltax:"+(center1.x-center2.x)+"deltay:"+(center1.y-center2.y));
            }
            result.x= (center1.x-center2.x);
            result.y= (center1.y-center2.y);
            return result;
        }
        return null;
    }
}
