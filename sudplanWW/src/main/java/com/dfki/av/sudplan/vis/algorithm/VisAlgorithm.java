package com.dfki.av.sudplan.vis.algorithm;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public abstract class VisAlgorithm implements IVisAlgorithm {

    /*
     * Logger.
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private Icon icon;
    /**
     *
     */
    private String desription;
    /**
     *
     */
    private String name;

    /**
     * 
     */
    public VisAlgorithm(){
        this("Default Visualization");
    }

    /**
     *
     * @param name
     */
    public VisAlgorithm(String name) {
        this("Default Visualization", "No description");
    }

    /**
     * @param n
     * @param d
     */
    public VisAlgorithm(String name, String description) {
        this(new ImageIcon(VisAlgorithm.class.getClassLoader().
                getResource("default-visalgo-icon.jpg")), 
                name, 
                description);
    }

    /**
     *
     * @param i
     * @param d
     */
    public VisAlgorithm(Icon i, String n, String d) {
        this.icon = i;
        this.name = n;
        this.desription = d;

    }

    @Override
    public Icon getIcon() {
        return this.icon;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.desription;
    }
}
