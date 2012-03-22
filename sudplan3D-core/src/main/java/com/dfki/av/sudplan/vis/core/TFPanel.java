/*
 *  TFPanel.java 
 *
 *  Created by DFKI AV on 01.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.core;

import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

/**
 *
 * @author steffen
 */
public abstract class TFPanel extends JPanel{
    /**
     * 
     * @return 
     */
    public abstract String getSelectedAttribute();
    /**
     * 
     * @param attributes 
     */
    public abstract boolean setAttributes(final List<String[]> attributes);
    /**
     * 
     * @param bg 
     */
    public abstract void setButtonGroup(final ButtonGroup bg);
    /**
     * 
     * @param l 
     */
    public abstract void setActionListener(ActionListener l);
    /**
     * 
     * @param b 
     */
    public abstract void setSelected(boolean b);
}
