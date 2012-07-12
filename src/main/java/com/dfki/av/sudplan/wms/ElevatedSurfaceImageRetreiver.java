/*
 *  ElevatedSurfaceImageRetreiver.java 
 *
 *  Created by DFKI AV on 12.07.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.geom.Sector;
import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SwingWorker for the retreival of the ElevatedSurfaceImages for the
 * ElevatedSurfaceLayer
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class ElevatedSurfaceImageRetreiver extends SwingWorker<Boolean, Void> {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ElevatedSurfaceImageRetreiver.class);
    /**
     * The support layer with the wms data
     */
    protected ElevatedSurfaceSupportLayer supportLayer;
    /**
     * Image resolution in pixels (IMAGE_SIZE*IMAGE_SIZE)
     */
    private static final int IMAGE_SIZE = 384;
    /**
     * The mime type for the image request
     */
    private final String image_format;
    /**
     * List of bounding boxes of the previous set of ElevatedSurfaceImages
     */
    private List<Sector> oldTiles;
    /**
     * List of bounding boxes of the next set of ElevatedSurfaceImage
     */
    private List<Sector> newTiles;

    /**
     * Creates a {@link ElevatedSurfaceImageRetreiver} which retreives
     * {@link ElevatedSurfaceImage} for the defined {@link ElevatedSurfaceSupportLayer}
     * supportLayer and the bounding boxes oldTiles and newTiles
     *
     * @param image_format mime type for the image retreival
     * @param supportLayer support layer with the wms data
     * @param oldTiles bounding boxes of the next set of ElevatedSurfaceImage
     * @param newTiles bounding boxes of the next set of ElevatedSurfaceImage
     */
    public ElevatedSurfaceImageRetreiver(String image_format, ElevatedSurfaceSupportLayer supportLayer, List<Sector> oldTiles, List<Sector> newTiles) {
        this.oldTiles = oldTiles;
        this.newTiles = newTiles;
        this.supportLayer = supportLayer;
        this.image_format = image_format;
    }

    /**
     * Generates a {@link ElevatedSurfaceImage} for the given bounding box from
     * the date of the {@link ElevatedSurfaceSupportLayer}
     *
     * @param suppertLayer support layer with the wms data
     * @param sector sector for the {@link ElevatedSurfaceImage} creation
     */
    protected void createElevatedSurfaceImage(ElevatedSurfaceSupportLayer suppertLayer, Sector sector) {
        firePropertyChange(PropertyChangeEventHolder.WMS_DOWNLOAD_ACTIVE, null, this);
        double scale = 1d;
        int level = -1;
        BufferedImage img = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
        try {
            suppertLayer.composeImageForSector(sector, IMAGE_SIZE, IMAGE_SIZE, scale,
                    level, image_format, true, img, 400000);
            ElevatedSurfaceImage tl = new ElevatedSurfaceImage(img, sector);
            firePropertyChange(PropertyChangeEventHolder.IMAGE_CREATION_COMPLETE, newTiles, tl);
        } catch (IllegalStateException ex) {
            log.debug("Empty Image Source in Sector: " + sector);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected Boolean doInBackground() throws URISyntaxException, Exception {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        if (oldTiles == null || oldTiles.isEmpty()) {
            for (Sector sector : newTiles) {
                firePropertyChange(PropertyChangeEventHolder.WMS_DOWNLOAD_ACTIVE, null, this);
                createElevatedSurfaceImage(supportLayer, sector);
                firePropertyChange(PropertyChangeEventHolder.IMAGE_REMOVAL, null, null);
            }
        } else {
            for (Sector sector : newTiles) {
                Boolean add = true;
                for (Sector oldSector : oldTiles) {
                    if (sector.equals(oldSector)) {
                        add = false;
                    }
                }
                if (add) {
                    firePropertyChange(PropertyChangeEventHolder.WMS_DOWNLOAD_ACTIVE, null, this);
                    createElevatedSurfaceImage(supportLayer, sector);
                    firePropertyChange(PropertyChangeEventHolder.IMAGE_REMOVAL, null, null);
                }
            }
        }
        return true;
    }

    /**
     *
     */
    @Override
    protected void done() {
        firePropertyChange(PropertyChangeEventHolder.WMS_DOWNLAOD_COMPLETE, null, this);
        try {
            if (get()) {
                firePropertyChange(PropertyChangeEventHolder.WWD_REDRAW, null, this);
            } else {
                log.warn("Download failed");
            }
        } catch (InterruptedException ex) {
            log.warn("done (InterruptedException)" + ex);
        } catch (ExecutionException ex) {
            log.warn("done (ExecutionException)" + ex);
        } catch (CancellationException ex) {
            log.warn("done (CancellationException)" + ex);
        }
    }
}
