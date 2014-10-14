/*
 * VisCreateTexture.java
 *
 * Created by DFKI AV on 01.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.vis.basic;

import de.dfki.av.geovisualizer.core.VisAlgorithmAbstract;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisCreateTexture extends VisAlgorithmAbstract {

    /**
     * The description for the {@link VisCreateTexture} algorithm.
     */
    public static final String DESCRIPTION = "Adds a texture for the WW canvas"
            + "visualization.";
    /**
     * A random unique identifier for an instance of {@link VisCreateTexture}.
     */
    private final UUID uuid;

    /**
     * Constructor.
     */
    public VisCreateTexture() {
        super("CreateTexture", VisCreateTexture.DESCRIPTION);

        uuid = UUID.randomUUID();
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {

        log.debug("Running {}", this.getClass().getSimpleName());
        setProgress(0);
        ArrayList<Layer> list = new ArrayList<>();
        if (attributes == null || attributes.length == 0) {
            log.warn("No attributes specified.");
        }
        setProgress(10);

        if (data instanceof File) {
            File file = (File) data;
            SurfaceImageLayer sul = new SurfaceImageLayer();
            sul.setOpacity(0.8);
            sul.setPickEnabled(false);
            sul.setName(file.getName() + " (" + uuid.toString() + ")");
            try {
                sul.addImage(file.getAbsolutePath());
            } catch (IOException ex) {
                log.error("Could not add image {}", ex.toString());
                sul = null;
            }
            list.add(sul);
        } else {
            log.error("Data type not supported.");
        }
        setProgress(100);
        log.debug("Finished {}", this.getClass().getSimpleName());

        return list;
    }
}
