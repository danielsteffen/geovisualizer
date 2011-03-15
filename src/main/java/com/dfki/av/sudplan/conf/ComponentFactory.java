/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.conf;

import com.dfki.av.sudplan.camera.CameraEvent;
import com.dfki.av.sudplan.camera.CameraListener;
import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.control.ComponentController;
import com.dfki.av.sudplan.geo.GeographicCameraAdapter;
import com.dfki.av.sudplan.ui.MainFrame;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ComponentFactory {

    private final static Logger logger = LoggerFactory.getLogger(ComponentFactory.class);

    /* ToDo would it be a good idea to search for common properties and build a abstract interface
     * in order to build a common interface for StandaloneApplication & VisualisationPanel
     */
    public static JFrame getStandaloneApplication(ApplicationConfiguration configuration)
            throws InitialisationException {
        if (configuration == null) {
            if (logger.isInfoEnabled()) {
                logger.info("No configuration properties, starting with default properties.");
            }
            configuration = new ApplicationConfiguration();
        }

        MainFrame newApplicatioFrame = null;
        ComponentController compControl = null;
        try {
            newApplicatioFrame = new MainFrame();
            ComponentBroker.getInstance().setMainFrame(newApplicatioFrame);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this will not work good idea to make a init method for mainframe
            newApplicatioFrame.getController().setConfiguration(configuration);
            ComponentBroker.getInstance().getController().getVisualisationComponent().getGeographicCamera().addCameraListner(new CameraListener() {

                @Override
                public void cameraMoved(CameraEvent cameraEvent) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("old: " + cameraEvent.getOldCameraPosition());
                        logger.debug("new: " + cameraEvent.getNewCameraPosition());
                    }
                }

                @Override
                public void cameraViewChanged(CameraEvent cameraEvent) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("old view: " + cameraEvent.getOldCameraViewDirection());
                        logger.debug("new view: " + cameraEvent.getNewCameraViewDirection());
                    }
                }

                @Override
                public void cameraRegistered(CameraEvent cameraEvent) {
                }

                @Override
                public void cameraUnregistered(CameraEvent cameraEvent) {
                }
            });
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                final String message = "Error during initialisation of ComponentController.";
                logger.error(message, ex);
                throw new InitialisationException(message, ex);
            }
        }
        return newApplicatioFrame;
    }

    public static JPanel getVisualisationPanel(final Properties configurationProperties) {
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: implemenent SingleMode
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
