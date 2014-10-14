/*
 * ColorBrewerProvider.java
 *
 * Created by DFKI AV on 08.02.2013.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.color.brewer;

import de.dfki.av.geovisualizer.core.color.ColorPalette;
import de.dfki.av.geovisualizer.core.color.IColorPalette;
import de.dfki.av.geovisualizer.core.color.spi.IColorPaletteProvider;
import de.dfki.av.geovisualizer.core.io.IOUtils;
import java.awt.Color;
import java.io.*;
import java.net.URL;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen@dfki.de>
 */
public class ColorBrewerProvider implements IColorPaletteProvider {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ColorBrewerProvider.class);
    /**
     * The name of the resource file.
     */
    private static final String BREWER_FILE = "brewer.txt";
    /**
     * The hash map managing the colorToken brewerPalettes provided by Brewer et
     * al.
     */
    private static final HashMap<String, IColorPalette> COLOR_PALETTES = init();


    /**
     * Initialize the {@link #COLOR_PALETTES} map.
     */
    private static HashMap init() {

        HashMap<String, BrewerPalette> brewerPalettes = new HashMap<>();

        URL url = Thread.currentThread().getContextClassLoader().getResource(BREWER_FILE);
        try {
            File tmpFile = IOUtils.downloadToTempFile(url);
            FileInputStream fis = new FileInputStream(tmpFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;

            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    LOG.debug("Input line: {}", line);
                    String[] tokens = line.split(" ");
                    LOG.debug("Tokens length: {}", tokens.length);
                    if (tokens.length >= 3) {
                        String name = tokens[0];
                        String[] nameParts = name.split("-");
                        LOG.debug("Palette name: {}", nameParts[0]);

                        try {
                            // Check wether the last part of the tokens is a 
                            // letter. If so an Exception is thrown and the 
                            // rest is skipped.
                            Integer.parseInt(nameParts[3]);

                            // Now check the category.
                            int i = Integer.parseInt(nameParts[1]);

                            // Check whether colorToken palette is already existing.
                            // If not create one and add it to the brewerPalettes.
                            BrewerPalette palette;
                            if (!brewerPalettes.containsKey(nameParts[0])) {
                                palette = new BrewerPalette(nameParts[0]);
                                brewerPalettes.put(nameParts[0], palette);
                            } else {
                                palette = brewerPalettes.get(nameParts[0]);
                            }

                            // Finally, parse the colorToken tokens and add them
                            // to the palette.
                            String colorToken = tokens[2];
                            String[] colorParts = colorToken.split(",");
                            Integer r = Integer.valueOf(colorParts[0]);
                            Integer g = Integer.valueOf(colorParts[1]);
                            Integer b = Integer.valueOf(colorParts[2]);
                            Color color = new Color(r, g, b);
                            palette.addColor(color, i);
                            LOG.debug("Color: {}", color.toString());
                        } catch (NumberFormatException e) {
                            LOG.debug("Skipping: {}", line);
                        }
                    } else {
                        LOG.debug("Skipping comment: {}", line);
                    }
                }
            }

        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        for (BrewerPalette palette : brewerPalettes.values()) {
            palette.initIcon();
        }
        return brewerPalettes;
    }


    /**
     *
     * @return
     */
    @Override
    public List<String> getColorPalettes() {
        Set keys = COLOR_PALETTES.keySet();
        ArrayList<String> names = new ArrayList<>();
        for (Iterator<String> it = keys.iterator(); it.hasNext();) {
            String key = it.next();
            names.add(key);
        }
        return Collections.unmodifiableList(names);
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public IColorPalette get(String name) {
        IColorPalette palette = null;
        if (COLOR_PALETTES.containsKey(name)) {
            palette = COLOR_PALETTES.get(name);
        }

        return palette;
    }
}
