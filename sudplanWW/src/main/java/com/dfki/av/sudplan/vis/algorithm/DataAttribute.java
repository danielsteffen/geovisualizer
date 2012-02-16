/*
 *  DataAttribute.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class DataAttribute {
    /*
     * Logger.
     */

    private static final Logger log = LoggerFactory.getLogger(DataAttribute.class);
    /**
     *
     */
    public final static int CLASSIFICATION_OFF = 0;
    /**
     *
     */
    public final static int CLASSIFICATION_AUTO = 1;
    /*
     * The classification method for the data attribute.
     */
    private int classification;
    /*
     * The attribute of the data source.
     */
    private String attribute;

    /**
     * Creates a
     * <code>DataAttribute</code> object for the data attribute
     * {@link #attribute}. The classification parameter for this attribute is
     * set to {@link DataAttribute#CLASSIFICATION_OFF}.
     *
     * @param attribute the attribute of the data source to visualize.
     * @throws IllegalArgumentException
     */
    public DataAttribute(String attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Parameter name for VisParameter equals null.");
        }
        this.attribute = attribute;
        this.classification = DataAttribute.CLASSIFICATION_OFF;
    }

    /**
     * Returns the {@link #classification} mode of the {@link #attribute}.
     * 
     * @return the {@link #classification} mode to return.
     */
    public int getClassificationMode() {
        return this.classification;
    }
}
