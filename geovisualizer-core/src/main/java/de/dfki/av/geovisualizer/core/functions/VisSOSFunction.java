/**
 * VisSOSFunction.java
 *
 * Created by DFKI AV on 21.09.2012. Copyright (c) 2011-2012 DFKI GmbH,
 * Kaiserslautern. All rights reserved. Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.functions.ColorClassification;
import de.dfki.av.geovisualizer.core.functions.NumberInterval;
import de.dfki.av.geovisualizer.core.functions.ui.TFPVisSOSFunction;
import java.awt.Color;

/**
 * Sensor visualization color transfere function..
 *
 * @author Tobias Zimmermann <tobias.zimmermann@dfki.de>
 */
public class VisSOSFunction extends ColorClassification {

    /**
     * The UI for the transfer function.
     */
    private AbstractTransferFunctionPanel panel;

    /**
     * Constructor.
     */
    public VisSOSFunction() {
        super();
        addClassification(new NumberInterval(), Color.GRAY);
    }

    @Override
    public String getName() {
        return "SOS Vis Function";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
        log.debug("No pre-processing required.");
    }

    @Override
    public AbstractTransferFunctionPanel getPanel() {
        if (this.panel == null) {
            this.panel = new TFPVisSOSFunction(this);
        }
        return this.panel;
    }
}