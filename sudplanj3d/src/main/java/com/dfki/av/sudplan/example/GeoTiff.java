/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.example;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.data.BasicDataRasterReaderFactory;
import gov.nasa.worldwind.data.DataImportUtil;
import gov.nasa.worldwind.data.GeotiffRasterReader;
import gov.nasa.worldwind.formats.tiff.GeotiffReader;
import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class GeoTiff {

    private final static Logger logger = LoggerFactory.getLogger(GeoTiff.class);

    public void readGeoTiff() {
//        GeotiffReader reader = null;
//        GeotiffRasterReader reader = null;
//        GeotiffImageReader reader = null;
//        ImageIORasterReader reader = null;
        try {
//            new BufferedReader(new FileReader(new File(getClass().getResource("/test.tiff").toURI()))).readLine();
//            reader = (GeotiffImageReader)GeotiffImageReaderSpi.inst().createReaderInstance();
//            reader = new GeotiffReader(new File(getClass().getResource("/test.tif").toURI()));
//              reader = new GeotiffRasterReader();
//            reader = new ImageIORasterReader(false);
            int imageIndex = 0;
            final File file= new File(getClass().getResource("/soder.tif").toURI());
            AVList values = new AVListImpl();
//            reader.read(new File(getClass().getResource("/cea2.tif").toURI()),values);
//            DataImportUtil.
            BasicDataRasterReaderFactory factory = new BasicDataRasterReaderFactory();
            GeotiffRasterReader reader = (GeotiffRasterReader)factory.findReaderFor(file, values);

            if (logger.isDebugEnabled()) {
                logger.debug("reader: "+reader);
            }
            printParams(values);
          
            reader.readMetadata(file,values);
            printParams(values);
//            if (null != image) {
//                values.setValue(AVKey.IMAGE, image);
//                values.setValue(AVKey.WIDTH, image.getWidth());
//                values.setValue(AVKey.HEIGHT, image.getHeight());
//            }
//            reader.copyMetadataTo(imageIndex, values);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: make all Exceptions final !!.
        } catch (final Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error while processing tiff",ex);
            }
        } finally {
//            if (reader != null) {
////                reader.close();
//            }
        }
    }

     public void readGeoTiff2() {
        GeotiffReader reader = null;
//        GeotiffRasterReader reader = null;
//        GeotiffImageReader reader = null;
//        ImageIORasterReader reader = null;
        try {
//            new BufferedReader(new FileReader(new File(getClass().getResource("/test.tiff").toURI()))).readLine();
//            reader = (GeotiffImageReader)GeotiffImageReaderSpi.inst().createReaderInstance();
            reader = new GeotiffReader(new File(getClass().getResource("/dem.tif").toURI()));
//              reader = new GeotiffRasterReader();
//            reader = new ImageIORasterReader(false);
            int imageIndex = 0;
            final File file= new File(getClass().getResource("/dem.tif").toURI());
            AVList values = new AVListImpl();
//            reader.read(new File(getClass().getResource("/cea2.tif").toURI()),values);
//            DataImportUtil.
//            BasicDataRasterReaderFactory factory = new BasicDataRasterReaderFactory();
//            GeotiffRasterReader reader = (GeotiffRasterReader)factory.findReaderFor(file, values);
                reader.read();
            if (logger.isDebugEnabled()) {
                logger.debug("reader: "+reader);
            }
            printParams(values);
            reader.copyMetadataTo(values);
            printParams(values);
//            if (null != image) {
//                values.setValue(AVKey.IMAGE, image);
//                values.setValue(AVKey.WIDTH, image.getWidth());
//                values.setValue(AVKey.HEIGHT, image.getHeight());
//            }
//            reader.copyMetadataTo(imageIndex, values);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: make all Exceptions final !!.
        } catch (final Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error while processing tiff",ex);
            }
        } finally {
//            if (reader != null) {
////                reader.close();
//            }
        }
    }

      public void readGeoTiff3() {
//        GeotiffReader reader = null;
//        GeotiffRasterReader reader = null;
//        GeotiffImageReader reader = null;
//        ImageIORasterReader reader = null;
        try {
//            new BufferedReader(new FileReader(new File(getClass().getResource("/test.tiff").toURI()))).readLine();
//            reader = (GeotiffImageReader)GeotiffImageReaderSpi.inst().createReaderInstance();
//            reader = new GeotiffReader(new File(getClass().getResource("/test.tif").toURI()));
//              reader = new GeotiffRasterReader();
//            reader = new ImageIORasterReader(false);
            final File file= new File(getClass().getResource("/soder.tif").toURI());
            AVList values = new AVListImpl();
            if (logger.isDebugEnabled()) {
                logger.debug("Elevations. "+DataImportUtil.isElevationData(file));
            }                                  
        } catch (final Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error while processing tiff",ex);
            }
        } finally {
//            if (reader != null) {
////                reader.close();
//            }
        }
    }

    public void printParams(AVList params){
          if (logger.isDebugEnabled()) {
                logger.debug("print values: ");
                Iterator<Entry<String,Object>> it = params.getEntries().iterator();
//                for (Entry<String,Object> entry : ) {
////                        logger.debug("object: "+entry.);
//                }
                while(it.hasNext()){
                    final Entry<String,Object> entry = it.next();
                    logger.debug("String: "+entry.getKey()+" value: "+entry.getValue()+" class: "+entry.getValue().getClass());
                }
            }
    }
}
