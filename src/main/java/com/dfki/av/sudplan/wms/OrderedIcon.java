/*
 * OrderedIcon.java
 *
 *  Created by DFKI AV on 22.08.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.OrderedRenderable;

/**
 *
 * @author tarek
 */
/**
 * Ordered Icon class is used here to draw the side bar. It implements
 * orderedRenderable {@link gov.nasa.worldwind.render.OrderedRenderable}and is
 * displayed along with the controls annotations.
 */
class OrderedIcon implements OrderedRenderable {

    /**
     * ControlLayer {@link com.dfki.av.sudplan.wms.WMSControlLayer} to which
     * this OrderedIcon is associated.
     */
    private WMSControlLayer cl;

    /**
     * Constructs a new Instance of the OrderedIcon object that is to be drawn
     * with the COntrolLayer {@link com.dfki.av.sudplan.wms.WMSControlLayer}
     * instance passed in the input parameter.
     *
     * @param cl ControlLayer which this orderedIcon is associated to.
     */
    public OrderedIcon(WMSControlLayer cl) {
        super();
        this.cl = cl;
    }

    @Override
    public double getDistanceFromEye() {
        return 0;
    }

    @Override
    public void pick(DrawContext dc, java.awt.Point pickPoint) {
        cl.draw(dc);
    }

    @Override
    public void render(DrawContext dc) {
        cl.draw(dc);
    }
}
