/*
 *  VisualizationPanel.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import com.dfki.av.sudplan.camera.Camera;
import com.dfki.av.sudplan.camera.SimpleCamera;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;
import gov.nasa.worldwind.layers.WorldMapLayer;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing the <code>WorldWindowGLCanvas</code> to render the virtual
 * globe.
 * 
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisualizationPanel extends JPanel implements VisualisationComponent {

    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());
    /**
     * The world wind GL canvas.
     */
    private WorldWindowGLCanvas wwd;
    
    /**
     * Constructs a visualization panel of the defined <code>Dimension</code>.
     * 
     * @param canvasSize size of the <code>WorldWindowGLCanvas</code>.
     */
    public VisualizationPanel(Dimension canvasSize) {
        super(new BorderLayout());

        this.wwd = new WorldWindowGLCanvas();
        this.wwd.setPreferredSize(canvasSize);

        // Create the default model as described in the current worldwind properties.
        Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        this.wwd.setModel(m);

        // Setup a select listener for the worldmap click-and-go feature
        this.wwd.addSelectListener(new ClickAndGoSelectListener(this.wwd, WorldMapLayer.class));

        ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
        this.wwd.getModel().getLayers().add(viewControlsLayer);
        this.wwd.addSelectListener(new ViewControlsSelectListener(this.wwd, viewControlsLayer));

        this.add(this.wwd, BorderLayout.CENTER);
    }

    /**
     * Returns the <code>WorldWindowGLCanvas</code>.
     * 
     * @return the <code>WorldWindowGLCanvas</code> to return.
     */
    public WorldWindowGLCanvas getWwd() {
        return this.wwd;
    }

    /**
     * Animates viewer to home position.
     */
    public void goToHome() {
        goTo(37.0, 27.0, 19000000.0, true);
    }

    /**
     * Sets view to the specified position defined by <code>latitude</code>, 
     * <code>longitude</code>, and <code>altitude</code>. If the flag
     * <code>altitude</code> is set to true the movement is animated. Otherwise
     * not.
     * 
     * @param latitude the latitude position in degrees.
     * @param longitude the longitude position in degrees.
     * @param altitude the altitude position in meters?
     * @param animated wheter the change should be animated or not.
     */
    public void goTo(double latitude, double longitude, double altitude, boolean animated) {
        View view = this.wwd.getView();
        if (animated) {
            view.goTo(Position.fromDegrees(latitude, longitude), altitude);
        } else {
            view.setEyePosition(Position.fromDegrees(latitude, longitude, altitude));
            wwd.redraw();
        }
    }

    @Override
    public void addLayer(Object layer) {
        if (layer == null) {
            if (log.isWarnEnabled()) {
                log.warn("Object trying to add equals to null.");
            }
            return;
        }

        if (layer instanceof Layer) {
            throw new UnsupportedOperationException("Not supported yet.");
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Can't add object. Currently, only objects of type Layer are supported.");
            }
        }
    }

    @Override
    public void removeLayer(Object layer) {
        if (layer == null) {
            if (log.isWarnEnabled()) {
                log.warn("Object trying to add equals to null.");
            }
            return;
        }

        if (layer instanceof Layer) {
            throw new UnsupportedOperationException("Not supported yet.");
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Can't remove object. Currently, only objects of type Layer are supported.");
            }
        }
    }

    @Override
    public Camera getCamera() {
        Position p = this.wwd.getView().getEyePosition();
        return new SimpleCamera(p);
    }

    @Override
    public void setCamera(Camera c) {
        if( c == null ){
            if (log.isWarnEnabled()) {
                log.warn("Camera trying to add equals to null.");
            }
            return;
        }
        goTo(c.getLatitude(), c.getLongitude(), c.getAltitude(), true);
    }

    @Override
    public void setModeZoom() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setModePan() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setModeRotate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setModeCombined() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
