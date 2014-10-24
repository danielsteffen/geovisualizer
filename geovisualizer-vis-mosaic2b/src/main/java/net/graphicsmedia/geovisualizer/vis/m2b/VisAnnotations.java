/*
 * VisAnnotations.java
 *
 * Created by DFKI AV on 24.10.2014.
 * Copyright (c) 2011-2014 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package net.graphicsmedia.geovisualizer.vis.m2b;

import de.dfki.av.geovisualizer.core.*;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.GlobeAnnotation;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.ImageIcon;

/**
 * The annotations visualization.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class VisAnnotations extends VisAlgorithmAbstract {

    /**
     * The description for the {@link VisAnnotations} algorithm.
     */
    public static final String DESCRIPTION = "The annotation visualization"
            + " for visualizing semantic data of data points.";

    /**
     * A random unique identifier for an instance of {@link VisAnnotations}.
     */
    private final UUID uuid;

    /**
     * Constructor
     */
    protected VisAnnotations() {
        super("Annotations visualization",
                VisAnnotations.DESCRIPTION,
                new ImageIcon(VisAnnotations.class.getClassLoader().
                        getResource("icons/VisAnnotations.png")));

        uuid = UUID.randomUUID();
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {
        log.debug("Running {}", this.getClass().getSimpleName());

        List<Layer> layers = new ArrayList<>();
        ISource iSource;
        setProgress(0);

        // 0 - Check data
        if (!(data instanceof ISource)) {
            log.error("Data type {} not supported for {}.",
                    data.getClass().getSimpleName(), this.getName());
            return layers;
        } else {
            iSource = (ISource) data;
        }
        setProgress(5);

        // 1 - Check and set all attributes
        setProgress(10);

        // 2 - Pre-processing data
        setProgress(30);

        // 3 - Create visualization
        RenderableLayer layer = createPointLayer(iSource);
        layers.add(layer);
        setProgress(95);

        // 4 - Create legends for visualization (if available)
        List<Layer> legends = createLegends(iSource.getName() + " (" + uuid.toString() + ")");
        layers.addAll(legends);
        setProgress(100);

        log.debug("Finished {}", this.getClass().getSimpleName());

        return layers;
    }

    /**
     * Creates a {@link RenderableLayer} with all available annotations from the
     * {@link ISource}.
     *
     * @param iSource the {@link ISource} as data source
     * @param attributes the attributes to use for the visualization
     * @return the {@link RenderableLayer} with the annotations.
     */
    private RenderableLayer createPointLayer(ISource iSource, String... attributes) {
        RenderableLayer layer = new RenderableLayer();
        layer.setName(iSource.getName() + " (" + uuid.toString() + ")");

        for (int i = 0; i < iSource.getFeatureCount(); i++) {

            List<List<double[]>> list = iSource.getPoints(i);

            for (int j = 0; j < list.size(); j++) {
                setProgress((int) (100 * j / (float) list.size()));
                for (double[] point : list.get(j)) {
                    Map<String, String> data = new HashMap<>();
                    Position position = Position.fromDegrees(point[1], point[0], 50);
                    GlobeAnnotation ga = new GlobeAnnotation("\n\n\n\n\nNASA World Wind SDK Tutorial - "
                            + "Displaying Annotations.", position, Font.decode("Arial-BOLD-13"));
                    layer.addRenderable(ga);
                }
            }
        }

        return layer;
    }
}
