/*
 *  VisAlgorithmAbstract.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.core;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public abstract class VisAlgorithmAbstract implements IVisAlgorithm {

    /*
     * Logger.
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private Icon icon;
    /**
     *
     */
    private String desription;
    /**
     *
     */
    private String name;
    /**
     *
     */
    private List<IVisParameter> visParameters;

    /**
     *
     */
    public VisAlgorithmAbstract() {
        this("Default Visualization");
    }

    /**
     *
     * @param name
     */
    public VisAlgorithmAbstract(String name) {
        this(name, "No description available.");
    }

    /**
     * @param n
     * @param d
     */
    public VisAlgorithmAbstract(String name, String description) {
        this(name,
                description,
                new ImageIcon(VisAlgorithmAbstract.class.getClassLoader().
                getResource("icons/icon-missing.png")));
    }

    /**
     *
     * @param i
     * @param d
     */
    public VisAlgorithmAbstract(String n, String d, Icon i) {
        this.icon = i;
        this.name = n;
        this.desription = d;
        this.visParameters = new ArrayList<IVisParameter>();
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
        return this.desription;
    }

    @Override
    public List<IVisParameter> getVisParameters() {
        return this.visParameters;
    }

    /**
     *
     * @param p
     */
    protected void addVisParameter(IVisParameter p) {
        this.visParameters.add(p);
    }

    /**
     *
     * @param attribute
     * @return
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
}
