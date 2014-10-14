/*
 * ExitAction.java
 *
 * Created by DFKI AV on 23.11.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.ui;

import gov.nasa.worldwind.WorldWindow;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.api.annotations.common.SuppressWarnings;

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
     * @param window the {@code gov.nasa.worldwind.WorldWindow}.
     */
    public ExitAction(final WorldWindow window) {
        super("Exit GeoVisualizer");
        this.worldWindow = window;
    }

    @Override
    @SuppressWarnings("DM_EXIT")
    public final void actionPerformed(final ActionEvent e) {
        if (worldWindow != null) {
            this.worldWindow.shutdown();
        }
        System.exit(0);
    }
}
