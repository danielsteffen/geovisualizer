package com.dfki.av.sudplan.vis.algorithm;

import gov.nasa.worldwind.layers.Layer;
import java.util.List;
import javax.swing.Icon;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface IVisAlgorithm {

    /**
     *
     * @return
     */
    public Icon getIcon();

    /**
     *
     * @return
     */
    public String getName();

    /**
     *
     * @return
     */
    public String getDescription();

    /**
     *
     * @param data
     * @return
     */
    public List<Layer> createLayersFromData(Object data);
}
