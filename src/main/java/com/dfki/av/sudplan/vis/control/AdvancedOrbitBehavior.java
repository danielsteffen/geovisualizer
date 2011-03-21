//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:honor to whom honor is due.
/*
 * $RCSfile: OrbitBehavior.java,v $
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 * $Revision: 1.6 $
 * $Date: 2007/10/08 23:08:02 $
 * $State: Exp $
 */
package com.dfki.av.sudplan.vis.control;

import com.dfki.av.sudplan.camera.Camera;
import com.dfki.av.sudplan.camera.SimpleCamera;
import com.dfki.av.sudplan.camera.TransformationEvent;
import com.dfki.av.sudplan.camera.TransformationListener;
import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.util.EarthFlat;
import java.awt.event.MouseEvent;
import java.awt.AWTEvent;

import javax.media.j3d.Transform3D;
import javax.media.j3d.Canvas3D;

import javax.vecmath.Vector3d;
import javax.vecmath.Point3d;
import javax.vecmath.Matrix3d;

import com.sun.j3d.utils.universe.ViewingPlatform;

import com.sun.j3d.internal.J3dUtilsI18N;
import com.sun.j3d.utils.behaviors.vp.ViewPlatformAWTBehavior;
import com.sun.j3d.utils.pickfast.PickCanvas;
import java.util.ArrayList;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.PickInfo;
import javax.vecmath.Vector2d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Moves the View around a point of interest when the mouse is dragged with
 * a mouse button pressed.  Includes rotation, zoom, and translation
 * actions. Zooming can also be obtained by using mouse wheel.
 * <p>
 * This behavior must be added to the ViewingPlatform
 * using the <code>ViewingPlatform.setViewPlatformBehavior</code> method.
 * <p>
 * The rotate action rotates the ViewPlatform around the point of interest
 * when the mouse is moved with the main mouse button pressed.  The
 * rotation is in the direction of the mouse movement, with a default
 * rotation of 0.01 radians for each pixel of mouse movement.
 * <p>
 * The zoom action moves the ViewPlatform closer to or further from the
 * point of interest when the mouse is moved with the middle mouse button
 * pressed (or Alt-main mouse button on systems without a middle mouse button).
 * The default zoom action is to translate the ViewPlatform 0.01 units for each
 * pixel of mouse movement.  Moving the mouse up moves the ViewPlatform closer,
 * moving the mouse down moves the ViewPlatform further away.
 * <p>
 * By default, the zoom action allows the ViewPlatform to move through
 * the center of rotation to orbit at a negative radius.
 * The <code>STOP_ZOOM</code> constructor flag will stop the ViewPlatform at
 * a minimum radius from the center.  The default minimum radius is 0.0
 * and can be set using the <code>setMinRadius</code> method.
 * <p>
 * The <code>PROPORTIONAL_ZOOM</code> constructor flag changes the zoom action
 * to move the ViewPlatform proportional to its distance from the center
 * of rotation.  For this mode, the default action is to move the ViewPlatform
 * by 1% of its distance from the center of rotation for each pixel of
 * mouse movement.
 * <p>
 * The translate action translates the ViewPlatform when the mouse is moved
 * with the right mouse button pressed (Shift-main mouse button on systems
 * without a right mouse button).  The translation is in the direction of the
 * mouse movement, with a default translation of 0.01 units for each pixel
 * of mouse movement.
 * <p>
 * The sensitivity of the actions can be scaled using the
 * <code>set</code><i>Action</i><code>Factor()</code> methods which scale
 * the default movement by the factor. The rotate and translate actions
 * have separate factors for x and y.
 * <p>
 * The actions can be reversed using the <code>REVERSE_</code><i>ACTION</i>
 * constructor flags.  The default action moves the ViewPlatform around the
 * objects in the scene.  The <code>REVERSE_</code><i>ACTION</i> flags can
 * make the objects in the scene appear to be moving in the direction
 * of the mouse movement.
 * <p>
 * The actions can be disabled by either using the
 * <code>DISABLE_</code><i>ACTION</i> constructor flags or the
 * <code>set</code><i>Action</i><code>Enable</code> methods.
 * <p>
 * The default center of rotation is (0, 0, 0) and can be set using the
 * <code>setRotationCenter()</code> method.
 *
 * @since Java 3D 1.2.1
 */
public class AdvancedOrbitBehavior extends ViewPlatformAWTBehavior {

