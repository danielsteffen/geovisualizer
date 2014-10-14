/*
 *  StandartDateFormat.java 
 *
 *  Created by DFKI AV on 11.04.2013.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.io.sos.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class which holds util methods for date conversion
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class StandartDateFormat {

    private final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
        @Override
        public DateFormat get() {
            return super.get();
        }

        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(getFormat());
        }

        @Override
        public void remove() {
            super.remove();
        }

        @Override
        public void set(DateFormat value) {
            super.set(value);
        }
    };

    public final Date convertStringToDate(String dateString)
            throws ParseException {
        return df.get().parse(dateString);
    }

    public final String convertDateToString(Date date) {
        return df.get().format(date);
    }

    /**
     * Returns the {@link String} representation of the date format. <p> Note:
     * Should be overridden in case you want to use this class with a different
     * date format.
     *
     * @return {@link String} representation of the date format.
     */
    protected String getFormat() {
        return "MM.dd.yyyy HH:mm:ss.SSS";
    }
}
