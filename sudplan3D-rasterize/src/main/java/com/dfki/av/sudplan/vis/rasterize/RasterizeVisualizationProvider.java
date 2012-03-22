/*
 *  DelaunayVisualizationProvider.java 
 *
 *  Created by DFKI AV on 16.03.2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.rasterize;

import com.dfki.av.sudplan.vis.core.IVisAlgorithm;
import com.dfki.av.sudplan.vis.spi.IVisAlgorithmProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author steffen
 */
public class RasterizeVisualizationProvider implements IVisAlgorithmProvider {

    @Override
    public List<String> getVisualizationNames() {
        List<String> visualizationList = new ArrayList<String>();
        visualizationList.add(VisRasterize.class.getName());

        return Collections.unmodifiableList(visualizationList);
    }

    @Override
    public IVisAlgorithm get(String name) {
        if (VisRasterize.class.getName().equalsIgnoreCase(name)) {
            return new VisRasterize();
        }
        return null;
    }
}
