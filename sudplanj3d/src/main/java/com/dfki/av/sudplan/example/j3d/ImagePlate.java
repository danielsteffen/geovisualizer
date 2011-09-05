/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.example.j3d;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.geometry.Triangulator;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
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
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
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
public class ImagePlate implements MouseListener {

    private final static Logger logger = LoggerFactory.getLogger(ImagePlate.class);
    BoundingSphere bigBounds = new BoundingSphere(new Point3d(), 1000);
    private GeometryInfo gi;

    float[] createCoordinateData() {
        float[] data = new float[48];         // ******
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

        data[i++] = 0f;
        data[i++] = 0f;
        data[i++] = 0f; //1
        data[i++] = 1f;
        data[i++] = 0f;
        data[i++] = 0f; //2
        data[i++] = 1f;
        data[i++] = 1f;
        data[i++] = 0f; //3
        data[i++] = 0f;
        data[i++] = 1f;
        data[i++] = 0f; //3
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
        int[] stripCount = {3, 3, 3, 3, 4};  // ******
        for (int i = 0; i < stripCount.length; i++) {
            System.out.println("stripCount[" + i + "] = " + stripCount[i]);
            total += stripCount[i];
        }

        if (total != coordinateData.length / 3) {
            System.out.println("  coordinateData vertex count: " + coordinateData.length / 3);
            System.out.println("stripCount total vertex count: " + total);
        }

        gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gi.setCoordinates(coordinateData);
        gi.setStripCounts(stripCount);
        printGeometry(gi);
        Triangulator tr = new Triangulator();
//        Triangulator tr = new Triangulator(GeometryInfo);
        System.out.println("begin triangulation");
        tr.triangulate(gi);
        System.out.println("  END triangulation");
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

//        Alpha rotationAlpha = new Alpha(-1, 16000);
//
//        RotationInterpolator rotator =
//                new RotationInterpolator(rotationAlpha, objSpin);

        // a bounding sphere specifies a region a behavior is active
        // create a sphere centered at the origin with radius of 1
//    BoundingSphere bounds = new BoundingSphere();
//        rotator.setSchedulingBounds(bigBounds);
//        objSpin.addChild(rotator);

        DirectionalLight lightD = new DirectionalLight();
        lightD.setDirection(new Vector3f(0.0f, -0.7f, -0.7f));
        lightD.setInfluencingBounds(bigBounds);
        contentRoot.addChild(lightD);

        AmbientLight lightA = new AmbientLight();
        lightA.setInfluencingBounds(bigBounds);
        contentRoot.addChild(lightA);

        Background background = new Background();
        background.setColor(1.0f, 1.0f, 1.0f);
        background.setApplicationBounds(bigBounds);
        contentRoot.addChild(background);

        // Let Java 3D perform optimizations on this scene graph.
        // contentRoot.compile();

        return contentRoot;
    } // end of CreateSceneGraph method of MobiusApp
    Canvas3D canvas3D;
    SimpleUniverse simpleU;
    BranchGroup scene = new BranchGroup();

    ;

    // Create a simple scene and attach it to the virtual universe
    public JPanel createUniverse() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        GraphicsConfiguration config =
                SimpleUniverse.getPreferredConfiguration();

        canvas3D = new Canvas3D(config);
        canvas3D.addMouseListener(this);
        JPanel mainPanel = new JPanel(new BorderLayout());
        JButton testButton = new JButton("Draw Image Plate");
        testButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                drawImagePlate();
            }
        });
        mainPanel.add(testButton, BorderLayout.NORTH);
        mainPanel.add(canvas3D, BorderLayout.CENTER);
        panel.add(mainPanel, BorderLayout.CENTER);
        scene.addChild(createSceneGraph(false));

        OrbitBehavior behaviour = new OrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL);
        // SimpleUniverse is a Convenience Utility class
        simpleU = new SimpleUniverse(canvas3D);
        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
//        simpleU.getViewingPlatform().setNominalViewingTransform();
        Transform3D t3dTemp = new Transform3D();
//	    double viewDistance = 1.0/Math.tan(fieldOfView/2.0);
        t3dTemp.set(new Vector3d(0.0, 0.0, 3.0));
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:look at does not function as expected.
        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(t3dTemp);
        final TransformGroup vpTG = simpleU.getViewingPlatform().getViewPlatformTransform();
        if (logger.isDebugEnabled()) {
            logger.debug("TransformGroup: " + vpTG);
        }
        final Transform3D t3d = new Transform3D();
        vpTG.getTransform(t3d);
        if (logger.isDebugEnabled()) {
            logger.debug("Transformation: " + t3d);
        }
