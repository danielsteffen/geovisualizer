/*
 *  WMSUtils.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplanX.wms;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import java.util.Set;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class WMSUtils {

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
