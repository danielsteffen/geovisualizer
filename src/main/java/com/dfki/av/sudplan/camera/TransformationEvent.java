/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.camera;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class TransformationEvent {
    private final static Logger logger = LoggerFactory.getLogger(TransformationEvent.class);

    public TransformationEvent(final Point3d oldPosition,final Point3d newPosition) {
        eventType = EVENT_TYPES.TRANSLATION;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }

    public TransformationEvent(Transform3D rotation) {
        eventType = EVENT_TYPES.ROTATION;
        this.rotation=rotation;
    }
    public static enum EVENT_TYPES {
        TRANSLATION,ROTATION
    };
    private EVENT_TYPES eventType;
    private Point3d oldPosition;
    private Point3d newPosition;

    private Transform3D rotation;

    public EVENT_TYPES getEventType() {
        return eventType;
    }

    public Point3d getNewPosition() {
        return newPosition;
    }

    public Point3d getOldPosition() {
        return oldPosition;
    }

    public Transform3D getRotation() {
        return rotation;
    }
}
