/*
 *  VisWiz.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.wiz;

import com.dfki.av.sudplan.Configuration;
import de.dfki.av.geovisualizer.core.IVisAlgorithm;
import de.dfki.av.geovisualizer.core.VisConfiguration;
import de.dfki.av.geovisualizer.core.VisWorker;
import de.dfki.av.geovisualizer.core.io.IOUtils;
import gov.nasa.worldwind.WorldWindow;
import java.awt.Dialog;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides the visualization wizard VisWiz.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public final class VisWiz {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(VisWiz.class);

    /**
     * Starts the visualization wizard VisWiz. After finishing the visualization
     * will be added automatically to the {@link WorldWindow} instance and the
     * {@link VisConfiguration} will be returned.
     *
     * @param worldWindow the {@link WorldWindow} where to add the
     * visualization.
     * @return the {@link VisConfiguration} to return or {@code null}
     * @throws IllegalArgumentException if worldWindow == null.
     */
    public static VisConfiguration execute(WorldWindow worldWindow) {
        return VisWiz.execute(worldWindow, null);
    }

    /**
     * Starts the visualization wizard VisWiz. After finishing the visualization
     * will be added automatically to the {@link WorldWindow} instance. The
     * {@link PropertyChangeListener} can be used to monitor the progress of the
     * visualization creation process. The name of the {@link PropertyChangeEvent}
     * event is {@link IVisAlgorithm#PROGRESS_PROPERTY}.
     *
     * @param worldWindow the {@link WorldWindow} where to add the
     * visualization.
     * @param listener the {@link PropertyChangeListener} to add.
     * @return the {@link VisConfiguration} or {@code null}.
     * @throws IllegalArgumentException if worldWindow == null.
     */
    public static VisConfiguration execute(WorldWindow worldWindow, PropertyChangeListener listener) {
        return VisWiz.execute(worldWindow, listener, null);
    }

    /**
     * Starts the visualization wizard VisWiz. After finishing the visualization
     * will be added automatically to the {@link WorldWindow} instance. The
     * {@link PropertyChangeListener} can be used to monitor the progress of the
     * visualization creation process. The name of the {@link PropertyChangeEvent}
     * event is {@link IVisAlgorithm#PROGRESS_PROPERTY}. If {@code data != null}
     * the visualization wizard {@link VisWiz} starts with the {@link AttributeSelectionPanel}
     * as first panel. Otherwise with the {@link DataSourceSelectionPanel}. In
     * case the {@code data} object is of type {@link InputStream} the data is
     * downloaded to a temporary {@link File} that is then used as source for
     * the {@link VisWiz}.
     *
     * @param worldWindow the {@link WorldWindow} where to add the
     * visualization.
     * @param listener the {@link PropertyChangeListener} to add.
     * @param data the data to use.
     * @return the {@link VisConfiguration} or {@code null}.
     * @throws IllegalArgumentException if worldWindow == null.
     */
    public static VisConfiguration execute(WorldWindow worldWindow, PropertyChangeListener listener, Object data) {

        if (worldWindow == null) {
            String msg = "WorldWindow == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        VisConfiguration visConfig = null;
        WizardDescriptor.Iterator iterator;
        WizardDescriptor wizardDescriptor;

        if (data == null) {
            iterator = new VisWizIterator(true);
            wizardDescriptor = new WizardDescriptor(iterator);
            wizardDescriptor.setTitleFormat(new MessageFormat("{0} ({1})"));
            wizardDescriptor.setTitle("VisWiz");
        } else {
            // do here the same hack as in IOUtils#Read for the InputStream case
            // This needs to be done since the AttributeTableFiller in 
            // AttributeSelectionPanel works with the Inputstream as the VisWorker 
            // which results in a NullPointerException.
            Object source = null;
            if (data instanceof InputStream) {
                log.debug("Data instance of InputStream.");
                InputStream is = null;
                try {
                    is = (InputStream) data;
                    File file = IOUtils.DownloadToTempFile(is);
                    file.deleteOnExit();
                    source = file.toURI();
                } catch (IOException ex) {
                    log.error(ex.toString());
                } finally {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        log.error(ex.toString());
                    }
                }
            } else {
                source = data;
            }
            // Check whether the download was successful.
            if (source == null) {
                log.error("source == null");
                return visConfig;
            }
            // ...ends here the fake.

            iterator = new VisWizIterator(false);
            wizardDescriptor = new WizardDescriptor(iterator);
            wizardDescriptor.setTitleFormat(new MessageFormat("{0} ({1})"));
            wizardDescriptor.setTitle("VisWiz");
            wizardDescriptor.putProperty("SelectedDataSource", source);
        }

        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setIconImage(Configuration.SUDPLAN_3D_IMAGE);
        dialog.setVisible(true);
        dialog.toFront();

        if (wizardDescriptor.getValue() == WizardDescriptor.FINISH_OPTION) {
            Object dataSource = wizardDescriptor.getProperty("SelectedDataSource");
            IVisAlgorithm visAlgo = (IVisAlgorithm) wizardDescriptor.getProperty("SelectedVisualization");
            if (listener != null) {
                visAlgo.addPropertyChangeListener(listener);
            }
            String[] dataAttributes = (String[]) wizardDescriptor.getProperty("SelectedDataAttributes");
            visConfig = new VisConfiguration(visAlgo, dataSource, dataAttributes);
            VisWorker producer = new VisWorker(visConfig, worldWindow);
            producer.execute();
        }

        return visConfig;
    }
}
