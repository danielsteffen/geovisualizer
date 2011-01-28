/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.control;

import com.dfki.av.sudplan.conf.ApplicationConfiguration;
import com.dfki.av.sudplan.ui.vis.VisualisationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ComponentController {

  private final static Logger logger = LoggerFactory.getLogger(ComponentController.class);

  private boolean initialiseLoggingEnabled = true;
  private boolean isInitialized;
  private VisualisationComponent visualisationComponent;
  private ApplicationConfiguration applicationConfiguration;

  public ComponentController(final VisualisationComponent visualisationComponent) {
    this(visualisationComponent, new ApplicationConfiguration());
  }

  public ComponentController(final VisualisationComponent visualisationComponent, final ApplicationConfiguration applicationConfiguration) {
    setVisualisationComponent(visualisationComponent);
    setApplicationConfiguration(applicationConfiguration);
    configureLogging();
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
  }

  public final ApplicationConfiguration getApplicationConfiguration() {
    return applicationConfiguration;
  }

  public final void setApplicationConfiguration(final ApplicationConfiguration applicationConfiguration) {
    this.applicationConfiguration = applicationConfiguration;
  }

  private void configureLogging() {
    if (applicationConfiguration.isLoggingEnabled()) {
      
    } else {
      logger.info("No logging will be configured. If the enveloping application does not configure logging, then it will not work");
    }
  }
}
