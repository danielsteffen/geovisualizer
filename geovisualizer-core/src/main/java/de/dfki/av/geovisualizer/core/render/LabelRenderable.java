/*
 * LabelRenderable.java
 *
 * Created by DFKI AV on 01.04.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.render;

import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class LabelRenderable implements Renderable {

    protected final OrderedLabel orderedLabel;

    public LabelRenderable(Legend legend, LabelAttributes attr, double x, double y,
            String halign, String valign) {
        this.orderedLabel = new OrderedLabel(legend, attr, x, y, halign, valign);
    }

    @Override
    public void render(DrawContext dc) {
        dc.addOrderedRenderable(this.orderedLabel);
    }
}
