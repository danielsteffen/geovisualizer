/*
 * ColorRuleClassification.java
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.functions.ui.TFPColorRuleClassification;
import java.awt.Color;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ColorRuleClassification extends ColorClassification {

    /**
     * The UI for the transfer function.
     */
    private AbstractTransferFunctionPanel panel;

    /**
     * Constructor.
     */
    public ColorRuleClassification() {
        super();
        addClassification(new NumberInterval(), Color.GRAY);
    }

    @Override
    public String getName() {
        return "Color rules";
    }

    @Override
    public void preprocess(ISource data, String attribute) {
        log.debug("No pre-processing required.");
    }

    @Override
    public AbstractTransferFunctionPanel getPanel() {
        if (this.panel == null) {
            this.panel = new TFPColorRuleClassification(this);
        }
        return this.panel;
    }
}
