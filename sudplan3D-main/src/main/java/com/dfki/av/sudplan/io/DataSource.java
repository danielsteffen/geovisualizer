
package com.dfki.av.sudplan.io;

/**
 *
 * @author steffen
 */
public interface DataSource {
    public double min(String dataAttributeName);
    public double max(String dataAttributeName);
}
