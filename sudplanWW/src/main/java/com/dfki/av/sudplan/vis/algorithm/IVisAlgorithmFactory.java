package com.dfki.av.sudplan.vis.algorithm;

import java.util.List;

/**
 *
 * @author steffen
 */
public interface IVisAlgorithmFactory {
    public List<String> getVisualizationNames();
    public IVisAlgorithm get(String name);
}
