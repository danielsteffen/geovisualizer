package com.dfki.av.sudplanX.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author steffen
 */
public class StringCategory implements Category {

    /**
     *
     */
    private List<String> strings;

    /**
     *
     * @param s
     */
    public StringCategory(final String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Parameter set to null.");
        }
        strings = new ArrayList<String>();
        strings.add(s);
    }

    /**
     *
     * @param s
     */
    public StringCategory(final List<String> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Parameter set to null.");
        }
        this.strings = list;
    }

    @Override
    public boolean includes(Object o) {
        if (o instanceof String) {
            String s = (String) o;
            for (Iterator<String> it = strings.iterator(); it.hasNext();) {
                String string = it.next();
                if (s.equalsIgnoreCase(string)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
