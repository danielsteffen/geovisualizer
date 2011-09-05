/*
 *  Created by DFKI AV on 05/09/2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.misc;

import java.io.File;
import java.util.logging.Logger;
//import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class JavaLoggingTest {

    private final static Logger logger = Logger.getLogger("JavaLoggingTest");

    public static void main(String[] args) {
        String loggingConfig = System.getProperty("java.util.logging.config.file");
        // Path where the log file resides (at the moment an absolute path)
        System.out.println(loggingConfig);
        File file = new File(loggingConfig);
        // Redirects Java logging to SLF4J
//        SLF4JBridgeHandler.install();
        System.out.println(file.getAbsolutePath());        
        System.out.println(loggingConfig);
        logger.fine("test");
    }
}
