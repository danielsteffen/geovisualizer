/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.example;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class Rotation extends JPanel {

    private final static Logger logger = LoggerFactory.getLogger(Rotation.class);
    SimpleUniverse simpleU;
    BranchGroup scene;
    TransformGroup transformGroup;
    Transform3D transformation;

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: setTranslate does overwrite the translation --> for combined use multiplication
    public Rotation() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config =
                SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);

        // Create the root of the branch graph
        scene = new BranchGroup();

//        TransformGroup objRotate = new TransformGroup(transformObject());
        transformGroup = new TransformGroup();
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.addChild(new ColorCube(0.4));
        scene.addChild(transformGroup);
        transformation = new Transform3D();      
        rotateTranslateCorrect();
          Transform3D transformationTrans = new Transform3D();
        transformationTrans.setTranslation(new Vector3d(3f, 0.0, 0.0));
        transformation.mul(transformationTrans);
        transformGroup.setTransform(transformation);
        simpleU = new SimpleUniverse(canvas3D);
        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(getViewTransformation());
        createAxis();
        simpleU.addBranchGraph(scene);
        Matrix4d transformMatrix = new Matrix4d();
        Transform3D tempTransform = new Transform3D();
        transformGroup.getTransform(tempTransform);
//        tempTransform.get(tempTransform);
        if (logger.isDebugEnabled()) {
            logger.debug("matrix: " + tempTransform);
        }
    } // end of HelloJava3Da (constructor)

//      public Transform3D rotateTranslateCorrect() {
//        Matrix4d transformMatrix = new Matrix4d();
////        Transform3D rotate = new Transform3D();
//        Transform3D tempRotate = new Transform3D();
//        transformation.get(transformMatrix);
//        if (logger.isDebugEnabled()) {
//            logger.debug("matrix: " + transformMatrix);
//        }
////        tempRotate.rotX(Math.PI/4);
//        Transform3D tz = new Transform3D();
//        final AxisAngle4f aa4f = new AxisAngle4f(0.0f, 0.0f, 1.0f, (float) Math.PI / 4);
//        tz.set(aa4f);
//        transformation.mul(tz);
//        transformGroup.setTransform(transformation);
////        tempRotate.rotY(Math.PI/4);
////        transformation.setTranslation(new Vector3d(3f,0.0,0.0));
////        tempRotate.rotX(Math.PI/4);
////        transformation.mul(tempRotate);
//        transformation.get(transformMatrix);
//        if (logger.isDebugEnabled()) {
//            logger.debug("matrix: " + transformMatrix);
//        }
//        return transformation;
//    }

    public Transform3D rotateTranslateCorrect() {
        Matrix4d transformMatrix = new Matrix4d();
//        Transform3D rotate = new Transform3D();
        Transform3D tempRotate = new Transform3D();
        transformation.get(transformMatrix);
        if (logger.isDebugEnabled()) {
            logger.debug("matrix: " + transformMatrix);
        }
//        tempRotate.rotX(Math.PI/4);
        Transform3D tz = new Transform3D();
        final AxisAngle4f aa4f = new AxisAngle4f(0.0f, 0.0f, 1.0f, (float) Math.PI / 4);
        tz.set(aa4f);        
        transformation.mul(tz);
        transformGroup.setTransform(transformation);
//        tempRotate.rotY(Math.PI/4);
//        transformation.setTranslation(new Vector3d(3f,0.0,0.0));
//        tempRotate.rotX(Math.PI/4);
//        transformation.mul(tempRotate);
        transformation.get(transformMatrix);
        if (logger.isDebugEnabled()) {
            logger.debug("matrix: " + transformMatrix);
        }
        return transformation;
    }

    public Transform3D rotateTranslate() {
        Matrix4d transformMatrix = new Matrix4d();
        Transform3D rotate = new Transform3D();
        Transform3D tempRotate = new Transform3D();
        rotate.get(transformMatrix);
        if (logger.isDebugEnabled()) {
            logger.debug("matrix: " + transformMatrix);
        }
//        tempRotate.rotX(Math.PI/4);
//        tempRotate.rotY(Math.PI/4);
        tempRotate.rotZ(Math.PI / 4);
        rotate.mul(tempRotate);
        rotate.get(transformMatrix);
        rotate.setTranslation(new Vector3d(3f, 0.0, 0.0));
        if (logger.isDebugEnabled()) {
            logger.debug("matrix: " + transformMatrix);
        }
        return rotate;
    }

    public Transform3D getViewTransformation() {
        Transform3D translation = new Transform3D();
        simpleU.getViewingPlatform().getViewPlatformTransform().getTransform(translation);
        translation.setTranslation(new Vector3d(0, 0, 15));
        return translation;
    }

//    public BranchGroup createSceneGraph() {
//
//    } // end of CreateSceneGraph method of HelloJava3Da
    public void createAxis() {
        Shape3D axisShape3D = new Shape3D();

        // create line for X axis
        LineArray axisXLines = new LineArray(2, LineArray.COORDINATES);
        axisShape3D.removeGeometry(0);
        axisShape3D.addGeometry(axisXLines);

        axisXLines.setCoordinate(0, new Point3f(-10.0f, 0.0f, 0.0f));
        axisXLines.setCoordinate(1, new Point3f(10.0f, 0.0f, 0.0f));

        Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
        Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
        Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);

        // create line for Y axis
        LineArray axisYLines = new LineArray(2,
                LineArray.COORDINATES | LineArray.COLOR_3);
        axisShape3D.addGeometry(axisYLines);

        axisYLines.setCoordinate(0, new Point3f(0.0f, -10.0f, 0.0f));
        axisYLines.setCoordinate(1, new Point3f(0.0f, 10.0f, 0.0f));

        axisYLines.setColor(0, green);
        axisYLines.setColor(1, blue);

        // create line for Z axis
        Point3f z1 = new Point3f(0.0f, 0.0f, -10.0f);
        Point3f z2 = new Point3f(0.0f, 0.0f, 10.0f);

        LineArray axisZLines = new LineArray(10,
                LineArray.COORDINATES | LineArray.COLOR_3);
        axisShape3D.addGeometry(axisZLines);

        axisZLines.setCoordinate(0, z1);
        axisZLines.setCoordinate(1, z2);
        axisZLines.setCoordinate(2, z2);
        axisZLines.setCoordinate(3, new Point3f(0.1f, 0.1f, 0.9f));
        axisZLines.setCoordinate(4, z2);
        axisZLines.setCoordinate(5, new Point3f(-0.1f, 0.1f, 0.9f));
        axisZLines.setCoordinate(6, z2);
        axisZLines.setCoordinate(7, new Point3f(0.1f, -0.1f, 0.9f));
        axisZLines.setCoordinate(8, z2);
        axisZLines.setCoordinate(9, new Point3f(-0.1f, -0.1f, 0.9f));

        Color3f colors[] = new Color3f[9];

        colors[0] = new Color3f(0.0f, 1.0f, 1.0f);
        for (int v = 0; v < 9; v++) {
            colors[v] = red;
        }

        axisZLines.setColors(1, colors);
        scene.addChild(axisShape3D);
    }
}
