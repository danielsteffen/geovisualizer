/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io.shape;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.io.AbstractLoader;
import com.dfki.av.sudplan.io.dem.RawArcGrid;
import com.dfki.av.sudplan.util.EarthFlat;
import com.dfki.av.sudplan.util.TimeMeasurement;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.SceneBase;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.Stripifier;
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecordPolygon;
import gov.nasa.worldwind.util.VecBuffer;
import gov.nasa.worldwind.util.WWUtil;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ShapeLoader extends AbstractLoader {

    private final static Logger logger = LoggerFactory.getLogger(ShapeLoader.class);
    final SceneBase createdScene = new SceneBase();
    final BranchGroup sceneBranch = new BranchGroup();
    final ArrayList<Point3f> points = new ArrayList<Point3f>();
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:Optimise waste of memory and time
    private final ArrayList<Double> pointColors = new ArrayList<Double>();
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:remove after Vienna
    final ArrayList<Color4f> polygonColors = new ArrayList<Color4f>();
    final ArrayList<Color4f> wireColors = new ArrayList<Color4f>();
    final ArrayList<Integer> polygonColorsIndex = new ArrayList<Integer>();
    final ArrayList<Integer> pointIndices = new ArrayList<Integer>();
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:color class
    final Color4f transparent = new Color4f(1.0f, 1.0f, 1.0f, 0.0f);
    final Color4f white = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);
    final Color4f gray = new Color4f(0.8f, 0.8f, 0.8f, 1.0f);
    final Color4f black = new Color4f(0.0f, 0.0f, 0.0f, 1.0f);
    final Color4f red = new Color4f(1.0f, 0.0f, 0.0f, 0.9f);
    final Color4f yellow = new Color4f(8.0f, 8.0f, 0.0f, 0.9f);
    final Color4f green = new Color4f(0.0f, 1.0f, 0.0f, 0.9f);
    private final Color4f lowConcentrationColor = green;
    private final Color4f mediumConcentrationColor = yellow;
    private final Color4f highConcentrationColor = red;
    private final float lowConcentrationThreshold = 40.0f;
    private final float mediumConcentrationThreshold = 50.0f;
    private final ArrayList<ShapefileObject> shapeArray = new ArrayList<ShapefileObject>();
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: Nasa Shapefile is not able to scoop with colon decimals. Fix!
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: remove after Vienna Demo
    final RawArcGrid heights = ComponentBroker.getInstance().getHeights();

    public static enum SHAPE_TYPE {

        POLYGON, POLYLINE
    };
    private Shapefile shp = null;
    SHAPE_TYPE shapeType = null;

    @Override
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:check exceptionhandling DEMLOADER
    public Scene load()
            throws FileNotFoundException,
            IncorrectFormatException,
            ParsingErrorException {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading scene...");
        }

        try {
            this.shp = new Shapefile(file);
            createPoints();
        } finally {
            if (shp != null) {
                shp.close();
            }
        }
        return createScene();
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: abtract DEMloader ?? Example Empty Scene. only difference is setting the geometry
    private Scene createScene() {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating scene...");
            TimeMeasurement.getInstance().startMeasurement(this);
        }
        createdScene.setSceneGroup(sceneBranch);
        if (shapeType == SHAPE_TYPE.POLYGON) {
            createPolygons();
        } else if (shapeType == SHAPE_TYPE.POLYLINE) {
            createPolylines();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Creating scene done. Time elapsed: "
                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms.");
        }
        return createdScene;
    }

    private void createPolygons() {
        final TransformGroup wireGroup = new TransformGroup();
        final Point3f[] coordinates = points.toArray(new Point3f[]{});
        final int[] stripCount = copyIntegerArray(pointIndices);
        final Color4f[] colorsPoly = polygonColors.toArray(new Color4f[]{});
        final Color4f[] colorsWire = wireColors.toArray(new Color4f[]{});
//        final Color4f[] colors = new Color4f[]{gray};
//        final int[] colorsIndex = copyIntegerArray(polygonColorsIndex);
        GeometryInfo gridGeometry = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gridGeometry.setCoordinates(coordinates);
//        gridGeometry.setColorIndices(colorsIndex);
        gridGeometry.setColors(colorsPoly);
        gridGeometry.setStripCounts(stripCount);
        GeometryInfo wireGeometry = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        wireGeometry.setCoordinates(coordinates);
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
//        NormalGenerator normalGenerator = new NormalGenerator();
//        normalGenerator.generateNormals(gridGeometry);
        if (logger.isDebugEnabled()) {
            logger.debug("Normalising geometry done. Time elapsed: "
                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
        }

        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this has to be configurable with a default value
        Appearance landscapeAppearance = new Appearance();
        PolygonAttributes pa = new PolygonAttributes();

        TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.15f);
        landscapeAppearance.setTransparencyAttributes(ta);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:wrong triangulation ??
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        landscapeAppearance.setPolygonAttributes(pa);

        Material material = new Material();
//        material.setDiffuseColor(new Color3f(0.2f, 0.2f, 0.2f));
//        material.setAmbientColor(new Color3f(0.98f, 0.98f, 0.98f));
//        material.setAmbientColor(new Color3f(0.8f, 0.8f, 0.8f));
        material.setColorTarget(Material.AMBIENT_AND_DIFFUSE);

        landscapeAppearance.setMaterial(material);

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
        lineAttribtues.setLineWidth(1.0f);
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
//        ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:does not work wire disapears
//        scaling.setScale(1.1);
        wireGroup.setTransform(scaling);
        sceneBranch.addChild(landscape);
        sceneBranch.addChild(wireGroup);
        wireGroup.addChild(wire);
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: checks for valid geometries e.g. line with one point
    private void createPolylines() {

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
            sceneBranch.addChild(lineShape);
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
            logger.debug("TotalIndex: "+currentIndex);
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
            logger.debug("Number of Shapes: "+shapeArray.size());
        }
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
            shapeType = SHAPE_TYPE.POLYGON;
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

    public void createPointsFromShape() {
        double minPerc = 9999.0;
        double maxPerc = -9999.0;
        while (shp.hasNext()) {
            final ShapefileRecord record = shp.nextRecord();
            Double elevation = this.extractDoubleAttribute("elevation", record);
            Double perc = this.extractDoubleAttribute("perc98d", record);
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
                while (coords.hasNext()) {
                    final double[] currentCoords = coords.next();

                    final Point3f originalPoint = new Point3f((float) currentCoords[0], (float) currentCoords[1], 0.0f);

                    if (currentHeights != null) {
                        originalPoint.setZ((float) currentHeights[coordCounter]);
                    }
//                            originalPoint.setZ((float) (originalPoint.getZ() + elevation));
//                        }
                    final Point3f transformedPoint = EarthFlat.geodeticToCartesian(originalPoint, EarthFlat.PLATE_CARREE_PROJECTION);
                    scalePoint3f(transformedPoint);
                    points.add(transformedPoint);
                    if (shapeType == SHAPE_TYPE.POLYLINE && currentHeights == null && heights != null) {
                        final Point3f nb = heights.getNearestNeighbour(transformedPoint);
//                        if (logger.isDebugEnabled()) {
//                            logger.debug("point: "+transformedPoint+" nb: " + nb);
//                        }
                        if (nb != null) {
                            if(nb.z<0.0f){
                                transformedPoint.setZ((float) (-0.039f));
                            } else {
                                transformedPoint.setZ((float) (nb.z + 0.002f));
                            }
                        }
                    } else if(shapeType == SHAPE_TYPE.POLYLINE && currentHeights == null){
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
                    } else if (shapeType == SHAPE_TYPE.POLYGON) {
//                        if (transformedPoint.getZ() < 0.000001f) {
//                            polygonColors.add(new Color4f(transparent));
//                            wireColors.add(new Color4f(transparent));
//                        } else {
                            polygonColors.add(new Color4f(gray));
                            wireColors.add(new Color4f(black));
//                        }

//                        polygonColorsIndex.add(0);
                    }
                    if (logger.isDebugEnabled()) {
//                            logger.debug("last point: "+polygons.get(polygons.size()-1));
                    }
                    coordCounter++;
                }
                pointIndices.add(buffer.getSize());
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Creating geometry done. Time elapsed: "
                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms.");
            if (logger.isDebugEnabled()) {
                logger.debug("min perc: " + minPerc + " max perc: " + maxPerc);
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
    protected Double extractDoubleAttribute(final String attributeName,final ShapefileRecord record) {
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

    

}
