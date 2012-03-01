/*
 *  IdentityFunction.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class IdentityFunction extends ScalarMultiplication {
    /*
     *
     */
    private static final Logger log = LoggerFactory.getLogger(IdentityFunction.class);

    /**
     * 
     */
    public IdentityFunction() {
        super();
    }
    
    @Override
    public String getName(){
        return "Identity Function";
    }
}
