/*
 *  SideBySideStereoSetup.java 
 *
 *  Created by DFKI AV on 01.08.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.stereo;

import com.dfki.av.sudplan.vis.VisualizationPanel;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.SceneController;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.InputHandler;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This class sets the setup for the side by side stereoscopic display by
 * creating one frame with the size of the 2 display screens and the advanced
 * stereo scene controller map left texture to the left half of the frame and
 * right texture to the right half of the frame.
 *
 * This class needs the default InputHandler to be set to StereoAWTInputHandler
 * and the default SceneController to be set to StereoOptionSceneController in
 * the worldwind.xml file.
 *
 * @author tarek
 */
public class SideBySideStereoSetup {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(SideBySideStereoSetup.class);
    /**
     * JFrame of the root single view window.
     */
    private JFrame singleView;
    /**
     * JFrame of the stereoView window.
     */
    private JFrame stereoView;
    /**
     * World window of the single view.
     */
    private WorldWindow worldWindow;
    /**
     * Visualization panel used in the stereo mode.
     */
    private VisualizationPanel vp;
    /**
     * Height of the screen in pixels.
     */
    private int height;
    /**
     * Width of the screen in pixels.
     */
    private int width;
    /**
     * Layer removed from a view mode and added to the other. (Scale Bar)
     */
    private Layer layer;

