/*
 *  CameraAction.java 
 *
 *  Created by DFKI AV on 23.11.2012.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.ui;

import de.dfki.av.geovisualizer.app.camera.Camera;
import de.dfki.av.geovisualizer.app.camera.Vector3D;
import de.dfki.av.geovisualizer.app.vis.VisualizationPanel;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class CameraAction extends AbstractAction {

    /**
     * The {@link VisualizationPanel}.
     */
    private final VisualizationPanel panel;
    /**
     * The {@link Camera}.
     */
    private Camera camera;

    /**
     *
     * @param panel
     * @param camera
     */
    public CameraAction(VisualizationPanel panel, Camera camera) {
        super("Camera animation");

        if (panel == null) {
            throw new IllegalArgumentException("panel == null");
        }

        if (camera == null) {
            throw new IllegalArgumentException("camera == null");
        }

        this.panel = panel;
        this.camera = camera;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Camera c = panel.getCamera();
        Vector3D vector = c.getViewingDirection();
        if (camera != null) {
            camera.setViewingDirection(vector);
        }

        panel.setCamera(camera);
    }
}
