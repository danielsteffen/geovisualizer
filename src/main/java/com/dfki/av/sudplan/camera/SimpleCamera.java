/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.camera;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.util.AdvancedBoundingBox;
import com.dfki.av.sudplan.vis.control.AdvancedOrbitBehavior;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.pickfast.PickCanvas;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.awt.Point;
import java.util.ArrayList;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PickInfo;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class SimpleCamera implements Camera, TransformationListener {

    private final static Logger logger = LoggerFactory.getLogger(SimpleCamera.class);
    private final ArrayList<CameraListener> cameraListeners = new ArrayList<CameraListener>();
    protected final AdvancedOrbitBehavior behavior;
    private final ViewingPlatform viewingPlatform;
    private final BranchGroup scene;
    private Bounds cameraBounds;
    private Point3d cameraPosition;
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: set default same as above
    private Vector3d cameraDirection = new Vector3d(0.0, 0.0, -1.0);
    private Canvas3D canvas;
    private double viewingDistance = 10000.0;    
    private Vector3d cameraUp;
    private Vector3d cameraDown;
    private Vector3d cameraLeft;
    private Vector3d cameraRight;
    private Vector3d cameraView;
    private PickCanvas pickCanvas;
    private AdvancedBoundingBox viewingBoundingBox;
    protected boolean cameraLoggingEnabled = false;

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:not nice with the branchGroup
    public SimpleCamera(final Viewer viewer, final BranchGroup scene) {
        behavior = new AdvancedOrbitBehavior(viewer.getCanvas3D(), scene,this, OrbitBehavior.REVERSE_ALL);
        this.scene = scene;
        this.viewingPlatform = viewer.getViewingPlatform();
        canvas = viewer.getCanvas3D();
        pickCanvas = new PickCanvas(canvas, scene);
        pickCanvas.setMode(PickInfo.PICK_GEOMETRY);
        pickCanvas.setFlags(PickInfo.CLOSEST_INTERSECTION_POINT);
        behavior.setProportionalZoom(true);
        behavior.setProportionalTranslate(true);
        behavior.setTransFactors(10, 10);
        behavior.setZoomFactor(0.5);
        behavior.addTransformationListener(this);
//        setCameraPosition(new Point3d(0.0, 0.0, 10.0));
        if (viewingPlatform != null) {
            viewingPlatform.setViewPlatformBehavior(behavior);
        }
    }

    public void addCameraListner(final CameraListener newListener) {
        if (!cameraListeners.contains(newListener)) {
            cameraListeners.add(newListener);
            newListener.cameraRegistered(new CameraEvent(this));
        }
    }

    public void removeCameraListner(final CameraListener listenerToRemove) {
        if (cameraListeners.contains(listenerToRemove)) {
            cameraListeners.remove(listenerToRemove);
            listenerToRemove.cameraUnregistered(new CameraEvent(this));
        }
    }

    @Override
    public Vector3d getCameraDirection() {
        return cameraDirection;
    }

    @Override
    public void setCameraPosition(final Point3d cameraPosition) {
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("setCameraPosition.");
        }
        if (cameraPosition != null) {
            behavior.setRotationCenter(new Point3d(cameraPosition.x, cameraPosition.y, 0.0));
        } else {
            behavior.setRotationCenter(new Point3d());
        }
        final Point3d oldPoint = this.cameraPosition;
        this.cameraPosition = cameraPosition;
        gotoPoint(cameraPosition);
        calculateBoundingBox();
        for (CameraListener cameraListener : cameraListeners) {
            if (logger.isDebugEnabled() && cameraLoggingEnabled) {
                logger.debug("camera listener");
            }
            cameraListener.cameraMoved(new CameraEvent(this, oldPoint, getCameraPosition(), getViewBoundingBox()));
        }
    }

    @Override
    public Point3d getCameraPosition() {
        return cameraPosition;
    }

    @Override
    public AdvancedBoundingBox getViewBoundingBox() {
        return viewingBoundingBox;
    }

    @Override
    public void setCameraDirection(final Vector3d cameraDirection) {
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("setCameraDirection");
        }
        this.cameraDirection = cameraDirection;
    }

    @Override
    public void translated(TransformationEvent transEvent) {
        if (transEvent != null) {
//            final BoundingBox viewBoundingBox = calculateBoundingBox();
//            if (logger.isDebugEnabled()) {
//                logger.debug("translated: old:"+transEvent.getOldPosition()+" new:"+transEvent.getNewPosition());
//            }
//            setCameraPosition(transEvent.getNewPosition());
            calculateBoundingBox();
            for (CameraListener cameraListener : cameraListeners) {
                cameraListener.cameraMoved(new CameraEvent(this, transEvent.getOldPosition(), transEvent.getNewPosition(), getViewBoundingBox()));
            }
        }
    }

    @Override
    public void rotated(TransformationEvent transEvent) {
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("rotation");
        }
        if (transEvent != null) {
//            final BoundingBox viewBoundingBox = calculateBoundingBox();
            calculateBoundingBox();
            final Vector3d oldDirection = getCameraDirection();
            final Vector3d newDirection = new Vector3d(DEFAULT_VIEW);
            transEvent.getRotation().transform(newDirection);
            newDirection.normalize();
            logCameraOrientation();
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: own method
            cameraUp = new Vector3d(DEFAULT_UP);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:don't have to be transformed simply based on one vector for example -up is down up rotated by 45 clockwise is right etc
            cameraDown = new Vector3d(DEFAULT_DOWN);
            cameraLeft = new Vector3d(DEFAULT_LEFT);
            cameraRight = new Vector3d(DEFAULT_RIGHT);
            transEvent.getRotation().transform(cameraUp);
            transEvent.getRotation().transform(cameraDown);
            transEvent.getRotation().transform(cameraLeft);
            transEvent.getRotation().transform(cameraRight);
            logCameraOrientation();
            if (!oldDirection.equals(newDirection)) {
                setCameraDirection(newDirection);
                for (CameraListener cameraListener : cameraListeners) {
                    cameraListener.cameraViewChanged(new CameraEvent(this, oldDirection, getCameraDirection(), getViewBoundingBox()));
                }
            } else {
                if (logger.isDebugEnabled() && cameraLoggingEnabled) {
                    logger.debug("same direction no change:");
                }
            }
        }
    }

    public Bounds getCameraBounds() {
        return cameraBounds;
    }

    public void setCameraBounds(final Bounds cameraBounds) {
        behavior.setSchedulingBounds(cameraBounds);
        this.cameraBounds = cameraBounds;
    }

    public ViewingPlatform getViewingPlatform() {
        return viewingPlatform;
    }

    private void calculateBoundingBox() {
        if (scene.isLive()) {
            final Point lowerLeft = new Point(0, canvas.getHeight());
            final Point lowerRight = new Point(canvas.getWidth(), canvas.getHeight());
            final Point upperLeft = new Point(0, 0);
            final Point upperRight = new Point(canvas.getWidth(), 0);
            final Point3d virtualLowerLeft = pickPoint(lowerLeft);
            final Point3d virtualLowerRight = pickPoint(lowerRight);
            final Point3d virtualUpperLeft = pickPoint(upperLeft);
            final Point3d virtualUpperRight = pickPoint(upperRight);
            if (virtualLowerLeft != null && virtualUpperRight != null) {
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: problem this is not right in 3d --> upper left could be more to the left than lower left
                viewingBoundingBox = new AdvancedBoundingBox(virtualLowerLeft, virtualUpperRight);
            } else if (virtualLowerLeft != null && virtualLowerRight != null) {
                final Point3d calculatedUpperRight = new Point3d(virtualLowerRight);
                calculatedUpperRight.y += viewingDistance * ComponentBroker.getInstance().getScalingFactor();
                viewingBoundingBox = new AdvancedBoundingBox(virtualLowerLeft, calculatedUpperRight);
            } else if (virtualLowerLeft != null) {
                viewingBoundingBox = null;
            } else if (virtualLowerRight != null) {
                viewingBoundingBox = null;
            } else {
                viewingBoundingBox = null;
            }
        } else {
            viewingBoundingBox = null;
        }
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("Viewableboundingbox: "+viewingBoundingBox);
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: special cases contains. For now they are not valid.

//        BoundingBox bounding = new BoundingBox(virtualLowerLeft,virtualUpperRight);
//        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
//            if (logger.isDebugEnabled() && cameraLoggingEnabled) {
//                logger.debug("bounding: "+bounding);
//            }
//        }
    }

    private Point3d pickPoint(final Point screenPoint) {
        Point3d pickPoint = null;
        pickCanvas.setShapeLocation(screenPoint.x, screenPoint.y);
        final PickInfo[] results = pickCanvas.pickAll();
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:Problems with DEM
        if (results != null) {
            pickPoint = results[results.length - 1].getClosestIntersectionPoint();
        }
        return pickPoint;
    }

