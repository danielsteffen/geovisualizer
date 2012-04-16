/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.ui;

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

public class LayerInfo {

    protected WMSCapabilities caps;
    protected AVListImpl params = new AVListImpl();

    protected String getTitle() {
        return params.getStringValue(AVKey.DISPLAY_NAME);
    }

    protected String getName() {
        return params.getStringValue(AVKey.LAYER_NAMES);
    }

    protected String getAbstract() {
        return params.getStringValue(AVKey.LAYER_ABSTRACT);
    }

    protected static LayerInfo create(WMSCapabilities caps, WMSLayerCapabilities layerCaps, WMSLayerStyle style) {
        // Create the layer info specified by the layer's capabilities entry and the selected style.

        LayerInfo linfo = new LayerInfo();
        linfo.caps = caps;
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

    protected static Object createComponent(WMSCapabilities caps, AVList params) {
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
