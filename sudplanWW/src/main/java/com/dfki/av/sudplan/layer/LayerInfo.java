/*
 *  LayerInfo.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.layer;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class LayerInfo {

    private WMSCapabilities caps;
    private AVListImpl params;

    public LayerInfo() {
        this.params = new AVListImpl();
    }

    public String getTitle() {
        return params.getStringValue(AVKey.DISPLAY_NAME);
    }

    public String getName() {
        return params.getStringValue(AVKey.LAYER_NAMES);
    }

    public String getAbstract() {
        return params.getStringValue(AVKey.LAYER_ABSTRACT);
    }
}
