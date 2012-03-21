package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.vis.core.ITransferFunction;
import com.dfki.av.sudplan.vis.spi.ITransferFunctionProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author steffen
 */
public class BasicTransferFunctionProvider implements ITransferFunctionProvider {

    @Override
    public List<String> getTransferFunctions() {
        List<String> tfList = new ArrayList<String>();
        // Color transferfunctions.
        tfList.add(ConstantColor.class.getName());
        tfList.add(ColorrampCategorization.class.getName());
        tfList.add(ColorRuleClassification.class.getName());
        tfList.add(RedGreenColorrampClassification.class.getName());
        tfList.add(ColorrampClassification.class.getName());
        // Number transferfunctions.
        tfList.add(ConstantNumber.class.getName());
        tfList.add(ScalarMultiplication.class.getName());
        tfList.add(IdentityFunction.class.getName());
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
        } else if (name.equalsIgnoreCase(ConstantNumber.class.getName())) {
            return new ConstantNumber();
        } else if (name.equalsIgnoreCase(ScalarMultiplication.class.getName())) {
            return new ScalarMultiplication();
        } else if (name.equalsIgnoreCase(IdentityFunction.class.getName())) {
            return new IdentityFunction();
        } else if (name.equalsIgnoreCase(ColorrampClassification.class.getName())) {
            return new ColorrampClassification();
        } 
        return null;
    }
}
