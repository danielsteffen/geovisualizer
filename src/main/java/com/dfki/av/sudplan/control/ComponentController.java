/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.control;

import com.dfki.av.sudplan.conf.ApplicationConfiguration;
import com.dfki.av.sudplan.conf.InitialisationException;
import com.dfki.av.sudplan.io.dem.ElevationShape;
import com.dfki.av.sudplan.io.shape.ShapeLoader;
import com.dfki.av.sudplan.io.shape.ShapefileObject;
import com.dfki.av.sudplan.layer.FeatureLayer;
import com.dfki.av.sudplan.layer.Layer;
import com.dfki.av.sudplan.layer.LayerManager;
import com.dfki.av.sudplan.layer.LayerStateEvent;
import com.dfki.av.sudplan.layer.SimpleLayerManager;
import com.dfki.av.sudplan.ui.MainFrame;
import com.dfki.av.sudplan.ui.ProgressPanel;
import com.dfki.av.sudplan.ui.vis.VisualisationComponent;
import com.dfki.av.sudplan.layer.LayerListener;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import javax.media.j3d.LineArray;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:generate Interface this is a Simple ComponentBroker
public class ComponentController implements DropTargetListener, LayerListener {

    private final static Logger logger = LoggerFactory.getLogger(ComponentController.class);
    private boolean initialiseLoggingEnabled = true;
    private boolean isInitialized;
    private VisualisationComponent visualisationComponent;
    private ApplicationConfiguration applicationConfiguration;
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:remove after Vienna
    private MainFrame mainFrame;
    private ElevationShape dem = null;
    private ShapeLoader concentrationLoader = null;
    private LayerManager layerManager = new SimpleLayerManager();
    private ArrayList<DropTarget> dropTargets = new ArrayList<DropTarget>();
//  private TransferHandler transferHandler =

    public ComponentController(final VisualisationComponent visualisationComponent) throws InitialisationException {
        this(visualisationComponent, new ApplicationConfiguration());
    }
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: Exception Handling

