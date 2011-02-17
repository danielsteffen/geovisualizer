/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io.dem;

import com.dfki.av.sudplan.control.ComponentBroker;
import java.util.ArrayList;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class RawArcGrid {

    private final Logger logger = LoggerFactory.getLogger(RawArcGrid.class);
    public final static String NUMBER_OF_COLUMNS = "ncols";
    public final static String NUMBER_OF_ROWS = "nrows";
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:rename xmin
    public final static String X_MINIMUM = "xllcorner";
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:rename ymin
    public final static String Y_MINIMUM = "yllcorner";
    public final static String CELLSIZE = "cellsize";
    public final static String NO_DATA_VALUE = "nodata_value";
    public final static String FILE_EXTENSION = ".asc";
    public final static String NAME = "ESRI ArcInfo " + FILE_EXTENSION + " file";
    private int coordinateCount;
    private int quadCount;
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: strange to have this here because there are no triangles
    private int triangleCount;
    private int numberOfColumns;
    private int numberOfRows;
    private Point2f origin;
    private float cellsize;
    private float noDataValue = Float.NaN;
    private Point3f[] rawCoordinates;
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: bad design is only used externaly. Attention could be terrible slow to
    private float scaleFactor = 1.0f;
    private float zExaggeration = 1.0f;
    /*ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: bad design if the sparse factor is changed before the initialisation
     * will not be readed correctly because the factor does influence the number of rows/columns
     */
    private float sparseFactor = 1.0f;

    public Point2f getOrigin() {
        return origin;
    }

    public void setOrigin(Point2f origin) {
        this.origin = origin;
    }

    public float getYMin() {
        return origin.getY();
    }

    public void setYMin(float yMin) {
        if (origin == null) {
            origin = new Point2f();
        }
        origin.setY(yMin);
    }

    public float getYMax() {
        return getYMin() + (getCellsize() * (getNumberOfRows() - 1));
    }

    public float getCellsize() {
        return cellsize;
    }

    public void setCellsize(final float cellsize) {
        this.cellsize = cellsize;
    }

    public float getXMin() {
        return origin.getX();
    }

    public void setXMin(final float xMin) {
        if (origin == null) {
            origin = new Point2f();
        }
        origin.setX(xMin);
    }

    public float getNoDataValue() {
        return noDataValue;
    }

    public void setNoDataValue(final float noDataValue) {
        this.noDataValue = noDataValue;
    }

    public int getNumberOfColumns() {
        return (int) Math.floor(numberOfColumns / sparseFactor);
    }

    public void setNumberOfColumns(final int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    public int getNumberOfRows() {
        return (int) Math.floor(numberOfRows / sparseFactor);
    }

    public void setNumberOfRows(final int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public Point3f[] getRawCoordinates() {
        return rawCoordinates;
    }

    public void setRawCoordinates(final Point3f[] rawCoordinates) {
        this.rawCoordinates = rawCoordinates;
    }

    public int getCoordinateCount() {
        return getNumberOfColumns() * getNumberOfRows();
    }

    public int getQuadCount() {
        return (numberOfColumns - 1) * (numberOfRows);
    }

    public int getTriangleCount() {
        return getQuadCount() * 2;
    }

    public Point3f getGridPoint(final int x, final int y) throws ArrayIndexOutOfBoundsException {
        if (x < 0 || x > getNumberOfColumns()) {
//            logger.debug("getGridPoint (" + x + "," + y + ")");
            throw new ArrayIndexOutOfBoundsException(x);
        }
        if (y < 0 || y > getNumberOfRows()) {
//            logger.debug("getGridPoint (" + x + "," + y + ")");
            throw new ArrayIndexOutOfBoundsException(y);
        }
        return rawCoordinates[(int) (x * sparseFactor + y * sparseFactor * (getNumberOfColumns()))];
    }

    public float getSparseFactor() {
        return sparseFactor;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:naiv implementation
    public Point3f getNearestNeighbour(final Point3f point) throws ArrayIndexOutOfBoundsException {        
        final int xIndex = (int) Math.floor((point.x/ComponentBroker.getInstance().getScalingFactor()-getXMin())/(getCellsize()));
        final int yIndex = (int) Math.floor((point.y/ComponentBroker.getInstance().getScalingFactor()-getYMin())/(getCellsize()));
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:better to raise the exception than to duplicate the check method is responsible for this
        ArrayList<Point3f> points = new ArrayList<Point3f>();
         if(xIndex  < getNumberOfColumns() && xIndex >= 0 && yIndex < getNumberOfRows() && yIndex >= 0){
            points.add(getGridPoint(xIndex, yIndex));
        }
        if(xIndex < getNumberOfColumns() && xIndex >= 0 && yIndex+1 < getNumberOfRows() && yIndex >= 0){
            points.add(getGridPoint(xIndex, yIndex+1));
        }
        if(xIndex +1 < getNumberOfColumns() && xIndex >= 0 && yIndex < getNumberOfRows() && yIndex >= 0){
            points.add(getGridPoint(xIndex+1, yIndex));
        }
        if(xIndex +1 < getNumberOfColumns() && xIndex >= 0 && yIndex+1 < getNumberOfRows() && yIndex >= 0){
            points.add(getGridPoint(xIndex+1, yIndex+1));
        }
        double zMax = -99999.0;
        Point3f maxNeigbour=null;
        for(Point3f currentPoint:points){
            if(currentPoint.z > zMax){
                zMax=currentPoint.z;
                maxNeigbour=currentPoint;
            }
        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("point: "+point);
//            logger.debug("x: "+xIndex+" y:"+yIndex);
//        }
//does work but makes no sense
        //        final int startIndex = xIndex+(yIndex*getNumberOfColumns());
//        final int stopIndex = xIndex+3+((yIndex+3)*getNumberOfColumns());
//        Point3f nearestNeighbour = null;
//        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:dangerous
//        double minimulDistance = 9999.0;
//        for (int i = startIndex; i < stopIndex; i++) {
//            final double tmpDistance = getDistance(point, rawCoordinates[i]);
////            if (logger.isDebugEnabled()) {
////                logger.debug("tmpDistance: "+tmpDistance);
////            }
//            if (nearestNeighbour != null) {
//                if (tmpDistance < minimulDistance) {
//                    minimulDistance = tmpDistance;
//                    nearestNeighbour = rawCoordinates[i];
//                }
//            } else {
//                minimulDistance = tmpDistance;
//                nearestNeighbour = rawCoordinates[i];
//            }
//        }
//        try{
//        return getGridPoint(xIndex, yIndex);
//        }catch(ArrayIndexOutOfBoundsException ex){
////            if (logger.isErrorEnabled()) {
////                logger.error("error");
////            }
//            return null;
//        }
        return maxNeigbour;
    }

    private double getDistance(final Point3f point1, final Point3f point2) {
//        if (logger.isDebugEnabled()) {
//            logger.debug("point1: "+point1+" point2: "+point2);
//            logger.debug("quad1: "+Math.pow((point2.x - point1.x),2));
//            logger.debug("quad2: "+Math.pow((point1.y - point2.y),2));
//        }
        return Math.sqrt(Math.pow((point1.x - point2.x),2) + Math.pow((point1.y - point2.y),2));
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: does not work properly ---> disortion of width/height
    public void setSparseFactor(final float sparseFactor) {
        this.sparseFactor = sparseFactor;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(final float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public float getzExaggeration() {
        return zExaggeration;
    }

    public void setzExaggeration(final float zExaggeration) {
        this.zExaggeration = zExaggeration;
    }
}