    /**
     * Constructs new instance of the SideBySideStereoSetup. It initializes the
     * singleView and the worldWindow attributes to the passed input parameters.
     * It calls the init method that initializes the rest of the components.
     *
     * @param root JFrame of the single view
     * @param worldWindow WorldWindow of the single view
     * @throws IllegalArgumentException is parameters set to {@code null}.
     */
    public SideBySideStereoSetup(JFrame root, WorldWindow worldWindow) {

        if (root == null) {
            String msg = "Parameter for JFrame set to null.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (worldWindow == null) {
            String msg = "Parameter for worldwindow set to null.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.singleView = root;
        this.worldWindow = (WorldWindowGLCanvas) worldWindow;

        init(worldWindow);
    }

    /**
     * Initialize components: Width and height to the width and height of the
     * screen, layer to the scale bar layer, stereoView to an undecorated, non
     * resizable and expanding to both screens jFrame, and finally activates the
     * side by side stereo mode by setting the corresponding flag in the
     * Advanced Stereo option scene controller instance associated.
     *
     * @param worldWindow worldWindow of the singleView,
     * @throws RuntimeException if wrong xml configuration.
     * @throws RuntimeException if not enough screens available.
     */
    private void init(WorldWindow worldWindow) {

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] graphicsDevices = ge.getScreenDevices();

        // Check if valid screen configuration
        log.debug("Number of GraphicsDevices: {}", graphicsDevices.length);
        int numScreens = 0;
        for (int i = 0; i < graphicsDevices.length; i++) {
            if (graphicsDevices[i].getType() == GraphicsDevice.TYPE_RASTER_SCREEN) {
                numScreens++;
            }
        }
        log.debug("Number of screens: {}", numScreens);
        if (numScreens < 2) {
            String msg = "Could not init side-by-side stereo mode. "
                    + "Only" + numScreens + " screens available.";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        DisplayMode displayMode0 = graphicsDevices[0].getDisplayMode();
        width = displayMode0.getWidth();
        height = displayMode0.getHeight();

        DisplayMode displayMode1 = graphicsDevices[1].getDisplayMode();
        int wDisplay1 = displayMode1.getWidth();
        int hDisplay1 = displayMode1.getHeight();

        if (width != wDisplay1 || height != hDisplay1) {
            String msg = "No valid configuration for side-by-side stereo mode. "
                    + "Screens have different resolution.";
            log.warn(msg);
        }

        vp = new VisualizationPanel(new Dimension(width, height));
        WorldWindowGLCanvas vpWwd = vp.getWwd();
        Model vpModel = vpWwd.getModel();
        Model oriModel = worldWindow.getModel();
        Globe globe = oriModel.getGlobe();
        vpModel.setGlobe(globe);

        LayerList vpLayers = vpModel.getLayers();
        LayerList oriLayers = oriModel.getLayers();
        layer = vpLayers.getLayerByName("Scale bar");
        vpLayers.remove(layer);
        vpLayers.addAllAbsent(oriLayers);
        layer = vpLayers.getLayerByName("Scale bar");
        vpLayers.remove(layer);

        stereoView = new JFrame("SideBySide - Demo");
        stereoView.setBounds(0, 0, width * 2, height);
        stereoView.setUndecorated(true);
        stereoView.getContentPane().add((WorldWindowGLCanvas) vpWwd,
                BorderLayout.CENTER);
        stereoView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        stereoView.setVisible(true);
        stereoView.requestFocus();
        stereoView.setResizable(false);

        double aspectLeft = (double) (width) / (double) (height);
        SceneController sc = ((WorldWindowGLCanvas) vpWwd).getSceneController();
        if (sc instanceof AdvancedStereoOptionSceneController) {
            AdvancedStereoOptionSceneController my = (AdvancedStereoOptionSceneController) sc;
            my.setAspect(aspectLeft);
            my.setWidth(width);
            my.setHeight(height);
            my.setSideBySide(true);
        } else {
            String msg = "SceneController is not instance of AdvancedStereoOptionSceneController";
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * Starts the side by side stereo mode. Maps the vertical exaggeration from
     * the single view. Adds all assigned selected listeners to the stereo mode.
     */
    public void start() {
        SceneController oriScCon = worldWindow.getSceneController();
        WorldWindowGLCanvas vpWwd = vp.getWwd();
        SceneController vpScCon = vpWwd.getSceneController();

        double ve = oriScCon.getVerticalExaggeration();
        vpScCon.setVerticalExaggeration(ve);
        vpWwd.setView(worldWindow.getView());

        LayerList oriLayers = worldWindow.getModel().getLayers();
        LayerList vpLayers = vpWwd.getModel().getLayers();
        oriLayers.add(layer);
        stereoView.setVisible(true);
        singleView.setVisible(false);

        Layer controlLayer = oriLayers.getLayerByName("MarchingCubes Control");

        if (controlLayer != null) {
            controlLayer.setPickEnabled(true);
            vpLayers.addIfAbsent(controlLayer);
            oriLayers.remove(controlLayer);
        }
        InputHandler handler = worldWindow.getInputHandler();
        StereoAWTInputHandler stereoHandler = (StereoAWTInputHandler) handler;
        final ArrayList<SelectListener> selLis = stereoHandler.getSelectListeners();
        for (int i = 0; i < selLis.size(); i++) {
            SelectListener sl = selLis.get(i);

            if (!(sl instanceof ClickAndGoSelectListener)
                    && !(sl instanceof ViewControlsSelectListener)) {
                vpWwd.addSelectListener(sl);
            }
        }
        vpWwd.addKeyListener(new StereoKeyListener(this));

    }

    /**
     * Stops the stereo mode and returns to the single view mode. Maps the
     * vertical exaggeration. Sets the cursors to default cursor.
     */
    public void stop() {
        SceneController oriScCon = worldWindow.getSceneController();
        WorldWindowGLCanvas vpWwd = vp.getWwd();
        SceneController vpScCon = vpWwd.getSceneController();

        double ve = vpScCon.getVerticalExaggeration();
        oriScCon.setVerticalExaggeration(ve);

        stereoView.setVisible(false);

        singleView.setVisible(true);
        LayerList vpLayers = vpWwd.getModel().getLayers();
        LayerList oriLayers = worldWindow.getModel().getLayers();
        Layer controlLayer = vpLayers.getLayerByName("MarchingCubes Control");

        if (controlLayer != null) {
            controlLayer.setPickEnabled(true);
            oriLayers.addIfAbsent(controlLayer);
            vpLayers.remove(controlLayer);
        }

        this.singleView.setVisible(true);
        stereoView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        singleView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        stereoView = null;
        vp = null;
    }

    /**
     * Accesses the visualization panel used in the side by side stereo mode.
     *
     * @return Visualization Panel used in the StereoView.
     */
    public VisualizationPanel getVp() {
        return vp;
    }
}
