/*
 *  CameraListener.java 
 *
 *  Created by DFKI AV on 19.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.camera;

import java.util.EventListener;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface CameraListener extends EventListener {

    public void cameraMoved(final CameraEvent cameraEvent);

    public void cameraViewChanged(final CameraEvent cameraEvent);

}
