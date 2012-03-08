package com.dfki.av.sudplan.vis.algorithm.functions;

import com.dfki.av.sudplan.io.DataInput;
import com.dfki.av.utils.ColorUtils;
import java.awt.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class ColorrampTransferFunction extends ColorTransferFunction {
    /*
     *
     */

    private static final Logger log = LoggerFactory.getLogger(ColorrampTransferFunction.class);
    /**
     *
     */
    private double min;
    /**
     *
     */
    private double max;
    /**
     *
     */
    private int numCategories;
    /**
     *
     */
    private Color[] colorramp;
    /**
     *
     */
    private Color startColor;
    /**
     *
     */
    private Color endColor;

    /**
     *
     */
    public ColorrampTransferFunction() {
        this.min = Double.MIN_VALUE;
        this.max = Double.MAX_VALUE;
        this.numCategories = 5;
        this.startColor = Color.RED;
        this.endColor = Color.GREEN;
        this.colorramp = ColorUtils.CreateLinearColorGradient(startColor, endColor, numCategories);
    }

    @Override
    public Object calc(Object o) {
        if (o == null) {
            log.error("Argument set to null.");
            return Color.GRAY;
        }
        
        if (o instanceof Number) {
            double arg = ((Number) o).doubleValue();
            double categorieSize = (this.max - this.min) / (double) this.getNumCategories();
            for (int i = 0; i < getNumCategories(); i++) {
                if (arg <= min + (i + 1) * categorieSize) {
                    return colorramp[i];
                }
            }
            log.error("Should not reach this part.");
        } else {
            log.error("Data type {} not supported", o.getClass().getSimpleName());
            return Color.GRAY;
        }
        return Color.GRAY;
    }

    @Override
    public String getName() {
        return "Color ramp";
    }

    @Override
    public void preprocess(DataInput data, String attribute) {
        log.debug("Pre-processing ...");
        this.colorramp = ColorUtils.CreateLinearColorGradient(startColor, endColor, numCategories);
        this.min = data.min(attribute);
        this.max = data.max(attribute);
        log.debug("Minimum for attribute {} is {}.", attribute, min);
        log.debug("Maximum for attribute {} is {}.", attribute, max);
        log.debug("Pre-processing finished.");
    }

    /**
     * @return the numCategories
     */
    public int getNumCategories() {
        return numCategories;
    }

    /**
     * @param numCategories the numCategories to set
     */
    public void setNumCategories(int numCategories) {
        if (numCategories <= 0) {
            throw new IllegalArgumentException("No valid argument. "
                    + "'numCategories' has to be greater 0.");
        }
        this.numCategories = numCategories;
    }

    /**
     * @return the startColor
     */
    public Color getStartColor() {
        return startColor;
    }

    /**
     * @param startColor the startColor to set
     */
    public void setStartColor(Color startColor) {
        if (startColor == null) {
            throw new IllegalArgumentException("Color parameter is null.");
        }
        this.startColor = startColor;
    }

    /**
     * @return the endColor
     */
    public Color getEndColor() {
        return endColor;
    }

    /**
     * @param endColor the endColor to set
     */
    public void setEndColor(Color endColor) {
        if (endColor == null) {
            throw new IllegalArgumentException("Color parameter is null.");
        }
        this.endColor = endColor;
    }
}