    private final Logger logger = LoggerFactory.getLogger(AdvancedOrbitBehavior.class);
    private Transform3D longditudeTransform = new Transform3D();
    private Transform3D latitudeTransform = new Transform3D();
    private Transform3D rotateTransform = new Transform3D();
    // needed for integrateTransforms but don't want to new every time
    private Transform3D temp1 = new Transform3D();
    private Transform3D temp2 = new Transform3D();
    private Transform3D translation = new Transform3D();
    private Vector3d transVector = new Vector3d();
    private Vector3d distanceVector = new Vector3d();
    private Vector3d centerVector = new Vector3d();
    private Vector3d invertCenterVector = new Vector3d();
    private double longditude = 0.0;
    private double latitude = 0.0;
    private double startDistanceFromCenter = 20.0;
    private double distanceFromCenter = 20.0;
    private Point3d rotationCenter = new Point3d();
    private Matrix3d rotMatrix = new Matrix3d();
    private Transform3D currentXfm = new Transform3D();
    private int mouseX = 0;
    private int mouseY = 0;
    private double rotXFactor = 1.0;
    private double rotYFactor = 1.0;
    private double transXFactor = 1.0;
    private double transYFactor = 1.0;
    private double zoomFactor = 1.0;
    private double xtrans = 0.0;
    private double ytrans = 0.0;
    private double ztrans = 0.0;
    private boolean zoomEnabled = true;
    private boolean rotateEnabled = true;
    private boolean translateEnabled = true;
    private boolean reverseRotate = false;
    private boolean reverseTrans = false;
    private boolean reverseZoom = false;
    private boolean alwaysZoom = true;
    private boolean stopZoom = false;
    private boolean proportionalZoom = false;
    private boolean proportionalTranslate = false;
    private double minRadius = 0.0;
    private int leftButton = TRANSLATE;
    private int rightButton = ROTATE;
    private int middleButton = ZOOM;
    private int actualInteractionMode = COMBINED;
    // the factor to be applied to wheel zooming so that it does not
    // look much different with mouse movement zooming.
    // This is a totally subjective factor.
    private float wheelZoomFactor = 50.0f;
    /**
     * Constructor flag to reverse the rotate behavior
     */
    public static final int REVERSE_ROTATE = 0x010;
    /**
     * Constructor flag to reverse the translate behavior
     */
    public static final int REVERSE_TRANSLATE = 0x020;
    /**
     * Constructor flag to reverse the zoom behavior
     */
    public static final int REVERSE_ZOOM = 0x040;
    /**
     * Constructor flag to reverse all the behaviors
     */
    public static final int REVERSE_ALL = (REVERSE_ROTATE | REVERSE_TRANSLATE
            | REVERSE_ZOOM);
    /**
     * Constructor flag that indicates zoom should stop when it reaches
     * the minimum orbit radius set by setMinRadius().  The minimus
     * radius default is 0.0.
     */
    public static final int STOP_ZOOM = 0x100;
    /**
     * Constructor flag to disable rotate
     */
    public static final int DISABLE_ROTATE = 0x200;
    /**
     * Constructor flag to disable translate
     */
    public static final int DISABLE_TRANSLATE = 0x400;
    /**
     * Constructor flag to disable zoom
     */
    public static final int DISABLE_ZOOM = 0x800;
    /**
     * Constructor flag to use proportional zoom, which determines
     * how much you zoom based on view's distance from the center of
     * rotation.  The percentage of distance that the viewer zooms
     * is determined by the zoom factor.
     */
    public static final int PROPORTIONAL_ZOOM = 0x1200;
    public static final int PROPORTIONAL_TRANSLATE = 0x1000;
    /**
     * Used to set the fuction for a mouse button to Rotate
     */
    public static final int ROTATE = 0;
    public boolean switchRotateButton = true;
    /**
     * Used to set the function for a mouse button to Translate
     */
    public static final int TRANSLATE = 1;
    /**
     * Used to set the function for a mouse button to Zoom
     */
    public static final int ZOOM = 2;
    /**
     * Used to set the function for a mouse button to Combined mode
     */
    public static final int COMBINED = 3;
    private static final double NOMINAL_ZOOM_FACTOR = .01;
    private static final double NOMINAL_PZOOM_FACTOR = 1.0;
    private static final double NOMINAL_ROT_FACTOR = .01;
    public static double NOMINAL_TRANS_FACTOR = .01;
    private static final double NOMINAL_PTRANS_FACTOR = 1.0;
    private double rotXMul = NOMINAL_ROT_FACTOR * rotXFactor;
    private double rotYMul = NOMINAL_ROT_FACTOR * rotYFactor;
    private double transXMul = NOMINAL_TRANS_FACTOR * transXFactor;
    private double transYMul = NOMINAL_TRANS_FACTOR * transYFactor;
    private double zoomMul = NOMINAL_ZOOM_FACTOR * zoomFactor;
    private final BranchGroup scene;
    private final PickCanvas pickCanvas;
    private final ArrayList<TransformationListener> transformationListeners = new ArrayList<TransformationListener>();
    private boolean transformationLoggingEnabled = false;
    private final Camera camera;
//    private boolean newValidMouseEvent = false;

    public void addTransformationListener(final TransformationListener newListener) {
        if (!transformationListeners.contains(newListener)) {
            transformationListeners.add(newListener);
        }
    }

    public void removeTransformationListener(final TransformationListener listenerToRemove) {
        if (transformationListeners.contains(listenerToRemove)) {
            transformationListeners.remove(listenerToRemove);
        }
    }
//    /**
//     * Parameterless constructor for this behavior.  This is intended for use
//     * by ConfiguredUniverse, which requires such a constructor for
//     * configurable behaviors.  The Canvas3D used to listen for mouse and
//     * mouse motion events is obtained from the superclass
//     * setViewingPlatform() method.
//     * @since Java 3D 1.3
//     */
//    public AdvancedOrbitBehavior() {
//	super(MOUSE_LISTENER | MOUSE_MOTION_LISTENER | MOUSE_WHEEL_LISTENER);
//    }
//
//    /**
//     * Creates a new OrbitBehavior
//     *
//     * @param c The Canvas3D to add the behavior to
//     */
//    public AdvancedOrbitBehavior(Canvas3D c) {
//	this(c, 0 );
//    }

    /**
     * Creates a new OrbitBehavior
     *
     * @param c The Canvas3D to add the behavior to
     * @param flags The option flags
     */
    public AdvancedOrbitBehavior(Canvas3D c, BranchGroup scene, Camera camera, int flags) {
        super(c, MOUSE_LISTENER | MOUSE_MOTION_LISTENER | MOUSE_WHEEL_LISTENER | flags);
        this.scene = scene;
        this.camera = camera;
        pickCanvas = new PickCanvas(canvases[0], scene);
        pickCanvas.setMode(PickInfo.PICK_GEOMETRY);
        pickCanvas.setFlags(PickInfo.CLOSEST_INTERSECTION_POINT);

        if ((flags & DISABLE_ROTATE) != 0) {
            rotateEnabled = false;
        }
        if ((flags & DISABLE_ZOOM) != 0) {
            zoomEnabled = false;
        }
        if ((flags & DISABLE_TRANSLATE) != 0) {
            translateEnabled = false;
        }
        if ((flags & REVERSE_TRANSLATE) != 0) {
            reverseTrans = true;
        }
        if ((flags & REVERSE_ROTATE) != 0) {
            reverseRotate = true;
        }
        if ((flags & REVERSE_ZOOM) != 0) {
            reverseZoom = true;
        }
        if ((flags & STOP_ZOOM) != 0) {
            stopZoom = true;
        }
        if ((flags & PROPORTIONAL_ZOOM) != 0) {
            proportionalZoom = true;
            zoomMul = NOMINAL_PZOOM_FACTOR * zoomFactor;
        }
        if ((flags & PROPORTIONAL_TRANSLATE) != 0) {
            proportionalTranslate = true;
            transYMul = NOMINAL_PTRANS_FACTOR * transYFactor;
            transXMul = NOMINAL_PTRANS_FACTOR * transXFactor;
        }
    }

    protected synchronized void processAWTEvents(final AWTEvent[] events) {
//        if (logger.isDebugEnabled()) {
//            logger.debug("process AWT Event");
//        }
        motion = false;
        for (int i = 0; i < events.length; i++) {
            if (events[i] instanceof MouseEvent) {
                detRotationCenter((MouseEvent) events[i]);
                processMouseEvent((MouseEvent) events[i]);
            }
        }
    }

