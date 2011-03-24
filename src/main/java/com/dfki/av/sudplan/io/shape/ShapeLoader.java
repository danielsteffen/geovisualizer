/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io.shape;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.io.AbstractSceneLoader;
import com.dfki.av.sudplan.layer.ElevationLayer;
import com.dfki.av.sudplan.util.AdvancedBoundingBox;
import com.dfki.av.sudplan.util.EarthFlat;
import com.dfki.av.sudplan.util.TimeMeasurement;
import com.dfki.av.sudplan.util.Triangle;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecordPolygon;
import gov.nasa.worldwind.util.VecBuffer;
import gov.nasa.worldwind.util.WWUtil;
import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.media.j3d.Appearance;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ShapeLoader extends AbstractSceneLoader {

    private static Logger logger = LoggerFactory.getLogger(ShapeLoader.class);
    ArrayList<Point3f> points = new ArrayList<Point3f>();
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:Optimise waste of memory and time
    private ArrayList<Double> pointColors = new ArrayList<Double>();
    private ArrayList<Double> pointADT = new ArrayList<Double>();
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:remove after Vienna
    ArrayList<Color4f> polygonColors = new ArrayList<Color4f>();
    ArrayList<Color4f> wireColors = new ArrayList<Color4f>();
    ArrayList<Integer> polygonColorsIndex = new ArrayList<Integer>();
    ArrayList<Integer> pointIndices = new ArrayList<Integer>();
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:color class
    final Color4f transparent = new Color4f(1.0f, 1.0f, 1.0f, 0.0f);
    final Color4f white = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);
    final Color4f gray = new Color4f(0.8f, 0.8f, 0.8f, 1.0f);
    final Color4f black = new Color4f(0.0f, 0.0f, 0.0f, 1.0f);
    final Color4f red = new Color4f(1.0f, 0.0f, 0.0f, 1.0f);
    final Color4f orange = new Color4f(1.0f, 0.6f, 0.1f, 0.9f);
    final Color4f yellow = new Color4f(1.0f, 1.0f, 0.0f, 1.0f);
    final Color4f green = new Color4f(0.0f, 1.0f, 0.0f, 1.0f);

