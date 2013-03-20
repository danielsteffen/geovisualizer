/*
 *  AttributeSelectionController.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.vis.wiz;

import java.awt.Component;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class AttributeSelectionController implements WizardDescriptor.ValidatingPanel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private AttributeSelectionPanel component;

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new AttributeSelectionPanel();
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
        WizardDescriptor descriptor = (WizardDescriptor) settings;
        if (component != null) {
            Object o = descriptor.getProperty("SelectedDataSource");
            component.setSelectedDataSource(o);
        }
    }

    @Override
    public void storeSettings(Object settings) {
        WizardDescriptor descriptor = (WizardDescriptor) settings;
        if (component != null) {
            List<String[]> l = component.getSelectedAttributes();
            descriptor.putProperty("DataAttributes", l);
        }
    }

    @Override
    public void validate() throws WizardValidationException {
        if (component != null) {
            List<String[]> attributes = component.getSelectedAttributes();
            if (attributes.isEmpty()) {
                throw new WizardValidationException(null, "No attribute selected.", null);
            }
        }
    }
}
