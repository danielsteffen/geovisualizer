/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.camera;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.util.AdvancedBoundingBox;
import com.dfki.av.sudplan.util.EarthFlat;
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
//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:hole class needs to be refactored it is inconsistend regarding the syncing with the viewing platform e.g. setposition updates the viewplatform but setDirection not
public class SimpleCamera implements Camera, TransformationListener {

    private final static Logger logger = LoggerFactory.getLogger(SimpleCamera.class);
    private final ArrayList<CameraListener> cameraListeners = new ArrayList<CameraListener>();
    protected final AdvancedOrbitBehavior behavior;
    private final ViewingPlatform viewingPlatform;
    private final BranchGroup scene;
    private Bounds cameraBounds;
//    private Point3d cameraPosition;
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: set default same as above
//    private Vector3d cameraDirection = new Vector3d(0.0, 0.0, -1.0);
    private Canvas3D canvas;
    private double viewingDistance = 1.0;
    private Vector3d cameraUp;
    private Vector3d cameraDown;
    private Vector3d cameraLeft;
    private Vector3d cameraRight;
    private PickCanvas pickCanvas;
    private AdvancedBoundingBox viewingBoundingBox;
    private AdvancedBoundingBox reducedBoundingBox;
    protected boolean cameraLoggingEnabled = false;
    protected boolean boundingBoxLogging = false;
    protected boolean resetViewLogging = false;
    protected boolean cameraPostionLogging = false;
    protected boolean calculateViewBoundingBoxLogging = false;

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:not nice with the branchGroup
    public SimpleCamera(final Viewer viewer, final BranchGroup scene) {
        behavior = new AdvancedOrbitBehavior(viewer.getCanvas3D(), scene, this, OrbitBehavior.REVERSE_ALL);
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

    @Override
    public void addCameraListner(final CameraListener newListener) {
        if (!cameraListeners.contains(newListener)) {
            cameraListeners.add(newListener);
            newListener.cameraRegistered(new CameraEvent(this));
        }
    }

    @Override
    public void removeCameraListner(final CameraListener listenerToRemove) {
        if (cameraListeners.contains(listenerToRemove)) {
            cameraListeners.remove(listenerToRemove);
            listenerToRemove.cameraUnregistered(new CameraEvent(this));
        }
    }

    @Override
    public void setCameraDirection(final Vector3d newCameraDirection) {
        
    }

    @Override
    public void lookAtPoint(Point3d pointToLookAt) {
        if (pointToLookAt != null) {
            Transform3D viewTransformation = new Transform3D();
            viewTransformation.lookAt(getCameraPosition(), pointToLookAt, getCameraUp());
            viewTransformation.invert();
            getViewingPlatform().getViewPlatformTransform().setTransform(viewTransformation);
        }
    }

    @Override
    public Vector3d getCameraDirection() {
        final Transform3D transform = new Transform3D();
        final Vector3d viewingVector = new Vector3d(DEFAULT_VIEW);
        getViewingPlatform().getViewPlatformTransform().getTransform(transform);
        transform.transform(viewingVector);
        return viewingVector;
    }

    @Override
    public void setCameraPosition(final Point3d cameraPosition) {
        final Point3d oldCameraPosition = getCameraPosition();
        gotoPoint(cameraPosition);
        cameraPositionChanged(oldCameraPosition, cameraPosition);
    }

    public void cameraPositionChanged(final Point3d oldPoint, final Point3d newPoint) {
        calculateBoundingBoxes();
        for (CameraListener cameraListener : cameraListeners) {
            if (logger.isDebugEnabled() && cameraPostionLogging) {
                logger.debug("camera listener: old:" + oldPoint + " new: " + newPoint);
            }
            cameraListener.cameraMoved(new CameraEvent(this, oldPoint, getCameraPosition(), getViewBoundingBox(), getReducedBoundingBox()));
        }
    }

    @Override
    public Point3d getCameraPosition() {
        final Transform3D transform = new Transform3D();
        final Vector3d translation = new Vector3d();
        getViewingPlatform().getViewPlatformTransform().getTransform(transform);
        transform.get(translation);
        return new Point3d(translation);
    }

    @Override
    public AdvancedBoundingBox getViewBoundingBox() {
        return viewingBoundingBox;
    }

//    @Override
//    public void setCameraDirection(final Vector3d cameraDirection) {
//        if (logger.isDebugEnabled()) {
//            logger.debug("setCameraDirection: " + cameraDirection);
//        }
////        final Vector3d oldCameraDirection = this.cameraDirection;
////        this.cameraDirection = cameraDirection;
////        updateOrientationVectors();
////        calculateBoundingBoxes();
////        for (CameraListener cameraListener : cameraListeners) {
////            cameraListener.cameraViewChanged(new CameraEvent(this, oldCameraDirection, getCameraDirection(), getViewBoundingBox(), getReducedBoundingBox()));
////        }
////        if (logger.isDebugEnabled()) {
////            logger.debug("setCameraDirection: " + cameraDirection);
////        }
//    }
    protected void cameraDirectionChanged(final Vector3d oldCameraDirecton) {
        updateOrientationVectors();
        calculateBoundingBoxes();
        for (CameraListener cameraListener : cameraListeners) {
            cameraListener.cameraViewChanged(new CameraEvent(this, oldCameraDirecton, getCameraDirection(), getViewBoundingBox(), getReducedBoundingBox()));
        }
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("setCameraDirection: " + getCameraDirection());
        }
    }