    public ComponentController(final VisualisationComponent visualisationComponent,
            final ApplicationConfiguration applicationConfiguration) throws InitialisationException {
        try {
            setVisualisationComponent(visualisationComponent);

            setApplicationConfiguration(applicationConfiguration);
//    visualisationComponent.getDnDComponent().setTransferHandler(handler);
//        configureLogging();        
            layerManager.addLayerListener(this);
        } catch (Exception ex) {
            final String message = "Error during Controller initialisation.";
            if (logger.isErrorEnabled()) {
                logger.error(message, ex);
            }
            throw new InitialisationException(message, ex);
        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:does not happen and
    public ComponentController() throws InitialisationException {
        this(null, new ApplicationConfiguration());
    }

    public final boolean isInitialiseLoggingEnabled() {
        return initialiseLoggingEnabled;
    }

    public final void setInitialiseLoggingEnabled(final boolean initialiseLogging) {
        this.initialiseLoggingEnabled = initialiseLogging;
    }

    public final VisualisationComponent getVisualisationComponent() {
        return visualisationComponent;
    }

    public final void setVisualisationComponent(final VisualisationComponent visualisationComponent) throws DragAndDropException {
        if (this.visualisationComponent != null) {
            removeDropTarget(getVisualisationComponent().getDnDComponent());
        }
        this.visualisationComponent = visualisationComponent;
        if (visualisationComponent != null) {
            addDropTarger(getVisualisationComponent().getDnDComponent());

        }
//    this.visualisationComponent.getDnDComponent().setTransferHandler(handler);
    }

    public final ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public final void setApplicationConfiguration(final ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(final MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    private void configureLogging() {
        if (applicationConfiguration.isLoggingEnabled()) {
        } else {
            logger.info("No logging will be configured. If the enveloping application does not configure logging,"
                    + " then it will not work.");
        }
    }

    @Override
    public void dragEnter(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(final DropTargetEvent dte) {
    }

    @Override
    public void dragOver(final DropTargetDragEvent dtde) {
    }

    @Override
    public void drop(final DropTargetDropEvent dtde) {
        if (logger.isDebugEnabled()) {
            logger.debug("DnD on Component: " + dtde.getDropTargetContext().getComponent() + ".");
            logger.debug("Data flavors: " + Arrays.deepToString(dtde.getTransferable().getTransferDataFlavors()));
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: check on unix & mac
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            Transferable tr = dtde.getTransferable();
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            try {
                List fileList = (List) tr.getTransferData(DataFlavor.javaFileListFlavor);
                if (logger.isDebugEnabled()) {
                    logger.debug("Drop list= " + fileList + ".");
                }
                setProgressDialogVisible(true);
                layerManager.addLayersFromFile(fileList);
            } catch (UnsupportedFlavorException ex) {
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:user information
                if (logger.isErrorEnabled()) {
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:is this correct when happens this ?
                    logger.error("The desired data format is not supported.", ex);
                }
            } catch (IOException ex) {
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: user information
                if (logger.isErrorEnabled()) {
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:is this correct when happens this ?
                    logger.error("Error while importing data.", ex);
                }
            }
            dtde.dropComplete(true);
        } else {
            if (logger.isDebugEnabled()) {
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:is this correct when happens this ?
                logger.debug("Drop rejected.");
            }
            dtde.rejectDrop();
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:poor design see above
    private void fileLoadingErrorNotification(final Exception ex, File file) {
        setProgressDialogVisible(false);
        if (logger.isErrorEnabled()) {
            logger.error("Error while loading file " + file.getName() + ".", ex);
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:i18n
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:better exceptionhandling give the user an reason
        JOptionPane.showMessageDialog(
                getMainFrame(),
                "The file: " + file.getName() + " could not be loaded.",
                "Error while loading File",
                JOptionPane.ERROR_MESSAGE);
    }
    private JDialog progressDialog;

    /*ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: replace by a more sophisticated panel e.g. floating panel 
     * withentries for each process.
     */
    private void setProgressDialogVisible(final boolean visible) {
        if (progressDialog == null) {
            progressDialog = new JDialog(mainFrame, "File Import");
            progressDialog.setContentPane(new ProgressPanel());
            progressDialog.pack();
        }
        progressDialog.setLocationRelativeTo(mainFrame);
        progressDialog.setVisible(visible);
    }

    public void setConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public ApplicationConfiguration getConfiguration() {
        return applicationConfiguration;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: remove after Vienna Meeting
    public void enableDEMTexture(final boolean enabled) {
        if (logger.isDebugEnabled()) {
            logger.debug("enabled: " + enabled + " dem: " + dem);
        }
        if (dem != null) {
//            dem.enableTexture(enabled);
        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: move and refactor after Vienna
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this could be used from the inital code of the loader almost exact the same code.
    public void recalculateColors(final Double lowConTresh, final Double mediumConTresh) {
//            concentration.recalculateColors(lowConTresh, mediumConTresh);
//         ShapefileObject[] shapes = concentrationLoader.getShapeArray().toArray(new ShapefileObject[]{});
        Double[] concentrations = concentrationLoader.getPointColors().toArray(new Double[]{});
        int counter = 0;
        for (ShapefileObject currentShapefileObject : concentrationLoader.getShapeArray()) {
            final LineArray currentGeometry = ((LineArray) currentShapefileObject.getGeometry());
            for (int i = 0; i < currentGeometry.getVertexCount(); i++) {
//                 if (logger.isDebugEnabled()) {
//                logger.debug("con: "+concentrations[counter]+" low: "+lowConTresh+" med: "+ mediumConTresh);
//            }
                if (concentrations[counter] < lowConTresh) {
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("green");
//                    }
                    currentGeometry.setColor(i, concentrationLoader.getLowConcentrationColor());
                } else if (concentrations[counter] < mediumConTresh) {
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("yellow");
//                    }
                    currentGeometry.setColor(i, concentrationLoader.getMediumConcentrationColor());
                } else {
//                     if (logger.isDebugEnabled()) {
//                        logger.debug("red");
//                    }
                    currentGeometry.setColor(i, concentrationLoader.getHighConcentrationColor());
                }
                if (i % 2 != 0) {
                    counter++;
                }
            }
            counter++;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Totalindex: " + counter);
        }
    }

    public void addDropTarger(final Component targetToAdd) throws DragAndDropException {
        final DropTarget newDropTarget = new DropTarget();
        newDropTarget.setDefaultActions(DnDConstants.ACTION_COPY_OR_MOVE);
        newDropTarget.setComponent(targetToAdd);
        try {
            newDropTarget.addDropTargetListener(this);
        } catch (TooManyListenersException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Too many DnD listeners.", ex);
            }
            throw new DragAndDropException("Not possible to register Listner", ex);
        }
        dropTargets.add(newDropTarget);
    }

    public void removeDropTarget(final Component targetToRemove) {
        if (targetToRemove != null) {
            DropTarget dropTargetToRemove = null;
            for (DropTarget currentTarget : dropTargets) {
                if (currentTarget.getComponent() != null && targetToRemove.equals(currentTarget.getComponent())) {
                    dropTargetToRemove = currentTarget;
                }
            }
            if (dropTargetToRemove != null) {
                dropTargetToRemove.removeDropTargetListener(this);
                dropTargets.remove(dropTargetToRemove);
            }
        }
    }

    public LayerManager getLayerManager() {
        return layerManager;
    }

    public void setLayerManager(final LayerManager layerManager) {
        this.layerManager = layerManager;
    }

    @Override
    public void layerAdded(final Layer addedLayer) {
        setProgressDialogVisible(false);
        if (addedLayer instanceof FeatureLayer) {
            visualisationComponent.addContent(((FeatureLayer) addedLayer).getDataObject());
        }
    }

    @Override
    public void layerNotAdded(final LayerStateEvent event) {
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: file is here not known
        fileLoadingErrorNotification(event.getEx(), event.getFile());
    }

    @Override
    public void layerRemoved(final Layer removedLayer) {
        if (removedLayer instanceof FeatureLayer) {
            visualisationComponent.removContent(((FeatureLayer) removedLayer).getDataObject());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof Layer) {
            final Layer layer = (Layer) evt.getSource();
            if (logger.isDebugEnabled()) {
                logger.debug("Layer " + layer.getName() + " property: " + evt.getPropertyName() + " has changed.");
            }
            if (evt.getPropertyName().equals("visible") && layer instanceof FeatureLayer) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Removing Content");
                }
                if (layer.isVisible()) {
                    visualisationComponent.addContent(((FeatureLayer) layer).getDataObject());
                } else {
                    visualisationComponent.removContent(((FeatureLayer) layer).getDataObject());
                }
            }
        }
    }

    public void enableModeZoom() {
            visualisationComponent.setModeZoom();
    }

    public void enableModePan() {
            visualisationComponent.setModePan();
    }

    public void enableModeCombined() {
            visualisationComponent.setModeCombined();
    }

    public void enableModeRotate() {
            visualisationComponent.setModeRotate();
    }

}