/*
 * IVisAlgorithm.java
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.layers.Layer;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.Icon;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface IVisAlgorithm {

    /**
     * Returns a {@link List} of {@link IVisParameter} for this algorithm.
     *
     * @return the {@link List} of {@link IVisParameter} to return
     */
    List<IVisParameter> getVisParameters();

    /**
     * Returns the {@link Icon} for this visualization.
     *
     * @return the {@link Icon} to return.
     */
    Icon getIcon();

    /**
     * Return the name of the visualization algorithm.
     *
     * @return the name to return.
     */
    String getName();

    /**
     * Return the description of this visualization algorithm.
     *
     * @return the description to return.
     */
    String getDescription();

    /**
     * Creates a {@link List} of {@link Layer} containing the visual elements.
     *
     * @param data the data set used for the visualization.
     * @param attributes the attributes from the data set to be used.
     * @return the {@link List} of {@link Layer} to return
     */
    List<Layer> createLayersFromData(Object data, Object[] attributes);

    /**
     * Add a {@link PropertyChangeListener} to the {@link IVisAlgorithm}. Can be
     * used to get information about the progress of the creation process.
     *
     * @param listener the {@link PropertyChangeListener} to add.
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove a {@link PropertyChangeListener} from this {@link IVisAlgorithm}.
     *
     * @param listener the {@link PropertyChangeListener} to remove.
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Returns the {@link SelectListener} for the visualization if set or
     * {@code null} otherwise.
     *
     * @return the {@link SelectListener} to return.
     */
    SelectListener getSelectListener();
    /**
     *
     */
    String NO_ATTRIBUTE = "<<NO_ATTRIBUTE>>";
    /**
     *
     */
    String PROGRESS_PROPERTY = "ivisalgorithm.progress";
}
