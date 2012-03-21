package com.dfki.av.sudplan.vis.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author steffen
 */
public class VisParameterImpl implements IVisParameter {

    /**
     *
     */
    private List<String> transferFunctions;
    /**
     *
     */
    private String name;
    /**
     *
     */
    private ITransferFunction transferFunction;

    /**
     *
     * @param name
     */
    public VisParameterImpl(String name, ITransferFunction function) {
        this.name = name;
        this.transferFunctions = new ArrayList<String>();
        this.transferFunction = function;
    }

    @Override
    public List<String> getAvailableTransferFunctions() {
        return this.transferFunctions;
    }

    @Override
    public boolean addTransferFunction(final String f) {
        if (f == null || f.isEmpty()) {
            throw new IllegalArgumentException("Can not add transferfunction. Value is null.");
        }

        if (f.isEmpty()) {
            throw new IllegalArgumentException("Can not add transferfunction. Value is empty String.");
        }
        return this.transferFunctions.add(f);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setTransferFunction(ITransferFunction f) {
        if (f == null) {
            throw new IllegalArgumentException("Can not set transferfunction. "
                    + "Value is null.");
        }
        transferFunction = f;
    }

    @Override
    public ITransferFunction getTransferFunction() {
        return this.transferFunction;
    }
}
