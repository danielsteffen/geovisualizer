/*
 * StereoKeyListener.java
 *
 * Created by DFKI AV on 01.08.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.stereo;

import gov.nasa.worldwind.SceneController;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * This class is a KeyListener that should be added to the frame of the side by
 * side stereo view to add the following functionalities: exits the view back to
 * the single one on pressing the escape button, small e 'e' increases the eye
 * separation distance, capital e 'E' decreases the eye separation distance, and
 * the space button swaps both left and right eyes with each other.
 *
 * @author tarek
 */
public class StereoKeyListener implements KeyListener {

    /**
     * List of previously assigned keyListeners.
     */
    private KeyListener[] listeners;
    /**
     * Side By Side Stereo Setup object.
     */
    private SideBySideStereoSetup stereo;
    /**
     * WorldWindow GL canvas of the stereo mode.
     */
    private WorldWindowGLCanvas wwd;

    /**
     * Constructs a StereoKeyListener object. Initializes the attributes by the
     * input parameter attributes and removes the KeyListeners already assigned
     * to be wrapped by this keyListener.
     *
     * @param stereo SideBySideStereoSetup object
     */
    public StereoKeyListener(SideBySideStereoSetup stereo) {
        this.stereo = stereo;
        this.wwd = stereo.getVp().getWwd();
        this.listeners = wwd.getKeyListeners();

        for (KeyListener kl : listeners) {
            wwd.removeKeyListener(kl);
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        for (KeyListener kl : listeners) {
            kl.keyTyped(ke);
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        char key = ke.getKeyChar();
        if (key == 'e') {
            increaseEyeSeparation();
        }
        if (key == 'E') {
            decreaseEyeSeparation();
        }
        for (KeyListener kl : listeners) {
            kl.keyPressed(ke);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE) {
            stereo.stop();
        }

        if (key == KeyEvent.VK_SPACE) {
            swapEyes();
        }
        for (KeyListener kl : listeners) {
            kl.keyReleased(ke);
        }
    }

    /**
     * Increases the separation distance between the 2 eyes by accessing the IOD
     * field in the AdvancedStereoOptionSceneController and redraws the scene.
     * Throws a runtime exception if the sceneController used is not an instance
     * of the AdvancedStereoOptionSceneController.
     */
    public void increaseEyeSeparation() {
        SceneController sc = wwd.getSceneController();

        if (sc instanceof AdvancedStereoOptionSceneController) {
            AdvancedStereoOptionSceneController mySc = (AdvancedStereoOptionSceneController) sc;

            mySc.setIOD(mySc.getIOD() + 0.05);


            wwd.redraw();
        } else {
            throw new RuntimeException("SceneConroller no instance of "
                    + "MyStereoOptionSceneController");
        }
    }

    /**
     * Decreases the separation distance between the 2 eyes by accessing the IOD
     * field in the AdvancedStereoOptionSceneController and redraws the scene.
     * Throws a runtime exception if the sceneController used is not an instance
     * of the AdvancedStereoOptionSceneController.
     */
    public void decreaseEyeSeparation() {
        SceneController sc = wwd.getSceneController();

        if (sc instanceof AdvancedStereoOptionSceneController) {
            AdvancedStereoOptionSceneController mySc = (AdvancedStereoOptionSceneController) sc;

            mySc.setIOD(mySc.getIOD() - 0.05);


            wwd.redraw();
        } else {
            throw new RuntimeException("SceneConroller no instance of"
                    + " MyStereoOptionSceneController");
        }
    }

    /**
     * Swap both eyes with each other by flipping the swapEyes flag in the
     * AdvancedStereoOptionSceneContoller and redraws the scene. Throws a
     * runtime exception if the sceneController used is not an instance of the
     * AdvancedStereoOptionSceneController.
     */
    public void swapEyes() {
        SceneController sc = wwd.getSceneController();

        if (sc instanceof AdvancedStereoOptionSceneController) {
            AdvancedStereoOptionSceneController mySc = (AdvancedStereoOptionSceneController) sc;

            mySc.setSwapEyes(!mySc.isSwapEyes());


            wwd.redraw();
        } else {
            throw new RuntimeException("SceneConroller no instance of"
                    + " MyStereoOptionSceneController");
        }
    }
}
