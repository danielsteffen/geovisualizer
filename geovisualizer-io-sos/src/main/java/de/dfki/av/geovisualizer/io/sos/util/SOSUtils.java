/*
 *  SOSUtils.java 
 *
 *  Created by DFKI AV on 01.02.2013.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.io.sos.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Class which holds util methods for SOS source handeling.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class SOSUtils {

    /**
     * Date format for parsing MM.dd.yyyy HH:mm:ss.SSS structured dates.
     */
    public static final StandartDateFormat SDF = new StandartDateFormat();
    /**
     * Date format for parsing from SOS data.
     */
    public static final SOSDateFormat SDF_SOS = new SOSDateFormat();
    /**
     * Date format for parsing from URL.
     */
    public static final URLDateFormat SDF_URL = new URLDateFormat();
    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(SOSUtils.class);

    /**
     * Transforms the given {@link Calendar} to {@link String} representation
     * with the format MM.dd.yyyy HH:mm:ss
     *
     * @param date Date to transform
     * @return {@link String} representation
     */
    public static String calendar2String(Date date) {
        return SDF.convertDateToString(date);
    }

    /**
     * Transforms the given {@link Calendar} to {@link String} representation
     * with the format yyyy-MM-dd'T'HH:mm:ss'.000+01:00'
     *
     * @param date Date to transform
     * @return {@link String} representation
     */
    public static String calendar2SOSString(Date date) {
        return SDF_SOS.convertDateToString(date);
    }

    /**
     * Transforms the given {@link String} representation with the format
     * MM.dd.yyyy HH:mm:ss to a {@link Calendar}
     *
     * @param timestamp Timestamp to transform
     * @return {@link Date} representation
     */
    public static Date string2Calendar(String timestamp) throws ParseException {
        return SDF.convertStringToDate(timestamp);
    }

    /**
     * Transforms the given {@link String} representation with the format
     * yyyy-MM-dd to a {@link Calendar}
     *
     * @param timestamp Timestamp to transform
     * @return {@link Date} representation
     */
    public static Date urlString2Calendar(String timestamp) {
        try {
            return SDF_URL.convertStringToDate(timestamp);
        } catch (ParseException ex) {
            log.warn("urlString2Calendar: " + ex.toString());
            return new Date();
        }
    }

    /**
     * Transforms the given {@link String} representation with the format
     * yyyy-MM-dd'T'HH:mm:ss'.000+01:00' to a {@link Calendar}
     *
     * @param timestamp Timestamp to transform
     * @return {@link Date} representation
     */
    public static Date sosString2Calendar(String timestamp) {
        try {
            final Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(timestamp);
            return calendar.getTime();
        } catch (Exception ex) {
            log.warn("sosString2Calendar: " + ex.toString());
            return new Date();
        }
    }

    /**
     * Parses a xml document from a {@link String} or {@link InputStream} input.
     *
     * @param input {@link String} or {@link InputStream} with the xml data
     * @return {@link Document}
     */
    public static Document parseDoc(Object input) {
        try {
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = null;
            if (input instanceof String) {
                doc = dBuilder.parse((String) input);
            } else if (input instanceof InputStream) {
                doc = dBuilder.parse((InputStream) input);
            }
            return doc;
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            log.error("parseDoc" + ex.toString());
        }
        return null;
    }
}
