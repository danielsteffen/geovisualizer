package com.dfki.av.sudplan.vis.algorithm;

/**
 *
 * @author steffen
 */
public class NumberCategory implements Category {

    /**
     * 
     */
    private double minValue;
    /**
     * 
     */
    private double maxValue;

    /**
     * 
     * @param min
     * @param max 
     */
    public NumberCategory(Number min, Number max) {
        if(min == null || max == null){
            throw new IllegalArgumentException("Parameter for NumberCategory null.");
        }
        this.minValue = min.doubleValue();
        this.maxValue = max.doubleValue();
    }

    @Override
    public boolean includes(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
        if (o instanceof Number) {
            double value = ((Number) o).doubleValue();

            if (minValue <= value && value <= maxValue) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
