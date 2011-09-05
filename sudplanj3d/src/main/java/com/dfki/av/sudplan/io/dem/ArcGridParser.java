package com.dfki.av.sudplan.io.dem;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.io.FileFormatException;
import com.dfki.av.sudplan.io.ParsingException;
import com.dfki.av.sudplan.io.SplitNotPossibleException;
import com.dfki.av.sudplan.util.EarthFlat;
import com.dfki.av.sudplan.util.TimeMeasurement;
import java.io.BufferedReader;

import javax.vecmath.Point3f;

import java.io.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dfki.av.sudplan.io.dem.RawArcGrid.*;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ArcGridParser {

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: documentation/unit tests
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: possible to parse without ncols/nrows (no array)
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: if there are more parsers make infrastructure for scaling,transformation etc.
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: performance improvement.
    ////ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: for grid reprojection it should be sufficient to transform the orgin.
    private final static Logger logger = LoggerFactory.getLogger(ArcGridParser.class);
    private RawArcGrid arcGrid = new RawArcGrid();
    private Point3f[] pointList;
    private final static String WHITE_SPACE = "\\s";
    private final static String WHITE_SPACES = WHITE_SPACE + "+";
    private final Reader reader;

    /**
     * Contructor requested an stream with coordinate informations
     *
     * @param reader -
     *            coordinate stream
     */
    public ArcGridParser(final Reader reader) {
        this.reader = reader;
    }

    public RawArcGrid parseArcGrid() throws ParsingException {
        if (logger.isDebugEnabled()) {
            logger.debug("Parsing ArcInfo Grid.");
        }
        int rowCount = 0;
        int currentColumn = 0;
        String currentValue = null;
        String currentLineDebug = null;
        String[] currentLineValues = null;
//        int sparseFactor = 2;
        try {
            arcGrid.setScaleFactor((float) ComponentBroker.getInstance().getScalingFactor());
            BufferedReader sr = new BufferedReader(reader);
            // READ PARAMETERFILES OF Arcinfo Grid
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:problem this must be variable --> no_data need not be there
            String[] currentKeyValuePair = doubleSplit(sr.readLine());
            if (!currentKeyValuePair[0].equalsIgnoreCase(NUMBER_OF_COLUMNS)) {
                throw new FileFormatException("No number of columns specified(" + NUMBER_OF_COLUMNS + ")");
            }
            arcGrid.setNumberOfColumns(Integer.parseInt(currentKeyValuePair[1]));
            if (logger.isDebugEnabled()) {
                logger.info("Number of columns= " + arcGrid.getNumberOfColumns());
            }
            currentKeyValuePair = doubleSplit(sr.readLine());
            if (!currentKeyValuePair[0].equalsIgnoreCase(NUMBER_OF_ROWS)) {
                throw new FileFormatException("No number of rows specified(" + NUMBER_OF_ROWS + ")");
            }
            arcGrid.setNumberOfRows(Integer.parseInt(currentKeyValuePair[1]));
            if (logger.isDebugEnabled()) {
                logger.debug("Number of rows= " + arcGrid.getNumberOfRows());
            }
            currentKeyValuePair = doubleSplit(sr.readLine());
            if (!currentKeyValuePair[0].equalsIgnoreCase(X_MINIMUM)) {
                throw new FileFormatException("No x orgin value specified(" + X_MINIMUM + ")");
            }

            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:check what happens if double is cast to float
            arcGrid.setXMin((float) EarthFlat.geodeticToCartesian(Float.parseFloat(currentKeyValuePair[1]), EarthFlat.PLATE_CARREE_PROJECTION));

            currentKeyValuePair = doubleSplit(sr.readLine());
            if (!currentKeyValuePair[0].equalsIgnoreCase(Y_MINIMUM)) {
                throw new FileFormatException("No y orgin value specified(" + Y_MINIMUM + ")");
            }
            arcGrid.setYMin((float) EarthFlat.geodeticToCartesian(Float.parseFloat(currentKeyValuePair[1]), EarthFlat.PLATE_CARREE_PROJECTION));

            logger.debug("Grid origin= {}", arcGrid.getOrigin());
            currentKeyValuePair = doubleSplit(sr.readLine());
            if (!currentKeyValuePair[0].equalsIgnoreCase(CELLSIZE)) {
                throw new FileFormatException("No cell size specified(" + CELLSIZE + ")");
            }
            arcGrid.setCellsize((float) EarthFlat.geodeticToCartesian(Float.parseFloat(currentKeyValuePair[1]), EarthFlat.PLATE_CARREE_PROJECTION));
            if (logger.isDebugEnabled()) {
                logger.debug("Cellsize= {}", arcGrid.getCellsize());
            }
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: problem with whitespaces use regex
            String testLine = sr.readLine().trim();
            try {
                currentKeyValuePair = doubleSplit(testLine);
                if (currentKeyValuePair[0].equalsIgnoreCase(NO_DATA_VALUE)) {
                    arcGrid.setNoDataValue(Float.parseFloat(currentKeyValuePair[1]));
                    if (logger.isInfoEnabled()) {
                        logger.info("No data value is= " + arcGrid.getNoDataValue());
                    }
                    testLine = null;
                }
            } catch (final SplitNotPossibleException ex) {
                if (logger.isDebugEnabled()) {
                    logger.info("No data value is not avaialable");
                }
            }
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:transform orgin;
            //            CoordinateTransformer.transformPointToInternalCoordinateSystem(2400,sourcePoint);
//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: make this configurable in order to only read a part of the grid.
//      arcGrid.setNumberOfRows(100);
//      arcGrid.setNumberOfColumns(100);
            float z = 0;
            int x = 0;
            int y = arcGrid.getNumberOfRows();
            pointList = new Point3f[arcGrid.getCoordinateCount()];
            if (logger.isDebugEnabled()) {
                logger.debug("Parsing raw coordinates...");
                logger.debug("coordninateCount: " + arcGrid.getCoordinateCount());
                logger.debug("Xmin: " + arcGrid.getXMin());
                logger.debug("Ymax: " + arcGrid.getYMax());
                logger.debug("Ymin: " + arcGrid.getYMin());
                TimeMeasurement.getInstance().startMeasurement(this);
            }
            int coordinateCount = 0;

            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: dangerous if one "real" coordinate row is split in several text rows. Check ESRI specification
            while (sr.ready() & rowCount < arcGrid.getNumberOfRows()) {
                String currentLine;
                if (testLine == null) {
                    currentLine = sr.readLine();
                } else {
                    currentLine = testLine;
                    testLine = null;
                }
                currentLineDebug = currentLine;
                final String[] currentRow = currentLine.split(WHITE_SPACE);
                currentLineValues = currentRow;
                for (currentColumn = 0; currentColumn < arcGrid.getNumberOfColumns(); currentColumn++) {
                    currentValue = currentRow[currentColumn];
                    z = Float.parseFloat(currentValue);
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:evil hack only for looking at the moment
                    if (z == arcGrid.getNoDataValue()) {
                        z = -1000.0f;
//                        z = 0.0f;
                    }
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:remove after Vienna             
                    final float sourceX = (arcGrid.getXMin() + (x * (new Float(arcGrid.getCellsize()))));
                    final float sourceY = (arcGrid.getYMin() + ((y - 1) * (new Float(arcGrid.getCellsize()))));
                    final float sourceZ = z;
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: the transformation is pretty expensive would be more effiecient to parse all at once check!
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: maybe there is a much simpler transformation from 4124 to 4326 --> e.g. by simply taking the values as lat/long the difference is only in the decimal check!
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: determine epsg from file
//                    final Point3f transformedPoint = CoordinateTransformer.transformPointToInternalCoordinateSystem(4326, new Point3f(sourceX, sourceY, sourceZ));
                    final Point3f sourcePoint = new Point3f(sourceX, sourceY, sourceZ);
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("source: " + sourcePoint);
//                    }
//                    final Point3f transformedPoint = CoordinateTransformer.transformPointToInternalCoordinateSystem(4326,sourcePoint);
                    final Point3f transformedPoint = sourcePoint;
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("point3f: " + transformedPoint);
//                    }
//                    transformedPoint.x *= arcGrid.getScaleFactor();
//                    transformedPoint.y *= arcGrid.getScaleFactor();
//                    transformedPoint.z *= arcGrid.getScaleFactor() * arcGrid.getzExaggeration();                    

                    transformedPoint.z *= arcGrid.getzExaggeration();
                    scalePoint3f(transformedPoint);
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:remove after vienna presentation
                    transformedPoint.z -= 0.01f;

//                    if (logger.isDebugEnabled()) {
//                        logger.debug("scaled: " + transformedPoint);
//                    }
                    pointList[(((y - 1) * arcGrid.getNumberOfColumns()) + x)] = transformedPoint;
//                    if (logger.isDebugEnabled() && y==1) {
//                        logger.debug("index: "+(((y-1)*arcGrid.getNumberOfColumns())+x)+" x: "+x+" y: "+y +" calc: "+sourceX);
//                    }
//                    if (logger.isDebugEnabled()) {
////                        logger.debug("x: "+x+" cell: "+arcGrid.getCellsize()+" scale: "+arcGrid.getScaleFactor());
////                        logger.debug("calculated x: "+(((x) * (new Float(arcGrid.getCellsize()))) )* arcGrid.getScaleFactor());
////                        logger.debug("point3f x:"+pointList[coordinateCount].getX()+" y:"+pointList[coordinateCount].getY());
//                        logger.debug("point3f: "+pointList[coordinateCount]);
//                    }
                    x++;
                    if (x == arcGrid.getNumberOfColumns()) {
                        x = 0;
                        y--;
                    }
                    coordinateCount++;
                }
                rowCount++;
            }
            arcGrid.setRawCoordinates(pointList);
            if (logger.isDebugEnabled()) {
                logger.debug("Coordinates successfully parsed.  Time elapsed: "
                        + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:Grid starts upperleft corner should start lower
                logger.debug("Firstpoint: " + arcGrid.getGridPoint(0, 0));
                logger.debug("lastPoint: " + arcGrid.getGridPoint(arcGrid.getNumberOfColumns()-1, arcGrid.getNumberOfRows()-1));
                logger.debug("Firstpoint: " + arcGrid.getRawCoordinates()[0]);
            }
            sr.close();
        } catch (Exception ex) {
            if (ex instanceof SplitNotPossibleException) {
                ex = new FileFormatException("Grid description at file begining is not correct", ex);
            }
            if (logger.isErrorEnabled()) {
                logger.error("currentLineDebug" + currentLineDebug + "previous value: " + currentLineValues[currentColumn - 1]);
            }
            throw new ParsingException("Error while parsing grid file. Line: " + (rowCount + 1) + " Column: " + (currentColumn + 1) + " value: " + currentValue, ex);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Grid dimensions: coordinate count: " + arcGrid.getCoordinateCount()
                    + ", number of rows: " + arcGrid.getNumberOfRows()
                    + ", number of columns: " + arcGrid.getNumberOfColumns());
        }
        arcGrid.setSparseFactor(2.0f);
        logger.debug("lastPoint: " + arcGrid.getGridPoint(arcGrid.getNumberOfColumns()-1, arcGrid.getNumberOfRows()-1));
        if (logger.isDebugEnabled()) {
            logger.debug("Grid dimensions: coordinate count: " + arcGrid.getCoordinateCount()
                    + ", number of rows: " + arcGrid.getNumberOfRows()
                    + ", number of columns: " + arcGrid.getNumberOfColumns());
        }
        return arcGrid;
    }

    /**
     * This method returns the complete Geometric/Color information for
     * generating the landscape
     *
     * @return
     */
//  public GeometryInfo getGeometryInfo() {
//    return geoInfo;
//  }
    private String[] doubleSplit(final String lineToSplit) throws SplitNotPossibleException {
        if (lineToSplit == null) {
            throw new SplitNotPossibleException("Line is empty.");
        }
        lineToSplit.trim();
        final String[] splitting = lineToSplit.split(WHITE_SPACES);
        if (splitting.length < 2) {
            throw new SplitNotPossibleException("White space split yields less results than 2. No key value pair.");
        }
        return splitting;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:central place
    private void scalePoint3f(final Point3f point) {
        point.x *= ComponentBroker.getInstance().getScalingFactor();
        point.y *= ComponentBroker.getInstance().getScalingFactor();
        point.z *= ComponentBroker.getInstance().getScalingFactor();
    }
}
