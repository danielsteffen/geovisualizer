package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.vis.core.ISource;
import java.awt.Color;

/**
 *
 * @author steffen
 */
public class ColorRuleClassification extends ColorClassification {

    /**
     *
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
    }
}
