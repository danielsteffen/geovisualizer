/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.control;

import com.dfki.av.sudplan.conf.ApplicationConfiguration;
import com.dfki.av.sudplan.io.dem.RawArcGrid;
import com.dfki.av.sudplan.io.dem.DEMLoader;
import com.dfki.av.sudplan.ui.ProgressPanel;
import com.dfki.av.sudplan.ui.vis.VisualisationComponent;
import com.dfki.av.sudplan.util.TimeMeasurement;
import com.sun.j3d.loaders.Scene;
import java.awt.Frame;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TooManyListenersException;
import javax.media.j3d.Shape3D;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ComponentController implements DropTargetListener {

    private final static Logger logger = LoggerFactory.getLogger(ComponentController.class);
    private boolean initialiseLoggingEnabled = true;
    private boolean isInitialized;
    private VisualisationComponent visualisationComponent;
    private ApplicationConfiguration applicationConfiguration;
    private Frame mainFrame;
    private final DropTarget dropTarget = new DropTarget();
//  private TransferHandler transferHandler =

    public ComponentController(final VisualisationComponent visualisationComponent) throws TooManyListenersException {
        this(visualisationComponent, new ApplicationConfiguration());
    }
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: Exception Handling

    public ComponentController(final VisualisationComponent visualisationComponent,
            final ApplicationConfiguration applicationConfiguration) throws TooManyListenersException {
        setVisualisationComponent(visualisationComponent);
        setApplicationConfiguration(applicationConfiguration);
//    visualisationComponent.getDnDComponent().setTransferHandler(handler);
        configureLogging();
    }

    public ComponentController() {
        configureDropBehaviour();
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

    public final void setVisualisationComponent(final VisualisationComponent visualisationComponent) {
        this.visualisationComponent = visualisationComponent;
        dropTarget.setComponent(visualisationComponent.getDnDComponent());
//    this.visualisationComponent.getDnDComponent().setTransferHandler(handler);
    }

    public final ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public final void setApplicationConfiguration(final ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public Frame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(Frame mainFrame) {
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
            logger.debug("DnD on Component: " + dropTarget.getComponent() + ".");
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
                    setProgressDialogVisible(true);
                    loadFile(fileList);
                }
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

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:encapsulate in an own class
    public void loadFile(List<File> files) {
        if (files == null) {
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("loading files...");
        }
        for (final File file : files) {
            if (logger.isInfoEnabled()) {
                logger.info("Trying to load file: " + file.getName() + ".");
            }
            try {
                /*ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:improve if the file
                 * extension is not correctly set automatic recognition should be done.
                 * In the worst case the user should be asked what filetype it is.
                 */
                if (file.getName().endsWith(RawArcGrid.FILE_EXTENSION)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Given file is of type: " + RawArcGrid.NAME + ".");
                        final DEMLoader demLoader = new DEMLoader();
                        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: generic version for all the loader
                        SwingWorker<Scene, Void> sceneLoader = new SwingWorker<Scene, Void>() {

                            @Override
                            protected Scene doInBackground() throws Exception {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Background load of DEM scene");
                                    TimeMeasurement.getInstance().startMeasurement(this);
                                }
                                final Scene loadedScene = demLoader.load(file);
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Background load of DEM scene done. Elapsed time: " +
                                            TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + ".");
                                }
                                return loadedScene;
                            }

                            @Override
                            protected void done() {
                                try {
                                    final Scene loadedScene = get();
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("Scene Bounds: "+((Shape3D)loadedScene.getSceneGroup().getChild(0)).getBounds());
                                    }
                                    visualisationComponent.addContent(loadedScene);
                                    setProgressDialogVisible(false);
                                } catch (Exception ex) {
                                    fileLoadingErrorNotification(ex, file);
                                }
                            }
                        };
                        sceneLoader.execute();
                    }
                } else {
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:i18n
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:default images look bad.
                    setProgressDialogVisible(false);
                    JOptionPane.showMessageDialog(
                            getMainFrame(),
                            "The given file could not be added. The file type is unknown.",
                            "Unrecognised File Type",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                fileLoadingErrorNotification(ex, file);
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("loading files done.");
        }
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

    private void configureDropBehaviour() {
        try {
            dropTarget.addDropTargetListener(this);
        } catch (TooManyListenersException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("It was not possible to create Drag & Drop support. Drag & Drop on the visualisation "
                        + "component will not work.", ex);
            }
        }
        dropTarget.setDefaultActions(DnDConstants.ACTION_COPY_OR_MOVE);
        if (visualisationComponent != null && visualisationComponent.getDnDComponent() != null) {
            dropTarget.setComponent(visualisationComponent.getDnDComponent());
        }
    }
}
