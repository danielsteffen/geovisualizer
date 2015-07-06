/**
 * VisAnnotations.java
 *
 * Created by <a href="mailto:daniel.steffen@graphicsmedia.net">Daniel
 * Steffen</a> on 01.08.2014.
 *
 * Copyright (c) 2014 MOSAIC 2B Consortium Members. All rights reserved.
 *
 * This software is developed as part of the project MOSAIC 2B
 * (http://www.mobile-empowerment.org) and has received funding from the
 * European Union’s Seventh Framework Programme for research, technological
 * development and demonstration under grant agreement no 611796.
 */
package net.graphicsmedia.geovisualizer.vis.m2b;

import de.dfki.av.geovisualizer.core.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.AnnotationLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.GlobeAnnotation;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.ImageIcon;

/**
 * The annotations visualization.
 *
 * @author Daniel Steffen
 */
public class VisAnnotations extends VisAlgorithmAbstract {

    /**
     * The description for the {@link VisAnnotations} algorithm.
     */
    public static final String DESCRIPTION = "This visualization has been"
            + " created within the MOSAIC 2B project. It is used to "
            + " visualize the data, received from the MOSAIC 2B REST API"
            + " that is based on the JSON values.";

    /**
     * A random unique identifier for an instance of {@link VisAnnotations}.
     */
    private final UUID uuid;

    /**
     * Constructor
     */
    protected VisAnnotations() {
        super("MOSAIC 2B Annotation Visualization",
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
        AnnotationLayer layer = createPointLayer(iSource, iSource.getAttributes().keySet());
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
     * Creates a {@link AnnotationLayer} with all available annotations from the
     * {@link ISource}.
     *
     * @param iSource the {@link ISource} as data source
     * @param attributes the attributes to use for the visualization
     * @return the {@link RenderableLayer} with the annotations.
     */
    private AnnotationLayer createPointLayer(ISource iSource, Iterable<String> attributeNames) {
        AnnotationLayer layer = new AnnotationLayer();
        layer.setName(iSource.getName() + " (" + uuid.toString() + ")");

        //for (int i = 0; i < iSource.getFeatureCount(); i++) {
        List<List<double[]>> list = iSource.getPoints(0);
        for (int j = 0; j < list.size(); j++) {
            setProgress((int) (100 * j / (float) list.size()));
            //for (double[] point : list.get(j)) {
            for (int i = 0; i < list.get(j).size(); i++) {
                double[] point = list.get(j).get(i);
                Position position = Position.fromDegrees(point[1], point[0], 0);
                String aString = createString(iSource, i, attributeNames);
                GlobeAnnotation ga = new GlobeAnnotation(aString, position,
                        Font.decode("Arial-BOLD-13"));
                ga.getAttributes().setBackgroundColor(new Color(.8f, .8f, .8f, .7f));
                ga.getAttributes().setSize(new Dimension(1000, 0));
                ga.getAttributes().setBorderColor(Color.BLACK);
                ga.setPickEnabled(false);
                ga.getAttributes().setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
                layer.addAnnotation(ga);
            }
        }
        //}
        layer.setOpacity(.7f);
        return layer;
    }

    /**
     *
     * @param iSource
     * @param featureId
     * @param attributeNames
     * @return
     */
    private String createString(ISource iSource, int featureId, Iterable<String> attributeNames) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String attribute : attributeNames) {
            stringBuilder.append(attribute);
            stringBuilder.append(": ");
            Object value = iSource.getValue(featureId, attribute);
            if (value instanceof double[]) {
                value = ((double[]) value)[0] + " " + ((double[]) value)[1];
            }
            stringBuilder.append(value.toString());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
