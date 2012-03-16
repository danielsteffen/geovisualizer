
package com.dfki.av.sudplan.vis.core;

/**
 *
 * @author steffen
 */
public interface DataSource {
    public double min(String dataAttributeName);
    public double max(String dataAttributeName);
}
