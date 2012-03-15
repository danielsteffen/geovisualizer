/*
 *  TFPanelFactory.java 
 *
 *  Created by DFKI AV on 26.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions.ui;

import com.dfki.av.sudplan.vis.ITransferFunction;
import com.dfki.av.sudplan.vis.TFPanel;
import com.dfki.av.sudplan.vis.functions.*;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonGroup;

/**
 *
 * @author steffen
 */
public class TFPanelFactory {

    /**
     *
     */
    private static TFPanelFactory factory = null;

    /**
     *
     * @return
     */
    public static TFPanelFactory getInstance() {
        if (factory == null) {
            factory = new TFPanelFactory();
        }
        return factory;
    }

    /**
     *
     */
    private TFPanelFactory() {
    }

    /**
     *
     * @param f
     * @param attributes
     * @param bg
     * @param l
     * @param b
     * @return
     */
    public TFPanel get(final ITransferFunction f, final List<String> attributes, ButtonGroup bg, ActionListener l, boolean b) {
        TFPanel panel = null;
        if (f instanceof RedGreenColorrampClassification) {
            RedGreenColorrampClassification t = (RedGreenColorrampClassification) f;
            panel = new TFPRedGreenColorrampClassification(t);
        } else if (f instanceof ColorrampClassification) {
            ColorrampClassification function = (ColorrampClassification) f;
            panel = new TFPColorrampClassification(function);
        } else if (f instanceof ConstantColor) {
            ConstantColor function = (ConstantColor) f;
            panel = new TFPConstantColor(function);
        } else if (f instanceof IdentityFunction) {
            IdentityFunction function = (IdentityFunction) f;
            panel = new TFPIdentityFunction(function);
        } else if (f instanceof ScalarMultiplication) {
            ScalarMultiplication function = (ScalarMultiplication) f;
            panel = new TFPScalarMultiplication(function);
        } else if (f instanceof ConstantNumber) {
            ConstantNumber function = (ConstantNumber) f;
            panel = new TFPConstantNumber(function);
        } else if (f instanceof ColorrampCategorization) {
            ColorrampCategorization function = (ColorrampCategorization) f;
            panel = new TFPColorrampCategorization(function);
        } else if (f instanceof ColorRuleClassification) {
            ColorRuleClassification function = (ColorRuleClassification) f;
            panel = new TFPColorRuleClassification(function);
        }
        panel.setAttributes(attributes);
        panel.setButtonGroup(bg);
        panel.setActionListener(l);
        panel.setSelected(b);
        
        
        return panel;
    }
}
