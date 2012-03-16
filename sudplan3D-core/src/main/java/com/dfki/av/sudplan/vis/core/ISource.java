
package com.dfki.av.sudplan.vis.core;

/**
 *
 * @author steffen
 */
public interface ISource {
    /**
     * 
     * @param dataAttributeName
     * @return 
     */
    public double min(String dataAttributeName);
    /**
     * 
     * @param dataAttributeName
     * @return 
     */
    public double max(String dataAttributeName);
}