    @Override
    public void translated(TransformationEvent transEvent) {
        if (transEvent != null) {
//            final BoundingBox viewBoundingBox = calculateBoundingBox();
            if (logger.isDebugEnabled() && cameraLoggingEnabled) {
                logger.debug("translated: old:" + transEvent.getOldPosition() + " new:" + transEvent.getNewPosition());
            }
            final Point3d oldPosition = transEvent.getOldPosition();
            final Point3d newPosition = transEvent.getNewPosition();
            if (!oldPosition.equals(newPosition)) {
                cameraPositionChanged(oldPosition, newPosition);
            } else {
                if (logger.isDebugEnabled() && cameraLoggingEnabled) {
                    logger.debug("same position no change:");
                }
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
            final Vector3d oldDirection = transEvent.getOldViewDirection();
            final Vector3d newDirection = transEvent.getNewViewDirection();
//            transEvent.getRotation().transform(newDirection);
//            newViewDirectionnewDirection.normalize();

            if (!oldDirection.equals(newDirection)) {
                cameraDirectionChanged(oldDirection);
                if (logger.isDebugEnabled() && cameraLoggingEnabled) {
                    logger.debug("setCameraDirection: " + getCameraDirection());
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

    @Override
    public void setCameraBounds(final Bounds cameraBounds) {
        behavior.setSchedulingBounds(cameraBounds);
        this.cameraBounds = cameraBounds;
    }

    @Override
    public ViewingPlatform getViewingPlatform() {
        return viewingPlatform;
    }

    @Override
    public void calculateBoundingBoxes() {
        calculateViewBoundingBox();
        calculcateReducedBoundingBox();
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:set private
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this is no boundingbox because it is not rectangular --> fix
    public void calculateViewBoundingBox() {
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("Calculate view BoundingBox");
        }
        if (scene.isLive()) {
            final Point lowerLeft = new Point(0, canvas.getHeight());
            final Point lowerRight = new Point(canvas.getWidth(), canvas.getHeight());
            final Point upperLeft = new Point(0, 0);
            final Point upperRight = new Point(canvas.getWidth(), 0);
            final Point3d virtualLowerLeft = pickPoint(lowerLeft);
            final Point3d virtualLowerRight = pickPoint(lowerRight);
            final Point3d virtualUpperLeft = pickPoint(upperLeft);
            final Point3d virtualUpperRight = pickPoint(upperRight);
            if (logger.isDebugEnabled() && cameraLoggingEnabled) {
                logger.debug("ll: " + virtualLowerLeft + " ul:" + virtualUpperLeft + " ur:" + virtualUpperRight + " lr:" + virtualLowerRight);
            }
            if (virtualUpperLeft != null && virtualUpperRight != null) {
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: problem this is not right in 3d --> upper left could be more to the left than lower left
                viewingBoundingBox = new AdvancedBoundingBox(virtualLowerLeft, virtualUpperLeft, virtualLowerRight, virtualUpperRight);
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
            logger.debug("Viewableboundingbox: " + viewingBoundingBox);
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: special cases contains. For now they are not valid.

//        BoundingBox bounding = new BoundingBox(virtualLowerLeft,virtualUpperRight);
//        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
//            if (logger.isDebugEnabled() && cameraLoggingEnabled) {
//                logger.debug("bounding: "+bounding);
//            }
//        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: Problems arise if this is not in the ++ sector of the lat/long fix after ATR
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:reuse points from viewable BoundingBox;
    public void calculcateReducedBoundingBox() {
        if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
            logger.debug("Calculate reduced BoundingBox");
        }
        if (scene.isLive()) {
            final Point lowerLeft = new Point(0, canvas.getHeight());
            final Point lowerRight = new Point(canvas.getWidth(), canvas.getHeight());
            final Point lowerCenter = new Point((int) canvas.getWidth() / 2, canvas.getHeight());
            final Point centerEye = new Point(canvas.getWidth() / 2, canvas.getHeight() / 2);

            if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                logger.debug("Pixel: ll: " + lowerLeft + " center: " + lowerCenter + " lr: " + lowerRight);
            }
//            final Point3d centerCenter = new Point3d();
//            canvas.getCenterEyeInImagePlate(centerEyeVector);

//            final Point centerEye =new Point(canvas.getWidth() / 2, (canvas.getHeight()/2)+(canvas.getHeight()/4));
            if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                logger.debug("Image: eye: " + centerEye);
            }
//            final Point3d virtualCenterEye = pickPoint(centerEye);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:explain the trick 
//            final Transform3D imageToWorld = new Transform3D();
//            canvas.getImagePlateToVworld(imageToWorld);

//            imageToWorld.transform(centerEyeVector);

            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:projected onto the plane


            final Transform3D worldToImage = new Transform3D();
            canvas.getVworldToImagePlate(worldToImage);
            final Point3d virtualLowerLeft = pickPoint(lowerLeft);
            final Point3d virtualLowerRight = pickPoint(lowerRight);
            Point3d virtualCenterEye = pickPoint(centerEye);
            if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                logger.debug("Pixel: eye: " + centerEye);
                logger.debug("Virtual: eye: " + virtualCenterEye);
            }
            if (virtualLowerLeft != null && virtualLowerRight != null) {
                while (centerEye.y < lowerCenter.y && virtualCenterEye == null) {
                    centerEye.y *= 1.1;
                    if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                        logger.debug("try to pick new Point: " + centerEye);
                    }
                    virtualCenterEye = pickPoint(centerEye);
                }
                if (virtualCenterEye == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("No suitable point could be found, not possible to calculate reduced boundingbox.");
                    }
                    reducedBoundingBox = null;
                    return;
                }
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:Eye point bad name this is not the eye point anymore
                if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                    logger.debug("Picked virtual eye point: " + virtualCenterEye);
                }
                final Point3d virtualLowerCenter = new Point3d(virtualLowerLeft.x + (virtualLowerRight.x - virtualLowerLeft.x) / 2, virtualLowerLeft.y, 0.0);
                if (lowerCenter != null) {
                    if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                        logger.debug("Virtual: ll: " + virtualLowerLeft + " center: " + virtualLowerCenter + " lr: " + virtualLowerRight);
                    }
                    final double distanceToBaseLine = virtualCenterEye.y - virtualLowerCenter.y;
                    if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                        logger.debug("Virtual: distance base line to eye: " + distanceToBaseLine);
                    }
                    final Point3d virtualExtendedEyePoint = new Point3d(virtualCenterEye);
                    virtualExtendedEyePoint.y += distanceToBaseLine;
                    if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                        logger.debug("Virtual: extended eye Position: " + virtualExtendedEyePoint);
                    }
                    virtualExtendedEyePoint.z = 0.0;
                    worldToImage.transform(virtualExtendedEyePoint);
                    if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                        logger.debug("Image: extended eye Position: " + virtualExtendedEyePoint);
                    }
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:some difference to the original extenden eye pos maybe missing precision in conversion
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: x does not work as expected need

//                final Point upperLeft = new Point(0, (int) Math.round(virtualExtendedEyePoint.y));
                    final Point2d upperLeft = new Point2d();
//                final Point upperRight = new Point(canvas.getWidth(), (int) Math.round(virtualExtendedEyePoint.y));
                    final Point2d upperRight = new Point2d();
                    canvas.getPixelLocationFromImagePlate(virtualExtendedEyePoint, upperLeft);
                    canvas.getPixelLocationFromImagePlate(virtualExtendedEyePoint, upperRight);
                    if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                        logger.debug("Pixel: extended eye Position: " + upperLeft);
                    }
                    upperRight.x = canvas.getWidth();
                    upperLeft.x = 0;
                    if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                        logger.debug("Pixel: ul: " + upperLeft + " ur: " + upperRight);
                    }
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:what if these points are null ? --> Simplification
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:then eye should be null
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:what if eye is null
                    final Point3d virtualUpperLeft = pickPoint(upperLeft);
                    final Point3d virtualUpperRight = pickPoint(upperRight);
                    if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                        logger.debug("Virtual: ul: " + virtualUpperLeft + " up: " + virtualUpperRight);
                    }
                    if (virtualLowerLeft != null && virtualUpperLeft != null && virtualUpperRight != null && virtualLowerRight != null) {
                        reducedBoundingBox = new AdvancedBoundingBox(virtualLowerLeft, virtualUpperLeft, virtualUpperRight, virtualLowerRight);
                    } else {
                        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: if it is not possible
                        reducedBoundingBox = null;
                    }
                } else {
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: if it is not possible
                    reducedBoundingBox = null;
                }
                if (logger.isDebugEnabled() && calculateViewBoundingBoxLogging) {
                    logger.debug("reducded BoundingBox :" + reducedBoundingBox);
                }

            } else {
                if (logger.isWarnEnabled() && calculateViewBoundingBoxLogging) {
                    logger.warn("Reduced BoundingBox could not be calculated");
                }
            }
