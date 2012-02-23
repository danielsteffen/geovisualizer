package com.dfki.av.sudplan.vis.algorithm;

/**
 *
 * @author steffen
 */
public class DefaultTransferFunction implements TransferFunction{
    public DefaultTransferFunction(){
    }

    @Override
    public Object execute(Object value) {
        // Do nothing
        return value;
    }

    @Override
    public String getName() {
        return "Default Transfer Function";
    }
}
