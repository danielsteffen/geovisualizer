/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.control;

import com.dfki.av.sudplan.io.dem.RawArcGrid;
import javax.media.j3d.BranchGroup;
import javax.swing.Icon;
import javax.swing.JFrame;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ComponentBroker {

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:How to do the configuration of setting up the connections between Gui and functionality
    //e.g. layerManager & layerComponent this should do the frame in my opinion. Frame should now Controller VisComp etc.
    public static final Icon LAYER_ICON = new javax.swing.ImageIcon(ComponentBroker.class.getResource("/com/dfki/av/sudplan/ui/icon/layer/layerIcon24.png"));
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:replace this completley with lookups
    private JFrame mainFrame;
    private ComponentController controller;

    /*ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this should be configured somewhere central and it
    must be ensured that everything to the universe will be scaled this way. ApplicationConfiguration
    could be a starting point and afterwards the universe scaling or something like that.
     */
//    private double scalingFactor=0.00001;
    private double scalingFactor = 0.001;
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: only for Vienna demo
    private RawArcGrid heights = null;
    private BranchGroup earthBranch;

    public RawArcGrid getHeights() {
        return heights;
    }

    public void setHeights(RawArcGrid heights) {
        this.heights = heights;
    }

    private ComponentBroker() {
    }

    public static ComponentBroker getInstance() {
        return ComponentBrokerHolder.INSTANCE;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:don't need this central remove
    public void setEarthBranch(final BranchGroup earthBranch) {
        this.earthBranch = earthBranch;
    }

    public BranchGroup getEarthBranch() {
        return earthBranch;
    }

    public double getInverseScalingFactor() {
        return 1/scalingFactor;
    }

    private static class ComponentBrokerHolder {

        private static final ComponentBroker INSTANCE = new ComponentBroker();
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(final JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public double getScalingFactor() {
        return scalingFactor;
    }

    public void setScalingFactor(final double scalingFactor) {
        this.scalingFactor = scalingFactor;
    }

    public ComponentController getController() {
        return controller;
    }

    public void setController(ComponentController controller) {
        this.controller = controller;
    }
}
