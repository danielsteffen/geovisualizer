package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.DataInput;
import java.util.List;

/**
 *
 * @author steffen
 */
public interface Categorization {
    public List<Category> execute(DataInput data, String attribute);
}