//            if (logger.isDebugEnabled() && cameraLoggingEnabled) {
//                logger.debug("ll: " + virtualLowerLeft + " ul:" + " lr:" + virtualLowerRight);
//            }
//            if (virtualLowerLeft != null && virtualUpperRight != null) {
//                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: problem this is not right in 3d --> upper left could be more to the left than lower left
//                viewingBoundingBox = new AdvancedBoundingBox(virtualLowerLeft, virtualUpperRight);
//            } else if (virtualLowerLeft != null && virtualLowerRight != null) {
//                final Point3d calculatedUpperRight = new Point3d(virtualLowerRight);
//                calculatedUpperRight.y += viewingDistance * ComponentBroker.getInstance().getScalingFactor();
//                viewingBoundingBox = new AdvancedBoundingBox(virtualLowerLeft, calculatedUpperRight);
//            } else if (virtualLowerLeft != null) {
//                viewingBoundingBox = null;
//            } else if (virtualLowerRight != null) {
//                viewingBoundingBox = null;
//            } else {
//                viewingBoundingBox = null;
//            }
//        } else {
//            viewingBoundingBox = null;
//        }
//        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
//            logger.debug("Viewableboundingbox: " + viewingBoundingBox);
//        }
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: special cases contains. For now they are not valid.

