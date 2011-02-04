/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io.dem;

import com.dfki.av.sudplan.io.AbstractLoader;
import com.dfki.av.sudplan.io.ParsingException;
import com.dfki.av.sudplan.util.TimeMeasurement;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.LoaderBase;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.SceneBase;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
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

  private boolean fromUrl = false;
  private RawArcGrid arcGrid;
  private Point3f[] triangleCoordinates;
  private ObjectFile test;

  @Override
  public Scene load(final Reader reader) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
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
  private void createTriangle() {
    if (logger.isDebugEnabled()) {
      logger.debug("Triangulating grid...");
      TimeMeasurement.getInstance().startMeasurement(this);
    }
    triangleCoordinates = new Point3f[arcGrid.getTriangleCount() * 3];
    int currentTriangle = 0;
    for (int currentRow = 0; currentRow < (arcGrid.getNumberOfRows() - 1); currentRow++) {
      for (int currentColumn = 0; currentColumn < (arcGrid.getNumberOfColumns() - 1); currentColumn++) {
//        logger.trace("row: "+currentRow+" column: "+currentColumn);
//        logger.trace("triangleIndex: " + (currentTriangle * 3) + " gridpointindex: (" + currentColumn + "," + (currentRow) + ")");
        triangleCoordinates[(currentTriangle * 3)] = arcGrid.getGridPoint(currentColumn, currentRow);
//        logger.debug("triangleIndex: " + (currentTriangle * 3 + 1) + " gridpointindex: (" + (currentColumn + 1) + "," + (currentRow + 1) + ")");
        triangleCoordinates[(currentTriangle * 3) + 1] = arcGrid.getGridPoint(currentColumn + 1, currentRow + 1);
//        logger.debug("triangleIndex: " + (currentTriangle * 3 + 2) + " gridpointindex: (" + currentColumn +"," + (currentRow + 1) + ")");
        triangleCoordinates[(currentTriangle * 3) + 2] = arcGrid.getGridPoint(currentColumn, currentRow + 1);
        currentTriangle++;
//        logger.debug("triangleIndex: " + (currentTriangle * 3) + " gridpointindex: (" + currentColumn + "," + (currentRow) + ")");
        triangleCoordinates[(currentTriangle * 3)] = arcGrid.getGridPoint(currentColumn, currentRow);
//        logger.debug("triangleIndex: " + (currentTriangle * 3 + 1) + " gridpointindex: (" + (currentColumn + 1) + "," + (currentRow) + ")");
        triangleCoordinates[(currentTriangle * 3) + 1] = arcGrid.getGridPoint(currentColumn + 1, currentRow);
//        logger.debug("triangleIndex: " + (currentTriangle * 3 + 2) + " gridpointindex: (" + (currentColumn + 1) + "," + (currentRow + 1) + ")");
        triangleCoordinates[(currentTriangle * 3) + 2] = arcGrid.getGridPoint(currentColumn + 1, currentRow + 1);
        currentTriangle++;
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Triangulating grid done. Time elapsed: " + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
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

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this is pretty expensive look into source code I think this could be done more performant for grids.
    if (logger.isDebugEnabled()) {
      logger.debug("Stripifying geometry...");
      TimeMeasurement.getInstance().startMeasurement(this);
    }
    Stripifier stripifier = new Stripifier();
    stripifier.stripify(gridGeometry);
    if (logger.isDebugEnabled()) {
      logger.debug("Stripifying geometry done. Time elapsed: " + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this is pretty expensive look into source code I think this could be done more performant for grids.
    if (logger.isDebugEnabled()) {
      logger.debug("Normalising geometry...");
      TimeMeasurement.getInstance().startMeasurement(this);
    }
    NormalGenerator normalGenerator = new NormalGenerator();
    normalGenerator.generateNormals(gridGeometry);
    if (logger.isDebugEnabled()) {
      logger.debug("Normalising geometry done. Time elapsed: " + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this has to be configurable with a default value
    Appearance landscapeAppearance = new Appearance();
    PolygonAttributes pa = new PolygonAttributes();

    pa.setCullFace(PolygonAttributes.CULL_NONE);
    landscapeAppearance.setPolygonAttributes(pa);

    Material material = new Material();
    material.setDiffuseColor(new Color3f(1.0f, 0.1f, 0.1f));
    landscapeAppearance.setMaterial(material);

    Shape3D landscape = new Shape3D();
    landscape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    landscape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    landscape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
    landscape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
    landscape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    landscape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    landscape.addGeometry(gridGeometry.getGeometryArray());
    landscape.setAppearance(landscapeAppearance);
    tmpBranch.addChild(landscape);
    if (logger.isDebugEnabled()) {
      logger.debug("creating scene done. Time elapsed: " + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms");
    }
    return tmpScene;
  }
  //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: better to generate directly the triangle strip array instead of using the stripifier
}