//        t3d.lookAt(new Point3d(0.1, 0.0, 5), new Point3d(0, 0, 0), new Vector3d(0, 0, 1));
        if (logger.isDebugEnabled()) {
            logger.debug("Transformation: " + t3d);
        }
//        t3d.invert();
        vpTG.setTransform(t3d);

        behaviour.setSchedulingBounds(bigBounds);
        simpleU.getViewingPlatform().setViewPlatformBehavior(behaviour);
        createAxis();
        simpleU.addBranchGraph(scene);
        return panel;
    } // end of GeomInfoApp constructor
    //  The following allows this to be run as an application
    //  as well as an applet

    public void createAxis() {
        Shape3D axisShape3D = new Shape3D();

        // create line for X axis
        LineArray axisXLines = new LineArray(2, LineArray.COORDINATES);
        axisShape3D.removeGeometry(0);
        axisShape3D.addGeometry(axisXLines);

        axisXLines.setCoordinate(0, new Point3f(-1.0f, 0.0f, 0.0f));
        axisXLines.setCoordinate(1, new Point3f(1.0f, 0.0f, 0.0f));

        Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
        Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
        Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);

        // create line for Y axis
        LineArray axisYLines = new LineArray(2,
                LineArray.COORDINATES | LineArray.COLOR_3);
        axisShape3D.addGeometry(axisYLines);

        axisYLines.setCoordinate(0, new Point3f(0.0f, -1.0f, 0.0f));
        axisYLines.setCoordinate(1, new Point3f(0.0f, 1.0f, 0.0f));

        axisYLines.setColor(0, green);
        axisYLines.setColor(1, blue);

        // create line for Z axis
        Point3f z1 = new Point3f(0.0f, 0.0f, -1.0f);
        Point3f z2 = new Point3f(0.0f, 0.0f, 1.0f);

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

    public void printGeometry(final GeometryInfo geoInfo) {
        logger.debug("Primitive: " + geoInfo.getPrimitive());
        logger.debug("Stripcount: " + geoInfo.getStripCounts());
        logger.debug("Coordinates: " + Arrays.deepToString(geoInfo.getCoordinates()));
        logger.debug("ToString: " + geoInfo.toString());
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        final TransformGroup vpTG = simpleU.getViewingPlatform().getViewPlatformTransform();
        final Transform3D t3d = new Transform3D();
        vpTG.getTransform(t3d);
        final Point3d mouse_pos = new Point3d();
        final int mouseX = e.getX();
        final int mouseY = e.getY();
        if (logger.isDebugEnabled()) {
            logger.debug("mouseX: " + mouseX + " mouseY: " + mouseY);
        }
        canvas3D.getPixelLocationInImagePlate(mouseX, mouseY, mouse_pos);
        if (logger.isDebugEnabled()) {
            logger.debug("point on image plate" + mouse_pos);

        }
        Transform3D motionToWorld = new Transform3D();
        canvas3D.getImagePlateToVworld(motionToWorld);
        motionToWorld.transform(mouse_pos);
        if (logger.isDebugEnabled()) {
            logger.debug("point in world" + mouse_pos);
        }
        Point3d centerEyePt = new Point3d();
        canvas3D.getCenterEyeInImagePlate(centerEyePt);
        motionToWorld.transform(centerEyePt);
        if (logger.isDebugEnabled()) {
            logger.debug("center eye" + centerEyePt);
        }
        mouse_pos.sub(centerEyePt);
        if (logger.isDebugEnabled()) {
            logger.debug("combined" + mouse_pos);
        }
//        new PickTool(null).set
//        t3d.lookAt(new Point3d(0,0, 20),new Point3d(), new Vector3d(0,0,1));
//        t3d.invert();
//        vpTG.setTransform(t3d);
        if (logger.isDebugEnabled()) {
            logger.debug("Viewing Platform TransformGroup: " + t3d);
        }
    }

    private void drawImagePlate() {
        final int imagePlateXMax = canvas3D.getWidth();
        final int imagePlateYMax = canvas3D.getHeight();
        Transform3D motionToWorld = new Transform3D();
        canvas3D.getImagePlateToVworld(motionToWorld);
        Point3d centerEyePt = new Point3d();
        if (logger.isDebugEnabled()) {
            logger.debug("image plate width/height: " + imagePlateXMax + "/" + imagePlateYMax);
        }
//        for (int currentY = 0; currentY <= imagePlateYMax; currentY++) {
//            for (int currentX = 0; currentX <= imagePlateXMax; currentX++) {
//                final Point3d currentPoint = new Point3d();
//                canvas3D.getPixelLocationInImagePlate(currentX, currentY, currentPoint);
//            }
//        }
        final Point3d ll = new Point3d();
        canvas3D.getCenterEyeInImagePlate(centerEyePt);
        canvas3D.getPixelLocationInImagePlate(0, imagePlateYMax, ll);
        if (logger.isDebugEnabled()) {
            logger.debug("ll: " + ll);
            logger.debug("centerEye: " + centerEyePt);
        }
        motionToWorld.transform(centerEyePt);
        motionToWorld.transform(ll);
        if (logger.isDebugEnabled()) {
            logger.debug("ll: " + ll);
            logger.debug("centerEye: " + centerEyePt);
        }
        double alpha = 0.0;
        if (ll.z != centerEyePt.z) {
            alpha = centerEyePt.z / (ll.z - centerEyePt.z);
        }

        Point3d planePt = new Point3d(centerEyePt.x - alpha * (ll.x
                - centerEyePt.x), centerEyePt.y - alpha * (ll.y
                - centerEyePt.y), 0.0);
        if (logger.isDebugEnabled()) {
            logger.debug("calculated point: " + planePt);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
    //not working
//      @Override
//    public void mouseClicked(final MouseEvent e) {
//        final TransformGroup vpTG = simpleU.getViewingPlatform().getViewPlatformTransform();
//        final Transform3D t3d = new Transform3D();
//        vpTG.getTransform(t3d);
//        final Point3d mouse_pos = new Point3d();
//        final int mouseX = e.getX();
//        final int mouseY = e.getY();
//        if (logger.isDebugEnabled()) {
//            logger.debug("mouseX: " + mouseX + " mouseY: " + mouseY);
//        }
//        canvas3D.getPixelLocationInImagePlate(mouseX, mouseY, mouse_pos);
//        if (logger.isDebugEnabled()) {
//            logger.debug("point on image plate" + mouse_pos);
//
//        }
//        Transform3D motionToWorld = new Transform3D();
//        canvas3D.getImagePlateToVworld(motionToWorld);
//        motionToWorld.transform(mouse_pos);
//        if (logger.isDebugEnabled()) {
//            logger.debug("point in world" + mouse_pos);
//
//        }
////        t3d.lookAt(new Point3d(0,0, 20),new Point3d(), new Vector3d(0,0,1));
////        t3d.invert();
////        vpTG.setTransform(t3d);
//        if (logger.isDebugEnabled()) {
//            logger.debug("Viewing Platform TransformGroup: " + t3d);
//        }
//
////        final int imagePlateXMax = canvas3D.getWidth();
////        final int imagePlateYMax = canvas3D.getHeight();
////        Transform3D motionToWorld = new Transform3D();
////        canvas3D.getImagePlateToVworld(motionToWorld);
//        Point3d centerEyePt = new Point3d();
////        if (logger.isDebugEnabled()) {
////            logger.debug("image plate width/height: " + imagePlateXMax + "/" + imagePlateYMax);
////        }
////        for (int currentY = 0; currentY <= imagePlateYMax; currentY++) {
////            for (int currentX = 0; currentX <= imagePlateXMax; currentX++) {
////                final Point3d currentPoint = new Point3d();
////                canvas3D.getPixelLocationInImagePlate(currentX, currentY, currentPoint);
////            }
////        }
//        final Point3d ll = new Point3d();
//        canvas3D.getCenterEyeInImagePlate(centerEyePt);
////        canvas3D.getPixelLocationInImagePlate(0, imagePlateYMax, ll);
//        if (logger.isDebugEnabled()) {
//            logger.debug("ll: " + ll);
//            logger.debug("centerEye: " + centerEyePt);
//        }
//        motionToWorld.transform(centerEyePt);
//        motionToWorld.transform(ll);
//        if (logger.isDebugEnabled()) {
//            logger.debug("ll: " + ll);
//            logger.debug("centerEye: " + centerEyePt);
//        }
//        double alpha = 0.0;
//        if (mouse_pos.z != centerEyePt.z) {
//            alpha = centerEyePt.z / (mouse_pos.z - centerEyePt.z);
//        }
//
//        Point3d planePt = new Point3d(centerEyePt.x - alpha * (mouse_pos.x
//                - centerEyePt.x), centerEyePt.y - alpha * (mouse_pos.y
//                - centerEyePt.y), 0.0);
//        if (logger.isDebugEnabled()) {
//            logger.debug("calculated point: "+planePt);
//        }
//
//    }
//    private void drawImagePlate() {
//        final int imagePlateXMax = canvas3D.getWidth();
//        final int imagePlateYMax = canvas3D.getHeight();
//        if (logger.isDebugEnabled()) {
//            logger.debug("image plate width/height: " + imagePlateXMax + "/" + imagePlateYMax);
//        }
////        for (int currentY = 0; currentY <= imagePlateYMax; currentY++) {
////            for (int currentX = 0; currentX <= imagePlateXMax; currentX++) {
////                final Point3d currentPoint = new Point3d();
////                canvas3D.getPixelLocationInImagePlate(currentX, currentY, currentPoint);
////            }
////        }
//        final Point3d ll = new Point3d();
//        final Point3d ul = new Point3d();
//        final Point3d lr = new Point3d();
//        final Point3d ur = new Point3d();
//        canvas3D.getPixelLocationInImagePlate(0,imagePlateYMax , ll);
//        canvas3D.getPixelLocationInImagePlate(0,0 , ul);
//        canvas3D.getPixelLocationInImagePlate(imagePlateXMax, imagePlateYMax, lr);
//        canvas3D.getPixelLocationInImagePlate(imagePlateXMax, 0, ur);
//        if (logger.isDebugEnabled()) {
//            logger.debug("ll: "+ll);
//            logger.debug("ul: "+ul);
//            logger.debug("ur: "+ur);
//            logger.debug("lr: "+lr);
//        }
//        Transform3D motionToWorld = new Transform3D();
//        canvas3D.getImagePlateToVworld(motionToWorld);
//        motionToWorld.transform(ll);
//        motionToWorld = new Transform3D();
//        canvas3D.getImagePlateToVworld(motionToWorld);
//        motionToWorld.transform(ul);
//        motionToWorld = new Transform3D();
//        canvas3D.getImagePlateToVworld(motionToWorld);
//        motionToWorld.transform(ur);
//        motionToWorld = new Transform3D();
//        canvas3D.getImagePlateToVworld(motionToWorld);
//        motionToWorld.transform(lr);
//        if (logger.isDebugEnabled()) {
//            logger.debug("ll: "+ll);
//            logger.debug("ul: "+ul);
//            logger.debug("ur: "+ur);
//            logger.debug("lr: "+lr);
//        }
//        Point3d[] data = new Point3d[]{ll,ul,ur,lr};
//        gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
//        gi.setCoordinates(data);
//    }
//    private void drawImagePlate() {
//        final int imagePlateXMax = canvas3D.getWidth();
//        final int imagePlateYMax = canvas3D.getHeight();
//        if (logger.isDebugEnabled()) {
//            logger.debug("image plate width/height: " + imagePlateXMax + "/" + imagePlateYMax);
//        }
////        for (int currentY = 0; currentY <= imagePlateYMax; currentY++) {
////            for (int currentX = 0; currentX <= imagePlateXMax; currentX++) {
////                final Point3d currentPoint = new Point3d();
////                canvas3D.getPixelLocationInImagePlate(currentX, currentY, currentPoint);
////            }
////        }
//        final Point3d ll = new Point3d();
//        final Point3d ul = new Point3d();
//        final Point3d lr = new Point3d();
//        final Point3d ur = new Point3d();
//        canvas3D.getPixelLocationInImagePlate(0,imagePlateYMax , ll);
//        canvas3D.getPixelLocationInImagePlate(0,0 , ul);
//        canvas3D.getPixelLocationInImagePlate(imagePlateXMax, imagePlateYMax, lr);
//        canvas3D.getPixelLocationInImagePlate(imagePlateXMax, 0, ur);
//        if (logger.isDebugEnabled()) {
//            logger.debug("ll: "+ll);
//            logger.debug("ul: "+ul);
//            logger.debug("ur: "+ur);
//            logger.debug("lr: "+lr);
//        }
//        Transform3D motionToWorld = new Transform3D();
//        canvas3D.getImagePlateToVworld(motionToWorld);
//        motionToWorld.transform(ll);
//        motionToWorld = new Transform3D();
//        canvas3D.getImagePlateToVworld(motionToWorld);
//        motionToWorld.transform(ul);
//        motionToWorld = new Transform3D();
//        canvas3D.getImagePlateToVworld(motionToWorld);
//        motionToWorld.transform(ur);
//        motionToWorld = new Transform3D();
//        canvas3D.getImagePlateToVworld(motionToWorld);
//        motionToWorld.transform(lr);
//        if (logger.isDebugEnabled()) {
//            logger.debug("ll: "+ll);
//            logger.debug("ul: "+ul);
//            logger.debug("ur: "+ur);
//            logger.debug("lr: "+lr);
//        }
//        Point3d[] data = new Point3d[]{ll,ul,ur,lr};
//        gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
//        gi.setCoordinates(data);
//    }
}
