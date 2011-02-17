/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.io.shape;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.util.Logging;
import java.nio.DoubleBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class AdvancedShapefile extends Shapefile{

    private final static Logger logger = LoggerFactory.getLogger(AdvancedShapefile.class);
    
    public AdvancedShapefile(Object o) {
        super(o);
    }



    @Override
    protected String validateCoordinateSystem(AVList avlist) {
        
        return super.validateCoordinateSystem(avlist);
    }

    @Override
    protected String validateProjection(AVList avlist) {
        return super.validateProjection(avlist);
    }

//    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:bad design better way to convert to the internal representation
//    public static void normalizeGeographicCoordinates(final DoubleBuffer buffer)
//    {
//        if (buffer == null)
//        {
//            final String message = "buffer is null";
//            logger.error(message);
//            throw new IllegalArgumentException(message);
//        }
//
//        if ((buffer.remaining() % 2) != 0)
//        {
//            String message = "";
//            logger.error(message);
//            throw new IllegalArgumentException(message);
//        }
//
//        while (buffer.hasRemaining())
//        {
//            buffer.mark();
//            Angle lon = Angle.fromDegrees(buffer.get());
//            Angle lat = Angle.fromDegrees(buffer.get());
//
//            buffer.reset();
//            buffer.put(Angle.normalizedLongitude(lon).degrees);
//            buffer.put(Angle.normalizedLatitude(lat).degrees);
//        }
//    }
}
