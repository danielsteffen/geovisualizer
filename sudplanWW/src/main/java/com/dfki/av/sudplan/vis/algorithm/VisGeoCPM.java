package com.dfki.av.sudplan.vis.algorithm;

import javax.swing.ImageIcon;

/**
 *
 * @author steffen
 */
public class VisGeoCPM extends VisExtrudePolygon {

    protected VisGeoCPM() {
        super("GeoCPM Visualization", "Visualization of GeoCPM results of Wuppertal.",
                new ImageIcon(VisGeoCPM.class.getClassLoader().
                getResource("icons/VisGeoCPM.png")), false);
    }
}
