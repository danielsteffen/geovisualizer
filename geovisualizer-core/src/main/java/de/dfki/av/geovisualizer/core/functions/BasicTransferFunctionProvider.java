/*
 * BasicTransferFunctionProvider.java
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions;

import de.dfki.av.geovisualizer.core.ITransferFunction;
import de.dfki.av.geovisualizer.core.spi.ITransferFunctionProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class BasicTransferFunctionProvider implements ITransferFunctionProvider {

    @Override
    public List<String> getTransferFunctions() {
        List<String> tfList = new ArrayList<>();
        // Color transferfunctions.
        tfList.add(ConstantColor.class.getName());
        tfList.add(ColorrampCategorization.class.getName());
        tfList.add(ColorRuleClassification.class.getName());
        tfList.add(RedGreenColorrampClassification.class.getName());
        tfList.add(ColorrampClassification.class.getName());
        tfList.add(NO2ColorClassification.class.getName());
        // Number transferfunctions.
        tfList.add(ConstantNumber.class.getName());
        tfList.add(ScalarMultiplication.class.getName());
        tfList.add(IdentityFunction.class.getName());
        tfList.add(UnitIntervalMapping.class.getName());
        tfList.add(AffineTransformation.class.getName());
        tfList.add(UnitIntervalMappingSpecial.class.getName());
        tfList.add(VisSOSFunction.class.getName());
        return Collections.unmodifiableList(tfList);
    }

    @Override
    public ITransferFunction get(String name) {
        if (name.equalsIgnoreCase(ConstantColor.class.getName())) {
            return new ConstantColor();
        } else if (name.equalsIgnoreCase(ColorrampCategorization.class.getName())) {
            return new ColorrampCategorization();
        } else if (name.equalsIgnoreCase(ColorRuleClassification.class.getName())) {
            return new ColorRuleClassification();
        } else if (name.equalsIgnoreCase(RedGreenColorrampClassification.class.getName())) {
            return new RedGreenColorrampClassification();
        } else if (name.equalsIgnoreCase(NO2ColorClassification.class.getName())) {
            return new NO2ColorClassification();
        } else if (name.equalsIgnoreCase(ConstantNumber.class.getName())) {
            return new ConstantNumber();
        } else if (name.equalsIgnoreCase(ScalarMultiplication.class.getName())) {
            return new ScalarMultiplication();
        } else if (name.equalsIgnoreCase(IdentityFunction.class.getName())) {
            return new IdentityFunction();
        } else if (name.equalsIgnoreCase(ColorrampClassification.class.getName())) {
            return new ColorrampClassification();
        } else if (name.equalsIgnoreCase(UnitIntervalMapping.class.getName())) {
            return new UnitIntervalMapping();
        } else if (name.equalsIgnoreCase(AffineTransformation.class.getName())) {
            return new AffineTransformation();
        } else if (name.equalsIgnoreCase(UnitIntervalMappingSpecial.class.getName())) {
            return new UnitIntervalMappingSpecial();
        } else if (name.equalsIgnoreCase(VisSOSFunction.class.getName())){
            return new VisSOSFunction();
        }
        return null;
    }
}
