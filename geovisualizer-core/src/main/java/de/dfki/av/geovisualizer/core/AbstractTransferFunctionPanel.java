/*
 * AbstractTransferFunctionPanel.java
 *
 * Created by DFKI AV on 01.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public abstract class AbstractTransferFunctionPanel extends JPanel {

    /**
     * Return the selected attribute.
     *
     * @return the selected attribute to return.
     */
    public abstract String getSelectedAttribute();

    /**
     * Set the available attributes to be shown by a {@link JComboBox} etc. Gets
     * a {@link List} of {@code String} arrays with the entries attribute name
     * and data type.
     *
     * @param attributes the attributes to set.
     * @return Returns {@code true} if the number of attributes added 
     * to the {@link JComboBox} is larger than 0.
     */
    public abstract boolean setAttributes(final List<String[]> attributes);
}
