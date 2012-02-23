
package com.dfki.av.sudplan.vis.algorithm;

/**
 *
 * @author steffen
 */
public class AutoColorClassification implements TransferFunction{

    public AutoColorClassification(){
    }

    @Override
    public Object execute(Object value) {
        return value;
    }

    @Override
    public String getName() {
        return "Auto Classification of Attribute (uniformly distributed; 5 classes)";
    }
}
