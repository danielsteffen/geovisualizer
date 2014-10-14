/*
 *  SOSDateFormat.java 
 *
 *  Created by DFKI AV on 11.04.2013.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.io.sos.util;

/**
 * Class which holds util methods for date conversion
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class SOSDateFormat extends StandartDateFormat {

    @Override
    protected String getFormat() {
        return "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
    }
}