//    @Override
//    public void gotoToHome() {
//        Transform3D viewTransformation = new Transform3D();
//        viewTransformation.setTranslation(new Vector3d(getCameraPosition()));
//        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this should be hided by the camera
//        getViewingPlatform().getViewPlatformTransform().setTransform(viewTransformation);
//
//    }
    @Override
    public void gotoPoint(Tuple3f point) {
        if (point == null) {
            return;
        }
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("Goto point: " + point);
        }
        if (point != null) {
            Transform3D viewTransformation = new Transform3D();
            viewTransformation.setTranslation(new Vector3f(point));
            getViewingPlatform().getViewPlatformTransform().setTransform(viewTransformation);
        }
    }

    @Override
    public void gotoPoint(Tuple3d point) {
        if (point == null) {
            return;
        }
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("Goto point: " + point);
        }
        gotoPoint(new Point3f(point));
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:if the center is more often used extend Bounding box
    @Override
    public void gotoBoundingBox(AdvancedBoundingBox boundingBox) {
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("Goto BoundingBox: " + boundingBox);
        }
        if (boundingBox != null && !boundingBox.isEmpty()) {
            if (getViewBoundingBox() == null) {
                calculateBoundingBox();
            }
//            final Point3d lower = new Point3d();
//            final Point3d upper = new Point3d();
//            boundingBox.getLower(lower);
//            boundingBox.getUpper(upper);
            final Transform3D viewTransformation = new Transform3D();
//            getViewingPlatform().getViewPlatformTransform().getTransform(viewTransformation);
//            viewTransformation.transform(currentPoint);
//            logger.debug("View Position: " + currentPoint);
            final AdvancedBoundingBox currentBoundingBox = getViewBoundingBox();
            final Point2d deltaCenter = boundingBox.getDelta(currentBoundingBox);
            final Point3d center = boundingBox.getCenter();
            if (logger.isDebugEnabled() && cameraLoggingEnabled) {
                logger.debug("old boundingBox: " + currentBoundingBox);
                logger.debug("delta center: " + deltaCenter);
                logger.debug("new center: " + center);
            }
            if (deltaCenter != null) {
                center.x += deltaCenter.x;
                center.y += deltaCenter.y;
                center.z += getCameraPosition().z;
                logger.debug("delta center: " + deltaCenter);
                viewTransformation.setTranslation(new Vector3d(center));
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:not fixed but so that all is seen
//            gotoPoint(new Point3d(lower.x+((upper.x-lower.x)/2),lower.y+((upper.y-lower.y)/2),20));
                gotoPoint(center);
            } else {
                if (logger.isDebugEnabled() && cameraLoggingEnabled) {
                    logger.debug("do delta avail.");
                }
            }
        }
    }

    public BranchGroup getScene() {
        return scene;
    }