    private void detRotationCenter(final MouseEvent e) {
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:duplicated code
//        if (logger.isDebugEnabled()) {
//            final int mouseX = e.getX();
//            final int mouseY = e.getY();
//            final Point3d mouse_pos = new Point3d();
//            canvases[0].getPixelLocationInImagePlate(mouseX, mouseY, mouse_pos);
//            Transform3D motionToWorld = new Transform3D();
//            canvases[0].getImagePlateToVworld(motionToWorld);
//            motionToWorld.transform(mouse_pos);
//            pickCanvas.setShapeLocation(e);
//            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:pick any does not work why ?
//            PickInfo result = null;
//            final PickInfo[] results = pickCanvas.pickAll();
//            Point3d pickPoint = null;
//            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:Problems with DEM
//            if (results != null) {
//                result = results[results.length - 1];
//                pickPoint = result.getClosestIntersectionPoint();
//            }
//            if (MouseEvent.MOUSE_CLICKED == e.getID()) {
//                logger.debug("Mouse Clicked: real: " + mouseX + "/" + mouseY + " world: " + mouse_pos.x + "/" + mouse_pos.y + " picked: " + (pickPoint != null ? pickPoint.x + "/" + pickPoint.y : "null"));
//            }
//        }

        if (MouseEvent.MOUSE_PRESSED == e.getID() || MouseEvent.MOUSE_RELEASED == e.getID() || (e.getButton() == MouseEvent.BUTTON1 && MouseEvent.MOUSE_DRAGGED == e.getID() || MouseEvent.MOUSE_WHEEL == e.getID())) {
//            newValidMouseEvent = true;
            if (logger.isDebugEnabled() && transformationLoggingEnabled) {
                logger.debug("mouse_event: id:" + e.getID() + " button: " + e.getButton());
            }
            final int mouseX = e.getX();
            final int mouseY = e.getY();
//            final Point3d eye_pos = new Point3d();
            final Point3d mouse_pos = new Point3d();
//            final Point3d center = new Point3d();
            canvases[0].getPixelLocationInImagePlate(mouseX, mouseY, mouse_pos);
//            canvases[0].getCenterEyeInImagePlate(eye_pos);
//            canvases[0].getPixelLocationInImagePlate(canvases[0].getWidth()/2,canvases[0].getHeight()/2, center);
//            if (logger.isDebugEnabled()) {
//                logger.debug("center: "+center);
//                logger.debug("mouse: "+new Point(mouseX,mouseY));
//            }
            Transform3D motionToWorld = new Transform3D();
            canvases[0].getImagePlateToVworld(motionToWorld);
            motionToWorld.transform(mouse_pos);
//            motionToWorld = new Transform3D();
//            canvases[0].getImagePlateToVworld(motionToWorld);
//            motionToWorld.transform(eye_pos);
//            motionToWorld = new Transform3D();
//            canvases[0].getImagePlateToVworld(motionToWorld);
//            motionToWorld.transform(center);
            mouse_pos.z = 0.0f;
//            eye_pos.z = 0.0f;
//            center.z = 0.0f;
//            if (logger.isDebugEnabled()) {
//                logger.debug("mouse_point: " + mouse_pos);
//                logger.debug("eye_pos: " + eye_pos);
//                logger.debug("center: "+center);
//            }
//            final PickTool pickTool = new PickTool(scene);


//            final Point3d eye_pos = new Point3d();
//            canvases[0].getCenterEyeInImagePlate(eye_pos);
//            motionToWorld.transform(eye_pos);
//            mouse_pos.sub(eye_pos);
//            final Vector3d direction = new Vector3d();
//            direction.sub(mouse_pos, eye_pos);
//            direction.normalize();
//            if (logger.isDebugEnabled()) {
//                logger.debug("eye_pos: "+eye_pos);
//                logger.debug("direction: "+direction);
//            }
//            final Vector3d mouseVector = new Vector3d(mouse_pos);
//            final Vector3d eyeVector = new Vector3d(eye_pos);
//            mouseVector.scale(eyeVector.length() / mouseVector.length());
//            eyeVector.add(mouseVector);
            pickCanvas.setShapeLocation(e);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:pick any does not work why ?
            PickInfo result = null;
            final PickInfo[] results = pickCanvas.pickAll();
            Point3d pickPoint = null;
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:Problems with DEM
            if (results != null) {
                result = results[results.length - 1];
                pickPoint = result.getClosestIntersectionPoint();
            }
//            if (logger.isDebugEnabled()) {
//                logger.debug("mouse_point: "+mouse_pos);
//                logger.debug("eye_point: "+eye_pos);
//                logger.debug("result: "+result);
//                logger.debug("pick_point: "+result.getClosestIntersectionPoint());
//                logger.debug("eye vector: "+eyeVector);
////                for (PickInfo pickInfo : results) {
////                    logger.debug("pickinfo: "+pickInfo.getClosestIntersectionPoint());
////                }
//            }
            if (pickPoint != null) {
                pickPoint.z = 0.0;
                setRotationCenter(result.getClosestIntersectionPoint());
            } else {
                mouse_pos.z = 0.0;
                setRotationCenter(mouse_pos);
            }
            if (logger.isDebugEnabled() && transformationLoggingEnabled) {
                logger.debug("Mouse position: real: " + mouseX + "/" + mouseY + " world: " + mouse_pos.x + "/" + mouse_pos.y + " picked: " + (pickPoint != null ? pickPoint.x + "/" + pickPoint.y : "null"));
            }
        } //else {
//            newValidMouseEvent = false;
//        }
    }

