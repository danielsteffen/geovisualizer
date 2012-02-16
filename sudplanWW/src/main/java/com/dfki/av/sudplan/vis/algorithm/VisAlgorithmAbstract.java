/*
 *  VisAlgorithmAbstract.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

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
     * Parameter definition of the visualization
     */
    protected List<VisParameter> parameters;

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
        this.parameters = new ArrayList<VisParameter>();
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
    public List<VisParameter> getVisParameter(){
        return this.parameters;
    }
}
