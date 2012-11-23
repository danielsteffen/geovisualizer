/*
 *  ExitAction.java 
 *
 *  Created by DFKI AV on 23.11.2012.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ExitAction extends AbstractAction {

    public ExitAction() {
        super("Exit GeoVisualizer");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}
