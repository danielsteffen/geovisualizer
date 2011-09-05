/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.util;

import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class GeomUtils {

    private final static Logger logger = LoggerFactory.getLogger(GeomUtils.class);

    public static void printNodePosition(final Node node) {
        Point3f pos = new Point3f();
        Transform3D tr = new Transform3D();
        node.getLocalToVworld(tr);
        tr.transform(pos);
        if (logger.isDebugEnabled()) {
            logger.debug("Node position: " + pos);
        }
    }
}
