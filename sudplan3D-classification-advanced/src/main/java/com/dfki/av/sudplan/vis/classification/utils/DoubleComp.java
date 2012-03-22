/*
 *  DoubleComp.java 
 *
 *  Created by DFKI AV on 20.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.classification.utils;

import java.util.Comparator;

/**
 *
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class DoubleComp implements Comparator {

    @Override
    public int compare(Object a, Object b) {
        if (((Double) a).doubleValue() < ((Double) b).doubleValue()) {
            return -1;
        }

        if (((Double) a).doubleValue() > ((Double) b).doubleValue()) {
            return 1;
        }

        return 0;
    }
}
