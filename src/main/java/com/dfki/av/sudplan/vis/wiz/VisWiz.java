/*
 *  VisWiz.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.wiz;

import com.dfki.av.sudplan.vis.VisWorker;
import com.dfki.av.sudplan.vis.core.IVisAlgorithm;
import gov.nasa.worldwind.WorldWindow;
import java.awt.Dialog;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

/**
 * This class provides the visualization wizard VisWiz. 
 * 
 * @author steffen
 */
public final class VisWiz {

    /**
     * Starts the visualization wizard VisWiz. After finishing the visualization
     * will be added automatically to the {@link WorldWindow} instance. The
     * {@link PropertyChangeListener} can be used to montior the progress of
     * the visualization creation process. The name of the {@link PropertyChangeEvent}
     * event is {@link IVisAlgorithm#PROGRESS_PROPERTY}.
     * 
     * @param worldWindow the {@link WorldWindow} where to add the visualization.
     * @param listener the {@link PropertyChangeListener} to add.
     * @throws IllegalArgumentException if {@code worldWindow} set to null.
     */
    public static void execute(WorldWindow worldWindow, PropertyChangeListener listener) {

        if(worldWindow == null){
            throw new IllegalArgumentException("WorldWindow not defined. Set to null.");
        }
        
        WizardDescriptor.Iterator iterator = new VisWizIterator();
        WizardDescriptor wizardDescriptor = new WizardDescriptor(iterator);
        wizardDescriptor.setTitleFormat(new MessageFormat("{0} ({1})"));
        wizardDescriptor.setTitle("VisWiz");

        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();

        if (wizardDescriptor.getValue() == WizardDescriptor.FINISH_OPTION) {
            Object data = wizardDescriptor.getProperty("SelectedDataSource");
            IVisAlgorithm visAlgo = (IVisAlgorithm) wizardDescriptor.getProperty("SelectedVisualization");
            if (listener != null) {
                visAlgo.addPropertyChangeListener(listener);
            }
            String[] dataAttributes = (String[]) wizardDescriptor.getProperty("SelectedDataAttributes");

            VisWorker producer = new VisWorker(data, visAlgo, dataAttributes, worldWindow);
            producer.execute();
        }
    }
}
