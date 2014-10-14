/*
 * BasicTransferFunctionPanelProvider.java
 *
 * Created by DFKI AV on 26.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions.ui;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.ITransferFunction;
import de.dfki.av.geovisualizer.core.functions.*;
import de.dfki.av.geovisualizer.core.spi.ITransferFunctionPanelProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class BasicTransferFunctionPanelProvider implements ITransferFunctionPanelProvider {

    @Override
    public List<String> getTFPanels() {
        List<String> tfPanels = new ArrayList<>();
        tfPanels.add(TFPConstantColor.class.getName());
        tfPanels.add(TFPColorRuleClassification.class.getName());
        tfPanels.add(TFPColorrampCategorization.class.getName());
        tfPanels.add(TFPColorrampClassification.class.getName());
        tfPanels.add(TFPRedGreenColorrampClassification.class.getName());
        tfPanels.add(TFPConstantNumber.class.getName());
        tfPanels.add(TFPScalarMultiplication.class.getName());
        tfPanels.add(TFPIdentityFunction.class.getName());
        tfPanels.add(TFPVisSOSFunction.class.getName());
        return Collections.unmodifiableList(tfPanels);
    }

    @Override
    public AbstractTransferFunctionPanel get(ITransferFunction f) {

        AbstractTransferFunctionPanel panel = null;
        if (f instanceof RedGreenColorrampClassification) {
            RedGreenColorrampClassification function = (RedGreenColorrampClassification) f;
            panel = new TFPRedGreenColorrampClassification(function);
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
        } else if (f instanceof VisSOSFunction){
            VisSOSFunction function = (VisSOSFunction)f;
            panel = new TFPVisSOSFunction(function);
        }

        return panel;
    }
}
