/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io.dem;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.io.AbstractSceneLoader;
import com.dfki.av.sudplan.util.TimeMeasurement;
import com.dfki.av.sudplan.util.Triangle;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import java.util.Arrays;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ElevationLoader extends AbstractSceneLoader {

    private final static Logger logger = LoggerFactory.getLogger(ElevationLoader.class);
    private RawArcGrid arcGrid;
    private Point3f[] triangleCoordinates;
    private TexCoord2f[] texCoords;
    private ObjectFile test;

    @Override
    public void fillScene() throws Exception {
        arcGrid = new ArcGridParser(reader).parseArcGrid();
        createTriangle();
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: really ugly remove        
        GeometryInfo gridGeometry = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
//        final Point3f[] coordinates = Arrays.copyOf(triangleCoordinates, triangleCoordinates.length);
        gridGeometry.setCoordinates(triangleCoordinates);
        gridGeometry.setTextureCoordinateParams(1, 2);
        gridGeometry.setTextureCoordinates(0, texCoords);
        //TODO Sebastian Puhl: DIRTY MEMORY FIX
        arcGrid.setRawCoordinates(null);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this is pretty expensive look into source code I think this could be done more performant for grids.
        if (logger.isDebugEnabled()) {
            logger.debug("Stripifying geometry...");
            TimeMeasurement.getInstance().startMeasurement(this);
        }
        Stripifier stripifier = new Stripifier();
        stripifier.stripify(gridGeometry);
        if (logger.isDebugEnabled()) {
            logger.debug("Stripifying geometry done. Time elapsed: "
                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
        }

        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this is pretty expensive look into source code I think this could be done more performant for grids.
        if (logger.isDebugEnabled()) {
            logger.debug("Normalising geometry...");
            TimeMeasurement.getInstance().startMeasurement(this);
        }
        NormalGenerator normalGenerator = new NormalGenerator();
        normalGenerator.generateNormals(gridGeometry);
        if (logger.isDebugEnabled()) {
            logger.debug("Normalising geometry done. Time elapsed: "
                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
        }
        ElevationShape shape = new ElevationShape(gridGeometry.getGeometryArray());
        getHeightInterpolation(new Point2f(2010.77f, 6602.53f));
        createdScene.getSceneGroup().addChild(shape);
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: create Triangles directly (do this directly while parsing --> only one run)
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: could be good to move this in ArcGridClass/or as mentioned above in parser
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: maybe test performance
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: attention textures are generated here. Please refactor
    private void createTriangle() {
        if (logger.isDebugEnabled()) {
            logger.debug("Triangulating grid...");
            TimeMeasurement.getInstance().startMeasurement(this);
        }
        triangleCoordinates = new Point3f[arcGrid.getTriangleCount() * 3];
        if (logger.isDebugEnabled()) {
            logger.debug("Triangle Coord Count: " + triangleCoordinates.length);
        }
        texCoords = new TexCoord2f[arcGrid.getTriangleCount() * 3];
        int currentTriangle = 0;
        float divider1 = (arcGrid.getNumberOfColumns() - 1);
        float multiplier1 = 10;
        float divider2 = (arcGrid.getNumberOfRows() - 1);
        float multiplier2 = 3.8f;
        for (int currentRow = 0; currentRow < (arcGrid.getNumberOfRows() - 1); currentRow++) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("row++"+currentRow);
//            }
            for (int currentColumn = 0; currentColumn < (arcGrid.getNumberOfColumns() - 1); currentColumn++) {
//                if (logger.isDebugEnabled()) {
//                logger.debug("Columm++"+currentColumn);
//            }
//        logger.trace("row: "+currentRow+" column: "+currentColumn);
//        logger.debug("triangleIndex: " + (currentTriangle * 3) + " gridpointindex: (" + currentColumn + "," + (currentRow) + ")");
                triangleCoordinates[(currentTriangle * 3)] = arcGrid.getGridPoint(currentColumn, currentRow);
                texCoords[(currentTriangle * 3)] = new TexCoord2f(((currentColumn) / (divider1)) * multiplier1, ((currentRow) / (divider2)) * multiplier2);
//        logger.debug("triangleIndex: " + (currentTriangle * 3 + 1) + " gridpointindex: (" + (currentColumn + 1) + "," + (currentRow + 1) + ")");
                triangleCoordinates[(currentTriangle * 3) + 1] = arcGrid.getGridPoint(currentColumn + 1, currentRow + 1);
                texCoords[(currentTriangle * 3) + 1] = new TexCoord2f(((currentColumn + 1) / (divider1)) * multiplier1, ((currentRow + 1) / (divider2)) * multiplier2);
//        logger.debug("triangleIndex: " + (currentTriangle * 3 + 2) + " gridpointindex: (" + currentColumn +"," + (currentRow + 1) + ")");
                triangleCoordinates[(currentTriangle * 3) + 2] = arcGrid.getGridPoint(currentColumn, currentRow + 1);
                texCoords[(currentTriangle * 3) + 2] = new TexCoord2f(((currentColumn) / (divider1)) * multiplier1, ((currentRow + 1) / (divider2)) * multiplier2);
                currentTriangle++;
//        logger.debug("triangleIndex: " + (currentTriangle * 3) + " gridpointindex: (" + currentColumn + "," + (currentRow) + ")");
                triangleCoordinates[(currentTriangle * 3)] = arcGrid.getGridPoint(currentColumn, currentRow);
                texCoords[(currentTriangle * 3)] = new TexCoord2f(((currentColumn) / (divider1)) * multiplier1, ((currentRow) / (divider2)) * multiplier2);
//        logger.debug("triangleIndex: " + (currentTriangle * 3 + 1) + " gridpointindex: (" + (currentColumn + 1) + "," + (currentRow) + ")");
                triangleCoordinates[(currentTriangle * 3) + 1] = arcGrid.getGridPoint(currentColumn + 1, currentRow);
                texCoords[(currentTriangle * 3) + 1] = new TexCoord2f(((currentColumn + 1) / (divider1)) * multiplier1, ((currentRow) / (divider2)) * multiplier2);
//        logger.debug("triangleIndex: " + (currentTriangle * 3 + 2) + " gridpointindex: (" + (currentColumn + 1) + "," + (currentRow + 1) + ")");
                triangleCoordinates[(currentTriangle * 3) + 2] = arcGrid.getGridPoint(currentColumn + 1, currentRow + 1);
                texCoords[(currentTriangle * 3) + 2] = new TexCoord2f(((currentColumn + 1) / (divider1)) * multiplier1, ((currentRow + 1) / (divider2)) * multiplier2);
//                if (logger.isDebugEnabled() && (currentColumn % 100) ==0) {
//                    logger.debug("row: "+currentRow+"divider2: "+divider2);
//                    logger.debug("textcoord1: "+texCoords[(currentTriangle * 3)]);
//                    logger.debug("textcoord2: "+texCoords[(currentTriangle * 3)+1]);
//                    logger.debug("textcoord3: "+texCoords[(currentTriangle * 3)+2]);
//                }
                currentTriangle++;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Triangle count: " + currentTriangle);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Triangulating grid done. Time elapsed: "
                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:check if we do for performance reasons everything in floatingpoint or double precision. We do not realy need double !
    public Point3f getHeightInterpolation(final Point2f point) {
//        if (logger.isDebugEnabled()) {
//            logger.debug("Height interpolation for point: " + point);
//        }
        Point3f result = null;
        if (point == null) {
            return result;
        }
        final Triangle triangle = getTriangle(point);
        if (triangle == null) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("Triangle could not be determined.");
//            }
            return result;
        }
        if (comparePointsWithoutZ(triangle.pointA, point)) {
            return triangle.pointA;
        } else if (comparePointsWithoutZ(triangle.pointB, point)) {
            return triangle.pointB;
        } else if (comparePointsWithoutZ(triangle.pointC, point)) {
            return triangle.pointC;
        }
        return triangle.interpolateHeight(point);
    }
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:Check performance, other methods
    // because of the regularity of the grid the area of the triangles is constant.
    private double cellDiagonal;
    private double halfCellDiagonal;
    private double triangleArea;

//    private Point3f interpolateHeight(Point3f[] triangle, Point2f point) {
//        Point3f result = null;
//        if (logger.isDebugEnabled()) {
//            logger.debug("calculate barycenter");
//        }
//        if (triangle == null || point == null || triangle.length < 3) {
//            return result;
//        }
//        Triangle test = new Triangle(triangle, getCellDiagonal(), arcGrid.getCellsize(), getHalfCellDiagonal());
//        if (logger.isDebugEnabled()) {
//            final double[] centers = test.getBarycentricCoords(new Vector3f(new Point3f(point.x, point.y, 0.0f)));
//            logger.debug("barycenter: " + centers[0] + " " + centers[1] + " " + centers[2]);
//        }
////        final Point3f interpolatedPoint = new Point3f(point.x, point.y, interpolatedZ);
//        return result;
//    }
    // normal generation fails ?
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:they are already calculated learn how to use them
//        final Vector3f p = new Vector3f(triangle[0]);
//        final Vector3f q = new Vector3f(triangle[1]);
//        final Vector3f r = new Vector3f(triangle[2]);
//        final Vector3f rpDiff = new Vector3f();
//        final Vector3f qpDiff = new Vector3f();
//        qpDiff.sub(q, p);
//        rpDiff.sub(r, p);
//        final Vector3f normalVector = new Vector3f();
////        normalVector.cross(rpDiff, qpDiff);
//        normalVector.cross(qpDiff, rpDiff);
//
//          //test
////        Point3f coordinates[] = gi.getCoordinates();
//    Vector3f facetNorms;
//    Vector3f a = new Vector3f();
//    Vector3f b = new Vector3f();
//
//	a.sub(triangle[2], triangle[1]);
//	b.sub(triangle[0], triangle[1]);
//	facetNorms = new Vector3f();
//	facetNorms.cross(a, b);
//        facetNorms.normalize();
//
//        final Vector3f check1 = new Vector3f(facetNorms);
//        final Vector3f check2 = new Vector3f(facetNorms);
//        final Vector3f check3 = new Vector3f(facetNorms);
//
//
//
////	facetNorms[t / 3].normalize();
//
//
//        if (logger.isDebugEnabled()) {
//            logger.debug("Normal check: " +facetNorms);
//            logger.debug("Normal check: " + p + q + r);
//            logger.debug("Normal check: " + check1.dot(p) + check2.dot(q) +  check3.dot(r));
//            logger.debug("Normal check: " + p.dot(check1) + q.dot(check2) +  r.dot(check3));
//            if (logger.isDebugEnabled()) {
//                logger.debug("zpart: "+((normalVector.x * (point.x - p.x)) + (normalVector.y * (point.y - p.y)) / -(normalVector.z)));
//            }
//        }
//        final float interpolatedZ = ((normalVector.x * (point.x - p.x)) + (normalVector.y * (point.y - p.y)) / -(normalVector.z)) + (p.z);
//        if (logger.isDebugEnabled()) {
//            logger.debug("interpolated: " + interpolatedZ + " other:z's" + p.z + "/" + q.z + "/" + r.z);
//        }
    // scanline method idea
//        final Point3f height1 =triangle[0];
//        final Point3f height2 =triangle[1];
//        final Point3f height3 =triangle[2];
//        final float intialValue = (height1.z - (height1.z-height2.z))*((height1.y-point.y)/(height1.y-height2.y));
//        final float finalValue = (height1.z - (height1.z-height3.z))*((height1.y-point.y)/(height1.y-height3.y));
//        final float pointValue = finalValue -(finalValue-intialValue)*((height1.y-point.y)/(height1.y-height3.y));
//         result;
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:performance would be better without the method and a fast case differencial in the getHeigtInterpolation
    private boolean comparePointsWithoutZ(final Point3f point1, final Point2f point2) {
        if (point1 == null || point2 == null) {
            return false;
        }
        return (point1.x == point2.x && point1.y == point2.y);
    }

    private Triangle getTriangle(final Point2f point) {
        Point3f[] triangleCoords = getTriangleCoords(point);
        if(triangleCoords != null){
            return new Triangle(triangleCoords);
        } else {
            return null;
        }
    }

    private Point3f[] getTriangleCoords(final Point2f point) {
        Point3f[] triangle = null;
        if (point == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Point is null.");
            }
            return triangle;
        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("min x/y" + (arcGrid.getXMin() * ComponentBroker.getInstance().getScalingFactor()) + "/" + (arcGrid.getYMin() * ComponentBroker.getInstance().getScalingFactor()) + " max: " + (arcGrid.getXMax() * ComponentBroker.getInstance().getScalingFactor()) + "/" + (arcGrid.getYMax() * ComponentBroker.getInstance().getScalingFactor()));
//        }
        if (point.x < (arcGrid.getXMin() * ComponentBroker.getInstance().getScalingFactor()) || point.x > (arcGrid.getXMax() * ComponentBroker.getInstance().getScalingFactor()) || point.y < (arcGrid.getYMin() * ComponentBroker.getInstance().getScalingFactor()) || point.y > (arcGrid.getYMax() * ComponentBroker.getInstance().getScalingFactor())) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("Point not in range.");
//            }
            return triangle;
        }
        final int column = (int) Math.floor((point.x - (arcGrid.getXMin() *ComponentBroker.getInstance().getScalingFactor())) / (arcGrid.getCellsize()*ComponentBroker.getInstance().getScalingFactor()));
        final int row = (int) Math.floor((point.y - (arcGrid.getYMin()*ComponentBroker.getInstance().getScalingFactor())) / (arcGrid.getCellsize()*ComponentBroker.getInstance().getScalingFactor()));
//        int firstTriangle = ((row * (arcGrid.getNumberOfColumns() * 2) * 3)+ ((column) * 2 * 3));
//        int firstTriangle = ((row) * ((arcGrid.getNumberOfColumns() * 2) * 3)+ 0);
         int firstTriangle = ((row) * (((arcGrid.getNumberOfColumns()-1) * 2) * 3))+ column*2*3;
        if (logger.isDebugEnabled()) {
//            logger.debug("calculated row/column: " + row + "/" + column);
//            logger.debug("triangle index: " + firstTriangle);
//            logger.debug("index: "+triangleCoordinates[firstTriangle+0]);
//            logger.debug("index1: "+triangleCoordinates[firstTriangle+1]);
//            logger.debug("index2: "+triangleCoordinates[firstTriangle+2]);
//            logger.debug("index3: "+triangleCoordinates[firstTriangle+3]);
//            logger.debug("index4: "+triangleCoordinates[firstTriangle+4]);
//            logger.debug("index5: "+triangleCoordinates[firstTriangle+5]);
//            logger.debug("index6: "+triangleCoordinates[firstTriangle+6]);
//            logger.debug("index7: "+triangleCoordinates[firstTriangle+7]);
//            logger.debug("index8: "+triangleCoordinates[firstTriangle+8]);
//            logger.debug("index9: "+triangleCoordinates[firstTriangle+9]);
//            logger.debug("index10: "+triangleCoordinates[firstTriangle+10]);
        }
        final double xInCell = point.x - (arcGrid.getXMin() *ComponentBroker.getInstance().getScalingFactor()) - column * (arcGrid.getCellsize()*ComponentBroker.getInstance().getScalingFactor());
        final double yInCell = point.y - (arcGrid.getYMin() *ComponentBroker.getInstance().getScalingFactor()) -row * (arcGrid.getCellsize()*ComponentBroker.getInstance().getScalingFactor());
//        if (logger.isDebugEnabled()) {
//            logger.debug("X in Cell: " + xInCell + " Y in Cell: " + yInCell);
//        }
        if (xInCell <= yInCell) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("First Triangle");
//            }
        } else {
//            if (logger.isDebugEnabled()) {
//                logger.debug("Second Triangle");
//            }
            firstTriangle += 2;
        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("coordinateCount: " + triangleCoordinates.length);
//        }
        triangle = new Point3f[]{triangleCoordinates[firstTriangle], triangleCoordinates[firstTriangle + 1], triangleCoordinates[firstTriangle + 2]};
        return triangle;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: better to generate directly the triangle strip array instead of using the stripifier
//    public RawArcGrid getArcGrid() {
//        return arcGrid;
//    }
//
//    public void setArcGrid(RawArcGrid arcGrid) {
//        this.arcGrid = arcGrid;
//    }
}
