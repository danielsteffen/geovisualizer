package com.dfki.av.sudplan.vis.algorithm.functions;

import com.dfki.av.sudplan.io.DataSource;
import com.dfki.av.sudplan.vis.algorithm.functions.classification.IClass;
import com.dfki.av.sudplan.vis.algorithm.functions.classification.NumberInterval;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class ColorRuleClassification extends ColorClassification {
    /*
     *
     */

    private static final Logger log = LoggerFactory.getLogger(ColorRuleClassification.class);
    /**
     *
     */
    private List<IClass> classes;
    /**
     *
     */
    private List<Color> colorramp;

    /**
     *
     */
    public ColorRuleClassification() {
        this.classes = new ArrayList<IClass>();
        this.colorramp = new ArrayList<Color>();
        addClassification(new NumberInterval(), Color.GRAY);
    }

    @Override
    public Object calc(Object o) {
        if (o == null) {
            log.error("Argument set to null.");
            return Color.GRAY;
        }

        if (o instanceof Number) {
            double arg = ((Number) o).doubleValue();
            for (int i = 0; i < classes.size(); i++) {
                IClass c = classes.get(i);
                if (c.contains(arg)) {
                    return colorramp.get(i);
                }
            }
        } else {
            log.error("Data type {} not supported", o.getClass().getSimpleName());
        }
        return Color.GRAY;
    }

    @Override
    public String getName() {
        return "Color rules";
    }

    @Override
    public void preprocess(DataSource data, String attribute) {
    }

    /**
     * 
     * @param c
     * @param color 
     */
    public void addClassification(IClass c, Color color) {
        this.classes.add(c);
        this.colorramp.add(color);
    }
    
    /**
     * 
     */
    public void clear(){
        this.classes.clear();
        this.colorramp.clear();
    }
}
