package com.dfki.av.sudplan.javax.swing;

import com.dfki.av.sudplan.j3d.Create;
import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author steffen
 */
public class JPanel3D extends javax.swing.JPanel {

    private String geoFileName;
    private MouseWheelZoom mz;
    private MouseTranslate mt;
    private MouseRotate mr;


    public JPanel3D() throws FileNotFoundException, IOException, URISyntaxException {
        this("test.txt");
    }

    public JPanel3D(String fileName) throws FileNotFoundException, IOException, URISyntaxException {
        super();
        this.geoFileName = fileName;

        initComponents();
    }

    private void initComponents() throws FileNotFoundException, IOException, URISyntaxException {
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);

        BranchGroup scene = createSceneGraph();
        scene.compile();
        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
        simpleU.getViewingPlatform().setNominalViewingTransform();
        simpleU.addBranchGraph(scene);
    }




    public BranchGroup createSceneGraph() throws FileNotFoundException, IOException, URISyntaxException {
        // Create the root of the branchgroup
        BranchGroup objRoot = new BranchGroup();
        Appearance app = new Appearance();
//        app.setColoringAttributes(ca);

        // transform x
        Transform3D verschieben = new Transform3D();
        verschieben.setTranslation(new Vector3f(0.5f, 0.0f, 0.0f));
        TransformGroup verschiebenGroup = new TransformGroup(verschieben);
        // rotation z
        Transform3D drehung = new Transform3D();
        drehung.rotZ(Math.toRadians(-90));
        TransformGroup objDreh = new TransformGroup(drehung);
        Transform3D drehung2 = new Transform3D();
        drehung2.rotY(Math.toRadians(-30));
        TransformGroup objDreh2 = new TransformGroup(drehung2);
        // set up the transformgroup. Enable it so that mouse behaviors code can modify it at runtime.
        TransformGroup objTrans = new TransformGroup();
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
        //Background bg = new Background(new Color3f(1.0f, 1.0f, 1.0f));
        //bg.setApplicationBounds(bounds);
        //objTrans.addChild(bg);

        // set up the mouse rotation behavior
        mr = new MouseRotate();
        mr.setTransformGroup(objTrans);
        mr.setSchedulingBounds(bounds);
        // setFactor(x,y)  0 = disable
        mr.setFactor(0.0, 0.007);
        mr.setEnable(false);
        objTrans.addChild(mr);

        

        // set up the mouse translate
        mt = new MouseTranslate();
        mt.setTransformGroup(objTrans);
        mt.setSchedulingBounds(bounds);
        mt.setFactor(0.0012, 0.0012);
        mt.setEnable(false);
        objTrans.addChild(mt);

        // set up the mouse WHEEL zoom behavior
        mz = new MouseWheelZoom();
        mz.setTransformGroup(objTrans);
        mz.setSchedulingBounds(bounds);
        mz.setFactor(0.05);
        mz.setEnable(true);
        objTrans.addChild(mz);

        // set up the keyboard zoom behavior
        KeyNavigatorBehavior keyNavigatorBehavior = new KeyNavigatorBehavior(objTrans);
        keyNavigatorBehavior.setSchedulingBounds(bounds);
        objTrans.addChild(keyNavigatorBehavior);

        // Set up the ambient light
        Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);
        AmbientLight ambientLightNode = new AmbientLight(ambientColor);
        ambientLightNode.setInfluencingBounds(bounds);
        objRoot.addChild(ambientLightNode);

        // Set up the directional lights
        Color3f light1Color = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f light1Direction = new Vector3f(0.5f, 0.5f, 0.5f);
        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        objRoot.addChild(light1);

        // Parent child
        // Koordinatesystem anlegen
        objRoot.addChild(Create.createKoordSystemY());
        objRoot.addChild(Create.createKoordSystemX());
        objRoot.addChild(objTrans);
        objTrans.addChild(objDreh);
        objDreh.addChild(objDreh2);
        objDreh2.addChild(verschiebenGroup);
        verschiebenGroup.addChild(Create.Dreieck(this.geoFileName));
//        verschiebenGroup.addChild(Create.Points(this.geoFileName));

        return objRoot;
    }
    public void setZoom(boolean enabled)
    {
        mz.setEnable(enabled);
    }

    public void setRotate(boolean enabled)
    {

        mr.setEnable(enabled);
    }

    public void setTranslate(boolean enabled)
    {
        mt.setEnable(enabled);
    }

}
