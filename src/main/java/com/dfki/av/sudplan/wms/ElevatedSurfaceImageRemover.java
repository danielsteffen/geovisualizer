/*
 *  ElevatedSurfaceImageRemover.java 
 *
 *  Created by DFKI AV on 12.07.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.render.Renderable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Swing Worker} for the removal of unused {@link ElevatedSurfaceImages}
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class ElevatedSurfaceImageRemover extends SwingWorker<List<Renderable>, Void> {

    /**
     * {@link Iterable} of {@link Renderable} to check.
     */
    protected Iterable<Renderable> images;
    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ElevatedSurfaceImageRemover.class);
    /**
     * {@link List} of Sectors to check
     */
    private final List<Sector> toRemove;

    /**
     * Creates a {@link ElevatedSurfaceImageRemover} with a defined
     * {@link Iterable} of {@link Renderable} to check.
     *
     * @param images current list of all Renderable in the layer
     */
    public ElevatedSurfaceImageRemover(Iterable<Renderable> images) {
        this.images = images;
        toRemove = null;
    }

    /**
     * Creates a {@link ElevatedSurfaceImageRemover} with a defined
     * {@link Iterable} of {@link Renderable} to check.
     *
     * @param images current list of all Renderable in the layer
     */
    public ElevatedSurfaceImageRemover(Iterable<Renderable> images, List<Sector> toRemove) {
        this.images = images;
        this.toRemove = toRemove;
    }

    /**
     * Retreive teh to removing {@link Renderable}s from a list of {@link Sector}s
     * @return List of {@link Renderable}s
     */
    private List<Renderable> getRenderable() {
        List<Renderable> remove = new ArrayList<Renderable>();
        for (Sector sector : toRemove) {
            for (Renderable renderable : images) {
                if (renderable instanceof ElevatedSurfaceImage) {
                    ElevatedSurfaceImage image = (ElevatedSurfaceImage) renderable;
                    if (image.getSector().equals(sector)) {
                        remove.add(renderable);
                    }
                }
            }
        }
        return remove;
    }

    /**
     * Checks the defined {@link Iterable} of {@link Renderable} on items which
     * must be removed
     *
     * @return List of {@link Renderable} which must be removed
     */
    private List<Renderable> check() {
        List<Renderable> remove = new ArrayList<Renderable>();
        for (Renderable renderable : images) {
            if (renderable instanceof ElevatedSurfaceImage) {
                ElevatedSurfaceImage image = (ElevatedSurfaceImage) renderable;
                for (Renderable renderable2 : images) {
                    if (renderable2 instanceof ElevatedSurfaceImage) {
                        ElevatedSurfaceImage image2 = (ElevatedSurfaceImage) renderable2;
                        if (image.getSector().contains(image2.getSector())
                                || image2.getSector().contains(image.getSector())) {
                            if (image.getId() < image2.getId()) {
                                remove.add(renderable);
                            } else if (image.getId() > image2.getId()) {
                                remove.add(renderable2);
                            }
                        }
                    }
                }
            }
        }
        return remove;
    }

    @Override
    protected List<Renderable> doInBackground() throws URISyntaxException, Exception {
        if (toRemove == null) {
            return check();
        } else {
            return getRenderable();
        }
    }

    @Override
    protected void done() {
        try {
            if (get() != null) {
                firePropertyChange(PropertyChangeEventHolder.IMAGE_REMOVAL, null, get());
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
