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

    /*
     *
     */
    public static IVisAlgorithm EXTRUDE_POLYGON(){
        return new VisExtrudePolygon();
    }
    /*
     *
     */
    public static IVisAlgorithm EXTRUDE_POLYLINE(){
        return new VisExtrudePolyline();
    }
    /*
     *
     */
    public static IVisAlgorithm POINT_CLOUD(){
        return new VisPointCloud();
    }
    /*
     *
     */
    public static IVisAlgorithm TIMESERIES(){
        return new VisTimeseries();
    }

    /**
     *
     * @return
     */
    public static Iterable<IVisAlgorithm> GET() {
        List<IVisAlgorithm> list = new ArrayList<IVisAlgorithm>();
        list.add(new VisExtrudePolygon());
        list.add(new VisExtrudePolyline());
        list.add(new VisPointCloud());
        list.add(new VisTimeseries());

        return list;
    }
}