//    final Color4f lightGreen = new Color4f(new Color(144, 238, 144, 255));
    final Color4f darkGreen = new Color4f(new Color(34, 139, 34, 255));
    final Color4f lightBlue = new Color4f(new Color(00, 191, 255, 255));
    final Color4f blue = new Color4f(new Color(0, 0, 255, 255));
    private final Color4f lowConcentrationColor = green;
    private final Color4f mediumConcentrationColor = yellow;
    private final Color4f highConcentrationColor = red;
    private final float lowConcentrationThreshold = 36.0f;
    private final float mediumConcentrationThreshold = 48.0f;
    private final ArrayList<ShapefileObject> shapeArray = new ArrayList<ShapefileObject>();
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: Nasa Shapefile is not able to scoop with colon decimals. Fix!
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: remove after Vienna Demo
    final ElevationLayer heights = ComponentBroker.getInstance().getHeights();
    final int buildingAlpha = 230;
    final Color4f building1 = new Color4f(new Color(220, 220, 220, buildingAlpha));
    final Color4f building2 = new Color4f(new Color(30, 144, 255, buildingAlpha));
    final Color4f building3 = new Color4f(new Color(176, 196, 222, buildingAlpha));
    final Color4f building4 = new Color4f(new Color(123, 104, 238, buildingAlpha));
    final Color4f building5 = new Color4f(new Color(175, 238, 238, buildingAlpha));
    final Color4f building6 = new Color4f(new Color(238, 232, 170, buildingAlpha));
    final Color4f building7 = new Color4f(new Color(219, 112, 147, buildingAlpha));
    final Color4f building8 = new Color4f(new Color(174, 238, 238, buildingAlpha));
    final Color4f building9 = new Color4f(new Color(238, 213, 183, buildingAlpha));
    final Color4f building10 = new Color4f(new Color(205, 193, 197, buildingAlpha));
    final Color4f building11 = new Color4f(new Color(238, 213, 210, buildingAlpha));
    final Color4f building12 = new Color4f(new Color(255, 106, 106, buildingAlpha));
    final Color4f building13 = new Color4f(new Color(185, 211, 238, buildingAlpha));
    final Color4f building14 = new Color4f(new Color(238, 220, 130, buildingAlpha));
    final Color4f building15 = new Color4f(new Color(238, 238, 0, buildingAlpha));
    final Color4f[] buildingColors = new Color4f[]{building1, building2, building3, building4, building5, building6, building7, building8, building9,
        building10, building11, building12, building13, building14, building15};

    private void correctNormals(GeometryInfo gridGeometry, boolean all) {
        final Vector3f up = new Vector3f(0, 0, 1);
        if (gridGeometry == null) {
            return;
        }
        int correctedVectorCount = 0;
        final Vector3f[] normals = gridGeometry.getNormals();
        if (normals != null) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("Normal count: " + normals.length);
//                logger.debug("Normal indices: " + gridGeometry.getNormalIndices());
//            }
            for (int i = 0; i < normals.length; i++) {
//                Vector3f currentNormal = normals[i];
//                double diff = EarthFlat.radiansToDeegree(up.angle(currentNormal));
//                if (diff > 91) {
//                    if (logger.isDebugEnabled() && !all) {
//                        logger.debug("diff: " + diff);
//                    }
//                    if (logger.isDebugEnabled() && !all) {
//                        logger.debug("current: "+currentNormal);
//                    }
//                    currentNormal.negate();
////                     if (logger.isDebugEnabled()) {
////                        logger.debug("current: "+currentNormal);
////                    }
                normals[i] = up;
//                    correctedVectorCount++;
//                } else {
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("diff: "+diff);
//                    }
//                }
            }
            gridGeometry.setNormals(normals);
        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("Corrected vectors: " + correctedVectorCount);
//        }
    }

    private boolean checkLastPart(int partCounter, int startingIndex) {
        for (int i = 0; i < partCounter - 3; i++) {
            final double area = Triangle.getSignedTriangleArea(points.get(startingIndex + i), points.get(startingIndex + i + 1), points.get(startingIndex + i + 2));
//            if (logger.isDebugEnabled()) {
//                logger.debug("part: "+partCounter+" i: "+i+" area: "+area);
//            }
            if (area < 0.0) {
                return false;
            }
        }
        return true;
    }

    public static enum SHAPE_TYPE {

        POLYGON_3D, POLYLINE, POLYGON_2D
    };
    private Shapefile shp = null;
    SHAPE_TYPE shapeType = null;

    @Override
    public void fillScene() throws Exception {
        try {
            this.shp = new Shapefile(file);
            createPoints();
            if (shapeType == SHAPE_TYPE.POLYGON_3D) {
                createBuildings();
            } else if (shapeType == SHAPE_TYPE.POLYLINE) {
//                createStreetLevelResults();
//                createStreetLevel3DResults();
                createStreetLevelResults2();
            } else if (shapeType == shapeType.POLYGON_2D) {
                createRooftopResults();
            }
        } finally {
            if (shp != null) {
                shp.close();
            }
            points = null;
            pointColors = null;
            pointADT = null;
            polygonColors = null;
            wireColors = null;
            polygonColorsIndex = null;
            pointIndices = null;
        }
    }

    private void createBuildings() {
        final TransformGroup wireGroup = new TransformGroup();
        final Point3f[] coordinatesPolygon = points.toArray(new Point3f[]{});
        final Point3f[] coordinatesWire = points.toArray(new Point3f[]{});
        final int[] stripCount = copyIntegerArray(pointIndices);
        final Color4f[] colorsPoly = polygonColors.toArray(new Color4f[]{});
        final Color4f[] colorsWire = wireColors.toArray(new Color4f[]{});
//        final Color4f[] colors = new Color4f[]{gray};
//        final int[] colorsIndex = copyIntegerArray(polygonColorsIndex);
        GeometryInfo gridGeometry = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gridGeometry.setCoordinates(coordinatesPolygon);
//        gridGeometry.setColorIndices(colorsIndex);
        gridGeometry.setColors(colorsPoly);
        gridGeometry.setStripCounts(stripCount);
        GeometryInfo wireGeometry = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        wireGeometry.setCoordinates(coordinatesWire);
        wireGeometry.setColors(colorsWire);
        wireGeometry.setStripCounts(stripCount);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this is pretty expensive look into source code I think this could be done more performant for grids.
        if (logger.isDebugEnabled()) {
            logger.debug("Stripifying geometry...");
            TimeMeasurement.getInstance().startMeasurement(this);
        }
        Stripifier stripifier = new Stripifier();
        stripifier.stripify(gridGeometry);

        stripifier.stripify(wireGeometry);
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
//        correctNormals(gridGeometry,true);
        correctNormals(gridGeometry, true);
        if (logger.isDebugEnabled()) {
            logger.debug("Normalising geometry done. Time elapsed: "
                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this has to be configurable with a default value
        Appearance landscapeAppearance = new Appearance();
        PolygonAttributes pa = new PolygonAttributes();

//        TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.15f);
//        landscapeAppearance.setTransparencyAttributes(ta);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:wrong triangulation ??
        pa.setCullFace(PolygonAttributes.CULL_NONE);

//        pa.setBackFaceNormalFlip(true);
        landscapeAppearance.setPolygonAttributes(pa);


//        material.setColorTarget(Material.AMBIENT_AND_DIFFUSE);

//        Color3f aColor = new Color3f(0.1f, 0.1f, 0.1f);
//        Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
//        Color3f dColor = new Color3f(0.4f, 0.4f, 0.4f);
//        Color3f sColor = new Color3f(0.5f, 0.5f, 0.5f);
        Color3f aColor = new Color3f(0.2f, 0.2f, 0.2f);
        Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f dColor = new Color3f(0.6f, 0.6f, 0.6f);
        Color3f sColor = new Color3f(0.8f, 0.8f, 0.8f);
        Material m = new Material(aColor, eColor, dColor, sColor, 10.0f);
        landscapeAppearance.setMaterial(m);
        //full
        ShapefileObject landscape = new ShapefileObject(gridGeometry.getGeometryArray());
        landscape.setCapability(ShapefileObject.ALLOW_GEOMETRY_WRITE);
        landscape.setCapability(ShapefileObject.ALLOW_GEOMETRY_READ);
        landscape.setCapability(ShapefileObject.ALLOW_APPEARANCE_OVERRIDE_WRITE);
        landscape.setCapability(ShapefileObject.ALLOW_APPEARANCE_OVERRIDE_READ);
        landscape.setCapability(ShapefileObject.ALLOW_APPEARANCE_WRITE);
        landscape.setCapability(ShapefileObject.ALLOW_APPEARANCE_READ);
        landscape.setAppearance(landscapeAppearance);

        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:make configurable
        //Wire
        ShapefileObject wire = new ShapefileObject(wireGeometry.getGeometryArray());
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this must be simpler in java3d fix !s
        AdvancedBoundingBox wireBounds = new AdvancedBoundingBox(wire.getBounds());
        Appearance materialAppear = new Appearance();
        Material mat = new Material();
//        mat.setDiffuseColor(new Color3f(0.1f, 0.1f, 0.1f));
//        mat.setAmbientColor(new Color3f(0.3f, 0.3f, 0.3f));
        materialAppear.setMaterial(mat);
        TransparencyAttributes tAttrib = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.1f);
        materialAppear.setTransparencyAttributes(tAttrib);
        PolygonAttributes polyAttrib = new PolygonAttributes();
        polyAttrib.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
        materialAppear.setPolygonAttributes(polyAttrib);
        LineAttributes lineAttribtues = new LineAttributes();
        lineAttribtues.setLineAntialiasingEnable(true);
        lineAttribtues.setLineWidth(0.2f);
        materialAppear.setLineAttributes(lineAttribtues);
//        ColoringAttributes blackColoring = new ColoringAttributes();
//        blackColoring.setColor(0.0f, 0.0f, 0.0f);
//        materialAppear.setColoringAttributes();
        wire.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        wire.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        wire.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
        wire.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
        wire.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        wire.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        wire.setAppearance(materialAppear);
        Transform3D scaling = new Transform3D();
        final Transform3D translateToOrigin = new Transform3D();
        final Transform3D translateBack = new Transform3D();
        final Vector3d originVector = new Vector3d(wireBounds.getCenter());
        if (logger.isDebugEnabled()) {
            logger.debug("original bounds: " + wireBounds);
        }
//        final AdvancedBoundingBox newBounds = new AdvancedBoundingBox(wireBounds);
        final Vector3d backVector = new Vector3d(originVector);
//        backVector.scale(1.01f);
        if (logger.isDebugEnabled()) {
            logger.debug("backVector: " + backVector);
        }
        translateBack.setTranslation(backVector);
        originVector.negate();
        if (logger.isDebugEnabled()) {
            logger.debug("originVector: " + originVector);
        }
        translateToOrigin.setTranslation(originVector);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this is not correct because this must be done per object.
//        scaling.setScale(new Vector3d(1.001, 1.001, 1.001));
//        scaling.setScale(new Vector3d(0.9999, 0.9990, 0.9990));
        wireGroup.setTransform(scaling);
        Transform3D wireTransformation = new Transform3D();
//        wireTransformation.mul(translateBack);
//        wireTransformation.mul(scaling);
//        wireTransformation.mul(translateToOrigin);
        wireGroup.setTransform(wireTransformation);
        if (logger.isDebugEnabled()) {
            logger.debug("transformed bounds: " + wireBounds);
        }
        createdScene.getSceneGroup().addChild(landscape);
        createdScene.getSceneGroup().addChild(wireGroup);
        wireGroup.addChild(wire);
    }

    private void createRooftopResults() {
        final Point3f[] coordinatesPolygon = points.toArray(new Point3f[]{});
        final int[] stripCount = copyIntegerArray(pointIndices);
        final Color4f[] colorsPoly = polygonColors.toArray(new Color4f[]{});
        GeometryInfo gridGeometry = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gridGeometry.setCoordinates(coordinatesPolygon);
        gridGeometry.setColors(colorsPoly);
        gridGeometry.setStripCounts(stripCount);
        if (logger.isDebugEnabled()) {
            logger.debug("points: " + coordinatesPolygon.length + " colors: " + colorsPoly.length + " strips: " + stripCount.length);
        }
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
//        NormalGenerator normalGenerator = new NormalGenerator();
//        normalGenerator.generateNormals(gridGeometry);
        if (logger.isDebugEnabled()) {
            logger.debug("Normalising geometry done. Time elapsed: "
                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
        }

        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this has to be configurable with a default value
        Appearance landscapeAppearance = new Appearance();
        PolygonAttributes pa = new PolygonAttributes();

        TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.3f);
        landscapeAppearance.setTransparencyAttributes(ta);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:wrong triangulation ??
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        landscapeAppearance.setPolygonAttributes(pa);

//        Material material = new Material();
//        material.setDiffuseColor(new Color3f(0.2f, 0.2f, 0.2f));
//        material.setAmbientColor(new Color3f(0.98f, 0.98f, 0.98f));
//        material.setAmbientColor(new Color3f(0.8f, 0.8f, 0.8f));
//        material.setColorTarget(Material.AMBIENT_AND_DIFFUSE);

//        landscapeAppearance.setMaterial(material);

        ShapefileObject landscape = new ShapefileObject(gridGeometry.getGeometryArray());
        landscape.setCapability(ShapefileObject.ALLOW_GEOMETRY_WRITE);
        landscape.setCapability(ShapefileObject.ALLOW_GEOMETRY_READ);
        landscape.setCapability(ShapefileObject.ALLOW_APPEARANCE_OVERRIDE_WRITE);
        landscape.setCapability(ShapefileObject.ALLOW_APPEARANCE_OVERRIDE_READ);
        landscape.setCapability(ShapefileObject.ALLOW_APPEARANCE_WRITE);
        landscape.setCapability(ShapefileObject.ALLOW_APPEARANCE_READ);
        landscape.setAppearance(landscapeAppearance);
        createdScene.getSceneGroup().addChild(landscape);
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: checks for valid geometries e.g. line with one point
    private void createStreetLevelResults() {

//         LineArray axisYLines = new LineArray(2,
//        LineArray.COORDINATES | LineArray.COLOR_3 );
//	    sceneBranch.addChild(new Shape3D(axisYLines));
//
//             final Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
//            final Color3f blue  = new Color3f(0.0f, 0.0f, 1.0f);
//	    axisYLines.setCoordinate(0, new Point3f( 2000.0f,6600.0f, 10.0f));
//	    axisYLines.setCoordinate(1, new Point3f( 2010.0f,6610.0f, 10.0f));
//
//             axisYLines.setColor(0, green);
//	    axisYLines.setColor(1, blue);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:convert these to arrays after creation this will be the same for every Method (polygons etc.)
        final int[] lineIndices = copyIntegerArray(pointIndices);
        final Point3f[] linePoints = points.toArray(new Point3f[]{});
        final Double[] pointColorArray = pointColors.toArray(new Double[]{});
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:should be made easier with a PolyLineObject
        if (logger.isDebugEnabled()) {
            logger.debug("Creating " + lineIndices.length + " lines.");
            logger.debug("Number of points: " + linePoints.length);
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:index lines, this is bad performance;        
        int currentIndex = 0;
        for (int i = 0; i < lineIndices.length; i++) {
//          for (int i = 0; i < 2; i++) {
            int currentLineSize = (lineIndices[i] - 1) * 2;
//            if (logger.isDebugEnabled() && currentIndex == 5934) {
//                logger.debug("Linesize: " + currentLineSize + " points: " + lineIndices[i] + "currentIndex: " + currentIndex);
//            }
            final LineArray currentLine = new LineArray(currentLineSize, LineArray.COORDINATES | LineArray.COLOR_4);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:api problem if you set this problem on a shape 3D.
            currentLine.setCapability(LineArray.ALLOW_COLOR_READ);
            currentLine.setCapability(LineArray.ALLOW_COLOR_WRITE);
            final ShapefileObject lineShape = new ShapefileObject(currentLine);
            Appearance lineAppearance = new Appearance();
            LineAttributes lineAttributes = new LineAttributes();
            lineAttributes.setLineWidth(2.0f);
            lineAppearance.setLineAttributes(lineAttributes);

            lineShape.setAppearance(lineAppearance);
            shapeArray.add(lineShape);
            createdScene.getSceneGroup().addChild(lineShape);
            for (int j = 0; j < currentLineSize - 1; j += 2) {
//                if (logger.isDebugEnabled() && currentIndex == 5934) {
//                    logger.debug("currentIndex: " + (j));
//                } 
//                if (logger.isDebugEnabled()) {
//                    logger.debug("Coordinate: "+j+" ="+linePoints[currentIndex]);
//                    logger.debug("Coordinate: "+(j+1)+" ="+linePoints[currentIndex+1]);
//                }
                currentLine.setCoordinate((j), linePoints[currentIndex]);
                setColor(currentLine, currentIndex, j);
                currentIndex++;
                currentLine.setCoordinate((j + 1), linePoints[currentIndex]);
                setColor(currentLine, currentIndex, (j + 1));
            }
            currentIndex++;
//            if (logger.isDebugEnabled()) {
//                final Point3f[] coordinates = new Point3f[currentLineSize];
            //                currentLine.getCoordinates(0, coordinates);
//                final Point3f first = new Point3f();
//                currentLine.getCoordinate(0,first);
//                logger.debug("Line points: "+Arrays.deepToString(coordinates));
//                logger.debug("Line points: "+first);
//            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("TotalIndex: " + currentIndex);
        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("Line creation done.");
//            Enumeration<Geometry> enums = shape.getAllGeometries();
//            int geomCounter = 0;
//            while(enums.hasMoreElements()){
//                enums.nextElement();
//                geomCounter++;
//            }
//            logger.debug("Number of geometries: "+geomCounter);
//        }
        if (logger.isDebugEnabled()) {
            logger.debug("Number of Shapes: " + shapeArray.size());
        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: checks for valid geometries e.g. line with one point
    private void createStreetLevel3DResults() {

//         LineArray axisYLines = new LineArray(2,
//        LineArray.COORDINATES | LineArray.COLOR_3 );
//	    sceneBranch.addChild(new Shape3D(axisYLines));
//
//             final Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
//            final Color3f blue  = new Color3f(0.0f, 0.0f, 1.0f);
//	    axisYLines.setCoordinate(0, new Point3f( 2000.0f,6600.0f, 10.0f));
//	    axisYLines.setCoordinate(1, new Point3f( 2010.0f,6610.0f, 10.0f));
//
//             axisYLines.setColor(0, green);
//	    axisYLines.setColor(1, blue);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:convert these to arrays after creation this will be the same for every Method (polygons etc.)
        final int[] lineIndices = copyIntegerArray(pointIndices);
        final Point3f[] linePoints = points.toArray(new Point3f[]{});
        logger.debug("line points: " + linePoints.length);
//        final Point3f[] polygonPoints = points.toArray(new Point3f[]{});
        final Double[] pointColorArray = pointColors.toArray(new Double[]{});
//        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:should be made easier with a PolyLineObject
//        if (logger.isDebugEnabled()) {
//            logger.debug("Creating " + lineIndices.length + " lines.");
//            logger.debug("Number of points: " + linePoints.length);
//        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:index lines, this is bad performance;
        int currentIndex = 0;
        for (int i = 0; i < lineIndices.length; i++) {
//          for (int i = 0; i < 2; i++) {
            int currentLineSize;
            if (i != lineIndices.length - 1) {
                currentLineSize = lineIndices[i] + 1;
            } else {
                currentLineSize = lineIndices[i];
            }
//            if (currentLineSize == 2) {
//                currentIndex++;
//                currentIndex++;
//                continue;
//            }
//            if (logger.isDebugEnabled() && currentIndex == 5934) {
//                logger.debug("Linesize: " + currentLineSize + " points: " + lineIndices[i] + "currentIndex: " + currentIndex);
//            }
            final GeometryInfo currentLine = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
//            currentLineSize, LineArray.COORDINATES | LineArray.COLOR_4
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:api problem if you set this problem on a shape 3D.
//            currentLine.setCapability(LineArray.ALLOW_COLOR_READ);
//            currentLine.setCapability(LineArray.ALLOW_COLOR_WRITE);

            final Point3f[] line = new Point3f[((currentLineSize - 1) * 2)];
            final Color4f[] lineColor = new Color4f[((currentLineSize - 1) * 2)];
            for (int j = 0; j < currentLineSize - 1; j++) {
//                if (logger.isDebugEnabled() && currentIndex == 5934) {
//                    logger.debug("currentIndex: " + (j));
//                }
//                if (logger.isDebugEnabled()) {
//                    logger.debug("Coordinate: "+j+" ="+linePoints[currentIndex]);
//                    logger.debug("Coordinate: "+(j+1)+" ="+linePoints[currentIndex+1]);
//                }
                final double currentColor = pointColors.get(currentIndex);
                if (currentColor < lowConcentrationThreshold) {
                    lineColor[j] = lowConcentrationColor;
                } else if (currentColor < mediumConcentrationThreshold) {
                    lineColor[j] = mediumConcentrationColor;
                } else {
                    lineColor[j] = highConcentrationColor;
                }
                line[j] = linePoints[currentIndex];
                currentIndex++;
            }
            final int resetIndex = currentIndex;
            currentIndex--;
            for (int j = 0; j < currentLineSize - 1; j++) {
//                if (logger.isDebugEnabled() && currentIndex == 5934) {
//                    logger.debug("currentIndex: " + (j));
//                }
//                if (logger.isDebugEnabled()) {
//                    logger.debug("Coordinate: "+j+" ="+linePoints[currentIndex]);
//                    logger.debug("Coordinate: "+(j+1)+" ="+linePoints[currentIndex+1]);
//                }

                lineColor[(currentLineSize - 1) + j] = lineColor[j];

                final Point3f zmodifiedPoint = new Point3f(linePoints[currentIndex]);
                //linear
                double height = Math.log10((pointADT.get(currentIndex) / maxADT) * 1000);
                height /= 50;
                // exp
//                final double height = Math.pow((((pointADT.get(currentIndex) / maxADT))*100),2);
//                if (logger.isDebugEnabled()) {
//                    logger.debug("ADT: " + pointADT.get(currentIndex));
//                    logger.debug("height: " + height);
//                }
                zmodifiedPoint.z += height;
                line[(currentLineSize - 1) + j] = zmodifiedPoint;
                currentIndex--;
            }
            currentIndex = resetIndex;
            if (i != lineIndices.length - 1) {
                currentIndex--;
            }
//            line[line.length - 1] = line[0];
//            if (logger.isDebugEnabled()) {
//                logger.debug("Coordinates: " + Arrays.deepToString(line));
//            }
//            lineColor[lineColor.length - 1] = lineColor[0];
//            swapArray(line);
            currentLine.setCoordinates(line);
//            currentLine.setColors(lineColor);
            currentLine.setStripCounts(new int[]{line.length});

            Stripifier stripifier = new Stripifier();
            stripifier.stripify(currentLine);
            NormalGenerator nGenerator = new NormalGenerator();
            nGenerator.generateNormals(currentLine);

            final ShapefileObject lineShape = new ShapefileObject(currentLine.getGeometryArray());
            Appearance lineAppearance = new Appearance();
            PolygonAttributes pa = new PolygonAttributes();
            Color3f aColor = new Color3f(0.3f, 0.3f, 0.3f);
            Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
            Color3f dColor = new Color3f(0.5f, 0.5f, 0.5f);
            Color3f sColor = new Color3f(1.0f, 1.0f, 1.0f);
            Material m = new Material(aColor, eColor, dColor, sColor, 50.0f);
            lineAppearance.setMaterial(m);
            //3d Effect disappears
//            TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.15f);
//            lineAppearance.setTransparencyAttributes(ta);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:wrong triangulation ??
            pa.setCullFace(PolygonAttributes.CULL_NONE);
            pa.setBackFaceNormalFlip(true);
            lineAppearance.setPolygonAttributes(pa);
            lineShape.setAppearance(lineAppearance);
//            Appearance lineAppearance = new Appearance();
//            LineAttributes lineAttributes = new LineAttributes();
//            lineAttributes.setLineWidth(2.0f);
//            lineAppearance.setLineAttributes(lineAttributes);
//            lineShape.setAppearance(lineAppearance);
            shapeArray.add(lineShape);
            createdScene.getSceneGroup().addChild(lineShape);
            currentIndex++;
//            if (logger.isDebugEnabled()) {
//                final Point3f[] coordinates = new Point3f[currentLineSize];
            //                currentLine.getCoordinates(0, coordinates);
//                final Point3f first = new Point3f();
//                currentLine.getCoordinate(0,first);
//                logger.debug("Line points: "+Arrays.deepToString(coordinates));
//                logger.debug("Line points: "+first);
//            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("TotalIndex: " + currentIndex);
        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("Line creation done.");
//            Enumeration<Geometry> enums = shape.getAllGeometries();
//            int geomCounter = 0;
//            while(enums.hasMoreElements()){
//                enums.nextElement();
//                geomCounter++;
//            }
//            logger.debug("Number of geometries: "+geomCounter);
//        }
        if (logger.isDebugEnabled()) {
            logger.debug("Number of Shapes: " + shapeArray.size());
        }
    }

    private void createStreetLevelResults2() {
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:convert these to arrays after creation this will be the same for every Method (polygons etc.)
        final int[] lineIndices = copyIntegerArray(pointIndices);
//        final Point3f[] linePoints = points.toArray(new Point3f[]{});
//        final Double[] pointColorArray = pointColors.toArray(new Double[]{});
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:should be made easier with a PolyLineObject
        if (logger.isDebugEnabled()) {
            logger.debug("Creating " + lineIndices.length + " lines.");
//            logger.debug("Number of points: " + linePoints.length);
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:index lines, this is bad performance;
        int currentIndex = 0;
        for (int i = 0; i < lineIndices.length; i++) {
            int currentLineSize = (lineIndices[i] - 1) * 2;
            for (int j = 0; j < currentLineSize - 1; j += 2) {
                createdScene.getSceneGroup().addChild(createPolygonShape(j, currentIndex++));
            }
            currentIndex++;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("TotalIndex: " + currentIndex);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Number of Shapes: " + shapeArray.size());
        }
    }

    private Shape3D createPolygonShape(int loopIndex, int pointIndex) {
//        if (logger.isDebugEnabled()) {
//            logger.debug("Create Polygon");
//        }
        final GeometryInfo geomInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);

        Color4f[] colors = new Color4f[4];
        Point3f first = points.get(pointIndex);
        Point3f fourth = new Point3f(first);
        double heightFourth = Math.log10((pointADT.get(pointIndex) / (maxADT / 100))) / 15;
        if (heightFourth < 0.01) {
            heightFourth = 0.01;
        }
        fourth.z += heightFourth;

        final double colorFirst = pointColors.get(pointIndex);
        if (colorFirst < lowConcentrationThreshold) {
            colors[0] = lowConcentrationColor;
            colors[3] = lowConcentrationColor;
        } else if (colorFirst < mediumConcentrationThreshold) {
            colors[0] = mediumConcentrationColor;
            colors[3] = mediumConcentrationColor;
        } else {
            colors[0] = highConcentrationColor;
            colors[3] = highConcentrationColor;
        }
        pointIndex++;
        Point3f second = points.get(pointIndex);
        Point3f third = new Point3f(second);
        double heightThird = Math.log10((pointADT.get(pointIndex) / (maxADT / 100))) / 15;
        if (heightThird < 0.01) {
            heightThird = 0.01;
        }
        third.z += heightThird;
        final double colorSecend = pointColors.get(pointIndex);
        if (colorSecend < lowConcentrationThreshold) {
            colors[1] = lowConcentrationColor;
            colors[2] = lowConcentrationColor;
        } else if (colorSecend < mediumConcentrationThreshold) {
            colors[1] = mediumConcentrationColor;
            colors[2] = mediumConcentrationColor;
        } else {
            colors[1] = highConcentrationColor;
            colors[2] = highConcentrationColor;
        }
        Point3f[] coordinates = new Point3f[]{first, second, third, fourth};
        int[] stripCount = new int[]{4};
//        if (logger.isDebugEnabled()) {
//            logger.debug("Polygon coords: "+Arrays.deepToString(coordinates));
//        }
        geomInfo.setCoordinates(coordinates);
        geomInfo.setStripCounts(stripCount);
        geomInfo.setColors(colors);


        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this is pretty expensive look into source code I think this could be done more performant for grids.
//        if (logger.isDebugEnabled()) {
//            logger.debug("Stripifying geometry...");
//            TimeMeasurement.getInstance().startMeasurement(this);
//        }
//        Stripifier stripifier = new Stripifier();
//        stripifier.stripify(geomInfo);

//        if (logger.isDebugEnabled()) {
//            logger.debug("Stripifying geometry done. Time elapsed: "
//                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
//        }

        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this is pretty expensive look into source code I think this could be done more performant for grids.
//        if (logger.isDebugEnabled()) {
//            logger.debug("Normalising geometry...");
//            TimeMeasurement.getInstance().startMeasurement(this);
//        }
        NormalGenerator normalGenerator = new NormalGenerator();
        normalGenerator.generateNormals(geomInfo);
//        correctNormals(geomInfo, true);
//        if (logger.isDebugEnabled()) {
//            logger.debug("Normalising geometry done. Time elapsed: "
//                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
//        }
        final ShapefileObject newPolygon = new ShapefileObject(geomInfo.getGeometryArray());
        Appearance polygonApperance = new Appearance();
        PolygonAttributes pa = new PolygonAttributes();
        Color3f aColor = new Color3f(0.2f, 0.2f, 0.2f);
        Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f dColor = new Color3f(0.9f, 0.9f, 0.9f);
        Color3f sColor = new Color3f(1.0f, 1.0f, 1.0f);
        Material m = new Material(aColor, eColor, dColor, sColor, 10.0f);
        polygonApperance.setMaterial(m);
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        pa.setBackFaceNormalFlip(true);
        polygonApperance.setPolygonAttributes(pa);
        newPolygon.setAppearance(polygonApperance);
        return newPolygon;
    }

    private void setColor(final LineArray line, final int colorIndex, final int lineIndex) {
        final double currentColor = pointColors.get(colorIndex);
        if (currentColor < lowConcentrationThreshold) {
            line.setColor(lineIndex, lowConcentrationColor);
        } else if (currentColor < mediumConcentrationThreshold) {
            line.setColor(lineIndex, mediumConcentrationColor);
        } else {
            line.setColor(lineIndex, highConcentrationColor);
        }
    }

//    private Shape3D createNewLineShape() {
//        final Shape3D shape = new Shape3D();
////ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this has to be configurable with a default value
//
//        Appearance materialAppear = new Appearance();
////        Material mat = new Material();
////        mat.setShininess(80.0f);
////        mat.setDiffuseColor(new Color3f(0.5f, 0.5f, 0.5f));
////        mat.setAmbientColor(new Color3f(0.2f, 0.2f, 0.2f));
////        materialAppear.setMaterial(mat);
//        LineAttributes lineAttribtues = new LineAttributes();
//        lineAttribtues.setLineAntialiasingEnable(true);
//        lineAttribtues.setLineWidth(1.2f);
//        materialAppear.setLineAttributes(lineAttribtues);
//        shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
//        shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
//        shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
//        shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
//        shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
//        shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
//        shape.setAppearance(materialAppear);
//        return shape;
//    }
    private void createPoints() {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating Geometry...");
            TimeMeasurement.getInstance().startMeasurement(this);
        }
        if (Shapefile.isPointType(shp.getShapeType())) {
            throw new UnsupportedOperationException("Not supported yet.");
        } else if (Shapefile.isMultiPointType(shp.getShapeType())) {
            throw new UnsupportedOperationException("Not supported yet.");
        } else if (Shapefile.isPolylineType(shp.getShapeType())) {
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:extract common parts (see polygon)
            shapeType = SHAPE_TYPE.POLYLINE;
            if (logger.isDebugEnabled()) {
                logger.debug("Shapefile is of type: " + shp.getShapeType());
                logger.debug("Shapefile number of records: " + shp.getNumberOfRecords());
                logger.debug("Shapefile length: " + shp.getLength());
            }
            createPointsFromShape();
        } else if (Shapefile.isPolygonType(shp.getShapeType())) {
            shapeType = SHAPE_TYPE.POLYGON_3D;
            if (logger.isDebugEnabled()) {
                logger.debug("Shapefile is of type: " + shp.getShapeType());
                logger.debug("Shapefile number of records: " + shp.getNumberOfRecords());
                logger.debug("Shapefile length: " + shp.getLength());
            }
            createPointsFromShape();
        } else {
            if (logger.isErrorEnabled()) {
                final String message = "Shapefile type is not supported: " + shp.getShapeType() + ".";
                logger.error(message);
                throw new IllegalArgumentException(message);
            }
        }
    }
    private double minADT = 9999.0;
    private double maxADT = -9999.0;
    private double averageADT = 0.0;
//    private HashSet<String> layers = new HashSet<String>();
    private final HashMap<String, Color4f> buildingColorMap = new HashMap<String, Color4f>();
    private int currentBuildingColorIndex = 0;

    public void createPointsFromShape() {
        double minPerc = 9999.0;
        double maxPerc = -9999.0;
        double minNo2dygn = 9999.0;
        double maxNo2dygn = -9999.0;
        double allADT = 0.0;
        int clockwiseCount = 0;
        while (shp.hasNext()) {
            final ShapefileRecord record = shp.nextRecord();
            Double elevation = this.extractDoubleAttribute("elevation", record);
            Double perc = this.extractDoubleAttribute("perc98d", record);
            Double no2dygn = this.extractDoubleAttribute("NO2dygn", record);
            Double adt = this.extractDoubleAttribute("ADT", record);
            String layer = this.extractStringAttribute("Layer", record);


            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this is all hardcoded must work in general.
            double[] currentHeights = null;
            if (record instanceof ShapefileRecordPolygon) {
                currentHeights = ((ShapefileRecordPolygon) record).getZValues();
            }
            if (logger.isDebugEnabled()) {
//                    logger.debug("Number of points: " + record.getNumberOfPoints());
//                    logger.debug("Number of parts: " + record.getNumberOfParts());
//                    logger.debug("Elevation: "+elevation);
                if (record instanceof ShapefileRecordPolygon) {
                    if (logger.isDebugEnabled()) {
//                            logger.debug("zRange length: " + ((ShapefileRecordPolygon) record).getZRange().length);
//                            logger.debug("zValues length: " + ((ShapefileRecordPolygon) record).getZValues().length);
                    }
                }
            }
            int coordCounter = 0;
            for (int i = 0; i < record.getNumberOfParts(); i++) {
                // Although the shapefile spec says that inner and outer boundaries can be listed in any order, it's
                // assumed here that inner boundaries are at least listed adjacent to their outer boundary, either
                // before or after it. The below code accumulates inner boundaries into the extruded polygon until an
                // outer boundary comes along. If the outer boundary comes before the inner boundaries, the inner
                // boundaries are added to the polygon until another outer boundary comes along, at which point a new
                // extruded polygon is started.
                VecBuffer buffer = record.getCompoundPointBuffer().subBuffer(i);
                Iterator<double[]> coords = buffer.getCoords().iterator();
                int partCounter = 0;
                Point3f[] currentPolygon = new Point3f[4];
                while (coords.hasNext()) {
                    final double[] currentCoords = coords.next();

                    final Point3f originalPoint = new Point3f((float) currentCoords[0], (float) currentCoords[1], 0.0f);

                    if (currentHeights != null) {
                        originalPoint.setZ((float) currentHeights[coordCounter]);
                    } else if (shapeType != shapeType.POLYLINE) {
                        shapeType = SHAPE_TYPE.POLYGON_2D;
                    }

                    if (shapeType == SHAPE_TYPE.POLYGON_3D) {
                        layer = layer.split(" ")[0];
                    }

//                            originalPoint.setZ((float) (originalPoint.getZ() + elevation));
//                        }
                    final Point3f transformedPoint = EarthFlat.geodeticToCartesian(originalPoint, EarthFlat.PLATE_CARREE_PROJECTION);
                    scalePoint3f(transformedPoint);

                    if (shapeType == SHAPE_TYPE.POLYGON_2D) {
                        if (partCounter == 4) {
                            AdvancedBoundingBox bbox = new AdvancedBoundingBox(new Point3d(currentPolygon[0]), new Point3d(currentPolygon[2]));
//                            if (logger.isDebugEnabled()) {
//                                logger.debug("boundingBox: "+bbox);
//                                logger.debug("center: "+);
//
//                            }
                            final Point3f center = bbox.getCenter3f();
                            center.z = 0.1f;
                            points.add(center);
                        } else {
                            currentPolygon[partCounter] = transformedPoint;
                        }
                    } else {
                        points.add(transformedPoint);
                    }
                    if (shapeType == SHAPE_TYPE.POLYLINE && currentHeights == null && heights != null) {
                        final Point3f nb = heights.getHeightInterpolation(transformedPoint);
//                        if (logger.isDebugEnabled()) {
//                            logger.debug("point: "+transformedPoint+" nb: " + nb);
//                        }
//                        if (logger.isDebugEnabled()) {
//                            logger.debug("nb: "+nb);
//                        }
                        if (nb != null) {
                            if (nb.z < -0.1f) {
                                transformedPoint.setZ((float) (-0.035f));
                            } else {
                                transformedPoint.setZ((float) (nb.z + 0.006f));
                            }
                        } else {
                            transformedPoint.setZ((float) (-0.035f));
                        }
                    } else if (shapeType == SHAPE_TYPE.POLYLINE && currentHeights == null) {
//                        if (logger.isDebugEnabled()) {
//                            logger.debug("no heights");
//                        }
                        transformedPoint.setZ((float) (-0.039f));
                    }
                    if (shapeType == SHAPE_TYPE.POLYLINE) {
                        if (perc != null) {
                            if (perc < minPerc) {
                                minPerc = perc;
                            }
                            if (perc > maxPerc) {
                                maxPerc = perc;
                            }
//                        if (logger.isDebugEnabled()) {
//                            logger.debug("perc: "+perc);
//                        }
                            pointColors.add(perc);
                        } else {
//                        if (logger.isDebugEnabled()) {
//                            logger.debug("No perc value !!");
//
//                        }
                            pointColors.add(-9999.0);
                        }
                        if (adt != null) {
                            allADT += adt;
                            if (adt < minADT) {
                                minADT = adt;
                            }
                            if (adt > maxADT) {
                                maxADT = adt;
                            }
                            pointADT.add(adt);
                        } else {
                            pointADT.add(-9999.0);
                        }
                    } else if (shapeType == SHAPE_TYPE.POLYGON_3D) {
                        if (layer != null) {
                            Color4f currentColor = null;
                            if ((currentColor = buildingColorMap.get(layer)) == null) {
                                buildingColorMap.put(layer, buildingColors[currentBuildingColorIndex]);
                                currentColor = buildingColors[currentBuildingColorIndex];
                                currentBuildingColorIndex++;
                            }
                            polygonColors.add(currentColor);
                        } else {
                            polygonColors.add(new Color4f(gray));
                        }
//                        if (transformedPoint.getZ() < 0.000001f) {
//                            polygonColors.add(new Color4f(transparent));
//                            wireColors.add(new Color4f(transparent));
//                        } else {

                        wireColors.add(new Color4f(0.0f, 0.0f, 0.0f, 0.2f));
//                        }
//                        polygonColorsIndex.add(0);
                    } else if (shapeType == SHAPE_TYPE.POLYGON_2D) {
                        if (partCounter == 4) {
                            if (no2dygn != null) {
                                if (no2dygn < minNo2dygn) {
                                    minNo2dygn = no2dygn;
                                }
                                if (no2dygn > maxNo2dygn) {
                                    maxNo2dygn = no2dygn;
                                }
//                        if (logger.isDebugEnabled()) {
//                            logger.debug("perc: "+perc);
//                        }
                                if (no2dygn < 18.0) {
                                    polygonColors.add(blue);
                                } else if (no2dygn < 24.0) {
                                    polygonColors.add(lightBlue);
                                } else if (no2dygn < 30) {
                                    polygonColors.add(green);
                                } else if (no2dygn < 36) {
                                    polygonColors.add(darkGreen);
                                } else if (no2dygn < 48) {
                                    polygonColors.add(yellow);
                                } else if (no2dygn < 60) {
                                    polygonColors.add(orange);
                                } else {
                                    polygonColors.add(red);
                                }

                            } else {
//                        if (logger.isDebugEnabled()) {
//                            logger.debug("No perc value !!");
//
//                        }
                                polygonColors.add(new Color4f(black));
                            }
                        }
                    }
                    if (logger.isDebugEnabled()) {
//                            logger.debug("last point: "+polygons.get(polygons.size()-1));
                    }
                    partCounter++;
                    coordCounter++;
                }
//                if (partCounter > 2) {
//                    if(!checkLastPart(partCounter, coordCounter - (partCounter-1))){
//                        clockwiseCount++;
//                    }//                }

                if (shapeType != shapeType.POLYGON_2D) {
                    pointIndices.add(buffer.getSize());
                }
            }
        }
        shp.close();
        //postprocess Centerpoints
        if (shapeType == SHAPE_TYPE.POLYGON_2D) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("length: " + points.size());
//                logger.debug("lengthSQRT: " + Math.sqrt(points.size()));
//            }
            if (logger.isDebugEnabled()) {
                logger.debug("points: " + points.size() + " pointsColors: " + polygonColors.size());
            }
            final ArrayList<Point3f> centerPoints = new ArrayList<Point3f>(points);
            final ArrayList<Color4f> centerColors = new ArrayList<Color4f>(polygonColors);
            points = new ArrayList<Point3f>();
            polygonColors = new ArrayList<Color4f>();
            final int rowSize = (int) Math.sqrt(centerPoints.size());
            final int columnSize = (int) rowSize;
            for (int i = 0; i < rowSize - 1; i++) {
                for (int j = 0; j < columnSize - 1; j++) {
                    final int rowOffset = i * (columnSize);
                    final int rowOffset2 = (i + 1) * (columnSize);
                    points.add(centerPoints.get(rowOffset + j));
                    polygonColors.add(centerColors.get(rowOffset + j));
                    points.add(centerPoints.get(rowOffset2 + j));
                    polygonColors.add(centerColors.get(rowOffset2 + j));
                    points.add(centerPoints.get(rowOffset2 + j + 1));
                    polygonColors.add(centerColors.get(rowOffset2 + j + 1));
                    points.add(centerPoints.get(rowOffset + j + 1));
                    polygonColors.add(centerColors.get(rowOffset + j + 1));
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("first: "+(rowOffset + j)+
//                                " second: "+(rowOffset2 + j)+
//                                " third: "+(rowOffset2 + j + 1)+
//                                "fourth: "+(rowOffset + j + 1));
//                        logger.debug("first: "+centerPoints.get(rowOffset + j)+
//                                " second: "+centerPoints.get(rowOffset2 + j)+
//                                " third: "+centerPoints.get(rowOffset2 + j + 1)+
//                                "fourth: "+centerPoints.get(rowOffset + j + 1));
//                    }
                    pointIndices.add(4);
                }
            }
        }

        if (shapeType == SHAPE_TYPE.POLYLINE) {
            averageADT = allADT / pointADT.size();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Creating geometry done. Time elapsed: "
                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms.");
            if (logger.isDebugEnabled()) {
                logger.debug("min perc: " + minPerc + " max perc: " + maxPerc);
                logger.debug("min no2: " + minNo2dygn + " max no2: " + maxNo2dygn);
                logger.debug("min adt: " + minADT + " max adt: " + maxADT + " average: " + averageADT);
                logger.debug("layers: " + Arrays.deepToString(buildingColorMap.keySet().toArray()));
                logger.debug("clockwiseCount: " + clockwiseCount);
            }
        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:performance nightmare
    private int[] copyIntegerArray(final ArrayList<Integer> array) {
        final int[] copy = new int[array.size()];
        int counter = 0;
        for (Integer currentInt : array) {
            copy[counter] = currentInt;
            counter++;
        }
        return copy;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:central place
    private void scalePoint3f(final Point3f point) {
        point.x *= ComponentBroker.getInstance().getScalingFactor();
        point.y *= ComponentBroker.getInstance().getScalingFactor();
        point.z *= ComponentBroker.getInstance().getScalingFactor();
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:credits replace
    /**
     * Determines whether a shapefile record contains a height attribute and return it if it does.
     *
     * @param record the record to search.
     *
     * @return the height value if a height attribute is found, otherwise null.
     */
    protected Double extractDoubleAttribute(final String attributeName, final ShapefileRecord record) {
        for (Map.Entry<String, Object> attr : record.getAttributes().getEntries()) {
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:hardcoded!!!!
            if (!attr.getKey().trim().equalsIgnoreCase(attributeName)) {
                continue;
            }
            ;

            Object o = attr.getValue();
            if (o instanceof Double) {
                return (Double) o;
            }

            if (o instanceof Integer) {
                return new Double(o.toString());
            }

            if (o instanceof String) {
                return WWUtil.convertStringToDouble(o.toString());
            }
        }

        return null;
    }

    protected String extractStringAttribute(final String attributeName, final ShapefileRecord record) {
        for (Map.Entry<String, Object> attr : record.getAttributes().getEntries()) {
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:hardcoded!!!!
            if (!attr.getKey().trim().equalsIgnoreCase(attributeName)) {
                continue;
            }
            ;

            Object o = attr.getValue();
            return o.toString();
        }
        return null;
    }

    public Color4f getLowConcentrationColor() {
        return lowConcentrationColor;
    }

    public float getLowConcentrationThreshold() {
        return lowConcentrationThreshold;
    }

    public Color4f getMediumConcentrationColor() {
        return mediumConcentrationColor;
    }

    public float getMediumConcentrationThreshold() {
        return mediumConcentrationThreshold;
    }

    public ArrayList<Double> getPointColors() {
        return pointColors;
    }

    public ArrayList<ShapefileObject> getShapeArray() {
        return shapeArray;
    }

    public Color4f getHighConcentrationColor() {
        return highConcentrationColor;
    }
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:remove after vienna
//    private void swapArray(Point3f[] arrayToSwap) {
//        if (arrayToSwap == null) {
//            return;
//        }
//        if (arrayToSwap.length == 1) {
//            return;
//        }
//        boolean even = (arrayToSwap.length % 2 == 0);
//        for (int i = 0; i < arrayToSwap.length / 2; i++) {
//            final Point3f tmp = arrayToSwap[(arrayToSwap.length - 1) - i];
//            arrayToSwap[(arrayToSwap.length - 1) - i] = arrayToSwap[i];
//            arrayToSwap[i] = tmp;
//        }
//    }
}
