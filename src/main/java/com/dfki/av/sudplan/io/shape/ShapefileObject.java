/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io.shape;

import javax.media.j3d.Geometry;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ShapefileObject extends Shape3D {
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:remove after Vienna


//    private final static Logger logger = LoggerFactory.getLogger(ShapefileObject.class);
//    private float lastMedium;
//    private float mediumConcentrationThreshold;
//    private float lastLow;
//    private float lowConcentrationThreshold;
//    private Color4f lowConcentrationColor;
//    private Color4f mediumConcentrationColor;
//    private Color4f highConcentrationColor;
//    private Double[] concentrationArray;
//
    public ShapefileObject(Geometry geometry) {
        super(geometry);
    }
//
//    public void setMediumConcentrationThreshold(float mediumConcentrationThreshold) {
//        this.mediumConcentrationThreshold = mediumConcentrationThreshold;
//    }
//
//    public void setLowConcentrationThreshold(float lowConcentrationThreshold) {
//        this.lowConcentrationThreshold = lowConcentrationThreshold;
//    }
//
//    public void setHighConcentrationColor(Color4f highConcentrationColor) {
//        this.highConcentrationColor = highConcentrationColor;
//    }
//
//    public void setMediumConcentrationColor(Color4f mediumConcentrationColor) {
//        this.mediumConcentrationColor = mediumConcentrationColor;
//    }
//
//    public void setLowConcentrationColor(Color4f lowConcentrationColor) {
//        this.lowConcentrationColor = lowConcentrationColor;
//    }
//
//    public Color4f getHighConcentrationColor() {
//        return highConcentrationColor;
//    }
//
//    public Color4f getLowConcentrationColor() {
//        return lowConcentrationColor;
//    }
//
//    public float getLowConcentrationThreshold() {
//        return lowConcentrationThreshold;
//    }
//
//    public Color4f getMediumConcentrationColor() {
//        return mediumConcentrationColor;
//    }
//
//    public float getMediumConcentrationThreshold() {
//        return mediumConcentrationThreshold;
//    }
//
//    void setConcentrationArray(Double[] concentrationArray) {
//        this.concentrationArray = concentrationArray;
//    }
//
//    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: remove after Vienna
//    public void recalculateColors(final Double lowConTresh, final Double mediumConTresh) {
//        if (logger.isDebugEnabled()) {
//            logger.debug("recalculate colors");
//        }
//        final LineArray geometry = (LineArray) getGeometry();
////        final Color4f[] colors= new Color4f[concentrationArray.length];
////        geometry.getColors(0, colors);
//        for (int i = 0; i < concentrationArray.length; i++) {
//            if (concentrationArray[i] < lowConTresh) {
//                geometry.setColor(i, lowConcentrationColor);
//            } else if (concentrationArray[i] < mediumConTresh) {
//                geometry.setColor(i, mediumConcentrationColor);
//            } else {
//                geometry.setColor(i, highConcentrationColor);
//            }
//        }
//        this.setGeometry(geometry);
//    }
}
