/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.camera;

import com.dfki.av.sudplan.util.AdvancedBoundingBox;
import javax.media.j3d.BoundingBox;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class CameraEvent {

    private final static Logger logger = LoggerFactory.getLogger(CameraEvent.class);
    private Vector3d oldCameraViewDirection;
    private Vector3d newCameraViewDirection;
    private double viewAngleDifference = Double.NaN;
    private Point3d newCameraPosition;
    private Point3d oldCameraPosition;
    private AdvancedBoundingBox cameraViewableBounds;
    private AdvancedBoundingBox camera2dContainingBounds;
    private AdvancedBoundingBox reducedBoundingBox;
    private Camera source;

    public CameraEvent(final Camera source, final Point3d oldCameraPosition, final Point3d newCameraPosition, final AdvancedBoundingBox cameraViewableBounds, final AdvancedBoundingBox reducedBoundingBox) {
        this.source = source;
        this.oldCameraPosition = oldCameraPosition;
        this.newCameraPosition = newCameraPosition;
        this.cameraViewableBounds = cameraViewableBounds;
        this.reducedBoundingBox = reducedBoundingBox;
    }

    public CameraEvent(final Camera source, final Vector3d oldDirection, final Vector3d newDirection, final AdvancedBoundingBox cameraViewableBounds, final AdvancedBoundingBox reducedBoundingBox) {
        this.source = source;
        this.oldCameraViewDirection = oldDirection;
        this.newCameraViewDirection = newDirection;
        if (oldDirection != null && newDirection != null) {
            viewAngleDifference = oldDirection.angle(newDirection);
        }
        this.cameraViewableBounds = cameraViewableBounds;
        this.reducedBoundingBox = reducedBoundingBox;
    }

    public CameraEvent(Camera source) {
        this.source = source;
    }

    public BoundingBox getCamera2dContainingBounds() {
        return camera2dContainingBounds;
    }

    public void setCamera2dContainingBounds(AdvancedBoundingBox camera2dContainingBounds) {
        this.camera2dContainingBounds = camera2dContainingBounds;
    }

    public Point3d getNewCameraPosition() {
        return newCameraPosition;
    }

    public Point3d getOldCameraPosition() {
        return oldCameraPosition;
    }

    public Vector3d getNewCameraViewDirection() {
        return newCameraViewDirection;
    }

    public Vector3d getOldCameraViewDirection() {
        return oldCameraViewDirection;
    }

    public AdvancedBoundingBox getCameraViewableBounds() {
        return cameraViewableBounds;
    }

    public void setCameraViewableBounds(AdvancedBoundingBox cameraViewableBounds) {
        this.cameraViewableBounds = cameraViewableBounds;
    }

    public Camera getSource() {
        return source;
    }

    public double getViewAngleDifference() {
        return viewAngleDifference;
    }

    public void setViewAngleDifference(double viewAngleDifference) {
        this.viewAngleDifference = viewAngleDifference;
    }

    public AdvancedBoundingBox getReducedBoundingBox() {
        return reducedBoundingBox;
    }
}
