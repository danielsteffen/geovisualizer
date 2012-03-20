/*
 *  ColorTransferFunction.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.vis.core.ITransferFunction;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public abstract class ColorTransferFunction implements ITransferFunction {

    /*
     * The logger for the all <code>ColorTransferFunction</code>.
     */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    
    /**
     * The {@link List} of {@link Color} objects used by the transfer function.
     */
    protected List<Color> colorList;

    /**
     * Creates a {@link ColorTransferFunction}.
     */
    public ColorTransferFunction() {
        this.colorList = new ArrayList<Color>();
    }
}
