/*
 *  TFPanel.java 
 *
 *  Created by DFKI AV on 01.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.wiz.tfpanels;

import javax.swing.JPanel;

/**
 *
 * @author steffen
 */
public abstract class TFPanel extends JPanel{
    public abstract String getSelectedAttribute();
}
