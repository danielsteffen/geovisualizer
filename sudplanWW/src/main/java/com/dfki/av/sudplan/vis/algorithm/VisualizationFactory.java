/*
 *  VisualizationFactory.java 
 *
 *  Created by DFKI AV on 26.01.2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.vis.IVisAlgorithm;
import com.dfki.av.sudplan.vis.IVisAlgorithmFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author steffen
 */
public class VisualizationFactory implements IVisAlgorithmFactory {

    @Override
    public List<String> getVisualizationNames() {
        List<String> visualizationList = new ArrayList<String>();
        visualizationList.add(VisBuildings.class.getSimpleName());
        visualizationList.add(VisExtrudePolyline.class.getSimpleName());
        visualizationList.add(VisExtrudePolygon.class.getSimpleName());
        visualizationList.add(VisTimeseries.class.getSimpleName());
        visualizationList.add(VisGeoCPM.class.getSimpleName());
        visualizationList.add(VisPointCloud.class.getSimpleName());
        
        return Collections.unmodifiableList(visualizationList);
    }

    @Override
    public IVisAlgorithm get(String name) {
        if (VisExtrudePolygon.class.getSimpleName().equalsIgnoreCase(name)) {
            return new VisExtrudePolygon();
        } else if (VisExtrudePolyline.class.getSimpleName().equalsIgnoreCase(name)) {
            return new VisExtrudePolyline();
        } else if (VisTimeseries.class.getSimpleName().equalsIgnoreCase(name)) {
            return new VisTimeseries();
        } else if (VisPointCloud.class.getSimpleName().equalsIgnoreCase(name)) {
            return new VisPointCloud();
        } else if (VisBuildings.class.getSimpleName().equalsIgnoreCase(name)) {
            return new VisBuildings();
        } else if (VisGeoCPM.class.getSimpleName().equalsIgnoreCase(name)){
            return new VisGeoCPM();
        }
        return null;
    }
}
