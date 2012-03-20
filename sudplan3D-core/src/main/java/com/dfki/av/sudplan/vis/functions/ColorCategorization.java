/*
 *  ColorCategorization.java 
 *
 *  Created by DFKI AV on 09.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.vis.core.ICategory;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author steffen
 */
public abstract class ColorCategorization extends ColorTransferFunction {

    /**
     *
     */
    protected List<ICategory> categories;

    /**
     *
     */
    public ColorCategorization() {
        super();
        this.categories = new ArrayList<ICategory>();
    }
}
