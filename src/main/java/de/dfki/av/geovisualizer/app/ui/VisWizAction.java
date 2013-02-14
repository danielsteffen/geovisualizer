/*
 *  VisWizAction.java 
 *
 *  Created by DFKI AV on 23.11.2012.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.ui;

import de.dfki.av.geovisualizer.app.vis.VisualizationPanel;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisWizAction extends AbstractAction {

    /**
     *
     */
    private final VisualizationPanel panel;

    /**
     *
     * @param panel
     */
    public VisWizAction(VisualizationPanel panel) {
        super("Run VisWiz");
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        panel.runVisWiz();
    }
}