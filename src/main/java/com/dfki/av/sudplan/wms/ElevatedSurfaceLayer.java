/*
 *  ElevatedSurfaceLayer.java 
 *
 *  Created by DFKI AV on 15.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.render.Renderable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class ElevatedSurfaceLayer extends SurfaceImageLayer {   
    private int image_id;
    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ElevatedSurfaceLayer.class);
    private final ElevatedSurfaceSupportLayer supportLayer;
    private final Double elevation;
    private final Double opac;
    private WMSImageRemover remover;

    /**
     *
     * @param layer
     * @param image_format
     * @param elevation
     * @param opac
     * @param sector
     */
    public ElevatedSurfaceLayer(WMSCapabilities caps, AVList params, Double elevation, Double opac, Sector sector) {
        super();
        this.elevation = elevation;
        this.opac = opac;
        this.setPickEnabled(false);
        
        AVList configParams = params.copy();
        String image_format = "";
        for (String s : caps.getImageFormats()) {
            if (s.contains("dds")) {
                image_format = s;
                break;
            }
        }
        for (String s : caps.getImageFormats()) {
            if (s.endsWith("gif") || s.endsWith("png")) {
                image_format = s;
                break;
            }
        }
        if (image_format.equals("")) {
            log.warn("No supported image format available.");
        }  
        image_id = 0;
        // Some wms servers are slow, so increase the timeouts and limits used by world wind's retrievers.
        configParams.setValue(AVKey.URL_CONNECT_TIMEOUT, 30000);
        configParams.setValue(AVKey.URL_READ_TIMEOUT, 30000);
        configParams.setValue(AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT, 60000);
        ElevatedSurfaceSupportLayer tmp = new ElevatedSurfaceSupportLayer(caps, configParams, image_format, this);
        this.supportLayer = tmp;
    }
    
    
    @Override
    public void setEnabled(boolean bln){
        super.setEnabled(bln);
        getSupportLayer().setEnabled(bln);
    }
    
    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener pl) {
        super.addPropertyChangeListener(pl);
        supportLayer.addPropertyChangeListener(pl);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("toRemove")) {
            if(evt.getNewValue() instanceof List<?>){
                List<Renderable> toRemove = (List<Renderable>) evt.getNewValue();
                removeRenderables(toRemove);
            }else if (remover == null || remover.isDone()) {
                remover = new WMSImageRemover(getRenderables());
                remover.addPropertyChangeListener(this);
                remover.execute();
            } else {
                // TODO
            }
        }
        if (evt.getPropertyName().equals("RemoveAll")) {
            removeAllRenderables();
        }
        if (evt.getPropertyName().equals("compose complete")) {
            if (evt.getNewValue() instanceof ElevatedSurfaceImage && evt.getOldValue() instanceof List<?>) {
                ElevatedSurfaceImage image = (ElevatedSurfaceImage) evt.getNewValue();
                image.setElevation(elevation);
                image.setOpacity(opac);
                image.setFloating(true);
                image.setId(image_id);
                image_id++;
                addRenderable(image);
            } else {
                log.warn("No ElevatedSurfaceImage");
            }
        }
    }

    /**
     *
     * @return the wms data as
     * <code>WMSTiledImageLayer</code>
     */
    public ElevatedSurfaceSupportLayer getSupportLayer() {
        return supportLayer;
    }

    /**
     * Removes all
     * <code>ElevatedSurfaceImage</code> from Layer
     *
     * @param renderables
     */
    public void removeRenderables(List<Renderable> renderables) {
        for (Renderable renderable : renderables) {
            if (renderable instanceof ElevatedSurfaceImage) {
                ElevatedSurfaceImage image = (ElevatedSurfaceImage) renderable;
                removeRenderable(renderable);
                image.dispose();
            }
        }
    }

    /**
     * Forces all
     * <code>Renderable</code> from type
     * <code>ElevatedSurfaceImage</code> to refresh.
     */
    void refresh() {
        for (Renderable renderable : getRenderables()) {
            if (renderable instanceof ElevatedSurfaceImage) {
                ((ElevatedSurfaceImage) renderable).refresh();
            }
        }
    }
    
    
class WMSImageRemover extends SwingWorker<List<Renderable> , Void> {

    protected Iterable<Renderable> images;


    /**
     *
     * @param sul
     * @param elevation
     * @param opacity
     * @param wms
     * @param List<sector>
     */
    public WMSImageRemover(Iterable<Renderable> images) {
        this.images = images;
    }

    /**
     *
     * @return true if wms data retreival was successfull
     * @throws URISyntaxException
     * @throws Exception
     */
    @Override
    protected List<Renderable> doInBackground() throws URISyntaxException, Exception {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        Thread.sleep(200);
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
                            }else if (image.getId() > image2.getId()){
                                toRemove.add(renderable2);
                            }
                        }
                    }
                }
            }
        }
        return toRemove;
    }

    /**
     *
     */
    @Override
    protected void done() {
        try {
            if (get() != null) {
                firePropertyChange("toRemove", null, get());
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
}
