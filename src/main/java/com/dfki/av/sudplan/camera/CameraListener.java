/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.camera;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface CameraListener {
    public void cameraMoved(final CameraEvent cameraEvent);
    public void cameraViewChanged(final CameraEvent cameraEvent);
    public void cameraRegistered(final CameraEvent cameraEvent);
    public void cameraUnregistered(final CameraEvent cameraEvent);
}
