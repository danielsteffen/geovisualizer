/*
 *  ElevatedSurfaceImageRemover.java 
 *
 *  Created by DFKI AV on 12.07.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

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
     * Creates a {@link ElevatedSurfaceImageRemover} with a defined
     * {@link Iterable} of {@link Renderable} to check.
     *
     * @param images current list of all Renderable in the layer
     */
    public ElevatedSurfaceImageRemover(Iterable<Renderable> images) {
        this.images = images;
    }

    /**
     * Checks the defined {@link Iterable} of {@link Renderable} on items which
     * must be removed
     *
     * @return List of {@link Renderable} which must be removed
     */
    private List<Renderable> check() {
        List<Renderable> toRemove = new ArrayList<Renderable>();
        for (Renderable renderable : images) {
            if (renderable instanceof ElevatedSurfaceImage) {
                ElevatedSurfaceImage image = (ElevatedSurfaceImage) renderable;
                for (Renderable renderable2 : images) {
                    if (renderable2 instanceof ElevatedSurfaceImage) {
                        ElevatedSurfaceImage image2 = (ElevatedSurfaceImage) renderable2;
                        if (image.getSector().contains(image2.getSector())
                                || image2.getSector().contains(image.getSector())) {
                            if (image.getId() < image2.getId()) {
                                toRemove.add(renderable);
                            } else if (image.getId() > image2.getId()) {
                                toRemove.add(renderable2);
                            }
                        }
                    }
                }
            }
        }
        return toRemove;
    }

    @Override
    protected List<Renderable> doInBackground() throws URISyntaxException, Exception {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        Thread.sleep(200);
        return check();
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
