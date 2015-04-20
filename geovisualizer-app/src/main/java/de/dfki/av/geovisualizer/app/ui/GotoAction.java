/*
 * GotoAction.java
 *
 * Created by DFKI AV on 29.11.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.ui;

import gov.nasa.worldwind.WorldWindow;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;

/**
 *
 */
public class GotoAction extends AbstractAction {

    /**
     * The {@link GotoDialog}.
     */
    private GotoDialog gotoDialog;

    /**
     * Constructor for the {@link GotoAction}.
     *
     * @param frame the {@link JFrame} to set.
     * @param worldWindow the {@link WorldWindow} to set.
     */
    public GotoAction(final JFrame frame, final WorldWindow worldWindow) {
        super("Goto");

        this.gotoDialog = new GotoDialog(frame, worldWindow);
        this.gotoDialog.setLocationRelativeTo(frame);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.gotoDialog.setVisible(true);
    }
}
