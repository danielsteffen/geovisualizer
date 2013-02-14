/*
 * ParameterMappingController.java 
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.vis.wiz;

import de.dfki.av.geovisualizer.core.IVisAlgorithm;
import java.awt.Component;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ParameterMappingController implements WizardDescriptor.Panel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private ParameterMappingPanel component;

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new ParameterMappingPanel();
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
        WizardDescriptor wiz = (WizardDescriptor) settings;
        IVisAlgorithm algo = (IVisAlgorithm) wiz.getProperty("SelectedVisualization");
        List<String[]> attributes = (List<String[]>) wiz.getProperty("DataAttributes");
        component.setSelectedVisualization(algo, attributes);
    }

    @Override
    public void storeSettings(Object settings) {
        WizardDescriptor wiz = (WizardDescriptor) settings;
        String[] attributes = component.getAttributes();
        wiz.putProperty("SelectedDataAttributes", attributes);
    }
}
