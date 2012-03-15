package com.dfki.av.sudplan.vis.spi;

import com.dfki.av.sudplan.vis.core.IVisAlgorithm;
import java.util.List;

/**
 *
 * @author steffen
 */
public interface IVisAlgorithmProvider {
    /**
     * 
     * @return 
     */
    public List<String> getVisualizationNames();
    /**
     * 
     * @param name
     * @return 
     */
    public IVisAlgorithm get(String name);
}
