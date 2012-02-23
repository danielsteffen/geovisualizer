/*
 *  VisPointCloud.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.UserFacingIcon;
import java.util.ArrayList;
import java.util.List;
import org.gdal.ogr.Geometry;

/**
 *
 * @author steffen
 */
public class VisPointCloud extends VisAlgorithmAbstract {

    /**
     *
     */
    public VisPointCloud() {
        super("Point Cloud Visualization");
        VisParameter parameter0 = new VisParameter("Default");
        parameters.add(parameter0);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attriutes) {

        List<Layer> layers = new ArrayList<Layer>();
        if (data instanceof Shapefile) {
            Shapefile shapefile = (Shapefile) data;
            IconLayer layer = new IconLayer();
            // 1 - Pre-processing data
            // 2 - Create visualization
            addIconsForPoints(shapefile, layer);
            layers.add(layer);
        } else {
            log.warn("Data type not supported for Extrude Polygon Visualization.");
        }

        return layers;
    }

    /**
     *
     * @param shp
     * @param layer
     */
    private void addIconsForPoints(Shapefile shpfile, IconLayer layer) {

        for (int i = 0; i < shpfile.getFeatureCount(); i++) {

            List<Geometry> list = shpfile.getGeometryList(i);
            for (int j = 0; j < list.size(); j++) {
                Geometry g = list.get(j);
                for (int u = 0; u < g.GetPointCount(); u++) {
                    double[] point = g.GetPoint_2D(u);
                    String iconSource = "icons/dot8x8.png";
                    UserFacingIcon icon = new UserFacingIcon(iconSource, Position.fromDegrees(point[1], point[0]));
                    layer.addIcon(icon);
                }
            }
        }
    }
}
