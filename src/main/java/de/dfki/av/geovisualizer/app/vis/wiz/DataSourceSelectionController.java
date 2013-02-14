/*
 * DataSourceSelectionController.java 
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.vis.wiz;

import java.awt.Component;
import java.io.File;
import java.net.URL;
import javax.swing.event.ChangeListener;
import org.apache.commons.validator.routines.UrlValidator;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class DataSourceSelectionController implements WizardDescriptor.ValidatingPanel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private DataSourceSelectionPanel component;

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new DataSourceSelectionPanel();
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
    }

    @Override
    public void storeSettings(Object settings) {
        WizardDescriptor descriptor = (WizardDescriptor) settings;
        Object o = component.getSelectedDataSource();
        descriptor.putProperty("SelectedDataSource", o);
    }

    @Override
    public void validate() throws WizardValidationException {

        Object source = component.getSelectedDataSource();
        if (source == null) {
            throw new WizardValidationException(null, "Please select data source.", null);
        }

        if (source instanceof File) {
            File file = (File) source;
            if (!file.exists()) {
                throw new WizardValidationException(null, "No valid file.", null);
            }
        }

        if (source instanceof URL) {
            URL url = (URL) source;
            UrlValidator urlValidator = new UrlValidator();
            if (!urlValidator.isValid(url.toString())) {
                throw new WizardValidationException(null, "No valid URL.", null);
            }
        }
    }
}
