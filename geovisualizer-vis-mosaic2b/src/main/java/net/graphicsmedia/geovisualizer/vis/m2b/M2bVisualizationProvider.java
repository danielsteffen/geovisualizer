/**
 * M2bVisualizationProvider.java
 *
 * Created by <a href="mailto:daniel.steffen@graphicsmedia.net">Daniel
 * Steffen</a> on 01.08.2014.
 *
 * Copyright (c) 2014 MOSAIC 2B Consortium Members. All rights reserved.
 *
 * This software is developed as part of the project MOSAIC 2B
 * (http://www.mobile-empowerment.org) and has received funding from the
 * European Union’s Seventh Framework Programme for research, technological
 * development and demonstration under grant agreement no 611796.
 */
package net.graphicsmedia.geovisualizer.vis.m2b;

import de.dfki.av.geovisualizer.core.IVisAlgorithm;
import de.dfki.av.geovisualizer.core.spi.IVisAlgorithmProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Daniel Steffen
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
