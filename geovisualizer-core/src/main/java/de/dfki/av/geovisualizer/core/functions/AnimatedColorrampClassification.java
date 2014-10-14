/*
 * AnimatedColorrampClassification.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.ISource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class AnimatedColorrampClassification extends ColorrampClassification {

    private List<String> timesteps;

    public AnimatedColorrampClassification() {
        super();
        timesteps = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "Animated color ramp";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
        super.preprocess(data, attribute);
    }

    @Override
    public Object calc(Object o) {
        return null;
    }

    public void addTimestepAttribute(String timestep) {
        timesteps.add(timestep);
    }
}
