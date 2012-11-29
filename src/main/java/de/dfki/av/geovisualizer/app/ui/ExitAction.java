/*
 *  ExitAction.java 
 *
 *  Created by DFKI AV on 23.11.2012.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.ui;

import gov.nasa.worldwind.WorldWindow;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Action to shutdown the GeoVisualizer application.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ExitAction extends AbstractAction {

    /**
     * Reference to the {@code WorldWindow}.
     */
    private WorldWindow worldWindow;

    /**
     * Constructor to exit the application and shutdown the
     * {@code gov.nasa.worldwind.WorldWindow}. If {@code worldWindow == null}
     * the application will exit without shutting down the
     * {@code gov.nasa.worldwind.WorldWindow}
     *
     * @param worldWindow the {@code gov.nasa.worldwind.WorldWindow}.
     */
    public ExitAction(WorldWindow worldWindow) {
        super("Exit GeoVisualizer");
        this.worldWindow = worldWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (worldWindow != null) {
            this.worldWindow.shutdown();
        }
        System.exit(0);
    }
}
