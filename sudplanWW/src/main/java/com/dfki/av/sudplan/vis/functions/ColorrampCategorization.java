/*
 *  ColorrampCategorization.java 
 *
 *  Created by DFKI AV on 09.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions;

import com.dfki.av.sudplan.vis.ICategory;
import com.dfki.av.sudplan.io.DataSource;
import com.dfki.av.sudplan.io.shapefile.Shapefile;
import com.dfki.av.utils.ColorUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class ColorrampCategorization extends ColorCategorization {

    /*
     *
     */
    private static final Logger log = LoggerFactory.getLogger(ColorrampCategorization.class);
    /**
     *
     */
    private List<ICategory> categories;
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
    public ColorrampCategorization() {
        this.startColor = Color.GREEN;
        this.endColor = Color.RED;
        this.categories = new ArrayList<ICategory>();
        this.categories.add(new StringCategory(""));
        this.colorramp = ColorUtils.CreateLinearHSVColorGradient(startColor, endColor, categories.size());
    }

    @Override
    public Object calc(Object o) {
        if (o == null) {
            log.error("Argument set to null.");
            return Color.GRAY;
        }

        for (int i = 0; i < categories.size(); i++) {
            ICategory c = categories.get(i);
            if (c.contains(o)) {
                return colorramp[i];
            }
        }
        log.error("Should not reach this part.");
        return Color.GRAY;
    }

    @Override
    public String getName() {
        return "Color ramp";
    }

    @Override
    public void preprocess(DataSource data, String attribute) {
        log.debug("Preprocessing ...");
        log.debug("Auto classification of attribute <{}>.", attribute);
        categories.clear();
        Shapefile shapefile = (Shapefile) data;
        for (int i = 0; i < shapefile.getFeatureCount(); i++) {
            Object o = shapefile.getAttributeOfFeature(i, attribute);
            if (o instanceof String) {
                if (!categories.contains(new StringCategory((String) o))) {
                    categories.add(new StringCategory((String) o));
                }
            } else if(o instanceof Number){
                if (!categories.contains(new NumberCategory((Number) o))) {
                    categories.add(new NumberCategory((Number) o));
                }
            }
        }
        log.debug("Generated {} categories.", categories.size());
        this.colorramp = ColorUtils.CreateLinearHSVColorGradient(startColor, endColor, categories.size());
        log.debug("Preprocessing finished.");
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
