/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.conf;

import com.dfki.av.sudplan.control.ComponentController;
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
  public static JFrame getStandaloneApplication(ApplicationConfiguration configuration) {
    logger.info("test");
    if (configuration == null) {
      logger.info("No configuration properties, starting with default properties");
      configuration = new ApplicationConfiguration();
    }
    final MainFrame newApplicatioFrame = new MainFrame();    
    ComponentController compControl = new ComponentController(newApplicatioFrame.getVisualisationComponent(), configuration);
    newApplicatioFrame.setController(compControl);
    return newApplicatioFrame;
  }

  public static JPanel getVisualisationPanel(Properties configurationProperties) {
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: implemenent SingleMode
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
