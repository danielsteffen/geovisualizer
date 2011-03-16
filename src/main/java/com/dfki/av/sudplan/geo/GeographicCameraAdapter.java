/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.geo;

import com.dfki.av.sudplan.camera.Camera;
import com.dfki.av.sudplan.camera.CameraEvent;
import com.dfki.av.sudplan.camera.CameraListener;
import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.util.AdvancedBoundingBox;
import com.dfki.av.sudplan.util.EarthFlat;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.util.ArrayList;
import javax.media.j3d.Bounds;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class GeographicCameraAdapter implements Camera, CameraListener {

    private final static Logger logger = LoggerFactory.getLogger(GeographicCameraAdapter.class);
    private final Camera camera;
    private final ArrayList<CameraListener> cameraListeners = new ArrayList<CameraListener>();

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this could also be used with epsg specification
    public GeographicCameraAdapter(final Camera camera) throws IllegalArgumentException {
        if (camera == null) {
            throw new IllegalArgumentException("Camera must not be null.");
        }
        this.camera = camera;
        this.camera.addCameraListner(this);
    }

    @Override
    public void addCameraListner(final CameraListener newListener) {
        if (!cameraListeners.contains(newListener)) {
            cameraListeners.add(newListener);
            newListener.cameraRegistered(new CameraEvent(this));
        }
    }

    @Override
    public void calculateBoundingBoxes() {
        camera.calculateBoundingBoxes();
    }

    @Override
    public Vector3d getCameraDirection() {
        final Vector3d cameraDirection = camera.getCameraDirection();
        if (cameraDirection == null) {
            return cameraDirection;
        }
        return cameraDirection;
    }

    @Override
    public Vector3d getCameraDown() {
        final Vector3d cameraDirection = camera.getCameraDown();
        if (cameraDirection == null) {
            return cameraDirection;
        }
        return cameraDirection;
    }

    @Override
    public Vector3d getCameraLeft() {
        final Vector3d cameraDirection = camera.getCameraLeft();
        if (cameraDirection == null) {
            return cameraDirection;
        }
        return cameraDirection;
    }

    @Override
    public Point3d getCameraPosition() {
        final Point3d cameraPosition = camera.getCameraPosition();
        if (cameraPosition == null) {
            return cameraPosition;
        }
        scaleTuple3d(cameraPosition, ComponentBroker.getInstance().getInverseScalingFactor());
        return EarthFlat.cartesianToGeodetic(cameraPosition, EarthFlat.PLATE_CARREE_PROJECTION);
    }

    @Override
    public Vector3d getCameraRight() {
        final Vector3d cameraDirection = camera.getCameraRight();
        if (cameraDirection == null) {
            return cameraDirection;
        }
        return cameraDirection;
    }

    @Override
    public Vector3d getCameraUp() {
        final Vector3d cameraDirection = camera.getCameraUp();
        if (cameraDirection == null) {
            return cameraDirection;
        }
        return cameraDirection;
    }

    @Override
    public AdvancedBoundingBox getViewBoundingBox() {
        final AdvancedBoundingBox viewBoundingBox = camera.getViewBoundingBox();
        if (viewBoundingBox == null) {
            return viewBoundingBox;
        }
        final AdvancedBoundingBox geographicBB = EarthFlat.cartesianToGeodetic(viewBoundingBox, EarthFlat.PLATE_CARREE_PROJECTION);
        Point3d scaledLower = geographicBB.getLower();
        Point3d scaledUpper = geographicBB.getUpper();
        scaleTuple3d(scaledLower, ComponentBroker.getInstance().getInverseScalingFactor());
        scaleTuple3d(scaledUpper, ComponentBroker.getInstance().getInverseScalingFactor());
        geographicBB.setUpper(scaledUpper);
        geographicBB.setLower(scaledLower);
        return geographicBB;
    }

    @Override
    public AdvancedBoundingBox getReducedBoundingBox() {
        final AdvancedBoundingBox reducedBoundingBox = camera.getReducedBoundingBox();
        if (reducedBoundingBox == null) {
            return reducedBoundingBox;
        }
        final AdvancedBoundingBox geographicBB = EarthFlat.cartesianToGeodetic(reducedBoundingBox, EarthFlat.PLATE_CARREE_PROJECTION);
        Point3d scaledLower = geographicBB.getLower();
        Point3d scaledUpper = geographicBB.getUpper();
        scaleTuple3d(scaledLower, ComponentBroker.getInstance().getInverseScalingFactor());
        scaleTuple3d(scaledUpper, ComponentBroker.getInstance().getInverseScalingFactor());
        geographicBB.setUpper(scaledUpper);
        geographicBB.setLower(scaledLower);
        return geographicBB;
    }

    @Override
    public ViewingPlatform getViewingPlatform() {
        return camera.getViewingPlatform();
    }

    @Override
    public void gotoBoundingBox(final AdvancedBoundingBox boundingBox) {
        final AdvancedBoundingBox cartesianBB = EarthFlat.geodeticToCartesian(boundingBox, EarthFlat.PLATE_CARREE_PROJECTION);
        Point3d scaledLower = cartesianBB.getLower();
        Point3d scaledUpper = cartesianBB.getUpper();
        scaleTuple3d(scaledLower, ComponentBroker.getInstance().getScalingFactor());
        scaleTuple3d(scaledUpper, ComponentBroker.getInstance().getScalingFactor());
        cartesianBB.setUpper(scaledUpper);
        cartesianBB.setLower(scaledLower);
        camera.gotoBoundingBox(cartesianBB);
    }

    @Override
    public void removeCameraListner(final CameraListener listenerToRemove) {
        if (cameraListeners.contains(listenerToRemove)) {
            cameraListeners.remove(listenerToRemove);
            listenerToRemove.cameraUnregistered(new CameraEvent(this));
        }
    }

    @Override
    public void setCameraBounds(final Bounds behaviourBounding) {
        camera.setCameraBounds(behaviourBounding);
    }

    @Override
    public void setCameraDirection(final Vector3d cameraDirection) {
        camera.setCameraDirection(cameraDirection);
    }

    @Override
    public void setCameraPosition(final Point3d cameraPosition) {
        if (cameraPosition == null) {
            camera.setCameraPosition(null);
        }
        final Point3d cartesianPoint = new Point3d(cameraPosition);
        scaleTuple3d(cartesianPoint, ComponentBroker.getInstance().getScalingFactor());
        camera.setCameraPosition(EarthFlat.geodeticToCartesian(cameraPosition, EarthFlat.PLATE_CARREE_PROJECTION));
    }

    @Override
    public void setCameraToInitalViewingDirection() {
        camera.setCameraToInitalViewingDirection();
    }

    @Override
    public void cameraMoved(CameraEvent cameraEvent) {
        for (CameraListener cameraListener : cameraListeners) {
            final Point3d oldCameraPosition = new Point3d(cameraEvent.getOldCameraPosition());
            scaleTuple3d(oldCameraPosition, ComponentBroker.getInstance().getInverseScalingFactor());
//            if (logger.isDebugEnabled()) {
//                logger.debug("old: "+oldCameraPosition);
//            }
            cameraListener.cameraMoved(new CameraEvent(
                    this,
                    EarthFlat.cartesianToGeodetic(oldCameraPosition, EarthFlat.PLATE_CARREE_PROJECTION),
                    getCameraPosition(),
                    getViewBoundingBox(),
                    getReducedBoundingBox()));
        }
    }

    @Override
    public void cameraRegistered(CameraEvent cameraEvent) {
    }

    @Override
    public void cameraUnregistered(CameraEvent cameraEvent) {
    }

    @Override
    public void cameraViewChanged(CameraEvent cameraEvent) {
        for (CameraListener cameraListener : cameraListeners) {
            cameraListener.cameraViewChanged(new CameraEvent(
                    this,
                    cameraEvent.getOldCameraViewDirection(),
                    getCameraDirection(),
                    getViewBoundingBox(),
                    getReducedBoundingBox()));
        }
    }

    @Override
    public void lookAtPoint(final Point3d pointToLookAt) {
        if (pointToLookAt == null) {
            return;
        }
        final Point3d cartesianPoint = new Point3d(pointToLookAt);
        scaleTuple3d(cartesianPoint, ComponentBroker.getInstance().getScalingFactor());
        camera.lookAtPoint(EarthFlat.geodeticToCartesian(cartesianPoint, EarthFlat.PLATE_CARREE_PROJECTION));
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:central place
    private void scaleTuple3d(final Tuple3d tuple, final double factor) {
        tuple.x *= factor;
        tuple.y *= factor;
//        tuple.z *= factor;
    }
}
