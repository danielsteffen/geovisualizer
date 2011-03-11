/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.camera;

import com.dfki.av.sudplan.vis.control.AdvancedOrbitBehavior;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.universe.Viewer;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
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
public class Camera2D extends SimpleCamera implements CameraListener {

    private final static Logger logger = LoggerFactory.getLogger(Camera2D.class);
    private boolean centerOn3dCamera = false;
    private boolean useReducedBoundingBox = true;
    private boolean keepZoomLevel = true;
    private final TransformGroup cameraObjectTG = new TransformGroup();
    private final Transform3D cameraObjectT = new Transform3D();
    private final TransformGroup cameraViewObjectTG = new TransformGroup();
    private final Transform3D cameraViewObjectT = new Transform3D();
    private final float cameraObjectDim = 10;
    private final float coneLength = cameraObjectDim * 4;

    public Camera2D(Viewer viewer, BranchGroup scene) {
        super(viewer, scene);
//        setUpCameraObjects();
        behavior.setInteractionMode(AdvancedOrbitBehavior.TRANSLATE);
    }

    @Override
    public void cameraMoved(final CameraEvent cameraEvent) {
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("camera moved: old:" + cameraEvent.getOldCameraPosition() + " new: " + cameraEvent.getNewCameraPosition());
            logger.debug("new viewable boundingbox: " + cameraEvent.getCameraViewableBounds());
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:make camera modes;
        if (centerOn3dCamera) {
            final Point3d newPoint = new Point3d(cameraEvent.getNewCameraPosition());
            if (keepZoomLevel && getCameraPosition() != null) {
                newPoint.z = getCameraPosition().z;
            }
            setCameraPosition(newPoint);
        } else if (useReducedBoundingBox) {
            gotoBoundingBox(cameraEvent.getReducedBoundingBox());
        }
    }

    @Override
    public void cameraViewChanged(final CameraEvent cameraEvent) {
        final Vector3d newDirection = cameraEvent.getNewCameraViewDirection();
        final Vector3d oldDirection = cameraEvent.getOldCameraViewDirection();
        newDirection.z = 0.0;
        oldDirection.z = 0.0;
        final Double angleDiff = newDirection.angle(oldDirection);
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("camera direction: old:" + oldDirection + " new: " + newDirection + " angleDiff:" + cameraEvent.getViewAngleDifference());
            if (logger.isDebugEnabled() && cameraLoggingEnabled) {
                logger.debug("angle without z: " + angleDiff);
                logger.debug("angle is NaN: " + (angleDiff == Double.NaN));
            }
        }
        if (!Double.isNaN(angleDiff)) {
//            setCameraDirection(newDirection);
            cameraDirectionChanged(oldDirection);
            final Transform3D tempRotate = new Transform3D();
            tempRotate.rotZ(angleDiff);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:does this let the object jump should this
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this is also a camera update
            centerViewConeToOrigin();
            cameraViewObjectT.mul(tempRotate);
            centerViewConeOnPeak();
        } else {
            if (logger.isErrorEnabled() && cameraLoggingEnabled) {
                logger.error("CAMERA ANGEL NaN.");
            }
        }
    }

    @Override
    public void cameraRegistered(CameraEvent cameraEvent) {
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("camera registered: ");
        }
        setCameraPosition(cameraEvent.getSource().getCameraPosition());
    }

    @Override
    public void cameraUnregistered(CameraEvent cameraEvent) {
        if (logger.isDebugEnabled() && cameraLoggingEnabled) {
            logger.debug("camera registered: " + this.getCameraPosition());
        }

    }

    @Override
    public void setCameraPosition(final Point3d cameraPosition) {
        super.setCameraPosition(cameraPosition);
        updateCameraObjects();
    }

    public boolean isCenterOn3dCamera() {
        return centerOn3dCamera;
    }

    public void setCenterOn3dCamera(final boolean centerOn3dCamera) {
        this.centerOn3dCamera = centerOn3dCamera;
    }

    public boolean isKeepZoomLevel() {
        return keepZoomLevel;
    }

    public void setKeepZoomLevel(boolean keepZoomLevel) {
        this.keepZoomLevel = keepZoomLevel;
    }

    private void setUpCameraObjects() {
        //the camara object
        cameraObjectTG.addChild(new ColorCube(cameraObjectDim));
        cameraObjectTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        getScene().addChild(cameraObjectTG);
        //the camera view object
        final Appearance cameraViewObjectApear = new Appearance();
        final ColoringAttributes cameraViewObjectCA = new ColoringAttributes(new Color3f(0.8f, 0.8f, 0.8f), ColoringAttributes.FASTEST);
        cameraViewObjectApear.setColoringAttributes(cameraViewObjectCA);
        cameraViewObjectTG.addChild(new Cone(cameraObjectDim, coneLength, cameraViewObjectApear));
        cameraViewObjectTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        cameraViewObjectTG.setTransform(cameraViewObjectT);
        cameraObjectTG.addChild(cameraViewObjectTG);
        centerViewConeOnPeak();
    }

    //this overwrites everything;
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:
    private void centerViewConeToOrigin() {
        cameraViewObjectT.setTranslation(new Vector3d(0, 0, 0));
        cameraViewObjectTG.setTransform(cameraViewObjectT);
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this translates (softly)
    private void centerViewConeOnPeak() {
        final Vector3d translate = new Vector3d(0, -coneLength / 2, 0);
        final Transform3D transTransform = new Transform3D();
        transTransform.setTranslation(translate);
        cameraViewObjectT.mul(transTransform);
        cameraViewObjectTG.setTransform(cameraViewObjectT);
    }

    private void updateCameraObjects() {
        if (getCameraPosition() != null) {
            final Vector3d newCameraObjectPosition = new Vector3d(getCameraPosition());
            newCameraObjectPosition.z = 0.0;
            if (logger.isDebugEnabled() && cameraLoggingEnabled) {
                logger.debug("camera translation: " + newCameraObjectPosition);
            }
            cameraObjectT.setTranslation(newCameraObjectPosition);
            cameraObjectTG.setTransform(cameraObjectT);
        }
        if (getCameraPosition() != null) {
            behavior.setRotationCenter(new Point3d(getCameraPosition().x, getCameraPosition().y, 0.0));
        } else {
            behavior.setRotationCenter(new Point3d());
        }
    }
    
}
