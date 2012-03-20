package com.dfki.av.sudplan.vis.core;

import org.gdal.ogr.Feature;

/**
 *
 * @author steffen
 */
public interface ISource {

    /**
     *
     * @return
     */
    public int getFeatureCount();

    /**
     *
     * @param id
     * @return
     */
    public Feature getFeature(int id);

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
