/*
 *  BasicVisualizationProvider.java 
 *
 *  Created by DFKI AV on 26.01.2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.basic;

import com.dfki.av.sudplan.vis.core.IVisAlgorithm;
import com.dfki.av.sudplan.vis.spi.IVisAlgorithmProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author steffen
 */
public class BasicVisualizationProvider implements IVisAlgorithmProvider {

    @Override
    public List<String> getVisualizationNames() {
        List<String> visualizationList = new ArrayList<String>();
        visualizationList.add(VisBuildings.class.getName());
        visualizationList.add(VisExtrudePolyline.class.getName());
        visualizationList.add(VisExtrudePolygon.class.getName());
        visualizationList.add(VisTimeseries.class.getName());
        visualizationList.add(VisPointCloud.class.getName());
        
        return Collections.unmodifiableList(visualizationList);
    }

    @Override
    public IVisAlgorithm get(String name) {
        if (VisExtrudePolygon.class.getName().equalsIgnoreCase(name)) {
            return new VisExtrudePolygon();
        } else if (VisExtrudePolyline.class.getName().equalsIgnoreCase(name)) {
            return new VisExtrudePolyline();
        } else if (VisTimeseries.class.getName().equalsIgnoreCase(name)) {
            return new VisTimeseries();
        } else if (VisPointCloud.class.getName().equalsIgnoreCase(name)) {
            return new VisPointCloud();
        } else if (VisBuildings.class.getName().equalsIgnoreCase(name)) {
            return new VisBuildings();
        } 
        return null;
    }
}
