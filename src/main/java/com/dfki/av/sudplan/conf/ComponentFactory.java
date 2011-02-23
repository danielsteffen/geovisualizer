/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.conf;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.control.ComponentController;
import com.dfki.av.sudplan.ui.MainFrame;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.logging.Level;
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
