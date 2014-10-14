/*
 * IColorPalette.java
 *
 * Created by DFKI AV on 11.02.2013.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.color;

import java.awt.Color;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface IColorPalette {
    
    /**
     * 
     * @return 
     */
    String getName();

    /**
     * 
     * @return 
     */
    int getMinNumColors();

    /**
     * 
     * @return 
     */
    int getMaxNumColors();

    /**
     * 
     * @return 
     */
    List<Color> getColors();
    
    /**
     * 
     * @param num
     * @return 
     */
    List<Color> getColors(int num);
    
    /**
     * 
     */
    void initIcon();
    
    /**
     * 
     * @return 
     */
    ImageIcon getIcon();
}
