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

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:sync with java3d this is hardcoded by knowledge;
    public final static Vector3d DEFAULT_UP = new Vector3d(0, 1.0, 0);
    public final static Vector3d DEFAULT_DOWN = new Vector3d(0, -1.0, 0);
    public final static Vector3d DEFAULT_LEFT = new Vector3d(-1.0, 0, 0);
    public final static Vector3d DEFAULT_RIGHT = new Vector3d(1.0, 0, 0);
    public final static Vector3d DEFAULT_VIEW = new Vector3d(0, 0, -1.0);

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

    public Vector3d getCameraRight();

    public Vector3d getCameraUp();

    public Vector3d getCameraDown();

    public Vector3d getCameraLeft();
}
