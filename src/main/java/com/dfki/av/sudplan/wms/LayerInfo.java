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
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwind.util.WWUtil;
import java.util.Set;

/**
 *
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class LayerInfo {

    public WMSCapabilities caps;
    public WMSLayerCapabilities lcaps;
    public AVListImpl params = new AVListImpl();

    /**
     * 
     * @return title of layer
     */
    public String getTitle() {
        return params.getStringValue(AVKey.DISPLAY_NAME);
    }

    /**
     * 
     * @return name of layer
     */
    public String getName() {
        return params.getStringValue(AVKey.LAYER_NAMES);
    }

    /**
     * 
     * @return layer abstract
     */
    public String getAbstract() {
        return params.getStringValue(AVKey.LAYER_ABSTRACT);
    }

    /**
     * 
     * @return wms capabilities
     */
    public WMSCapabilities getWMSCapabilities() {
        return caps;
    }

    /**
     * 
     * @return layer capabilities
     */
    public WMSLayerCapabilities getLayerCapabilities() {
        return lcaps;
    }

    /**
     * 
     * @return  wms parameter
     */
    public AVListImpl getParameter() {
        return params;
    }

    /**
     * 
     * @param caps
     * @param layerCaps
     * @param style
     * @return <code>LayerInfo</code> of requestet wms data
     */
    public static LayerInfo create(WMSCapabilities caps, WMSLayerCapabilities layerCaps, WMSLayerStyle style) {
        // Create the layer info specified by the layer's capabilities entry and the selected style.

        LayerInfo linfo = new LayerInfo();
        linfo.caps = caps;
        linfo.lcaps = layerCaps;
        linfo.params = new AVListImpl();
        linfo.params.setValue(AVKey.LAYER_NAMES, layerCaps.getName());
        if (style != null) {
            linfo.params.setValue(AVKey.STYLE_NAMES, style.getName());
        }
        String abs = layerCaps.getLayerAbstract();
        if (!WWUtil.isEmpty(abs)) {
            linfo.params.setValue(AVKey.LAYER_ABSTRACT, abs);
        }

        linfo.params.setValue(AVKey.DISPLAY_NAME, makeTitle(caps, linfo));

        return linfo;
    }

    /**
     *
     * @param caps
     * @param layerInfo
     * @return title for wms data
     */
    protected static String makeTitle(WMSCapabilities caps, LayerInfo layerInfo) {
        String layerNames = layerInfo.params.getStringValue(AVKey.LAYER_NAMES);
        String styleNames = layerInfo.params.getStringValue(AVKey.STYLE_NAMES);
        String[] lNames = layerNames.split(",");
        String[] sNames = styleNames != null ? styleNames.split(",") : null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lNames.length; i++) {
            if (sb.length() > 0) {
                sb.append(", ");
            }

            String layerName = lNames[i];
            WMSLayerCapabilities lc = caps.getLayerByName(layerName);
            String layerTitle = lc.getTitle();
            sb.append(layerTitle != null ? layerTitle : layerName);

            if (sNames == null || sNames.length <= i) {
                continue;
            }

            String styleName = sNames[i];
            WMSLayerStyle style = lc.getStyleByName(styleName);
            if (style == null) {
                continue;
            }

            sb.append(" : ");
            String styleTitle = style.getTitle();
            sb.append(styleTitle != null ? styleTitle : styleName);
        }

        return sb.toString();
    }

    /**
     *
     * @param caps
     * @param params
     * @return wms layer
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
            // Ignore the exception, and just return null.
        }

        return null;
    }

    /**
     *
     * @param caps
     * @return factory key
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

    /**
     * 
     * @return layer name
     */
    @Override
    public String toString() {
        return params.getStringValue(AVKey.DISPLAY_NAME);
    }
}
