/*
 *  PropertyChangeEventHolder.java 
 *
 *  Created by DFKI AV on 12.07.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

/**
 * Interface which holds the PropertyNames for the PropertyChangeEvents of the
 * com.dfki.av.sudplan.wms package
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public interface PropertyChangeEventHolder {

    /**
     * Name of the event which is fired when a wms download is active.
     */
    public static final String WMS_DOWNLOAD_ACTIVE = "wms_download_active";
    /**
     * Name of the event which is fired when a wms download is finished.
     */
    public static final String WMS_DOWNLAOD_COMPLETE = "wms_download_complete";
    /**
     * Name of the event which is fired when an image creation is finished.
     */
    public static final String IMAGE_CREATION_COMPLETE = "image_creation_complete";
    /**
     * Name of the event which is fired when a check is needed if images must be
     * removed.
     */
    public static final String IMAGE_REMOVAL = "image_removal";
    /**
     * Name of the event which is fired when all images should be removed.
     */
    public static final String IMAGE_REMOVAL_COMPLETE = "image_removal_complete";
    /**
     * Name of the event which is fired if the layer info retreival is
     * completed.
     */
    public static final String LAYERINFO_RETREIVAL_COMPLETE = "layerinfo_retreival_complete";
    /**
     * Name of the event which is fired if the layer info retreival has failed.
     */
    public static final String LAYERINFO_RETREIVAL_FAILED = "layerinfo_retreival_failed";
    /**
     * Name of the event which is fired if a wwd redraw is needed.
     */
    public static final String WWD_REDRAW = "wwd_redraw";
}
