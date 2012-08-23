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
import org.openide.util.Exceptions;

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
     *
     * @param root JFrame of the single view, IllegalArgument exception is
     * thrown if null.
     * @param worldWindow WorldWindow of the single view,
     * IllegalArgumentException is thrown if null.
     */
    SideBySideStereoSetup(JFrame root, WorldWindow worldWindow) {

        if (root == null) {
            throw new IllegalArgumentException("Parameter for JFrame set to null.");
        }

        if (worldWindow == null) {
            throw new IllegalArgumentException("Parameter for worldwindow"
                    + " set to null.");
        }

        this.singleView = root;
        this.worldWindow = (WorldWindowGLCanvas) worldWindow;
        try {
            init(worldWindow);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    /**
     * Initialize components: Width and height to the width and height of the
     * screen, layer to the scale bar layer, stereoView to an undecorated, non
     * resizable and expanding to both screens jFrame, and finally activates the
     * side by side stereo mode by setting the corresponding flag in the
     * Advanced Stereo option scene controller instance associated.
     *
     * @param worldWindow worldWindow of the singleView, throws an
     * IllegalArgumentException if null.
     *
     */
    private void init(WorldWindow worldWindow) {
        if (worldWindow == null) {
            throw new IllegalArgumentException("Paramerer for worldwindow"
                    + " set to null");
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] graphicsDevices = ge.getScreenDevices();
        DisplayMode displayMode = graphicsDevices[0].getDisplayMode();
        width = displayMode.getWidth();
        height = displayMode.getHeight();

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
            throw new IllegalArgumentException("SceneController is not instance of"
                    + " AdvancedStereoOptionSceneController");
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
