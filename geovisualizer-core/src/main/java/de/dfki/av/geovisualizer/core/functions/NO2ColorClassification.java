/*
 * NO2ColorClassification.java
 *
 * Created by DFKI AV on 20.09.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.functions.ui.TFPNO2ColorClassification;
import java.awt.Color;

/**
 * NO2 color classification.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class NO2ColorClassification extends ColorClassification {

    /**
     * The UI for the transfer function.
     */
    private AbstractTransferFunctionPanel panel;

    /**
     * Constructor.
     */
    public NO2ColorClassification() {
        super();
        NumberInterval ni = new NumberInterval(Double.valueOf(0),
                Double.valueOf(30));
        addClassification(ni, Color.GREEN);
        ni = new NumberInterval(Double.valueOf(30), Double.valueOf(40));
        addClassification(ni, Color.YELLOW);
        ni = new NumberInterval(Double.valueOf(40), Double.valueOf(100));
        addClassification(ni, Color.ORANGE);
        ni = new NumberInterval(Double.valueOf(100), Double.valueOf(300));
        addClassification(ni, Color.MAGENTA);
        ni = new NumberInterval(Double.valueOf(300), Double.valueOf(400));
        addClassification(ni, Color.RED);
        ni = new NumberInterval(Double.valueOf(400), Double.valueOf(2000));
        addClassification(ni, Color.WHITE);
    }

    @Override
    public String getName() {
        return "NO2 color classification";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
        log.debug("No pre-processing required.");
    }

    @Override
    public AbstractTransferFunctionPanel getPanel() {
        if (this.panel == null) {
            this.panel = new TFPNO2ColorClassification();
        }
        return this.panel;
    }
}
