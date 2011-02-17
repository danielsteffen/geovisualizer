/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io.dem;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.io.AbstractLoader;
import com.dfki.av.sudplan.io.ParsingException;
import com.dfki.av.sudplan.util.TimeMeasurement;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.SceneBase;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.image.TextureLoader;
import java.io.FileNotFoundException;
import java.net.URL;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class DEMLoader extends AbstractLoader {

    private final static Logger logger = LoggerFactory.getLogger(DEMLoader.class);
    private RawArcGrid arcGrid;
    private Point3f[] triangleCoordinates;
    private TexCoord2f[] texCoords;
    private ObjectFile test;

    @Override
    public Scene load() throws
            FileNotFoundException,
            IncorrectFormatException,
            ParsingErrorException {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading scene...");
        }
        try {
            arcGrid = new ArcGridParser(reader).parseArcGrid();
            createTriangle();
            return createScene();
        } catch (ParsingException ex) {
            final ParsingErrorException newException = new ParsingErrorException();
            newException.fillInStackTrace();
            newException.initCause(ex);
            throw newException;
        }
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
        texCoords = new TexCoord2f[arcGrid.getTriangleCount() * 3];
        int currentTriangle = 0;
        float divider1=(arcGrid.getNumberOfColumns()-1);
        float multiplier1 = 10;
        float divider2=(arcGrid.getNumberOfRows()-1);
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
                texCoords[(currentTriangle * 3)] = new TexCoord2f(((currentColumn)/(divider1))*multiplier1,((currentRow)/(divider2))*multiplier2);
//        logger.debug("triangleIndex: " + (currentTriangle * 3 + 1) + " gridpointindex: (" + (currentColumn + 1) + "," + (currentRow + 1) + ")");
                triangleCoordinates[(currentTriangle * 3) + 1] = arcGrid.getGridPoint(currentColumn + 1, currentRow + 1);
                texCoords[(currentTriangle * 3)+1] = new TexCoord2f(((currentColumn+1)/(divider1))*multiplier1,((currentRow+1)/(divider2))*multiplier2);
//        logger.debug("triangleIndex: " + (currentTriangle * 3 + 2) + " gridpointindex: (" + currentColumn +"," + (currentRow + 1) + ")");
                triangleCoordinates[(currentTriangle * 3) + 2] = arcGrid.getGridPoint(currentColumn, currentRow + 1);
                texCoords[(currentTriangle * 3)+2] = new TexCoord2f(((currentColumn)/(divider1))*multiplier1,((currentRow+1)/(divider2))*multiplier2);
                currentTriangle++;
//        logger.debug("triangleIndex: " + (currentTriangle * 3) + " gridpointindex: (" + currentColumn + "," + (currentRow) + ")");
                triangleCoordinates[(currentTriangle * 3)] = arcGrid.getGridPoint(currentColumn, currentRow);
                texCoords[(currentTriangle * 3)] = new TexCoord2f(((currentColumn)/(divider1))*multiplier1,((currentRow)/(divider2))*multiplier2);
//        logger.debug("triangleIndex: " + (currentTriangle * 3 + 1) + " gridpointindex: (" + (currentColumn + 1) + "," + (currentRow) + ")");
                triangleCoordinates[(currentTriangle * 3) + 1] = arcGrid.getGridPoint(currentColumn + 1, currentRow);
                texCoords[(currentTriangle * 3)+1] = new TexCoord2f(((currentColumn+1)/(divider1))*multiplier1,((currentRow)/(divider2))*multiplier2);
//        logger.debug("triangleIndex: " + (currentTriangle * 3 + 2) + " gridpointindex: (" + (currentColumn + 1) + "," + (currentRow + 1) + ")");
                triangleCoordinates[(currentTriangle * 3) + 2] = arcGrid.getGridPoint(currentColumn + 1, currentRow + 1);
                texCoords[(currentTriangle * 3)+2] = new TexCoord2f(((currentColumn+1)/(divider1))*multiplier1,((currentRow+1)/(divider2))*multiplier2);
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
            logger.debug("Triangulating grid done. Time elapsed: "
                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
        }
    }

    private Scene createScene() {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating scene...");
            TimeMeasurement.getInstance().startMeasurement(this);
        }
        final SceneBase tmpScene = new SceneBase();
        final BranchGroup tmpBranch = new BranchGroup();
        tmpScene.setSceneGroup(tmpBranch);
        GeometryInfo gridGeometry = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
        gridGeometry.setCoordinates(triangleCoordinates);
        gridGeometry.setTextureCoordinateParams(1, 2);
        gridGeometry.setTextureCoordinates(0, texCoords);

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
        DEMShape shape = new DEMShape(gridGeometry.getGeometryArray());
        tmpBranch.addChild(shape);
        if (logger.isDebugEnabled()) {
            logger.debug("Creating scene done. Time elapsed: "
                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms.");
        }
        return tmpScene;
    }
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: better to generate directly the triangle strip array instead of using the stripifier

     public RawArcGrid getArcGrid() {
        return arcGrid;
    }

    public void setArcGrid(RawArcGrid arcGrid) {
        this.arcGrid = arcGrid;
    }

    private void createTextureCoordinates(){
//        Point2d[] textureCoordinates = new Point2d[triangleCoordinates.length];
        arcGrid.getCellsize();
        arcGrid.getCellsize();
    }
}
