/*
 * IPlugin.java
 *
 * Created by DFKI AV on 12.03.2014.
 * Copyright (c) 2011-2014 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.plugins;

import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

/**
 * Interface definition for plugins.
 */
public interface IPlugin extends PropertyChangeListener {

    /**
     * Return the name of the plug-in. The name will be used and be displayed in
     * the menu bar under 'Tools - Plug-ins'.
     *
     * @return the name of the plug-in to return.
     */
    String getName();

    /**
     * Add a {@link PropertyChangeListener} to the {@link IPlugin}. Can be used
     * to receive information from the plug-in.
     *
     * @param listener the {@link PropertyChangeListener} to add.
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove a {@link PropertyChangeListener} from this {@code IVisAlgorithm}.
     *
     * @param listener the {@link PropertyChangeListener} to remove.
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Return the {@link AbstractAction} to be executed when the
     * {@link JMenuItem} in the plugin menubar is pressed.
     *
     * @return the {@link AbstractAction} to return.
     */
    AbstractAction getAbstractAction();
}
