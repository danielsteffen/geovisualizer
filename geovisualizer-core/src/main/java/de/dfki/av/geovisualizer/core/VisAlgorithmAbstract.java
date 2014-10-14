/*
 * VisAlgorithmAbstract.java
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import de.dfki.av.geovisualizer.core.functions.ColorTransferFunction;
import de.dfki.av.geovisualizer.core.render.Legend;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public abstract class VisAlgorithmAbstract implements IVisAlgorithm {

    /*
     * Logger.
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());
    /**
     * The {@link Icon} to be shown.
     */
    private Icon icon;
    /**
     * The {@link #description} of the visualization technique.
     */
    private String description;
    /**
     * The {@link #name} of the visualization.
     */
    private String name;
    /**
     * The {@link List} of {@link IVisParameter} for the visualization.
     */
    private List<IVisParameter> visParameters;
    /**
     * Instance for property change support.
     */
    private PropertyChangeSupport propertySupport;
    /**
     * The progress of the visualization process. Used for UI.
     */
    private int progress;
    /**
     * The {@link SelectListener} to be used from outside to control the
     * visualization. May be {@code null} if none is available.
     */
    private SelectListener selectListener;

    /**
     * Constructor. Sets{@link #name} to "Default Visualization",
     * {@link #description} to "No description available.", and
     * "icon-missing.png".
     */
    public VisAlgorithmAbstract() {
        this("Default Visualization");
    }

    /**
     * Constructor. Sets {@link #description} to "No description available.",
     * and "icon-missing.png".
     *
     * @param name the name to set.
     */
    public VisAlgorithmAbstract(String name) {
        this(name, "No description available.");
    }

    /**
     * Constructor. Sets {@link #icon} to and "icon-missing.png".
     *
     * @param name the name to set.
     * @param description the description to set.
     */
    public VisAlgorithmAbstract(String name, String description) {
        this(name,
                description,
                new ImageIcon(VisAlgorithmAbstract.class.getClassLoader().
                getResource("icons/icon-missing.png")));
    }

    /**
     * Constructor.
     *
     * @param name the name to set.
     * @param description the description to set.
     * @param icon the icon to set
     */
    public VisAlgorithmAbstract(String name, String description, Icon icon) {
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.visParameters = new ArrayList<>();
        this.propertySupport = new PropertyChangeSupport(this);
        this.progress = 0;
    }

    @Override
    public Icon getIcon() {
        return this.icon;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertySupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertySupport.removePropertyChangeListener(listener);
    }

    @Override
    public List<IVisParameter> getVisParameters() {
        return this.visParameters;
    }

    /**
     * Add a {@link IVisParameter} to the {@link #visParameters}.
     *
     * @param p the parameter to set.
     * @throws IllegalArgumentException if argument is {@code null}.
     */
    protected void addVisParameter(IVisParameter p) {
        if (p == null) {
            String errorMessage = "Invalid argument. Parameter set to null";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        this.visParameters.add(p);
    }

    /**
     * Returns a list of {@link Legend} elements depending on the
     * {@link #visParameters} and the {@link ITransferFunction} added to to
     * {@link IVisParameter}. Currently, only legends for
     * {@link ColorTransferFunction}s are supported.
     *
     * @param name the name for the {@link Legend} to set.
     * @return the list of {@link Legend} elements to return.
     */
    protected List<Layer> createLegends(String name) {
        if (name == null || name.isEmpty()) {
            name = getName();
            log.warn("Invalid argument. Passing empty String or null. Using default name.");
        }

        List<Layer> legendList = new ArrayList<>();
        List<IVisParameter> parameterList = getVisParameters();
        for (IVisParameter iVisParameter : parameterList) {
            ITransferFunction function = iVisParameter.getTransferFunction();
            if (function instanceof ColorTransferFunction) {
                ColorTransferFunction ctf = (ColorTransferFunction) function;
                String legendTitle = name + " - " + iVisParameter.getName();
                Legend legend = ctf.getLegend(legendTitle);
                if (legend != null) {
                    RenderableLayer rLayer = new RenderableLayer();
                    rLayer.setName(name + "-Legend-" + iVisParameter.getName());
                    rLayer.addRenderable(legend);
                    rLayer.setEnabled(false);
                    legendList.add(rLayer);
                }
            }
        }
        return legendList;
    }

    /**
     * Check whether the attribute is valid
     *
     * @param attribute the attribute to check.
     * @return the attribute as {@link String} element to return.
     */
    protected String checkAttribute(Object attribute) {
        String ret = IVisAlgorithm.NO_ATTRIBUTE;
        if (attribute != null) {
            if (attribute instanceof String) {
                ret = (String) attribute;
                if (ret.isEmpty()) {
                    ret = IVisAlgorithm.NO_ATTRIBUTE;
                    log.warn("Attribute is empty. Setting attribute to default.");
                }
            } else {
                log.warn("Attribute is instance of {}. Attribute set to default.",
                        attribute.getClass().getSimpleName());
            }
        } else {
            log.warn("Attribute is null. Setting attribute to default.");
        }
        return ret;
    }

    /**
     * Sets the progress value of the visualization algorithm where
     * <code>0<=value<=100</code>. If
     * <code>value < 0</code> the {@link #progress} is set to 0. If the
     * <code>value > 100</code> the {@link #progress} is set to 100.
     *
     * @param value the progress to set.
     */
    protected void setProgress(int value) {
        if (value < 0) {
            value = 0;
        }
        if (value > 100) {
            value = 100;
        }
        int oldValue = this.progress;
        this.progress = value;
        this.propertySupport.firePropertyChange(IVisAlgorithm.PROGRESS_PROPERTY, oldValue, this.progress);
    }

    @Override
    public SelectListener getSelectListener() {
        return this.selectListener;
    }

    /**
     * Set the {@link SelectListener} for the visualization to control it.
     *
     * @param listener the {@link SelectListener} to set.
     */
    public void setSelectListener(SelectListener listener) {
        this.selectListener = listener;
    }
}
