/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.io.dem.RawArcGrid;
import com.dfki.av.sudplan.layer.ElevationLayer;
import com.dfki.av.sudplan.layer.Layer;
import com.dfki.av.sudplan.layer.LayerIntialisationException;
import com.dfki.av.sudplan.layer.LayerStateEvent;
import com.dfki.av.sudplan.layer.LayerStateListener;
import com.dfki.av.sudplan.layer.ShapeLayer;
import com.dfki.av.sudplan.layer.texture.GeographicImageLayer;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class IOUtils {

    private final static Logger logger = LoggerFactory.getLogger(IOUtils.class);

    public static Layer createLayerFromFile(final File file) throws LayerIntialisationException {
        Layer newLayer = null;
        if (file == null) {
            return null;
        }
        if (file.getName().endsWith(RawArcGrid.FILE_EXTENSION)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Given file is of type: " + RawArcGrid.NAME + ".");
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: generic version for all the loader
            }
            newLayer = new ElevationLayer(file);
            ComponentBroker.getInstance().setHeights(((ElevationLayer) newLayer).getGrid());
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:make constant and also check for shx dbf prj etc. also use suffix same above
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: make something generic with suffixes
        } else if (file.getName().endsWith(".shp")) {
            newLayer = new ShapeLayer(file);
        } else if (file.getName().endsWith(".tif") || file.getName().endsWith(".tiff")) {
            newLayer = new GeographicImageLayer(file);
        } else {
            new LayerIntialisationException("The given file could not be added. The file type is unknown.");
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:i18n
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:default images look bad.
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: duplicated code
//                JOptionPane.showMessageDialog(
//                        getMainFrame(),
//                        "The given file could not be added. The file type is unknown.",
//                        "Unrecognised File Type",
//                        JOptionPane.INFORMATION_MESSAGE);
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:remove only for Vienna Demo
        return newLayer;
    }

    public static Layer createLayerFromURL(final URL fileURL) throws LayerIntialisationException {
        if (fileURL == null) {
            return null;
        }
        return createLayerFromFileString(fileURL.getFile());
    }

    public static Layer createLayerFromFileString(final String fileString) throws LayerIntialisationException {
        if (fileString == null) {
            return null;
        }
        return createLayerFromFile(new File(fileString));
    }

    public List<Layer> addLayersFromFile(final List<File> files) throws LayerIntialisationException {
        List<Layer> newLayers = new ArrayList<Layer>();
        if (files == null) {
            return newLayers;
        }
        for (final File file : files) {
            final Layer newLayer = createLayerFromFile(file);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:what if there is a problem while adding layers ??
            if (newLayer != null) {
                newLayers.add(newLayer);
            }
        }
        return newLayers;
    }
}
