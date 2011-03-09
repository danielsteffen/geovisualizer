/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.example;
import java.util.logging.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class JavaLoggingRedirection {
    private final static Logger logger =  java.util.logging.Logger.getLogger("org.wombat");


    public void testLogging(){        
        logger.fine("java logging test");
    }
}
