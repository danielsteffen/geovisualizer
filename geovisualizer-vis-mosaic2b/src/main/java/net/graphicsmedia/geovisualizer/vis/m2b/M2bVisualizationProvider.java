/*
 * M2bVisualizationProvider.java
 *
 * Created by DFKI AV on 24.10.2014.
 * Copyright (c) 2011-2014 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package net.graphicsmedia.geovisualizer.vis.m2b;

import de.dfki.av.geovisualizer.core.IVisAlgorithm;
import de.dfki.av.geovisualizer.core.spi.IVisAlgorithmProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class M2bVisualizationProvider implements IVisAlgorithmProvider {

    @Override
    public List<String> getVisualizationNames() {
        List<String> visualizationList = new ArrayList<>();
        visualizationList.add(VisAnnotations.class.getName());

        return Collections.unmodifiableList(visualizationList);
    }

    @Override
    public IVisAlgorithm get(String name) {
        IVisAlgorithm algorithm = null;
        if (VisAnnotations.class.getName().equalsIgnoreCase(name)) {
            algorithm = new VisAnnotations();
        }
        return algorithm;
    }
}
