/*
 *  ShapefileLoader2.java 
 *
 *  Created by DFKI AV on 13.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.layer;

import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import gov.nasa.worldwindx.examples.util.ShapefileLoader;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ShapefileLoader2 extends ShapefileLoader {

    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());
    protected String attribute;
    
    public ShapefileLoader2(String attribute){
        super();
        this.attribute = attribute;
    }
    
    /**
     * 
     * @param record
     * @param s
     * @return 
     */
    protected Object getValue(ShapefileRecord record, String s) {
        if (record.getAttributes() == null) {
            return null;
        }

        for (Map.Entry<String, Object> attr : record.getAttributes().getEntries()) {
            if (!attr.getKey().equalsIgnoreCase(s)) {
                continue;
            }

            Object o = attr.getValue();
            return o;
        }

        return null;
    }
}
