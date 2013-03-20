/*
 * VisualizationSelectionController.java 
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.vis.wiz;

import de.dfki.av.geovisualizer.core.IVisAlgorithm;
import java.awt.Component;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisualizationSelectionController implements WizardDescriptor.Panel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private VisualizationSelectionPanel component;

    /**
     *
     */
    public VisualizationSelectionController() {
        super();
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new VisualizationSelectionPanel();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
    }

    @Override
    public final void addChangeListener(ChangeListener l) {
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(Object settings) {
    }

    @Override
    public void storeSettings(Object settings) {
        WizardDescriptor wiz = (WizardDescriptor) settings;
        if (component != null) {
            IVisAlgorithm algo = component.getSelectedVisualization();
            wiz.putProperty("SelectedVisualization", algo);
        }
    }
}
