/*
 * BasicColorPaletteProvider.java 
 *
 * Created by DFKI AV on 11.02.2013.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.color;

import de.dfki.av.geovisualizer.core.color.spi.IColorPaletteProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Daniel Steffen
 */
public class BasicColorPaletteProvider implements IColorPaletteProvider {

    @Override
    public List<String> getColorPalettes() {
        ArrayList<String> palettes = new ArrayList<>();
        palettes.add(RedGreenPalette.class.getName());
        return Collections.unmodifiableList(palettes);
    }

    @Override
    public IColorPalette get(String name) {
        if(name.equalsIgnoreCase(RedGreenPalette.class.getName())){
            RedGreenPalette rgp = new RedGreenPalette();
            rgp.initIcon();
            return rgp;
        }
        return null;
    }
}
