package com.dfki.av.sudplan.vis;

import com.dfki.av.sudplan.vis.IVisAlgorithm;
import java.util.List;

/**
 *
 * @author steffen
 */
public interface IVisAlgorithmFactory {
    public List<String> getVisualizationNames();
    public IVisAlgorithm get(String name);
}
