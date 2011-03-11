/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.geo;

import com.dfki.av.sudplan.camera.Camera;
import com.dfki.av.sudplan.camera.CameraEvent;
import com.dfki.av.sudplan.camera.CameraListener;
import com.dfki.av.sudplan.util.AdvancedBoundingBox;
import com.dfki.av.sudplan.util.EarthFlat;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.util.ArrayList;
import javax.media.j3d.Bounds;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
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
        return EarthFlat.cartesianToGeodetic(cameraDirection, EarthFlat.PLATE_CARREE_PROJECTION);
    }

    @Override
    public Vector3d getCameraDown() {
        final Vector3d cameraDirection = camera.getCameraDown();
        if (cameraDirection == null) {
            return cameraDirection;
        }
        return EarthFlat.cartesianToGeodetic(cameraDirection, EarthFlat.PLATE_CARREE_PROJECTION);
    }

    @Override
    public Vector3d getCameraLeft() {
        final Vector3d cameraDirection = camera.getCameraLeft();
        if (cameraDirection == null) {
            return cameraDirection;
        }
        return EarthFlat.cartesianToGeodetic(cameraDirection, EarthFlat.PLATE_CARREE_PROJECTION);
    }

    @Override
    public Point3d getCameraPosition() {
        final Point3d cameraPosition = camera.getCameraPosition();
        if (cameraPosition == null) {
            return cameraPosition;
        }
        return EarthFlat.cartesianToGeodetic(cameraPosition, EarthFlat.PLATE_CARREE_PROJECTION);
    }

    @Override
    public Vector3d getCameraRight() {
        final Vector3d cameraDirection = camera.getCameraRight();
        if (cameraDirection == null) {
            return cameraDirection;
        }
        return EarthFlat.cartesianToGeodetic(cameraDirection, EarthFlat.PLATE_CARREE_PROJECTION);
    }

    @Override
    public Vector3d getCameraUp() {
        final Vector3d cameraDirection = camera.getCameraUp();
        if (cameraDirection == null) {
            return cameraDirection;
        }
        return EarthFlat.cartesianToGeodetic(cameraDirection, EarthFlat.PLATE_CARREE_PROJECTION);
    }

    @Override
    public AdvancedBoundingBox getViewBoundingBox() {
        final AdvancedBoundingBox viewBoundingBox = camera.getViewBoundingBox();
        if (viewBoundingBox == null) {
            return viewBoundingBox;
        }
        return EarthFlat.cartesianToGeodetic(viewBoundingBox, EarthFlat.PLATE_CARREE_PROJECTION);
    }

    @Override
    public AdvancedBoundingBox getReducedBoundingBox() {
        final AdvancedBoundingBox viewBoundingBox = camera.getReducedBoundingBox();
        if (viewBoundingBox == null) {
            return viewBoundingBox;
        }
        return EarthFlat.cartesianToGeodetic(viewBoundingBox, EarthFlat.PLATE_CARREE_PROJECTION);
    }

    @Override
    public ViewingPlatform getViewingPlatform() {
        return camera.getViewingPlatform();
    }

    @Override
    public void gotoBoundingBox(final AdvancedBoundingBox boundingBox) {
        camera.gotoBoundingBox(EarthFlat.geodeticToCartesian(boundingBox, EarthFlat.PLATE_CARREE_PROJECTION));
    }

    @Override
    public void gotoPoint(final Tuple3f point) {
        if (point == null) {
            camera.gotoPoint((Point3f) null);
        }
        camera.gotoPoint(EarthFlat.geodeticToCartesian(new Point3d(point), EarthFlat.PLATE_CARREE_PROJECTION));
    }

    @Override
    public void gotoPoint(final Tuple3d point) {
        camera.gotoPoint(EarthFlat.geodeticToCartesian(point, EarthFlat.PLATE_CARREE_PROJECTION));
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
        camera.setCameraDirection(EarthFlat.geodeticToCartesian(cameraDirection, EarthFlat.PLATE_CARREE_PROJECTION));
    }

    @Override
    public void setCameraPosition(final Point3d cameraPosition) {
        camera.setCameraPosition(EarthFlat.geodeticToCartesian(cameraPosition, EarthFlat.PLATE_CARREE_PROJECTION));
    }

    @Override
    public void setCameraToInitalViewingDirection() {
        camera.setCameraToInitalViewingDirection();
    }

    @Override
    public void cameraMoved(CameraEvent cameraEvent) {
        for (CameraListener cameraListener : cameraListeners) {
            cameraListener.cameraMoved(new CameraEvent(
                    this,
                    EarthFlat.geodeticToCartesian(cameraEvent.getOldCameraPosition(), EarthFlat.PLATE_CARREE_PROJECTION),
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
                    EarthFlat.geodeticToCartesian(cameraEvent.getOldCameraViewDirection(), EarthFlat.PLATE_CARREE_PROJECTION),
                    getCameraDirection(),
                    getViewBoundingBox(),
                    getReducedBoundingBox()));
        }
    }
}
