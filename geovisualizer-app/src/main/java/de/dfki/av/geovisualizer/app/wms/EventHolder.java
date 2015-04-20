/*
 * PropertyChangeEventHolder.java
 *
 * Created by DFKI AV on 12.07.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.wms;

/**
 * Interface which holds the PropertyNames for the PropertyChangeEvents of the
 * {@code de.dfki.geovisualizer.app.wms} package.
 */
public interface EventHolder {

    /**
     * Name of the event which is fired when a WMS download is active.
     */
    String WMS_DOWNLOAD_ACTIVE = "wms_download_active";
    /**
     * Name of the event which is fired when a WMS download is finished.
     */
    String WMS_DOWNLAOD_COMPLETE = "wms_download_complete";
    /**
     * Name of the event which is fired when an image creation is finished.
     */
    String IMAGE_CREATION_COMPLETE = "image_creation_complete";
    /**
     * Name of the event which is fired when a check is needed if images must be
     * removed.
     */
    String IMAGE_REMOVAL = "image_removal";
    /**
     * Name of the event which is fired when all images should be removed.
     */
    String IMAGE_REMOVAL_COMPLETE = "image_removal_complete";
    /**
     * Name of the event which is fired if the layer info retrieval is
     * completed.
     */
    String LAYERINFO_RETREIVAL_COMPLETE = "layerinfo_retreival_complete";
    /**
     * Name of the event which is fired if the layer info retreival has failed.
     */
    String LAYERINFO_RETREIVAL_FAILED = "layerinfo_retreival_failed";
    /**
     * Name of the event which is fired if a wwd redraw is needed.
     */
    String WWD_REDRAW = "wwd_redraw";
}
