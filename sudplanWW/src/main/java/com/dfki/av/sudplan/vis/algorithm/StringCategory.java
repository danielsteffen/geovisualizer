package com.dfki.av.sudplan.vis.algorithm;

/**
 *
 * @author steffen
 */
public class StringCategory implements Category {

    private String string;

    public StringCategory(String s) {
        this.string = s;
    }

    @Override
    public boolean elementOf(Object o) {
        if (o instanceof String) {
            String s = (String) o;
            if (s.equalsIgnoreCase(string)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
