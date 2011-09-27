/*
 *  VisualizationPanel.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Position;
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
 *
 * @author steffen
 */
public class VisualizationPanel extends JPanel implements VisualisationComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());
    protected WorldWindowGLCanvas wwd;

    public VisualizationPanel(Dimension canvasSize) {
        super(new BorderLayout());

        this.wwd = new WorldWindowGLCanvas();
        this.wwd.setPreferredSize(canvasSize);
        this.wwd.setMinimumSize(new Dimension(800, 600));

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

    protected WorldWindowGLCanvas createWorldWindow() {
        return new WorldWindowGLCanvas();
    }

    public WorldWindowGLCanvas getWwd() {
        return wwd;
    }

    @Override
    public void addContent(Object scene) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void enableDirectedLight(boolean enabled) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeContent(Object dataObject) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void goToHome() {
        View view = this.wwd.getView();
        view.goTo(Position.fromDegrees(37, 27), 19000000.0);
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

    @Override
    public void goTo(double latitude, double longitude, double elevation) {
        View view = this.wwd.getView();
        view.goTo(Position.fromDegrees(latitude, longitude), elevation);
    }
}
