/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.main;

import com.dfki.av.sudplan.conf.ApplicationConfiguration;
import com.dfki.av.sudplan.conf.ComponentFactory;
import com.dfki.av.sudplan.conf.InitialisationException;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class Starter {
private final static Logger logger = LoggerFactory.getLogger(Starter.class);
  public static void main(String[] args) {
    
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: CLI support
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        try {          
          ComponentFactory.getStandaloneApplication(new ApplicationConfiguration()).setVisible(true);
        } catch (InitialisationException ex) {
          if(logger.isErrorEnabled()){
            logger.error("Error while starting application.",ex);
          }
        }
      }
    });
  }
}
