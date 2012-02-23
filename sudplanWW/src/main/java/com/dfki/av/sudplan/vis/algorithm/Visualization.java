/*
 *  Visualization.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author steffen
 */
public class Visualization {

    public static IVisAlgorithm DEFAULT = new VisAlgorithmDefault();
    public static IVisAlgorithm EXTRUDE_POLYGON = new VisExtrudePolygon();
    public static IVisAlgorithm EXTRUDE_POLYLINE = new VisExtrudePolyline();
    public static IVisAlgorithm POINT_CLOUD = new VisPointCloud();
    public static IVisAlgorithm TIMESERIES = new VisTimeseries();
    public static Iterable<IVisAlgorithm> LIST = createList();

    private static Iterable<IVisAlgorithm> createList() {
        List<IVisAlgorithm> list = new ArrayList<IVisAlgorithm>();
        
        list.add(DEFAULT);
        list.add(EXTRUDE_POLYGON);
        list.add(EXTRUDE_POLYLINE);
//        list.add(POINT_CLOUD);
        list.add(TIMESERIES);

        return list;
    }
}
