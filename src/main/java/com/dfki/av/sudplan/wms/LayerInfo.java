/*
 *  LayerInfo.java 
 *
 *  Created by DFKI AV on 15.04.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.Factory;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwindx.applications.worldwindow.core.WMSLayerInfo;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for handling the LayerInfo for the ElevatedSurfaceLayer
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class LayerInfo extends WMSLayerInfo {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(LayerInfo.class);    
    /**
     * The {@link WMSLayerCapabilities} for the corresponding WMS layer
     */
    public WMSLayerCapabilities layerCaps;
    /**
     * The {@link WMSCapabilities} for the corresponding WMS data
     */
    public WMSCapabilities caps;
    /**
     * The parameters as {@link AVList} for the creation of an WMS layer
     */
    public AVList params;

    /**
     * Constructs a layer info of the defined capabilities and parameters
     *
     * @param caps {@link WMSCapabilities} for the corresponding WMS data
     * @param layerCaps {@link WMSLayerCapabilities} for the corresponding WMS
     * layer
     * @param style {@link WMSLayerStyle} for the corresponding WMS layer
     */
    public LayerInfo(WMSCapabilities caps, WMSLayerCapabilities layerCaps, WMSLayerStyle style) {
        super(caps, layerCaps, style);
        this.layerCaps = layerCaps;
        this.params = super.getParams();
        this.caps = caps;
    }

    /**
     * Return the name of the layer.
     *
     * @return the name of the layer.
     */
    public String getName() {
        return this.layerCaps.getName();
    }

    /**
     * Returns the layer name of the corresponding WMS layer
     *
     * @return layer name as {@link String}
     */
    @Override
    public String toString() {
        return params.getStringValue(AVKey.DISPLAY_NAME);
    }

    /**
     * Creates an WMS layer defined through the {@link WMSCapabilities} caps and
     * the parameters {@link #params} ({@link AVList}).
     *
     * @param caps {@link WMSCapabilities} for the corresponding WMS data
     * @param params parameters as {@link AVList} for the creation of an WMS
     * layer
     * @return a WMS layer or {@code null}
     */
    public static Object createComponent(WMSCapabilities caps, AVList params) {
        AVList configParams = params.copy(); // Copy to insulate changes from the caller.

        // Some wms servers are slow, so increase the timeouts and limits used by world wind's retrievers.
        configParams.setValue(AVKey.URL_CONNECT_TIMEOUT, 30000);
        configParams.setValue(AVKey.URL_READ_TIMEOUT, 30000);
        configParams.setValue(AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT, 60000);

        try {
            String factoryKey = getFactoryKeyForCapabilities(caps);
            Factory factory = (Factory) WorldWind.createConfigurationComponent(factoryKey);
            return factory.createFromConfigSource(caps, configParams);
        } catch (Exception e) {
            log.error(e.toString());
        }

        return null;
    }

    /**
     * Retrieves the factory key for the WMS layer creation.
     *
     * @param caps {@link WMSCapabilities} for the corresponding WMS data
     * @return factory key as {@link String} for the layer creation
     */
    protected static String getFactoryKeyForCapabilities(WMSCapabilities caps) {
        boolean hasApplicationBilFormat = false;

        Set<String> formats = caps.getImageFormats();
        for (String s : formats) {
            if (s.contains("application/bil")) {
                hasApplicationBilFormat = true;
                break;
            }
        }

        return hasApplicationBilFormat ? AVKey.ELEVATION_MODEL_FACTORY : AVKey.LAYER_FACTORY;
    }
}