//        BoundingBox bounding = new BoundingBox(virtualLowerLeft,virtualUpperRight);
//        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
//            if (logger.isDebugEnabled() && cameraLoggingEnabled) {
//                logger.debug("bounding: "+bounding);
//            }
        }
    }

    private Point3d pickPoint(final Point2d screenPoint) {
        return pickPoint(new Point((int) screenPoint.x, (int) screenPoint.y));
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
        if (logger.isDebugEnabled() && boundingBoxLogging) {
            logger.debug("Goto BoundingBox: " + boundingBox);
            logger.debug("current Bounding" + getViewBoundingBox());
        }
        if (logger.isDebugEnabled() && resetViewLogging) {
            logger.debug("camera direction: " + getCameraDirection());
            logger.debug("camera position: " + getCameraPosition());
        }
//        setCameraToInitalViewingDirection();
//        if (logger.isDebugEnabled() && boundingBoxLogging) {
//            logger.debug("Goto BoundingBox: " + boundingBox);
//            logger.debug("current Bounding" + getViewBoundingBox());
//        }
//        if (logger.isDebugEnabled() && resetViewLogging) {
//            logger.debug("camera direction: " + getCameraDirection());
//            logger.debug("camera position: " + getCameraPosition());
//        }
        if (boundingBox != null && !boundingBox.isEmpty()) {
//            if (getViewBoundingBox() == null) {
            calculateBoundingBoxes();
//            }
            if (logger.isDebugEnabled() && boundingBoxLogging) {
                logger.debug("old Bounding" + getViewBoundingBox());
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
            if (logger.isDebugEnabled() && boundingBoxLogging) {
                logger.debug("old/new contains : " + currentBoundingBox.containsBoundingBox(boundingBox));
                logger.debug("new/old contains : " + boundingBox.containsBoundingBox(currentBoundingBox));
            }
            final Point2d deltaCenter = currentBoundingBox.getDelta(boundingBox);
            final Point3d center = boundingBox.getCenter();
            if (logger.isDebugEnabled() && boundingBoxLogging) {
                logger.debug("center: " + center + " cameraPosition: " + getCameraPosition());
            }
            center.z = getCameraPosition().z;
            final Point3d oldCenter = viewingBoundingBox.getCenter();

            if (deltaCenter != null) {
                oldCenter.x += deltaCenter.x;
                oldCenter.y += deltaCenter.y;
                oldCenter.z += getCameraPosition().z;
            }
            if (logger.isDebugEnabled() && boundingBoxLogging) {
                logger.debug("old boundingBox: " + currentBoundingBox);
                logger.debug("delta center: " + deltaCenter);
                logger.debug("new center: " + center);
                logger.debug("new center calculated with delta: " + oldCenter);
            }
            if (center != null) {
//
//                logger.debug("delta center: " + deltaCenter + " newCenter: " + center);
//                viewTransformation.setTranslation(new Vector3d(center));
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:not fixed but so that all is seen
//                gotoPoint(center);
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:does not work 100 % coorectly multiple zoom to extends leads to repositioning
//                final double s = Math.min(boundingBox.getWidth() / currentBoundingBox.getWidth(), boundingBox.getHeight() / currentBoundingBox.getHeight());
                if (logger.isDebugEnabled() && boundingBoxLogging) {
                    logger.debug("width/height: old: " + currentBoundingBox.getWidth() + "/" + currentBoundingBox.getHeight() + ", new: " + boundingBox.getWidth() + "/" + boundingBox.getHeight());
//                    logger.debug("scaling factor: " + s);
                }
//                if (s != Double.POSITIVE_INFINITY && s != 0) {
//                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this is a ugly hack
//                    if (center.z < 0.14) {
//                        if (logger.isDebugEnabled() && boundingBoxLogging) {
//                            logger.debug("z to small increasing: ");
//                            center.z = 0.14;
//                        }
//                    }
//                    center.z *= s;
//                }
                final double biggerBoundingSide = Math.max(boundingBox.getWidth(), boundingBox.getHeight());
                double gamma = EarthFlat.radiansToDeegree(canvas.getView().getFieldOfView());
                final double alpha = EarthFlat.deegreeToRadians((180 - gamma) / 2);
                gamma = canvas.getView().getFieldOfView();
                final double a = (biggerBoundingSide / Math.sin(gamma)) * Math.sin(alpha);
                final double h = Math.sqrt(Math.pow(a, 2) - Math.pow(biggerBoundingSide / 2, 2));
                if (logger.isDebugEnabled() && boundingBoxLogging) {
                    logger.debug("c: " + biggerBoundingSide);
                    logger.debug("sinGamma: " + Math.sin(gamma));
                    logger.debug("c/sinGamma: " + (biggerBoundingSide / Math.sin(gamma)));
                    logger.debug("sinApha: " + Math.sin(alpha));
                    logger.debug("gamma: " + gamma);
                    logger.debug("alpha: " + alpha);
                    logger.debug("a: " + a);
                    logger.debug("h: " + h);
                }
                center.z = h;
                if (logger.isDebugEnabled() && boundingBoxLogging) {
                    logger.debug("point: " + center);
                }
                setCameraPosition(center);
            } else {
                if (logger.isDebugEnabled() && boundingBoxLogging) {
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

    protected void updateOrientationVectors() {
        final Transform3D viewTransform = new Transform3D();
        viewingPlatform.getViewPlatformTransform().getTransform(viewTransform);
        logCameraOrientation();
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: own method
        cameraUp = new Vector3d(DEFAULT_UP);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:don't have to be transformed simply based on one vector for example -up is down up rotated by 45 clockwise is right etc
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this should be done in the setCameraDirection
        cameraDown = new Vector3d(DEFAULT_DOWN);
        cameraLeft = new Vector3d(DEFAULT_LEFT);
        cameraRight = new Vector3d(DEFAULT_RIGHT);
        viewTransform.transform(cameraUp);
        viewTransform.transform(cameraDown);
        viewTransform.transform(cameraLeft);
        viewTransform.transform(cameraRight);
        logCameraOrientation();
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:clear concept what controls what does changes in vwp triggers the camera or wise versa
    @Override
    public void setCameraToInitalViewingDirection() {
        if (logger.isDebugEnabled() && resetViewLogging) {
            logger.debug("resetView");
        }
        if (logger.isDebugEnabled() && resetViewLogging) {
            logger.debug("reset camera direction: " + getCameraDirection());
            logger.debug("reset camera position: " + getCameraPosition());
        }
        final Vector3d oldCameraDirection = getCameraDirection();
        final Transform3D oldCameraTransformation = new Transform3D();
        final Vector3d position = new Vector3d();
        viewingPlatform.getViewPlatformTransform().getTransform(oldCameraTransformation);
        oldCameraTransformation.get(position);
        final Transform3D newCameraTransformation = new Transform3D();
        newCameraTransformation.setTranslation(position);
        viewingPlatform.getViewPlatformTransform().setTransform(newCameraTransformation);
        cameraDirectionChanged(oldCameraDirection);
        if (logger.isDebugEnabled() && resetViewLogging) {
            logger.debug("reset camera direction: " + getCameraDirection());
            logger.debug("reset camera position: " + getCameraPosition());
        }
    }

    public AdvancedBoundingBox getReducedBoundingBox() {
        return reducedBoundingBox;
    }
}
