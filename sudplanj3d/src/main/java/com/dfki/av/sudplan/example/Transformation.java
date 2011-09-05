/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.example;

import com.dfki.av.sudplan.vis.control.AdvancedOrbitBehavior;
import com.dfki.av.sudplan.vis.control.CameraBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class Transformation extends JPanel {

    private final static Logger logger = LoggerFactory.getLogger(Transformation.class);
    SimpleUniverse simpleU;

    public Transformation() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config =
                SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);

        // Create the root of the branch graph
        final BranchGroup scene = new BranchGroup();

//        TransformGroup objRotate = new TransformGroup(transformObject());
        TransformGroup objRotate = new TransformGroup();
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        scene.addChild(objRotate);
        objRotate.addChild(new ColorCube(0.4));
        MouseRotate behavior = new MouseRotate();
        behavior.setTransformGroup(objRotate);
        objRotate.addChild(behavior);
        behavior.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
//        scene.addChild(new ColorCube(0.4));
        // SimpleUniverse is a Convenience Utility class
        simpleU = new SimpleUniverse(canvas3D);
        CameraBehavior cameraBehavior = new CameraBehavior(canvas3D, scene, AdvancedOrbitBehavior.REVERSE_ALL);
//        AdvancedOrbitBehavior behavior = new AdvancedOrbitBehavior(canvas3D, scene, AdvancedOrbitBehavior.REVERSE_ALL);
        cameraBehavior.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
//        behavior.setSchedulingBounds(new BoundingSphere(new Point3d(),1000));

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        simpleU.getViewingPlatform().setNominalViewingTransform();
//        simpleU.getViewingPlatform().setViewPlatformBehavior(cameraBehavior);
//        simpleU.getViewingPlatform().setViewPlatformBehavior(behavior);
//        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(transformView());
//        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(transformView());
//        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(transformView());
        simpleU.addBranchGraph(scene);
    } // end of HelloJava3Da (constructor)

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
        rotateX.rotX(-0.1);
        rotate.mul(rotateX);
//        Vector3d translation = new Vector3d();
//        original.get(translation);
//        rotate.setTranslation(translation);
        rotate.get(transformMatrix);
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