    protected void processMouseEvent(final MouseEvent evt) {
        if (evt.getID() == MouseEvent.MOUSE_PRESSED) {
            mouseX = evt.getX();
            mouseY = evt.getY();
            motion = true;
        } else if (evt.getID() == MouseEvent.MOUSE_DRAGGED) {
            int xchange = evt.getX() - mouseX;
            int ychange = evt.getY() - mouseY;
            // rotate
            if (rotate(evt)) {
                if (reverseRotate) {
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("long: "+longditude+" lat: "+latitude);
//                    }
                    longditude -= xchange * rotXMul;
                    latitude -= ychange * rotYMul;
                } else {
                    longditude += xchange * rotXMul;
                    latitude += ychange * rotYMul;
                }
            } // translate
            else if (translate(evt)) {
                doTranslateOperations(xchange, ychange);
            } // zoom
            else if (zoom(evt)) {
                doZoomOperations(ychange);
            }
            mouseX = evt.getX();
            mouseY = evt.getY();
            motion = true;
        } else if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
        } else if (evt.getID() == MouseEvent.MOUSE_WHEEL) {
            if (zoom(evt)) {
                // if zooming is done through mouse wheel,
                // the amount of increments the wheel changed,
                // multiplied with wheelZoomFactor is used,
                // so that zooming speed looks natural compared to mouse movement zoom.
                if (evt instanceof java.awt.event.MouseWheelEvent) {
                    // I/O differenciation is made between
                    // java.awt.event.MouseWheelEvent.WHEEL_UNIT_SCROLL or
                    // java.awt.event.MouseWheelEvent.WHEEL_BLOCK_SCROLL so
                    // that behavior remains stable and not dependent on OS settings.
                    // If getWheelRotation() was used for calculating the zoom,
                    // the zooming speed could act differently on different platforms,
                    // if, for example, the user sets his mouse wheel to jump 10 lines
                    // or a block.
                    int zoom =
                            ((int) (((java.awt.event.MouseWheelEvent) evt).getWheelRotation()
                            * wheelZoomFactor));
                    doZoomOperations(zoom);
                    motion = true;
                }
            }
        }
    }

    /*extraction of the zoom algorithms so that there is no code duplication or source 'uglyfication'.
     */
    private void doZoomOperations(int ychange) {
        if (proportionalZoom) {
            if (reverseZoom) {
                if ((distanceFromCenter
                        - (zoomMul * ychange * distanceFromCenter / 100.0))
                        > minRadius) {
                    distanceFromCenter -= (zoomMul * ychange
                            * distanceFromCenter / 100.0);
                } else {
                    distanceFromCenter = minRadius;
                }
            } else {
                if ((distanceFromCenter
                        + (zoomMul * ychange * distanceFromCenter / 100.0))
                        > minRadius) {
                    distanceFromCenter += (zoomMul * ychange
                            * distanceFromCenter / 100.0);
                } else {
                    distanceFromCenter = minRadius;
                }
            }
        } else {
            if (stopZoom) {
                if (reverseZoom) {
                    if ((distanceFromCenter - ychange * zoomMul) > minRadius) {
                        distanceFromCenter -= ychange * zoomMul;
                    } else {
                        distanceFromCenter = minRadius;
                    }
                } else {
                    if ((distanceFromCenter + ychange * zoomMul) > minRadius) {
                        distanceFromCenter += ychange * zoomMul;
                    } else {
                        distanceFromCenter = minRadius;
                    }
                }
            } else {
                if (reverseZoom) {
                    distanceFromCenter -= ychange * zoomMul;
                } else {
                    distanceFromCenter += ychange * zoomMul;
                }
            }
        }
    }

    protected void doTranslateOperations(final int xchange, final int ychange) {
        if (isProportionalTranslate()) {
            if (reverseTrans) {
                xtrans -= xchange * transXMul * distanceFromCenter / 100.0;
                ytrans += ychange * transYMul * distanceFromCenter / 100.0;
            } else {
                xtrans += xchange * transXMul * distanceFromCenter / 100.0;
                ytrans -= ychange * transYMul * distanceFromCenter / 100.0;
            }
        } else {
            if (reverseTrans) {
                xtrans -= xchange * transXMul;
                ytrans += ychange * transYMul;
            } else {
                xtrans += xchange * transXMul;
                ytrans -= ychange * transYMul;
            }
        }
    }

    /**
     * Sets the ViewingPlatform for this behavior.  This method is
     * called by the ViewingPlatform.
     * If a sub-calls overrides this method, it must call
     * super.setViewingPlatform(vp).
     * NOTE: Applications should <i>not</i> call this method.
     */
    @Override
    public void setViewingPlatform(ViewingPlatform vp) {
        super.setViewingPlatform(vp);

        if (vp != null) {
            resetView();
            integrateTransforms();
        }
    }

    /**
     * Reset the orientation and distance of this behavior to the current
     * values in the ViewPlatform Transform Group
     */
    private void resetView() {
        Vector3d centerToView = new Vector3d();

        targetTG.getTransform(targetTransform);

        targetTransform.get(rotMatrix, transVector);
        centerToView.sub(transVector, rotationCenter);
        distanceFromCenter = centerToView.length();
        startDistanceFromCenter = distanceFromCenter;

        targetTransform.get(rotMatrix);
        rotateTransform.set(rotMatrix);

        // compute the initial x/y/z offset
        temp1.set(centerToView);
        rotateTransform.invert();
        rotateTransform.mul(temp1);
        rotateTransform.get(centerToView);
        xtrans = centerToView.x;
        ytrans = centerToView.y;
        ztrans = centerToView.z;

        // reset rotMatrix
        rotateTransform.set(rotMatrix);
    }

    @Override
    protected synchronized void integrateTransforms() {
//        // Check if the transform has been changed by another
//        // behavior
        latitudeTransform.rotX(latitude);
        longditudeTransform.rotY(longditude);

        integrateTransforms(latitudeTransform);
        integrateTransforms(longditudeTransform);
//        if (logger.isDebugEnabled()) {
//            final Matrix4d temp = new Matrix4d();
//            longditudeTransform.get(temp);
//            logger.debug("long transformation: "+temp);
//        }
//        rotateTransform.mul(rotateTransform, latitudeTransform);
//        rotateTransform.mul(rotateTransform, longditudeTransform);


    }

