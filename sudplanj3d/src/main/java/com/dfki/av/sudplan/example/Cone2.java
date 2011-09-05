/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.example;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
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
public class Cone2 extends JPanel {

    private final static Logger logger = LoggerFactory.getLogger(Cone2.class);
    private SimpleUniverse simpleU;
    BranchGroup scene;
    BoundingSphere bigBounds = new BoundingSphere(new Point3d(), 1000);
    private GeometryInfo gi;

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: triangle for simplification;
    public Shape3D createCone(float rad, float height) {
        Shape3D cone = new Shape3D();
        float[] data = new float[15];         // ******
        int i = 0;
        data[i++] = 0f;
        data[i++] = height / 2f;
        data[i++] = 0f; //1
        data[i++] = -rad;
        data[i++] = -height / 2f;
        data[i++] = rad; //2
        data[i++] = rad;
        data[i++] = -height / 2f;
        data[i++] = rad; //3
        data[i++] = -rad;
        data[i++] = -height / 2f;
        data[i++] = -rad; //4
        data[i++] = rad;
        data[i++] = -height / 2f;
        data[i++] = -rad; //5
        int[] stripCount = {5};
        gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gi.setCoordinates(data);
        gi.setStripCounts(stripCount);
        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(gi);
        gi.recomputeIndices();

        Stripifier st = new Stripifier();
        st.stripify(gi);
        gi.recomputeIndices();
        cone.setGeometry(gi.getGeometryArray());
        return cone;
    }

    public Cone2() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config =
                SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);

        // Create the root of the branch graph
        scene = new BranchGroup();

//        TransformGroup objRotate = new TransformGroup(transformObject());
        TransformGroup objRotate = new TransformGroup();
        final float coneLength = 2f;
        final Vector3d translate = new Vector3d(0, -coneLength/2, 0);
        final Transform3D translation = new Transform3D();
        translation.setTranslation(translate);
        final Transform3D targetTrans = new Transform3D();
        final Transform3D rotation = new Transform3D();        
        objRotate.getTransform(targetTrans);
//        targetTrans.mul(rotation);
        rotation.rotZ(0.5);
//        objRotate.setTransform(targetTrans);
//        final Alpha rotationAlpha = new Alpha(-1, 16000);
//        RotationInterpolator rotator =
//                new RotationInterpolator(rotationAlpha, objRotate);
        objRotate.addChild(createCone(0.5f, coneLength));
//        targetTrans.set(translate);                
        targetTrans.mul(rotation);
        targetTrans.mul(translation);
//        objRotate.getTransform(targetTrans);
//        targetTrans.set(translate);
        objRotate.setTransform(targetTrans);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//        scene.addChild(objRotate);
//        rotator.setSchedulingBounds(bigBounds);
//        rotator.setTransformAxis();
//        objRotate.addChild(rotator);
        scene.addChild(objRotate);
        Appearance coneAppearance = new Appearance();

//        objRotate.addChild(new com.sun.j3d.utils.geometry.Cone(0.5f, coneLength, new Appearance()));

//        MouseRotate behavior = new MouseRotate();
//        behavior.setTransformGroup(objRotate);
//        objRotate.addChild(behavior);
//        behavior.setSchedulingBounds(bigBounds);
//                AdvancedOrbitBehavior behavior = new AdvancedOrbitBehavior(canvas3D, scene, AdvancedOrbitBehavior.REVERSE_ALL);
//                behavior.setSchedulingBounds(new BoundingSphere(new Point3d(),1000));
        simpleU = new SimpleUniverse(canvas3D);
        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(transformView());
        setUpLight();
//        simpleU.getViewingPlatform().setViewPlatformBehavior(behavior);
        createAxis();
        simpleU.addBranchGraph(scene);
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:light,raster,universe etc for all examples --> hiwi
    public void setUpLight() {
//          DirectionalLight lightD = new DirectionalLight();
//    lightD.setDirection(new Vector3f(0.0f, -0.7f, -0.7f));
//    lightD.setInfluencingBounds(bigBounds);
//    scene.addChild(lightD);

        AmbientLight lightA = new AmbientLight();
        lightA.setInfluencingBounds(bigBounds);
        scene.addChild(lightA);
    }

    public Transform3D transformObject() {
        Matrix4d transformMatrix = new Matrix4d();
        Transform3D rotate = new Transform3D();
        Transform3D tempRotate = new Transform3D();
//               rotate.rotX(Math.PI/10.0d);
//        rotate.rotY(Math.PI/4.0d);
//        rotate.rotZ(Math.PI/4.0d);
        rotate.get(transformMatrix);
        if (logger.isDebugEnabled()) {
            logger.debug("matrix: " + transformMatrix);
        }
//        rotate.setScale(3.2);
//        rotate.setScale(1.2);
        //rotate.mul --> leads to Excepiton
        rotate.get(transformMatrix);
        if (logger.isDebugEnabled()) {
            logger.debug("matrix: " + transformMatrix);
        }
        return rotate;
    }

    public Transform3D transformView() {
        Matrix4d transformMatrix = new Matrix4d();
        Transform3D rotate = new Transform3D();
        Transform3D rotateX = new Transform3D();
//        Transform3D original = new Transform3D();
//	Transform3D tempRotate = new Transform3D();
        simpleU.getViewingPlatform().getViewPlatformTransform().getTransform(rotate);
//        simpleU.getViewingPlatform().getViewPlatformTransform().getTransform(original);
        rotate.get(transformMatrix);
        if (logger.isDebugEnabled()) {
            logger.debug("matrix: " + transformMatrix);
        }
//        rotate.rotX(Math.PI/4.0d);
//        rotate.rotY(Math.PI/100.0d);
//        rotateX.rotX(-0.1);
//        rotate.mul(rotateX);
//        Vector3d translation = new Vector3d();
//        original.get(translation);
//        rotate.setTranslation(translation);
//        rotate.get(transformMatrix);
        final Vector3d translate = new Vector3d(0, 0, 15);
        rotate.set(translate);
        if (logger.isDebugEnabled()) {
            logger.debug("matrix: " + transformMatrix);
        }
//        rotate.setTranslation(new Vector3d(0,0,5));
        return rotate;
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
