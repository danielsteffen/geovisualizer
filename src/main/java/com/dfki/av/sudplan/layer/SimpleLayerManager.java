/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.layer;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.io.dem.RawArcGrid;
import com.dfki.av.sudplan.layer.texture.GeographicImageLayer;
import com.dfki.av.sudplan.layer.texture.ImageLayer;
import com.dfki.av.sudplan.layer.texture.Texturable;
import com.dfki.av.sudplan.util.TimeMeasurement;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class SimpleLayerManager implements LayerManager {

    private final static Logger logger = LoggerFactory.getLogger(SimpleLayerManager.class);
    private final ArrayList<Layer> layers = new ArrayList<Layer>();
    private final ArrayList<LayerListener> layerListener = new ArrayList<LayerListener>();
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:right postion here ? should be in single loader or datastrutures
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:idea Layout manager will find loaders --> lookup and automaticly use them

//    public static enum FILE_TYPES {
//
//        SHAPE, ARC_INFO_GRID
//    };
    @Override
    public synchronized void addLayer(final Layer layer) {
        if (layer != null && !layers.contains(layer)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Adding layer: " + layer.getName());
                logger.debug("Layer BoundingBox: " + layer.getBoundingBox());
            }
            if (layer instanceof ImageLayer) {
                notifyTextureProviderAdded((ImageLayer) layer);
            }
            layers.add(layer);
            layer.addPropertyChangeListener(this);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:maybe a notifcation method not to duplicate the code but then you duplicate the event evaluation
            for (LayerListener currentListener : layerListener) {
                layer.addPropertyChangeListener(currentListener);
                currentListener.layerAdded(layer);
            }
        }
    }

    @Override
    public synchronized void removeLayer(final Layer layerToRemove) {
        if (layerToRemove != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Remove layer: " + layerToRemove.getName());
            }
            if (layerToRemove instanceof ImageLayer) {
                notifyTextureProviderRemoved((ImageLayer) layerToRemove);
            }
            if (layers.contains(layerToRemove)) {
                layers.remove(layerToRemove);
                layerToRemove.removePropertyChangeListener(this);
                for (LayerListener currentListener : layerListener) {
                    currentListener.layerRemoved(layerToRemove);
                }
            }
        }
    }

    @Override
    public void removeLayers(final ArrayList<Layer> layersToRemove) {
        if (layersToRemove != null && layersToRemove.size() > 0) {
            for (Layer currentLayer : layersToRemove) {
                removeLayer(currentLayer);
            }
        }
    }

    @Override
    public Layer getLayer(final int index) {
        return layers.get(index);
    }

    @Override
    public List<Layer> getLayers() {
        return layers;
    }

    @Override
    public void scrollToLayer(final Layer layer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addLayerListener(final LayerListener listener) {
        if (!layerListener.contains(listener)) {
            layerListener.add(listener);
        }
    }

    @Override
    public void removeLayerListener(final LayerListener listener) {
        if (layerListener.contains(listener)) {
            layerListener.remove(listener);
        }
    }

    @Override
    public void addLayerFromFile(final File file) {
        if (logger.isInfoEnabled()) {
            logger.info("Trying to load file: " + file.getName() + ".");
        }
        try {
            /*ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:improve if the file
             * extension is not correctly set automatic recognition should be done.
             * In the worst case the user should be asked what filetype it is.
             */
            LayerWorker worker = new LayerWorker(file);
            worker.execute();
        } catch (Exception ex) {
//                fileLoadingErrorNotification(ex, file);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:duplicated
            for (LayerStateListener currentListener : layerListener) {
                currentListener.layerNotAdded(new LayerStateEvent(
                        System.currentTimeMillis(),
                        file,
                        ex));
            }
        }
    }

    @Override
    public void addLayerFromFile(final String filename) {
        addLayerFromFile(new File(filename));
    }

    @Override
    public void addLayerFromFile(final URL fileURL) {
        addLayerFromFile(fileURL.getFile());
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:encapsulate in an own class
    @Override
    public void addLayersFromFile(final List<File> files) {
        if (files == null) {
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("loading files...");
        }
        for (final File file : files) {
            addLayerFromFile(file);
        }
        if (logger.isInfoEnabled()) {
            logger.info("loading files done.");
        }
    }

    private void notifyTextureProviderAdded(final ImageLayer layer) {

        if (logger.isDebugEnabled()) {
            logger.debug("Adding ImageLayer to Texturables.");
        }
        for (final Layer currentLayer : layers) {
            if (currentLayer instanceof Texturable) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Adding layer to texturable: " + currentLayer);
                }
                ((Texturable) currentLayer).addTextureProvider(((ImageLayer) layer));
            }
        }
    }

    private void notifyTextureProviderRemoved(final ImageLayer layer) {
        if (layer instanceof ImageLayer) {
            if (logger.isDebugEnabled()) {
                logger.debug("Removing ImageLayer from Texturables.");
            }
            for (final Layer currentLayer : layers) {
                if (currentLayer instanceof Texturable) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Removing layer from texturable: " + currentLayer);
                    }
                    ((Texturable) currentLayer).removeTextureProvider(((ImageLayer) layer));
                }
            }
        }
    }

    class LayerWorker extends SwingWorker<Layer, Void> {

        public LayerWorker(File file) {
            this.file = file;
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:see idea above depending on the layertype the right loader will be used
        File file;

        @Override
        protected Layer doInBackground() throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug("Background load of layer");
                TimeMeasurement.getInstance().startMeasurement(this);
            }
            Layer newLayer = null;
            if (file.getName().endsWith(RawArcGrid.FILE_EXTENSION)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Given file is of type: " + RawArcGrid.NAME + ".");
                    newLayer = new ElevationLayer(file);
                    ComponentBroker.getInstance().setHeights(((ElevationLayer) newLayer).getGrid());
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: generic version for all the loader
                }
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:make constant and also check for shx dbf prj etc. also use suffix same above
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: make something generic with suffixes
            } else if (file.getName().endsWith(".shp")) {
                newLayer = new ShapeLayer(file);
            } else if (file.getName().endsWith(".tif") || file.getName().endsWith(".tiff")) {
                newLayer = new GeographicImageLayer(file);
            } else {
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:i18n
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:default images look bad.
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: duplicated code
                for (LayerStateListener currentListener : layerListener) {
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:bad design + duplicated
                    currentListener.layerNotAdded(new LayerStateEvent(
                            System.currentTimeMillis(),
                            file,
                            new Exception("The given file could not be added. The file type is unknown.")));
                }
//                JOptionPane.showMessageDialog(
//                        getMainFrame(),
//                        "The given file could not be added. The file type is unknown.",
//                        "Unrecognised File Type",
//                        JOptionPane.INFORMATION_MESSAGE);
            }
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:remove only for Vienna Demo

            if (logger.isDebugEnabled()) {
                logger.debug("Background load of layer done. Elapsed time: "
                        + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + ".");
            }
            return newLayer;
        }

        @Override
        protected void done() {
            try {
                addLayer(get());
//                                    if (loadedScene.getSceneGroup().getChild(0) instanceof ElevationShape) {
//                                        dem = (ElevationShape) loadedScene.getSceneGroup().getChild(0);
//                                        mainFrame.enableDEMButtons(true);
//                                        if (logger.isDebugEnabled()) {
//                                            logger.debug("Scene Bounds: " + dem.getBounds());
//                                        }
//                                    }
//                                    visualisationComponent.addContent(loadedScene);
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Error while postprocessing loaded layer.", ex);
                }
//                                    fileLoadingErrorNotification(ex, file);
                for (LayerStateListener currentListener : layerListener) {
                    currentListener.layerNotAdded(new LayerStateEvent(
                            System.currentTimeMillis(),
                            file,
                            ex));
                }
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
}
//   if (logger.isDebugEnabled()) {
//                        logger.debug("Given file is of type: ESRI Shapefile.");
//                        final ShapeLoader localShapeLoader = new ShapeLoader();
//                        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: generic version for all the loader
//                        SwingWorker<Scene, Void> sceneLoader = new SwingWorker<Scene, Void>() {
//
//                            @Override
//                            protected Scene doInBackground() throws Exception {
//                                if (logger.isDebugEnabled()) {
//                                    logger.debug("Background load of shapefile");
//                                    TimeMeasurement.getInstance().startMeasurement(this);
//                                }
//                                final Scene loadedScene = localShapeLoader.load(file);
//                                if (logger.isDebugEnabled()) {
//                                    logger.debug("Background load of shapefile scene done. Elapsed time: "
//                                            + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + ".");
//                                }
//                                return loadedScene;
//                            }
//
//                            @Override
//                            protected void done() {
//                                try {
//                                    final Scene loadedScene = get();
//                                    if (logger.isDebugEnabled()) {
//                                        logger.debug("Scene Bounds: " + ((Shape3D) loadedScene.getSceneGroup().getChild(0)).getBounds());
//                                    }
//                                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:remove after Vienna
//                                    if (loadedScene.getSceneGroup().getChild(0) instanceof ShapefileObject) {
//                                        final ShapefileObject shpObj = (ShapefileObject) loadedScene.getSceneGroup().getChild(0);
//                                        if (localShapeLoader.getShapeArray().size() != 0) {
//                                            concentrationLoader = localShapeLoader;
//                                            mainFrame.enableControls(true);
//                                        }
//                                    }
//                                    visualisationComponent.addContent(loadedScene);
//                                    setProgressDialogVisible(false);
//                                } catch (Exception ex) {
//                                    fileLoadingErrorNotification(ex, file);
//                                }
//                            }
//                        };
//                        sceneLoader.execute();

