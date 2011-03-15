/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.example;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
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
public class LightTransformation extends JPanel {

    private final static Logger logger = LoggerFactory.getLogger(LightTransformation.class);
    SimpleUniverse simpleU;
    BranchGroup scene;
    BoundingSphere bigBounds = new BoundingSphere(new Point3d(), 1000);

    public LightTransformation() {
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
        final Vector3d translate = new Vector3d(0,0,0);
        final Transform3D targetTrans = new Transform3D();
        final Transform3D rotation = new Transform3D();
//        rotation.rotZ(15);
        objRotate.getTransform(targetTrans);
        targetTrans.setTranslation(translate);
//        targetTrans.mul(rotation);
//        targetTrans.set(translate);
//        targetTrans.mul(rotation);
        objRotate.setTransform(targetTrans);
//        objRotate.getTransform(targetTrans);
//        targetTrans.set(translate);
//        objRotate.setTransform(targetTrans);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//        scene.addChild(objRotate);
        scene.addChild(objRotate);
        Appearance coneAppearance = new Appearance();
        objRotate.addChild(new com.sun.j3d.utils.geometry.Cone(0.5f,coneLength));
        MouseRotate behavior = new MouseRotate();
        behavior.setTransformGroup(objRotate);
        objRotate.addChild(behavior);
        behavior.setSchedulingBounds(bigBounds);
//                AdvancedOrbitBehavior behavior = new AdvancedOrbitBehavior(canvas3D, scene, AdvancedOrbitBehavior.REVERSE_ALL);
//                behavior.setSchedulingBounds(new BoundingSphere(new Point3d(),1000));
        simpleU = new SimpleUniverse(canvas3D);
        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(transformView());
        setUpLight();
//        simpleU.getViewingPlatform().setViewPlatformBehavior(behavior);
        simpleU.addBranchGraph(scene);        
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:light,raster,universe etc for all examples --> hiwi
    public void setUpLight(){
          DirectionalLight lightD = new DirectionalLight();
    lightD.setDirection(new Vector3f(0.0f, -0.7f, -0.7f));
//    lightD.setDirection(new Vector3f(0.0f, -0.5f, -0.7f));
//      lightD.setDirection(new Vector3f(3.0f, 0.0f, -15.0f));
    lightD.setInfluencingBounds(bigBounds);
    scene.addChild(lightD);

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
        final Vector3d translate = new Vector3d(0,0,10);
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
}
