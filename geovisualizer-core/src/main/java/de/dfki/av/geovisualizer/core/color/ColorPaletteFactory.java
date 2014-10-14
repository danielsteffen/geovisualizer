/*
 * ColorPaletteFactory.java 
 *
 * Created by DFKI AV on 11.02.2013.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.color;

import de.dfki.av.geovisualizer.core.color.spi.IColorPaletteProvider;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ColorPaletteFactory {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ColorPaletteFactory.class);
    /**
     * The {@link List} of names of available color palettes.
     */
    private static final List<String> COLORPALETTE_NAMES = new ArrayList<>();
    /**
     * The {@link List} of available {@link IColorPaletteProvider}.
     */
    private static final List<IColorPaletteProvider> PROVIDERS = new ArrayList<>();

    /**
     * Initialize the IColorPaletteProvider using SPI technology.
     */
    static {
        ServiceLoader<IColorPaletteProvider> service = ServiceLoader.load(IColorPaletteProvider.class);
        for (Iterator<IColorPaletteProvider> providers = service.iterator(); providers.hasNext();) {
            IColorPaletteProvider provider = providers.next();
            LOG.info("Found plugin: {}", provider.getClass().getName());
            COLORPALETTE_NAMES.addAll(provider.getColorPalettes());
            PROVIDERS.add(provider);
        }
    }

    /**
     * Return the names of available ColorPalettes.
     *
     * @return a {@link List} of names of available color palettes.
     */
    public static List<String> getNames() {
        return Collections.unmodifiableList(COLORPALETTE_NAMES);
    }

    /**
     * Return an instance of an {@link IColorPalette} if available. If not color
     * palette with the name {@code name} can be found {@code null} is returned.
     *
     * @param name the name of the color palette.
     * @param numColors number of colors in the palette.
     * @return the {@link IColorPalette} to return.
     */
    public static IColorPalette newInstance(String name) {
        IColorPalette colorPalette = null;
        for (IColorPaletteProvider provider : PROVIDERS) {
            colorPalette = provider.get(name);
            if (colorPalette != null) {
                break;
            }
        }
        return colorPalette;
    }

    /**
     * Quick test which color palettes are currently available.
     *
     * @param args
     */
    public static void main(String[] args) {
        List<String> names = ColorPaletteFactory.getNames();
        for (Iterator<String> it = names.iterator(); it.hasNext();) {
            String colorPaletteName = it.next();
            LOG.debug("Found color palette: {}", colorPaletteName);
            IColorPalette palette = ColorPaletteFactory.newInstance(colorPaletteName);
            LOG.debug(palette.toString());
        }
    }
}
