/*
 *  Created by DFKI AV on 05/09/2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan;

import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.PlaceNamesPanel;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 *
 * @author steffen
 */
public class SudplanApp extends ApplicationTemplate {

    public static class AppFrame extends ApplicationTemplate.AppFrame {

        public AppFrame() {
            super(true, true, false);
            this.getLayerPanel().add(makeControlPanel(), BorderLayout.SOUTH);
        }

        private JPanel makeControlPanel() {
            return new PlaceNamesPanel(this.getWwd());
        }
    }

    public static void main(String[] args) {
        ApplicationTemplate.start("World Wind Place Names", AppFrame.class);
    }
}