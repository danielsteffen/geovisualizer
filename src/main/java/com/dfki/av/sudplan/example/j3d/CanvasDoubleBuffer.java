/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.example.j3d;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.util.Arrays;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class CanvasDoubleBuffer {

    private GeometryInfo gi;

    Point3f[] createCoordinateData() {
        Point3f[] data = new Point3f[12];         // ******
        int i = 0;
        data[i++] = new Point3f(0f, 0f, 0f);
        data[i++] = new Point3f(0.5f, 0.5f, 1f);
        data[i++] = new Point3f(1f, 0f, 0f);


        data[i++] = new Point3f(0f, 1f, 0f);
        data[i++] = new Point3f(0f, 0f, 0f);
        data[i++] = new Point3f(0.5f, 0.5f, 1f);


        data[i++] = new Point3f(1f, 0f, 0f);
        data[i++] = new Point3f(1f, 1f, 0f);
        data[i++] = new Point3f(0.5f, 0.5f, 1f);

        data[i++] = new Point3f(0f, 1f, 0f);
        data[i++] = new Point3f(0.5f, 0.5f, 1f);
        data[i++] = new Point3f(1f, 1f, 0f);

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
       BranchGroup axisBG = new BranchGroup();
         // create line for X axis
	    LineArray axisXLines = new LineArray(2, LineArray.COORDINATES  );
	    axisBG.addChild(new Shape3D(axisXLines));

	    axisXLines.setCoordinate(0, new Point3f(-1.0f, 0.0f, 0.0f));
	    axisXLines.setCoordinate(1, new Point3f( 1.0f, 0.0f, 0.0f));

            final Color3f red   = new Color3f(Color.red);
            final Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
            final Color3f blue  = new Color3f(0.0f, 0.0f, 1.0f);

	    // create line for Y axis
	    LineArray axisYLines = new LineArray(2,
		LineArray.COORDINATES | LineArray.COLOR_3 );
	    axisBG.addChild(new Shape3D(axisYLines));

	    axisYLines.setCoordinate(0, new Point3f( 0.0f,-1.0f, 0.0f));
	    axisYLines.setCoordinate(1, new Point3f( 0.0f, 1.0f, 0.0f));

	    axisYLines.setColor(0, green);
	    axisYLines.setColor(1, blue);

	    // create line for Z axis
	    Point3f z1 = new Point3f( 0.0f, 0.0f,-1.0f);
	    Point3f z2 = new Point3f( 0.0f, 0.0f, 1.0f);

	    LineArray axisZLines = new LineArray(10,
			LineArray.COORDINATES  | LineArray.COLOR_3
		);
	    axisBG.addChild(new Shape3D(axisZLines));

	    axisZLines.setCoordinate(0, z1);
	    axisZLines.setCoordinate(1, z2);
	    axisZLines.setCoordinate(2, z2);
	    axisZLines.setCoordinate(3, new Point3f( 0.1f, 0.1f, 0.9f));
	    axisZLines.setCoordinate(4, z2);
	    axisZLines.setCoordinate(5, new Point3f(-0.1f, 0.1f, 0.9f));
	    axisZLines.setCoordinate(6, z2);
	    axisZLines.setCoordinate(7, new Point3f( 0.1f,-0.1f, 0.9f));
	    axisZLines.setCoordinate(8, z2);
	    axisZLines.setCoordinate(9, new Point3f(-0.1f,-0.1f, 0.9f));

            Color3f colors[] = new Color3f[10];        //array of colors

            colors[0] = new Color3f(0.0f, 1.0f, 1.0f); //set the first color
            for(int v = 1; v < 10; v++){               //set the remaining colors
		colors[v] = red;
	    }

            axisZLines.setColors(0, colors);           // add colors to geometry
            return axisBG;
    } // end of CreateSceneGraph method of MobiusApp

    // Create a simple scene and attach it to the virtual universe
    public JPanel createUniverse() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        GraphicsConfiguration config =
                SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
//          Canvas3D canvas3D = new DoubleBufferedCanvas3D(config);
//            System.out.println("isDoubleBuffered: "+canvas3D.isDoubleBuffered());
//            System.out.println("isDoubleBuffering supported: "+canvas3D.getDoubleBufferAvailable());
//        canvas3D.setDoubleBufferEnable(true);
//        System.out.println("isDoubleBuffered: "+canvas3D.isDoubleBuffered());
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

    public void printGeometry(final GeometryInfo geoInfo) {
        System.out.println("");
        System.out.println("Primitive: " + geoInfo.getPrimitive());
        if (geoInfo.getStripCounts() != null) {
            System.out.println("Stripcounts: " + geoInfo.getStripCounts().length);
        } else {
            System.out.println("Stripcounts: " + null);
        }
        System.out.println("Coordinates: " + Arrays.deepToString(geoInfo.getCoordinates()));
        System.out.println("ToString: " + geoInfo.toString());
        System.out.println("");
    }
}
