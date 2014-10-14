/*
 * VisPointCloud.java
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.UserFacingIcon;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisPointCloud extends VisAlgorithmAbstract {

    /**
     * The description for the {@link VisPointCloud} algorithm.
     */
    public static final String DESCRIPTION = "Old point cloud visualization"
            + " using icons for rendering. Only useful for a small set of "
            + "points. For large point clouds use the new point cloud "
            + "visualization.";
    /**
     * The filename of the icon used for rendering the point.
     */
    private static final String POINT_ICON = "icons/dot8x8.png";

    /**
     * The VisPointCloud constructor.
     */
    protected VisPointCloud() {
        super("Point cloud visualization (old)",
                VisPointCloud.DESCRIPTION,
                new ImageIcon(VisPointCloud.class.getClassLoader().
                getResource("icons/VisPointCloud.png")));
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attriutes) {

        log.debug("Running {}", this.getClass().getSimpleName());
        setProgress(0);

        List<Layer> layers = new ArrayList<>();
        if (data instanceof ISource) {
            ISource iSource = (ISource) data;
            // 1 - Pre-processing data
            // ... nothing to do until now.
            // 2 - Create visualization
            IconLayer layer = createIconLayer(iSource);
            layers.add(layer);
        } else {
            log.warn("Data type not supported for Extrude Polygon Visualization.");
        }

        setProgress(100);
        log.debug("Finished {}", this.getClass().getSimpleName());

        return layers;
    }

    /**
     * Returns an {@link IconLayer} with a {@link UserFacingIcon} for the
     * vertices of the geometry of each feature.
     *
     * @param source the {@link ISource} element
     * @return a {@link IconLayer} with {@link UserFacingIcon} elements.
     */
    private IconLayer createIconLayer(ISource source) {

        IconLayer layer = new IconLayer();
        layer.setName(source.getName());

        for (int i = 0; i < source.getFeatureCount(); i++) {
            List<List<double[]>> list = source.getPoints(i);
            for (int j = 0; j < list.size(); j++) {
                setProgress((int) (100 * j / (float) list.size()));
                for (double[] point : list.get(j)) {
                    Position pos = Position.fromDegrees(point[1], point[0]);
                    UserFacingIcon icon = new UserFacingIcon(POINT_ICON, pos);
                    layer.addIcon(icon);
                }
            }
        }
        return layer;
    }
}
