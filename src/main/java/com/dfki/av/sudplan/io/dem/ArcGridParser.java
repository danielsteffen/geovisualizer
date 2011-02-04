package com.dfki.av.sudplan.io.dem;

import com.dfki.av.sudplan.io.FileFormatException;
import com.dfki.av.sudplan.io.ParsingException;
import com.dfki.av.sudplan.io.SplitNotPossibleException;
import com.dfki.av.sudplan.util.Measurement;
import com.dfki.av.sudplan.util.TimeMeasurement;
import java.util.Arrays;
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
        try {
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
            if (!currentKeyValuePair[0].equalsIgnoreCase(LEFTMOST_X_COORDINATE)) {
                throw new FileFormatException("No x orgin value specified(" + LEFTMOST_X_COORDINATE + ")");
            }
            arcGrid.setLeftmostXValue(Float.parseFloat(currentKeyValuePair[1]));

            currentKeyValuePair = doubleSplit(sr.readLine());
            if (!currentKeyValuePair[0].equalsIgnoreCase(BOTTOMMOST_Y_COORDINATE)) {
                throw new FileFormatException("No y orgin value specified(" + BOTTOMMOST_Y_COORDINATE + ")");
            }
            arcGrid.setBottommostYValue(Float.parseFloat(currentKeyValuePair[1]));

            logger.debug("Grid origin= {}", arcGrid.getOrigin());
            currentKeyValuePair = doubleSplit(sr.readLine());
            if (!currentKeyValuePair[0].equalsIgnoreCase(CELLSIZE)) {
                throw new FileFormatException("No cell size specified(" + CELLSIZE + ")");
            }
            arcGrid.setCellsize(Float.parseFloat(currentKeyValuePair[1]));
            if (logger.isDebugEnabled()) {
                logger.debug("Cellsize= {}", arcGrid.getCellsize());
            }
            String testLine = sr.readLine();
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
                TimeMeasurement.getInstance().startMeasurement(this);
            }
            int coordinateCount = 0;
            int rowCount = 0;
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: dangerous if one "real" coordinate row is split in several text rows. Check ESRI specification
            while (sr.ready() & rowCount < arcGrid.getNumberOfRows()) {
                String currentLine;
                if (testLine == null) {
                    currentLine = sr.readLine();
                } else {
                    currentLine = testLine;
                    testLine = null;
                }
                final String[] currentRow = currentLine.split(WHITE_SPACE);
                for (int currentColumn = 0; currentColumn < arcGrid.getNumberOfColumns(); currentColumn++) {
                    z = Float.parseFloat(currentRow[currentColumn]);
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:evil hack only for looking at the moment
                    if (z == arcGrid.getNoDataValue()) {
                        z = 1;
                    }
                    pointList[coordinateCount] = new Point3f((x) * (new Float(arcGrid.getCellsize()) * arcGrid.getScaleFactor()), (y)
                            * (new Float(arcGrid.getCellsize()) * arcGrid.getScaleFactor()), z * arcGrid.getScaleFactor() * arcGrid.getzExaggeration());
                    x++;
                    if (x == arcGrid.getNumberOfColumns()) {
                        x = 0;
                        y--;
                    }
                    coordinateCount++;
                }
                rowCount++;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Coordinates successfully parsed.  Time elapsed: " + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
            }
            sr.close();
        } catch (Exception ex) {
            if (ex instanceof SplitNotPossibleException) {
                ex = new FileFormatException("Grid description at file begining is not correct", ex);
            }
            throw new ParsingException("Error while parsing grid file.", ex);
        }
        arcGrid.setRawCoordinates(pointList);
        if (logger.isDebugEnabled()) {
            logger.debug("Grid dimensions: coordinate count: " + arcGrid.getCoordinateCount() + ", number of rows: " + arcGrid.getNumberOfRows() + ", number of columns: " + arcGrid.getNumberOfColumns());
        }
        arcGrid.setSparseFactor(1.0f);
        if (logger.isDebugEnabled()) {
            logger.debug("Grid dimensions: coordinate count: " + arcGrid.getCoordinateCount() + ", number of rows: " + arcGrid.getNumberOfRows() + ", number of columns: " + arcGrid.getNumberOfColumns());
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
}
