/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.util;

import java.awt.Dimension;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.Bounds;
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
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:does it make sense to make this three dimensional ?? boundings should not be dimensional
        final Point3d centerPoint = new Point3d(upper.x - lower.x, upper.y - lower.x, upper.z - upper.z);
        return centerPoint;
    }

    public Dimension getDelta(final AdvancedBoundingBox box) {
        if (box != null) {
            final Dimension result = new Dimension();
            final Point3d center1 = box.getCenter();
            final Point3d center2 = this.getCenter();
            result.setSize(center2.x-center1.x, center2.y-center1.y);
            return result;
        }
        return null;
    }
}
