/*
 * ColorTransferFunction.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.ITransferFunction;
import de.dfki.av.geovisualizer.core.render.Legend;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public abstract class ColorTransferFunction implements ITransferFunction {

    /*
     * The logger for the all <code>ColorTransferFunction</code>.
     */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * The {@link List} of {@link Color} objects used by the transfer function.
     */
    protected List<Color> colorList;
    /**
     * The {@link Legend} for the transfer function.
     */
    private Legend legend;

    /**
     * Creates a {@link ColorTransferFunction}.
     */
    public ColorTransferFunction() {
        this.colorList = new ArrayList<>();
        this.legend = null;
    }

    /**
     * Returns the {@link Legend} for the transfer function.
     *
     * @return the {@link Legend} to return. Returns {@code null} if no
     * {@code Legend} is available.
     */
    public Legend getLegend() {
        return this.legend;
    }

    /**
     * Set the {@link Legend} for this transfer function.
     *
     * @param legend the{@link Legend} to set.
     */
    public void setLegend(Legend legend) {
        this.legend = legend;
    }

    public abstract Legend getLegend(String title);
}