//    private static enum INTEGRATION_TYPE {Latitude,Longitude};
    protected void integrateTransforms(Transform3D rotation) {
//        if (logger.isDebugEnabled()) {
//            logger.debug("integrate transform: ");
//        }
        if (logger.isDebugEnabled() && transformationLoggingEnabled) {
            logger.debug("distance from Center: " + distanceFromCenter);
            logger.debug("start Distance: " + startDistanceFromCenter);
        }
        targetTG.getTransform(currentXfm);
        if (!targetTransform.equals(currentXfm)) {
            resetView();
        }
//        latitudeTransform.rotX(latitude);
//        longditudeTransform.rotY(longditude);
//        rotateTransform.mul(rotateTransform, latitudeTransform);
//        rotateTransform.mul(rotateTransform, longditudeTransform);
        rotateTransform.mul(rotateTransform, rotation);
        distanceVector.z = distanceFromCenter - startDistanceFromCenter;

        temp1.set(distanceVector);
        temp1.mul(rotateTransform, temp1);

        // want to look at rotationCenter
        transVector.x = rotationCenter.x + xtrans;
        transVector.y = rotationCenter.y + ytrans;
        transVector.z = rotationCenter.z + ztrans;

        translation.set(transVector);
        targetTransform.mul(temp1, translation);

        // handle rotationCenter
        temp1.set(centerVector);
        temp1.mul(targetTransform);

        invertCenterVector.x = -centerVector.x;
        invertCenterVector.y = -centerVector.y;
        invertCenterVector.z = -centerVector.z;

        temp2.set(invertCenterVector);
        targetTransform.mul(temp1, temp2);
//        if (logger.isDebugEnabled()) {
//            logger.debug("after transformation");
//        }
        final Point3d oldPosition = new Point3d();
        final Point3d newPosition = new Point3d();
        final Vector3d oldViewDirection = new Vector3d(SimpleCamera.DEFAULT_VIEW);
        final Vector3d newViewDirection = new Vector3d(SimpleCamera.DEFAULT_VIEW);
        currentXfm.transform(oldPosition);
        currentXfm.transform(oldViewDirection);
        oldViewDirection.normalize();
        targetTransform.transform(newPosition);
        targetTransform.transform(newViewDirection);
        newViewDirection.normalize();
//        if (logger.isDebugEnabled()) {
//            logger.debug("after event variables");
//        }
        if (logger.isDebugEnabled() && transformationLoggingEnabled) {
            logger.debug("oldPosition: " + oldPosition + " newPosition: " + newPosition);
        }
        if (logger.isDebugEnabled() && transformationLoggingEnabled) {
            logger.debug("oldView: " + oldViewDirection + " newView: " + newViewDirection);
        }
        if (!viewTransCheck(newPosition, oldPosition)) {
            return;
        }
        if (logger.isDebugEnabled() && transformationLoggingEnabled) {
            logger.debug("oldPosition: " + oldPosition + " newPosition: " + newPosition);
        }
        final Vector3d oldTranslation = new Vector3d();
        currentXfm.get(oldTranslation);

//        try {
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: here is the failure
//            final Vector3d oldUpVector = new Vector3d(
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: Singelton is in not standalone not initialized. Must be initialized in not stand alone
//                    ComponentBroker.getInstance().getController().getVisualisationComponent().get3dCamera().getCameraUp());
//            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:is already calculated
//            final Vector3d oldViewVector = new Vector3d(
//                    ComponentBroker.getInstance().getController().getVisualisationComponent().get3dCamera().getCameraDirection());
//            final Vector3d newUpVector = new Vector3d(SimpleCamera.DEFAULT_UP);
//            targetTransform.transform(newUpVector);
//            oldUpVector.z = 0.0;
//            oldViewVector.z = 0.0;
//            final double deegres = EarthFlat.radiansToDeegree(oldUpVector.angle(oldViewVector));
//            Transform3D zCorrection = null;
//        } catch (Throwable th) {
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:npe is catched only
//            if (logger.isErrorEnabled()) {
//                logger.error("Fehler: ", th);
//            }
//        }
//        if (logger.isDebugEnabled() && !Double.isNaN(deegres) && deegres != 0.0) {
////            logger.debug("deegree difference: " + deegres);
////            logger.debug("OldUp: " + oldUpVector);
////            logger.debug("newUp: " + newUpVector);
////            logger.debug("oldView: " + oldViewVector);
////            logger.debug("correction: " + EarthFlat.radiansToDeegree(newUpVector.angle(oldUpVector)));
//            zCorrection = new Transform3D();
//            zCorrection.ro
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("transform integrated");
//        }
        targetTG.setTransform(targetTransform);

        // reset yaw and pitch angles
        longditude = 0.0;
        latitude = 0.0;

        for (TransformationListener currentListener : transformationListeners) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("transformation"+rotateTransform);
//            }
            final Transform3D rotationPart = new Transform3D(targetTransform);
            currentListener.rotated(new TransformationEvent(oldViewDirection, newViewDirection));
        }

        for (TransformationListener currentListener : transformationListeners) {

            final Vector3d newTranslation = new Vector3d();

            targetTransform.get(newTranslation);
//            if (logger.isDebugEnabled()) {
//                logger.debug("behaviour: old: "+new Point3d(oldTranslation)+" new:"+new Point3d(transVector));
//            }
            if (!oldPosition.equals(newPosition)) {
                if (logger.isDebugEnabled() && transformationLoggingEnabled) {
                    logger.debug("oldPosition: " + oldPosition + " newPosition: " + newPosition);
                }
                currentListener.translated(new TransformationEvent(new Point3d(oldPosition), new Point3d(newPosition)));
            }
        }
    }

    private boolean viewTransCheck(final Point3d newPosition, final Point3d oldPosition) {

        final Vector3d newCameraUp = new Vector3d(Camera.DEFAULT_UP);
        targetTransform.transform(newCameraUp);

        if (newPosition.z < 0.0) {
            if (logger.isDebugEnabled() && transformationLoggingEnabled) {
                logger.debug("View transformation not allowed z would be negative: ");
            }
            return false;
        }

        //Europe camera pos (1666.4799082660177, 6114.203458541641, 6949.083219005746)        

        if (newPosition.x < -2000 || newPosition.x > 6000) {
            if (logger.isDebugEnabled() && transformationLoggingEnabled) {
                logger.debug("View transformation not allowed, x not in view constraint ");
            }
            return false;
        }

        if (newPosition.y < 3500 || newPosition.y > 8700) {
            if (logger.isDebugEnabled() && transformationLoggingEnabled) {
                logger.debug("View transformation not allowed, y not in view constraint ");
            }
            return false;
        }

        if ((newPosition.z > 8000)) {
            if (logger.isDebugEnabled() && transformationLoggingEnabled) {
                logger.debug("View transformation not allowed, z would greater too great: ");
            }
            return false;
        }

        final double upVectorDiff = Math.floor(EarthFlat.EARTH_UP.angle(newCameraUp) * 100) / 100;
        if (logger.isDebugEnabled() && transformationLoggingEnabled) {
            logger.debug("upVectorDiff: " + upVectorDiff);
        }

        if ((upVectorDiff > (Math.PI / 2) || upVectorDiff < 0.0)) {
            if (logger.isDebugEnabled() && transformationLoggingEnabled) {
                logger.debug("View transformation not allowed angle between camera and earth must be between 0...PI/2: " + upVectorDiff);
            }
            return false;
        }

        return true;
    }

    /**
     * Sets the center around which the View rotates.
     * The default is (0,0,0).
     * @param center The Point3d to set the center of rotation to
     */
    public synchronized void setRotationCenter(Point3d center) {
        Point3d centerDelta = new Point3d();
        centerDelta.sub(centerVector, center);
        Transform3D invRot = new Transform3D(rotateTransform);
        invRot.invert();
        invRot.transform(centerDelta);
        xtrans += centerDelta.x;
        ytrans += centerDelta.y;
        ztrans += centerDelta.z;
        rotationCenter.x = center.x;
        rotationCenter.y = center.y;
        rotationCenter.z = center.z;
        centerVector.set(rotationCenter);
    }

    /**
     * Property which sets the center around which the View rotates.
     * Used by ConfiguredUniverse.
     * @param center array of length 1 containing an instance of Point3d
     * @since Java 3D 1.3
     */
    public void RotationCenter(Object[] center) {
        if (!(center.length == 1 && center[0] instanceof Point3d)) {
            throw new IllegalArgumentException("RotationCenter must be a single Point3d");
        }

        setRotationCenter((Point3d) center[0]);
    }

    /**
     * Places the value of the center around which the View rotates
     * into the Point3d.
     * @param center The Point3d
     */
    public void getRotationCenter(Point3d center) {
        center.x = rotationCenter.x;
        center.y = rotationCenter.y;
        center.z = rotationCenter.z;
    }

    // TODO
    // Need to add key factors for Rotate, Translate and Zoom
    // Method calls should just update MAX_KEY_ANGLE, KEY_TRANSLATE and
    // KEY_ZOOM
    //
    // Methods also need to correctly set sign of variables depending on
    // the Reverse settings.
    /**
     * Sets the rotation x and y factors.  The factors are used to determine
     * how many radians to rotate the view for each pixel of mouse movement.
     * The view is rotated factor * 0.01 radians for each pixel of mouse
     * movement.  The default factor is 1.0.
     * @param xfactor The x movement multiplier
     * @param yfactor The y movement multiplier
     **/
    public synchronized void setRotFactors(double xfactor, double yfactor) {
        rotXFactor = xfactor;
        rotYFactor = yfactor;
        rotXMul = NOMINAL_ROT_FACTOR * xfactor;
        rotYMul = NOMINAL_ROT_FACTOR * yfactor;
    }

    /**
     * Property which sets the rotation x and y factors.
     * Used by ConfiguredUniverse.
     * @param factors array of length 2 containing instances of Double
     * @since Java 3D 1.3
     */
    public void RotFactors(Object[] factors) {
        if (!(factors.length == 2
                && factors[0] instanceof Double && factors[1] instanceof Double)) {
            throw new IllegalArgumentException("RotFactors must be two Doubles");
        }

        setRotFactors(((Double) factors[0]).doubleValue(),
                ((Double) factors[1]).doubleValue());
    }

    /**
     * Sets the rotation x factor.  The factors are used to determine
     * how many radians to rotate the view for each pixel of mouse movement.
     * The view is rotated factor * 0.01 radians for each pixel of mouse
     * movement.  The default factor is 1.0.
     * @param xfactor The x movement multiplier
     **/
    public synchronized void setRotXFactor(double xfactor) {
        rotXFactor = xfactor;
        rotXMul = NOMINAL_ROT_FACTOR * xfactor;
    }

    /**
     * Property which sets the rotation x factor.
     * Used by ConfiguredUniverse.
     * @param xFactor array of length 1 containing instance of Double
     * @since Java 3D 1.3
     */
    public void RotXFactor(Object[] xFactor) {
        if (!(xFactor.length == 1 && xFactor[0] instanceof Double)) {
            throw new IllegalArgumentException("RotXFactor must be a Double");
        }

        setRotXFactor(((Double) xFactor[0]).doubleValue());
    }

    /**
     * Sets the rotation y factor.  The factors are used to determine
     * how many radians to rotate the view for each pixel of mouse movement.
     * The view is rotated factor * 0.01 radians for each pixel of mouse
     * movement.  The default factor is 1.0.
     * @param yfactor The y movement multiplier
     **/
    public synchronized void setRotYFactor(double yfactor) {
        rotYFactor = yfactor;
        rotYMul = NOMINAL_ROT_FACTOR * yfactor;
    }

    /**
     * Property which sets the rotation y factor.
     * Used by ConfiguredUniverse.
     * @param yFactor array of length 1 containing instance of Double
     * @since Java 3D 1.3
     */
    public void RotYFactor(Object[] yFactor) {
        if (!(yFactor.length == 1 && yFactor[0] instanceof Double)) {
            throw new IllegalArgumentException("RotYFactor must be a Double");
        }

        setRotYFactor(((Double) yFactor[0]).doubleValue());
    }

    /**
     * Sets the translation x and y factors.  The factors are used to determine
     * how many units to translate the view for each pixel of mouse movement.
     * The view is translated factor * 0.01 units for each pixel of mouse
     * movement.  The default factor is 1.0.
     * @param xfactor The x movement multiplier
     * @param yfactor The y movement multiplier
     **/
    public synchronized void setTransFactors(double xfactor,
            double yfactor) {
        transXFactor = xfactor;
        transYFactor = yfactor;
        transXMul = NOMINAL_TRANS_FACTOR * xfactor;
        transYMul = NOMINAL_TRANS_FACTOR * yfactor;
    }

    /**
     * Property which sets the translation x and y factors.
     * Used by ConfiguredUniverse.
     * @param factors array of length 2 containing instances of Double
     * @since Java 3D 1.3
     */
    public void TransFactors(Object[] factors) {
        if (!(factors.length == 2
                && factors[0] instanceof Double && factors[1] instanceof Double)) {
            throw new IllegalArgumentException("TransFactors must be two Doubles");
        }

        setTransFactors(((Double) factors[0]).doubleValue(),
                ((Double) factors[1]).doubleValue());
    }

    /**
     * Sets the translation x factor.  The factors are used to determine
     * how many units to translate the view for each pixel of mouse movement.
     * The view is translated factor * 0.01 units for each pixel of mouse
     * movement.  The default factor is 1.0.
     * @param xfactor The x movement multiplier
     **/
    public synchronized void setTransXFactor(double xfactor) {
        transXFactor = xfactor;
        transXMul = NOMINAL_TRANS_FACTOR * xfactor;
    }

    /**
     * Property which sets the translation x factor.
     * Used by ConfiguredUniverse.
     * @param xFactor array of length 1 containing instance of Double
     * @since Java 3D 1.3
     */
    public void TransXFactor(Object[] xFactor) {
        if (!(xFactor.length == 1 && xFactor[0] instanceof Double)) {
            throw new IllegalArgumentException("TransXFactor must be a Double");
        }

        setTransXFactor(((Double) xFactor[0]).doubleValue());
    }

    /**
     * Sets the translation y factor.  The factors are used to determine
     * how many units to translate the view for each pixel of mouse movement.
     * The view is translated factor * 0.01 units for each pixel of mouse
     * movement.  The default factor is 1.0.
     * @param yfactor The y movement multiplier
     **/
    public synchronized void setTransYFactor(double yfactor) {
        transYFactor = yfactor;
        transYMul = NOMINAL_TRANS_FACTOR * yfactor;
    }

    /**
     * Property which sets the translation y factor.
     * Used by ConfiguredUniverse.
     * @param yFactor array of length 1 containing instance of Double
     * @since Java 3D 1.3
     */
    public void TransYFactor(Object[] yFactor) {
        if (!(yFactor.length == 1 && yFactor[0] instanceof Double)) {
            throw new IllegalArgumentException("TransYFactor must be a Double");
        }

        setTransYFactor(((Double) yFactor[0]).doubleValue());
    }

    /**
     * Sets the zoom factor.  The factor is used to determine how many
     * units to zoom the view for each pixel of mouse movement.
     * The view is zoomed factor * 0.01 units for each pixel of mouse
     * movement.  For proportional zoom, the view is zoomed factor * 1%
     * of the distance from the center of rotation for each pixel of
     * mouse movement.  The default factor is 1.0.
     * @param zfactor The movement multiplier
     */
    public synchronized void setZoomFactor(double zfactor) {
        zoomFactor = zfactor;
        if (proportionalZoom) {
            zoomMul = NOMINAL_PZOOM_FACTOR * zfactor;
        } else {
            zoomMul = NOMINAL_ZOOM_FACTOR * zfactor;
        }
    }

    /**
     * Property which sets the zoom factor.
     * Used by ConfiguredUniverse.
     * @param zFactor array of length 1 containing instance of Double
     * @since Java 3D 1.3
     */
    public void ZoomFactor(Object[] zFactor) {
        if (!(zFactor.length == 1 && zFactor[0] instanceof Double)) {
            throw new IllegalArgumentException("ZoomFactor must be a Double");
        }

        setZoomFactor(((Double) zFactor[0]).doubleValue());
    }

    /**
     * Returns the x rotation movement multiplier
     * @return The movement multiplier for x rotation
     */
    public double getRotXFactor() {
        return rotXFactor;
    }

    /**
     * Returns the y rotation movement multiplier
     * @return The movement multiplier for y rotation
     */
    public double getRotYFactor() {
        return rotYFactor;
    }

    /**
     * Returns the x translation movement multiplier
     * @return The movement multiplier for x translation
     */
    public double getTransXFactor() {
        return transXFactor;
    }

    /**
     * Returns the y translation movement multiplier
     * @return The movement multiplier for y translation
     */
    public double getTransYFactor() {
        return transYFactor;
    }

    /**
     * Returns the zoom movement multiplier
     * @return The movement multiplier for zoom
     */
    public double getZoomFactor() {
        return zoomFactor;
    }

    /**
     * Enables or disables rotation.  The default is true.
     * @param enabled true or false to enable or disable rotate
     */
    public synchronized void setRotateEnable(boolean enabled) {
        rotateEnabled = enabled;
    }

    /**
     * Property which enables or disables rotation.
     * Used by ConfiguredUniverse.
     * @param enabled array of length 1 containing instance of Boolean
     * @since Java 3D 1.3
     */
    public void RotateEnable(Object[] enabled) {
        if (!(enabled.length == 1 && enabled[0] instanceof Boolean)) {
            throw new IllegalArgumentException("RotateEnable must be Boolean");
        }

        setRotateEnable(((Boolean) enabled[0]).booleanValue());
    }

    /**
     * Enables or disables zoom. The default is true.
     * @param enabled true or false to enable or disable zoom
     */
    public synchronized void setZoomEnable(boolean enabled) {
        zoomEnabled = enabled;
    }

    /**
     * Property which enables or disables zoom.
     * Used by ConfiguredUniverse.
     * @param enabled array of length 1 containing instance of Boolean
     * @since Java 3D 1.3
     */
    public void ZoomEnable(Object[] enabled) {
        if (!(enabled.length == 1 && enabled[0] instanceof Boolean)) {
            throw new IllegalArgumentException("ZoomEnable must be Boolean");
        }

        setZoomEnable(((Boolean) enabled[0]).booleanValue());
    }

    /**
     * Enables or disables translate. The default is true.
     * @param enabled true or false to enable or disable translate
     */
    public synchronized void setTranslateEnable(boolean enabled) {
        translateEnabled = enabled;
    }

    /**
     * Property which enables or disables translate.
     * Used by ConfiguredUniverse.
     * @param enabled array of length 1 containing instance of Boolean
     * @since Java 3D 1.3
     */
    public void TranslateEnable(Object[] enabled) {
        if (!(enabled.length == 1 && enabled[0] instanceof Boolean)) {
            throw new IllegalArgumentException("TranslateEnable must be Boolean");
        }

        setTranslateEnable(((Boolean) enabled[0]).booleanValue());
    }

    /**
     * Retrieves the state of rotate enabled
     * @return the rotate enable state
     */
    public boolean getRotateEnable() {
        return rotateEnabled;
    }

    /**
     * Retrieves the state of zoom enabled
     * @return the zoom enable state
     */
    public boolean getZoomEnable() {
        return zoomEnabled;
    }

    /**
     * Retrieves the state of translate enabled
     * @return the translate enable state
     */
    public boolean getTranslateEnable() {
        return translateEnabled;
    }

    boolean rotate(MouseEvent evt) {
        if (rotateEnabled) {
            if ((leftButton == ROTATE)
                    && (!evt.isAltDown() && !evt.isMetaDown())) {
                return true;
            }
            if ((middleButton == ROTATE)
                    && (evt.isAltDown() && !evt.isMetaDown())) {
                return true;
            }
            if ((rightButton == ROTATE)
                    && (!evt.isAltDown() && evt.isMetaDown())) {
                return true;
            }
        }
        return false;
    }

    boolean zoom(MouseEvent evt) {
        if (zoomEnabled) {
            if (evt instanceof java.awt.event.MouseWheelEvent) {
                return true;
            }
            if ((leftButton == ZOOM)
                    && (!evt.isAltDown() && !evt.isMetaDown())) {
                return true;
            }
            if ((middleButton == ZOOM)
                    && (evt.isAltDown() && !evt.isMetaDown())) {
                return true;
            }
            if ((rightButton == ZOOM)
                    && (!evt.isAltDown() && evt.isMetaDown())) {
                return true;
            }
        }
        return false;
    }

    boolean translate(MouseEvent evt) {
        if (translateEnabled) {
            if ((leftButton == TRANSLATE)
                    && (!evt.isAltDown() && !evt.isMetaDown())) {
                return true;
            }
            if ((middleButton == TRANSLATE)
                    && (evt.isAltDown() && !evt.isMetaDown())) {
                return true;
            }
            if ((rightButton == TRANSLATE)
                    && (!evt.isAltDown() && evt.isMetaDown())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the minimum radius for the OrbitBehavior.  The zoom will
     * stop at this distance from the center of rotation.  The default
     * is 0.0.  The minimum will have no affect if the STOP_ZOOM constructor
     * flag is not set.
     * @param r the minimum radius
     * @exception IllegalArgumentException if the radius is less than 0.0
     */
    public synchronized void setMinRadius(double r) {
        if (r < 0.0) {
            throw new IllegalArgumentException(J3dUtilsI18N.getString("OrbitBehavior1"));
        }
        minRadius = r;
    }

    /**
     * Property which sets the minimum radius for the OrbitBehavior.
     * Used by ConfiguredUniverse.
     * @param r array of length 1 containing instance of Double
     * @since Java 3D 1.3
     */
    public void MinRadius(Object[] r) {
        if (!(r.length == 1 && r[0] instanceof Double)) {
            throw new IllegalArgumentException("MinRadius must be a Double");
        }

        setMinRadius(((Double) r[0]).doubleValue());
    }

    /**
     * Returns the minimum orbit radius.  The zoom will stop at this distance
     * from the center of rotation if the STOP_ZOOM constructor flag is set.
     * @return the minimum radius
     */
    public double getMinRadius() {
        return minRadius;
    }

    /**
     * Set reverse translate behavior.  The default is false.
     * @param state if true, reverse translate behavior
     * @since Java 3D 1.3
     */
    public void setReverseTranslate(boolean state) {
        reverseTrans = state;
    }

    /**
     * Property which sets reverse translate behavior.
     * Used by ConfiguredUniverse.
     * @param state array of length 1 containing instance of Boolean
     * @since Java 3D 1.3
     */
    public void ReverseTranslate(Object[] state) {
        if (!(state.length == 1 && state[0] instanceof Boolean)) {
            throw new IllegalArgumentException("ReverseTranslate must be Boolean");
        }

        setReverseTranslate(((Boolean) state[0]).booleanValue());
    }

    /**
     * Set reverse rotate behavior.  The default is false.
     * @param state if true, reverse rotate behavior
     * @since Java 3D 1.3
     */
    public void setReverseRotate(boolean state) {
        reverseRotate = state;
    }

    /**
     * Property which sets reverse rotate behavior.
     * Used by ConfiguredUniverse.
     * @param state array of length 1 containing instance of Boolean
     * @since Java 3D 1.3
     */
    public void ReverseRotate(Object[] state) {
        if (!(state.length == 1 && state[0] instanceof Boolean)) {
            throw new IllegalArgumentException("ReverseRotate must be Boolean");
        }

        setReverseRotate(((Boolean) state[0]).booleanValue());
    }

    /**
     * Set reverse zoom behavior.  The default is false.
     * @param state if true, reverse zoom behavior
     * @since Java 3D 1.3
     */
    public void setReverseZoom(boolean state) {
        reverseZoom = state;
    }

    /**
     * Property which sets reverse zoom behavior.
     * Used by ConfiguredUniverse.
     * @param state array of length 1 containing instance of Boolean
     * @since Java 3D 1.3
     */
    public void ReverseZoom(Object[] state) {
        if (!(state.length == 1 && state[0] instanceof Boolean)) {
            throw new IllegalArgumentException("ReverseZoom must be Boolean");
        }

        setReverseZoom(((Boolean) state[0]).booleanValue());
    }

    /**
     * Set proportional zoom behavior.  The default is false.
     * @param state if true, use proportional zoom behavior
     * @since Java 3D 1.3
     */
    public synchronized void setProportionalZoom(boolean state) {
        proportionalZoom = state;

        if (state) {
            zoomMul = NOMINAL_PZOOM_FACTOR * zoomFactor;
        } else {
            zoomMul = NOMINAL_ZOOM_FACTOR * zoomFactor;
        }
    }

    public void setProportionalTranslate(boolean proportionalTranslate) {
        this.proportionalTranslate = proportionalTranslate;
    }

    public boolean isProportionalTranslate() {
        return proportionalTranslate;
    }

    /**
     * Property which sets proportional zoom behavior.
     * Used by ConfiguredUniverse.
     * @param state array of length 1 containing instance of Boolean
     * @since Java 3D 1.3
     */
    public void ProportionalZoom(Object[] state) {
        if (!(state.length == 1 && state[0] instanceof Boolean)) {
            throw new IllegalArgumentException("ProportionalZoom must be Boolean");
        }

        setProportionalZoom(((Boolean) state[0]).booleanValue());
    }

    public void setInteractionMode(int newInteractionMode) {
        if (newInteractionMode != actualInteractionMode) {
            actualInteractionMode = newInteractionMode;
            alternateInteractionMode(newInteractionMode);
        }
    }

    private void alternateInteractionMode(int newInteractionMode) {
        setLeftButton(TRANSLATE);
        setRightButton(ROTATE);
        if (newInteractionMode == COMBINED) {
            setZoomEnable(true);
            setTranslateEnable(true);
            setRotateEnable(true);
        } else if (newInteractionMode == ROTATE) {
            if (switchRotateButton) {
                setLeftButton(ROTATE);
                setRightButton(TRANSLATE);
            } else {
                setLeftButton(TRANSLATE);
                setRightButton(ROTATE);
            }
            if (alwaysZoom) {
                setZoomEnable(true);
            } else {
                setZoomEnable(false);
            }
            setTranslateEnable(false);
            setRotateEnable(true);
        } else if (newInteractionMode == TRANSLATE) {
            if (alwaysZoom) {
                setZoomEnable(true);
            } else {
                setZoomEnable(false);
            }
            setTranslateEnable(true);
            setRotateEnable(false);
        } else if (newInteractionMode == ZOOM) {
            setZoomEnable(true);
            setTranslateEnable(false);
            setRotateEnable(false);
        } else {
            logger.debug("The interaction Mode should be one of these:"
                    + "COMBINED, ROTATE, ZOOM, TRANSLATE");
        }
    }

    public boolean isAlwaysZoom() {
        return alwaysZoom;
    }

    public void setAlwaysZoom(boolean alwaysZoom) {
        this.alwaysZoom = alwaysZoom;
    }

    public int getLeftButton() {
        return leftButton;
    }

    public void setLeftButton(int leftButton) {
        this.leftButton = leftButton;
    }

    public int getRightButton() {
        return rightButton;
    }

    public void setRightButton(int rightButton) {
        this.rightButton = rightButton;
    }

    public boolean isSwitchRotateButton() {
        return switchRotateButton;
    }

    public void setSwitchRotateButton(boolean switchRotateButton) {
        this.switchRotateButton = switchRotateButton;
    }
}
