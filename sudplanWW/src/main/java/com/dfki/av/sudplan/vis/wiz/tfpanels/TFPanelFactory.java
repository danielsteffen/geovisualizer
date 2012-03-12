package com.dfki.av.sudplan.vis.wiz.tfpanels;

import com.dfki.av.sudplan.vis.algorithm.functions.*;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonGroup;

/**
 *
 * @author steffen
 */
public class TFPanelFactory {

    public static TFPanel get(final ITransferFunction f, final List<String> attributes, ButtonGroup bg, ActionListener l, boolean b) {
        TFPanel panel = null;
        if (f instanceof RedGreenColorrampClassification) {
            RedGreenColorrampClassification t = (RedGreenColorrampClassification) f;
            panel = new TFPRedGreenColorrampTransferFunction(t, attributes, bg, l, b);
        } else if (f instanceof ColorrampClassification) {
            ColorrampClassification function = (ColorrampClassification) f;
            panel = new TFPColorrampTransferFunction(function, attributes, bg, l, b);
        } else if (f instanceof ConstantColorTransferFunction) {
            ConstantColorTransferFunction function = (ConstantColorTransferFunction) f;
            panel = new TFPConstantColorTransferFunction(function, attributes, bg, l, b);
        } else if (f instanceof IdentityFunction) {
            IdentityFunction function = (IdentityFunction) f;
            panel = new TFPIdentityFunction(function, attributes, bg, l, b);
        } else if (f instanceof ScalarMultiplication) {
            ScalarMultiplication function = (ScalarMultiplication) f;
            panel = new TFPScalarMultiplication(function, attributes, bg, l, b);
        } else if (f instanceof ConstantNumberTansferFunction) {
            ConstantNumberTansferFunction function = (ConstantNumberTansferFunction) f;
            panel = new TFPConstantNumberTransferFunction(function, attributes, bg, l, b);
        } else if (f instanceof ColorrampCategorization) {
            ColorrampCategorization function = (ColorrampCategorization) f;
            panel = new TFPColorrampCategorization(function, attributes, bg, l, b);
        } else if(f instanceof ColorRuleClassification){
            ColorRuleClassification function = (ColorRuleClassification) f;
            panel = new TFPColorRuleClassification(function, attributes, bg, l, b);
        }
        return panel;
    }
}
