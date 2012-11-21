/*
 *  VisualizationPanel.java 
 *
 *  Created by DFKI AV on 24.09.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.vis;

import de.dfki.av.geovisualizer.core.io.IOUtils;
import gov.nasa.worldwind.globes.ElevationModel;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.terrain.CompoundElevationModel;
import gov.nasa.worldwind.terrain.LocalElevationModel;
import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads local elevations and adds them as {@link LocalElevationModel} to the
 * {@link Globe}.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ElevationsLoader extends SwingWorker<ElevationModel, Void> {

    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(ElevationsLoader.class);
    /**
     * The {@link Globe} where to add the {@link LocalElevationModel}.
     */
    private Globe globe;
    /**
     * The name of the GeoTiff file with the local elevations.
     */
    private String filename;

    /**
     * Constructor.
     *
     * @param globe the {@link Globe} where the {@link LocalElevationModel} is
     * added.
     * @param filename the name of the GeoTiff file with the local elevations.
     * @throws IllegalArgumentException if globe == null or if filename == null
     * or if file is empty.
     */
    public ElevationsLoader(final Globe globe, String filename) {
        if (globe == null) {
            String msg = "globe == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (filename == null) {
            String msg = "filename == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (filename.isEmpty()) {
            String msg = "filename is empty";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.globe = globe;
        this.filename = filename;
    }

    @Override
    protected ElevationModel doInBackground() throws Exception {
        log.debug("Loading elevation {}.", this.filename);
        ClassLoader loader = this.getClass().getClassLoader();
        URL url = loader.getResource(this.filename);
        File file = IOUtils.DownloadToTempFile(url);
        final LocalElevationModel elevationModel = new LocalElevationModel();
        elevationModel.addElevations(file);

        return elevationModel;
    }

    @Override
    protected void done() {
        try {
            ElevationModel elevationModel = get();
            if (elevationModel != null) {
                ElevationModel currentElevationModel = globe.getElevationModel();
                if (currentElevationModel instanceof CompoundElevationModel) {
                    log.debug("Current elevation model instance of CompoundElevationModel.");
                    CompoundElevationModel cem = (CompoundElevationModel) currentElevationModel;
                    cem.addElevationModel(elevationModel);
                } else {
                    log.debug("Current elevation model instance of CompoundElevationModel.");
                    globe.setElevationModel(elevationModel);
                }
            } else {
                String msg = "elevationModel == null";
                log.error(msg);
            }
        } catch (InterruptedException ex) {
            log.error(ex.toString());
        } catch (ExecutionException ex) {
            log.error(ex.toString());
        }
    }
}
