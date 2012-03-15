package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.vis.ICategory;

/**
 *
 * @author steffen
 */
public class NumberCategory implements ICategory{

    /**
     * 
     */
    private Number number;

    /**
     * 
     */
    public NumberCategory(Number n) {
        this.number = n;
    }

    @Override
    public boolean contains(Object o) {
        if(o instanceof Number){
            Number n = (Number)o;
            return n.doubleValue() == number.doubleValue();
        }
        return false;
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof NumberCategory){
            NumberCategory n = (NumberCategory)o;
            return n.number.doubleValue() == number.doubleValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.number != null ? this.number.hashCode() : 0);
        return hash;
    }
}
