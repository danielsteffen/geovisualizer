/*
 * IdentityFunction.java
 *
 * Created by DFKI AV on 29.02.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.functions.ui.TFPIdentityFunction;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class IdentityFunction extends ScalarMultiplication {

    /**
     * The UI element for the transfer function.
     */
    private AbstractTransferFunctionPanel panel;

    @Override
    public String getName() {
        return "Identity Function";
    }

    @Override
    public AbstractTransferFunctionPanel getPanel() {
        if (this.panel == null) {
            this.panel = new TFPIdentityFunction(this);
        }
        return this.panel;
    }
}
