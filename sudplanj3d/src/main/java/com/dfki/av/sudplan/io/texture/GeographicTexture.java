/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io.texture;

import com.dfki.av.sudplan.util.AdvancedBoundingBox;
import com.dfki.av.sudplan.util.EarthFlat;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.formats.tiff.GeotiffReader;
import gov.nasa.worldwind.geom.Sector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture2D;
import javax.vecmath.Point3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class GeographicTexture extends Texture2D {

    private final static Logger logger = LoggerFactory.getLogger(GeographicTexture.class);
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:implement the rest

    public static enum GEOGRAPHICTEXTURE_PROPERTIES {

        BOUNDING_BOX, EPSG, HEIGHT, WIDTH
    };
    private int imageHeight;
    private int imageWidth;
    private AdvancedBoundingBox boundingBox;
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:implement
    private int epsg;

    public GeographicTexture(final int mipMapMode,final int format,final int width,final int height,final AdvancedBoundingBox boundingBox) {
        super(mipMapMode, format, width, height);
        if (logger.isDebugEnabled()) {
            logger.debug("width: "+width+" height: "+height);
        }
        this.boundingBox=boundingBox;
    }

    public static GeographicTexture createGeographicTexture(final File file) throws Exception {
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: right scaling if it does not fit
        final AVList metadata = new AVListImpl();
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this should only be used to load images design
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:if a geotiff is loaded with elevation information not a texture should be generated
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:idea use geotiffs to generate height,slope textures --> properties of imagelayer --> imagefromrasterlayer
//        BasicDataRasterReaderFactory factory = new BasicDataRasterReaderFactory();
//        GeotiffRasterReader reader = (GeotiffRasterReader)factory.findReaderFor(file, metadata);
        GeotiffReader reader = new GeotiffReader(file);
        if (logger.isDebugEnabled()) {
            logger.debug("Used raster reader: "+reader);
        }
//        reader.readMetadata(file, metadata);
        final BufferedImage image = reader.read();
        if (logger.isDebugEnabled()) {
            logger.debug("Buffered image width:"+image.getWidth()+" height: "+image.getHeight());
        }
        reader.copyMetadataTo(metadata);
//        reader.read
        printParams(metadata);
        final int imageHeight = (Integer) metadata.getValue(AVKey.HEIGHT);
        final int imageWidth = (Integer) metadata.getValue(AVKey.WIDTH);
        final Sector wwBoundingBox = (Sector) metadata.getValue(AVKey.SECTOR);
        if (wwBoundingBox == null) {
            throw new Exception("Boundingbox is null, creation of texture not possible");
        }
        final double[] deegreeArray = wwBoundingBox.asDegreesArray();
        final Point3d lower = new Point3d(deegreeArray[2], deegreeArray[0], 0.0);
        final Point3d upper = new Point3d(deegreeArray[3], deegreeArray[1], 0.0);
        final int epsg = (Integer) metadata.getValue(AVKey.PROJECTION_EPSG_CODE);
        final AdvancedBoundingBox boundingBox = new AdvancedBoundingBox(
                EarthFlat.scalePoint3d(EarthFlat.geodeticToCartesian(lower, EarthFlat.PLATE_CARREE_PROJECTION)),
                EarthFlat.scalePoint3d(EarthFlat.geodeticToCartesian(upper, EarthFlat.PLATE_CARREE_PROJECTION))
                );
        final GeographicTexture newTexture = new GeographicTexture(BASE_LEVEL,RGBA, imageWidth,imageHeight,boundingBox);
        ImageComponent2D imageComponent = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, image);
        newTexture.setImage(0, imageComponent);
        return newTexture;
    }

    private static void printParams(AVList params) {
        if (logger.isDebugEnabled()) {
            Iterator<Entry<String, Object>> it = params.getEntries().iterator();
            while (it.hasNext()) {
                final Entry<String, Object> entry = it.next();
                logger.debug("String: " + entry.getKey() + " value: " + entry.getValue());
            }
        }
    }

    public AdvancedBoundingBox getBoundingBox() {
        return boundingBox;
    }

}
