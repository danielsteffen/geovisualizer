/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.vis.control;

import com.sun.j3d.utils.behaviors.vp.ViewPlatformAWTBehavior;
import com.sun.j3d.utils.pickfast.PickCanvas;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PickInfo;
import javax.media.j3d.Transform3D;
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
//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: not needed at the moment
public class CameraBehavior extends ViewPlatformAWTBehavior {

    private final static Logger logger = LoggerFactory.getLogger(CameraBehavior.class);
    private final PickCanvas pickCanvas;
    private final BranchGroup scene;
    private Point3d currentPickPoint;
    private int mouseX;
    private int mouseY;
    private Transform3D currentXfm = new Transform3D();
    private double rotMul = 0.01;
    private double rotx = 0.0;
    private double roty = 0.0;

    public CameraBehavior(final Canvas3D c, final BranchGroup scene, int flags) {
        super(c, MOUSE_LISTENER | MOUSE_MOTION_LISTENER | MOUSE_WHEEL_LISTENER | flags);
        this.scene = scene;
        pickCanvas = new PickCanvas(canvases[0], scene);
        pickCanvas.setMode(PickInfo.PICK_GEOMETRY);
        pickCanvas.setFlags(PickInfo.CLOSEST_INTERSECTION_POINT);
    }

    @Override
    protected void integrateTransforms() {
        final Vector3d translation = new Vector3d();
        targetTG.getTransform(currentXfm);
        currentXfm.get(translation);
        if (logger.isDebugEnabled()) {
            logger.debug("transform: " + currentXfm);
//            logger.debug("translation: " + translation);
        }
	Transform3D rotatey = new Transform3D();
        Transform3D rotatex = new Transform3D();
//        if (logger.isDebugEnabled()) {
//            logger.debug("rotx integrate: " + rotx);
//        }
        currentXfm.setTranslation(new Vector3d());
        rotatey.rotY(rotx);        
        rotatex.rotX(roty);
        currentXfm.mul(rotatey);
        currentXfm.setTranslation(new Vector3d());
        currentXfm.mul(rotatex);
        currentXfm.setTranslation(translation);
//        currentXfm.setTranslation(new Vector3d(0, 0, 5));
        if (logger.isDebugEnabled()) {
            logger.debug("transform: " + currentXfm);
        }
        targetTG.setTransform(currentXfm);
//        tempRotate.rotX(x);
        // reset yaw and pitch angles
        rotx = 0.0;
        roty = 0.0;
    }

    @Override
    protected void processAWTEvents(AWTEvent[] events) {
        motion = false;
        for (int i = 0; i < events.length; i++) {
            if (events[i] instanceof MouseEvent) {
//                detClickedPoint((MouseEvent) events[i]);
                processMouseEvent((MouseEvent) events[i]);
            }
        }
    }

    private void detClickedPoint(final MouseEvent e) {

        if (MouseEvent.MOUSE_PRESSED == e.getID() || MouseEvent.MOUSE_RELEASED == e.getID() || (e.getButton() == MouseEvent.BUTTON1 && MouseEvent.MOUSE_DRAGGED == e.getID() || MouseEvent.MOUSE_WHEEL == e.getID())) {
//            newValidMouseEvent = true;
            if (logger.isDebugEnabled()) {
                logger.debug("mouse_event: id:" + e.getID() + " button: " + e.getButton());
            }
            final int mouseX = e.getX();
            final int mouseY = e.getY();

            final Point3d mouse_pos = new Point3d();
            canvases[0].getPixelLocationInImagePlate(mouseX, mouseY, mouse_pos);
            Transform3D motionToWorld = new Transform3D();
            canvases[0].getImagePlateToVworld(motionToWorld);
            motionToWorld.transform(mouse_pos);
            mouse_pos.z = 0.0f;
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
            if (pickPoint != null) {
                pickPoint.z = 0.0;
                currentPickPoint = result.getClosestIntersectionPoint();
            } else {
                mouse_pos.z = 0.0;
                currentPickPoint = result.getClosestIntersectionPoint();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Mouse position: real: " + mouseX + "/" + mouseY + " world: " + mouse_pos.x + "/" + mouse_pos.y + " picked: " + (pickPoint != null ? pickPoint.x + "/" + pickPoint.y : "null"));
            }
        } else {
//            newValidMouseEvent = false;
        }
    }

    protected void processMouseEvent(final MouseEvent evt) {
        if (evt.getID() == MouseEvent.MOUSE_PRESSED) {
            mouseX = evt.getX();
            mouseY = evt.getY();
            motion = true;
        } else if (evt.getID() == MouseEvent.MOUSE_DRAGGED) {
            int xchange = evt.getX() - mouseX;
            rotx += xchange * rotMul;
//            if((evt.getModifiers() & MouseEvent.CTRL_MASK)!=0){
//                if (logger.isDebugEnabled()) {
//                    logger.debug("control down");
//                }
                int ychange = evt.getY() - mouseY;
                roty += ychange * rotMul;
//            }
            
            
//            if (logger.isDebugEnabled()) {
//                logger.debug("event: "+evt.getX());
//                logger.debug("mouse: : "+mouseX);
//                logger.debug("xchange: : "+xchange);
//                logger.debug("xchange: : "+rotx);
//            }
            
//            if (logger.isDebugEnabled()) {
//                logger.debug("rotx: "+rotx);
//            }
            mouseX = evt.getX();
            mouseY = evt.getY();
            motion = true;
            // rotate
//            if (rotate(evt)) {
//                if (reverseRotate) {
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("long: " + longditude + " lat: " + latitude);
//                    }
//                    longditude -= xchange * rotXMul;
//                    latitude -= ychange * rotYMul;
//                } else {
//                    longditude += xchange * rotXMul;
//                    latitude += ychange * rotYMul;
//                }
//            } // translate
//            else if (translate(evt)) {
//                doTranslateOperations(xchange, ychange);
//            } // zoom
//            else if (zoom(evt)) {
//                doZoomOperations(ychange);
//            }
//            mouseX = evt.getX();
//            mouseY = evt.getY();
//            motion = true;
//        } else if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
//        } else if (evt.getID() == MouseEvent.MOUSE_WHEEL) {
//            if (zoom(evt)) {
//                // if zooming is done through mouse wheel,
//                // the amount of increments the wheel changed,
//                // multiplied with wheelZoomFactor is used,
//                // so that zooming speed looks natural compared to mouse movement zoom.
//                if (evt instanceof java.awt.event.MouseWheelEvent) {
//                    // I/O differenciation is made between
//                    // java.awt.event.MouseWheelEvent.WHEEL_UNIT_SCROLL or
//                    // java.awt.event.MouseWheelEvent.WHEEL_BLOCK_SCROLL so
//                    // that behavior remains stable and not dependent on OS settings.
//                    // If getWheelRotation() was used for calculating the zoom,
//                    // the zooming speed could act differently on different platforms,
//                    // if, for example, the user sets his mouse wheel to jump 10 lines
//                    // or a block.
//                    int zoom =
//                            ((int) (((java.awt.event.MouseWheelEvent) evt).getWheelRotation()
//                            * wheelZoomFactor));
//                    doZoomOperations(zoom);
//                    motion = true;
//                }
//            }
        }
    }
}
