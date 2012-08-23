/*
 *  Settings.java 
 *
 *  Created by DFKI AV on 01.08.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public final class Settings {
    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(Settings.class);
    /**
     * User home directory of SUDPLAN 3D component.
     */
    public static final String SUDPLAN_3D_USER_HOME = initialize();

    private static String initialize(){
        String seperator = System.getProperty("file.separator");
        String userHome = System.getProperty("user.home");
        String sudplan_user_home = userHome + seperator + ".sudplan3D";
        File sudplanDirectory = new File(sudplan_user_home);
        if (sudplanDirectory.exists()) {
            log.debug("Directory already existing.");
        } else {
            if (sudplanDirectory.mkdir()) {
                log.debug("Directory: {} created.", sudplan_user_home);
            } else {
                log.debug("Could not create directory {}.", sudplan_user_home);
            }
        }
        return sudplan_user_home;
    }
}
