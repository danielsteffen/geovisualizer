/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io.dem;

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
    public final static String LEFTMOST_X_COORDINATE = "xllcorner";
    public final static String BOTTOMMOST_Y_COORDINATE = "yllcorner";
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
    private float scaleFactor = 0.01f;
    private float zExaggeration = 1f;
    private float sparseFactor = 1.0f;

    public Point2f getOrigin() {
        return origin;
    }

    public void setOrigin(Point2f origin) {
        this.origin = origin;
    }

    public float getBottommostYValue() {
        return origin.getY();
    }

    public void setBottommostYValue(float bottommostYValue) {
        if (origin == null) {
            origin = new Point2f();
        }
        origin.setY(bottommostYValue);
    }

    public float getCellsize() {
        return cellsize;
    }

    public void setCellsize(final float cellsize) {
        this.cellsize = cellsize;
    }

    public float getLeftmostXValue() {
        return origin.getY();
    }

    public void setLeftmostXValue(final float leftmostXValue) {
        if (origin == null) {
            origin = new Point2f();
        }
        origin.setX(leftmostXValue);
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
            logger.debug("getGridPoint (" + x + "," + y + ")");
            throw new ArrayIndexOutOfBoundsException(x);
        }
        if (y < 0 || y > getNumberOfRows()) {
            logger.debug("getGridPoint (" + x + "," + y + ")");
            throw new ArrayIndexOutOfBoundsException(y);
        }
        return rawCoordinates[(int)(x * sparseFactor + y * sparseFactor * (getNumberOfColumns()))];
    }

    public float getSparseFactor() {
        return sparseFactor;
    }

    public void setSparseFactor(final float sparseFactor) {
        this.sparseFactor = sparseFactor;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public float getzExaggeration() {
        return zExaggeration;
    }

    public void setzExaggeration(float zExaggeration) {
        this.zExaggeration = zExaggeration;
    }
}
