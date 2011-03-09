/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.camera;

import com.dfki.av.sudplan.util.AdvancedBoundingBox;
import com.sun.j3d.utils.behaviors.vp.ViewPlatformBehavior;
import com.sun.j3d.utils.universe.ViewingPlatform;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.Bounds;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface Camera {
    public Vector3d getCameraDirection();
    public void setCameraDirection(final Vector3d cameraDirection);
    public Point3d getCameraPosition();
    public void setCameraPosition(final Point3d cameraPosition);
    public BoundingBox getViewBoundingBox();
    public void setCameraBounds(Bounds behaviourBounding);    
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:hide
    public ViewingPlatform getViewingPlatform();    
    
    public void gotoBoundingBox(final AdvancedBoundingBox boundingBox);
    public void gotoPoint(final Tuple3f point);
    public void gotoPoint(final Tuple3d point);

     public void addCameraListner(final CameraListener newListener);
     public void removeCameraListner(final CameraListener listenerToRemove);
}