//    public AdvancedBoundingBox getViewBoundingBox() {
//        final Point realPosition = canvas.getLocation();
//        final Point canvasPositionRealLower = new Point(realPosition.x, realPosition.y + canvas.getHeight());
//        final Point canvasPositionRealUpper = new Point(realPosition.x + canvas.getWidth(), realPosition.y);
//        logger.debug("canvas position lower real: " + canvasPositionRealLower);
//        logger.debug("canvas position upper real: " + canvasPositionRealUpper);
//        Transform3D motionToWorld = new Transform3D();
//        canvas.getImagePlateToVworld(motionToWorld);
//        final Point3d canvasPositionVirtualLower = new Point3d();
//        final Point3d canvasPositionVirtualUpper = new Point3d();
//        canvas.getPixelLocationInImagePlate(canvasPositionRealLower.x, canvasPositionRealLower.y, canvasPositionVirtualLower);
//        canvas.getPixelLocationInImagePlate(canvasPositionRealUpper.x, canvasPositionRealUpper.y, canvasPositionVirtualUpper);
//        if (logger.isDebugEnabled()) {
//            logger.debug("image plate lower" + canvasPositionVirtualLower);
//            logger.debug("image plate upper" + canvasPositionVirtualUpper);
//        }
//        motionToWorld.transform(canvasPositionVirtualLower);
//        motionToWorld.transform(canvasPositionVirtualUpper);
//        logger.debug("canvas position lower virtual: " + canvasPositionVirtualLower);
//        logger.debug("canvas position upper virtual: " + canvasPositionVirtualUpper);
//        AdvancedBoundingBox vViewBoundingBox = new AdvancedBoundingBox(canvasPositionVirtualLower, canvasPositionVirtualUpper);
////                logger.debug("Viewing bounding box: " + vViewBoundingBox);
//        return vViewBoundingBox;
//    }

    private void logCameraOrientation() {
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("up: " + cameraUp + " down: " + cameraDown + " left: " + cameraLeft + " right: " + cameraRight);
        }
    }

    @Override
    public Vector3d getCameraDown() {
        return cameraDown;
    }

    @Override
    public Vector3d getCameraLeft() {
        return cameraLeft;
    }

    @Override
    public Vector3d getCameraRight() {
        return cameraRight;
    }

    @Override
    public Vector3d getCameraUp() {
        return cameraUp;
    }
}
