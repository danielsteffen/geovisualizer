/*
 * BasicVisualizationProvider.java
 *
 * Created by DFKI AV on 26.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.vis.basic;

import de.dfki.av.geovisualizer.core.IVisAlgorithm;
import de.dfki.av.geovisualizer.core.spi.IVisAlgorithmProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class BasicVisualizationProvider implements IVisAlgorithmProvider {

    @Override
    public List<String> getVisualizationNames() {
        List<String> visualizationList = new ArrayList<>();
        visualizationList.add(VisBuildings.class.getName());
        visualizationList.add(VisExtrudePolyline.class.getName());
        visualizationList.add(VisExtrudePolygon.class.getName());
        visualizationList.add(VisTimeseries.class.getName());
        visualizationList.add(VisPointCloudNew.class.getName());
        visualizationList.add(VisPins.class.getName());

        return Collections.unmodifiableList(visualizationList);
    }

    @Override
    public IVisAlgorithm get(String name) {
        IVisAlgorithm algorithm = null;
        if (VisExtrudePolygon.class.getName().equalsIgnoreCase(name)) {
            algorithm = new VisExtrudePolygon();
        } else if (VisExtrudePolyline.class.getName().equalsIgnoreCase(name)) {
            algorithm = new VisExtrudePolyline();
        } else if (VisTimeseries.class.getName().equalsIgnoreCase(name)) {
            algorithm = new VisTimeseries();
        } else if (VisBuildings.class.getName().equalsIgnoreCase(name)) {
            algorithm = new VisBuildings();
        } else if (VisPointCloudNew.class.getName().equalsIgnoreCase(name)) {
            algorithm = new VisPointCloudNew();
        } else if (VisPins.class.getName().equalsIgnoreCase(name)) {
            algorithm = new VisPins();
        }
        return algorithm;
    }
}
