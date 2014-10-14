/*
 * IColorPaletteProvider.java
 *
 * Created by DFKI AV on 11.02.2013.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.color.spi;

import de.dfki.av.geovisualizer.core.color.IColorPalette;
import java.util.List;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface IColorPaletteProvider {

    /**
     *
     * @return
     */
    List<String> getColorPalettes();

    /**
     *
     * @param name
     * @return
     */
    IColorPalette get(String name);
}
