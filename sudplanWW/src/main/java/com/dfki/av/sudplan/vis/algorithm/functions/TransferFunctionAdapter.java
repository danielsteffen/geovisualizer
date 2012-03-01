package com.dfki.av.sudplan.vis.algorithm.functions;

import com.dfki.av.sudplan.io.DataInput;

/**
 *
 * @author steffen
 */
public class TransferFunctionAdapter implements ITransferFunction{

    @Override
    public Object calc(Object o) {
        return o;
    }

    @Override
    public String getName() {
        return "None";
    }

    @Override
    public void preprocess(DataInput data, String attribute) {
    }
}
