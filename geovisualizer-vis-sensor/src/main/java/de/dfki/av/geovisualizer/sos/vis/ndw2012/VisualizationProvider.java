/*
 *  VisualizationProvider.java 
 *
 *  Created by DFKI AV on 26.01.2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.sos.vis.ndw2012;

import de.dfki.av.geovisualizer.core.IVisAlgorithm;
import de.dfki.av.geovisualizer.core.spi.IVisAlgorithmProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisualizationProvider implements IVisAlgorithmProvider {

    @Override
    public List<String> getVisualizationNames() {
        List<String> visualizationList = new ArrayList<>();
        visualizationList.add(VisNachtDieWissenSchafft.class.getName());
        visualizationList.add(VisNachtDieWissenSchafftBars.class.getName());

        return Collections.unmodifiableList(visualizationList);
    }

    @Override
    public IVisAlgorithm get(String name) {
        if (VisNachtDieWissenSchafft.class.getName().equalsIgnoreCase(name)) {
            return new VisNachtDieWissenSchafft();
        }
        if (VisNachtDieWissenSchafftBars.class.getName().equalsIgnoreCase(name)) {
            return new VisNachtDieWissenSchafftBars();
        }
        
        return null;
    }
}
