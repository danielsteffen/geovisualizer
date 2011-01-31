/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.example;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.geometry.Triangulator;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.util.Arrays;
import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class Example3 {


  private GeometryInfo gi;

  float[] createCoordinateData() {
    float[] data = new float[36];         // ******
    int i = 0;    


    data[i++] = 0f;
    data[i++] = 0f;
    data[i++] = 0f; //1
    data[i++] = 1f;
    data[i++] = 0f;
    data[i++] = 0f; //2
    data[i++] = 0.5f;
    data[i++] = 0.5f;
    data[i++] = 1f; //3
//    System.out.println("end polygon; total vertex count: " + i / 3);

    data[i++] = 0f;
    data[i++] = 1f;
    data[i++] = 0f; //1
    data[i++] = 0f;
    data[i++] = 0f;
    data[i++] = 0; //2
    data[i++] = 0.5f;
    data[i++] = 0.5f;
    data[i++] = 1f; //3

    data[i++] = 1f;
    data[i++] = 0f;
    data[i++] = 0f; //1
    data[i++] = 1f;
    data[i++] = 1f;
    data[i++] = 0f; //3
    data[i++] = 0.5f;
    data[i++] = 0.5f;
    data[i++] = 1.0f; //2

    data[i++] = 0f;
    data[i++] = 1f;
    data[i++] = 0f; //1
    data[i++] = 0.5f;
    data[i++] = 0.5f;
    data[i++] = 1.0f; //2
    data[i++] = 1f;
    data[i++] = 1f;
    data[i++] = 0f; //3

//    data[i++] = 0f;
//    data[i++] = 0f;
//    data[i++] = 0f; //1
//    data[i++] = 1f;
//    data[i++] = 0f;
//    data[i++] = 0f; //2
//    data[i++] = 1f;
//    data[i++] = 1f;
//    data[i++] = 0f; //3
//    data[i++] = 0f;
//    data[i++] = 1f;
//    data[i++] = 0f; //3 
    System.out.println("end polygon; total vertex count: " + i / 3);

//    data[i++] = ;
//    data[i++] = ;
//    data[i++] = ; //1
//    System.out.println("end polygon; total vertex count: " + i / 3);
//
//    data[i++] = ;
//    data[i++] = ;
//    data[i++] = ; //1
//    System.out.println("end polygon; total vertex count: " + i / 3);

    
    return data;
  }

  Appearance createMaterialAppearance() {

    Appearance materialAppear = new Appearance();
    PolygonAttributes polyAttrib = new PolygonAttributes();
    polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
    materialAppear.setPolygonAttributes(polyAttrib);

    Material material = new Material();
    material.setDiffuseColor(new Color3f(1.0f, 0.0f, 0.0f));
    materialAppear.setMaterial(material);

    return materialAppear;
  }

  Appearance createWireFrameAppearance() {

    Appearance materialAppear = new Appearance();
    PolygonAttributes polyAttrib = new PolygonAttributes();
    polyAttrib.setPolygonMode(PolygonAttributes.POLYGON_LINE);
    materialAppear.setPolygonAttributes(polyAttrib);
    ColoringAttributes redColoring = new ColoringAttributes();
    redColoring.setColor(1.0f, 0.0f, 0.0f);
    materialAppear.setColoringAttributes(redColoring);

    return materialAppear;
  }

  /////////////////////////////////////////////////
  //
  // create scene graph branch group
  //
  public BranchGroup createSceneGraph(boolean wireFrame) {
    int total = 0;

    System.out.println("\n --- geometry debug information --- \n");

    float[] coordinateData = null;
    coordinateData = createCoordinateData();
//    int[] stripCount = {17, 17, 5, 5, 5, 5, 5, 5, 5};  // ******
//        int[] stripCount = {17,17,17};  // ******
//        int[] stripCount = {3,3};  // ******
//    int[] stripCount = {3,3,3,3,4};  // ******
//    for (int i = 0; i < stripCount.length; i++) {
//      System.out.println("stripCount[" + i + "] = " + stripCount[i]);
//      total += stripCount[i];
//    }

    if (total != coordinateData.length / 3) {
      System.out.println("  coordinateData vertex count: " + coordinateData.length / 3);
      System.out.println("stripCount total vertex count: " + total);
    }

    gi = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
    gi.setCoordinates(coordinateData);
//    gi.setStripCounts(stripCount);
    printGeometry(gi);
//    Triangulator tr = new Triangulator();
//        Triangulator tr = new Triangulator(GeometryInfo);
//    System.out.println("begin triangulation");
//    tr.triangulate(gi);
//    System.out.println("  END triangulation");
    printGeometry(gi);
    gi.recomputeIndices();

    NormalGenerator ng = new NormalGenerator();
    ng.generateNormals(gi);
    gi.recomputeIndices();

    Stripifier st = new Stripifier();
    st.stripify(gi);
    printGeometry(gi);
    gi.recomputeIndices();

    Shape3D part = new Shape3D();
    if (wireFrame == true) {
      part.setAppearance(createWireFrameAppearance());
    } else {
      part.setAppearance(createMaterialAppearance());
    }
    part.setGeometry(gi.getGeometryArray());

    /////////////////////////////

    BranchGroup contentRoot = new BranchGroup();

    // Create the transform group node and initialize it to the
    // identity. Add it to the root of the subgraph.
    TransformGroup objSpin = new TransformGroup();
    objSpin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    contentRoot.addChild(objSpin);

    objSpin.addChild(part);

    ////////////////////////
//    LineStripArray lineArray = new LineStripArray(69, LineArray.COORDINATES, stripCount); //*****
    
//        LineStripArray lineArray = new LineStripArray(51, LineArray.COORDINATES, stripCount); //*****
//    lineArray.setCoordinates(0, coordinateData);
    Appearance blueColorAppearance = new Appearance();
    ColoringAttributes blueColoring = new ColoringAttributes();
    blueColoring.setColor(0.0f, 0.0f, 1.0f);
    blueColorAppearance.setColoringAttributes(blueColoring);
    LineAttributes lineAttrib = new LineAttributes();
    lineAttrib.setLineWidth(2.0f);
    blueColorAppearance.setLineAttributes(lineAttrib);
//    objSpin.addChild(new Shape3D(lineArray, blueColorAppearance));

    Alpha rotationAlpha = new Alpha(-1, 16000);

    RotationInterpolator rotator =
            new RotationInterpolator(rotationAlpha, objSpin);

    // a bounding sphere specifies a region a behavior is active
    // create a sphere centered at the origin with radius of 1
    BoundingSphere bounds = new BoundingSphere();
    rotator.setSchedulingBounds(bounds);
    objSpin.addChild(rotator);

    DirectionalLight lightD = new DirectionalLight();
    lightD.setDirection(new Vector3f(0.0f, -0.7f, -0.7f));
    lightD.setInfluencingBounds(bounds);
    contentRoot.addChild(lightD);

    AmbientLight lightA = new AmbientLight();
    lightA.setInfluencingBounds(bounds);
    contentRoot.addChild(lightA);

    Background background = new Background();
    background.setColor(1.0f, 1.0f, 1.0f);
    background.setApplicationBounds(bounds);
    contentRoot.addChild(background);

    // Let Java 3D perform optimizations on this scene graph.
    // contentRoot.compile();

    return contentRoot;
  } // end of CreateSceneGraph method of MobiusApp

  // Create a simple scene and attach it to the virtual universe
  public JPanel createUniverse() {
    final JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    GraphicsConfiguration config =
            SimpleUniverse.getPreferredConfiguration();

    Canvas3D canvas3D = new Canvas3D(config);
    panel.add(canvas3D, BorderLayout.CENTER);
    BranchGroup scene = createSceneGraph(false);

    // SimpleUniverse is a Convenience Utility class
    SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

    // This will move the ViewPlatform back a bit so the
    // objects in the scene can be viewed.
    simpleU.getViewingPlatform().setNominalViewingTransform();

    simpleU.addBranchGraph(scene);
    return panel;
  } // end of GeomInfoApp constructor
  //  The following allows this to be run as an application
  //  as well as an applet

  public void printGeometry(final GeometryInfo geoInfo){
    System.out.println("");
    System.out.println("Primitive: "+geoInfo.getPrimitive());
    System.out.println("Stripcount: "+geoInfo.getStripCounts());
    System.out.println("Coordinates: "+Arrays.deepToString(geoInfo.getCoordinates()));
    System.out.println("ToString: "+geoInfo.toString());
    System.out.println("");
  }


}
