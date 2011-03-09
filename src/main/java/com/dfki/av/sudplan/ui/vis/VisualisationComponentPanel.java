/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * VisualisationComponentPanel.java
 *
 * Created on 25.01.2011, 12:37:30
 */
package com.dfki.av.sudplan.ui.vis;

import com.dfki.av.sudplan.camera.Camera;
import com.dfki.av.sudplan.camera.Camera2D;
import com.dfki.av.sudplan.camera.SimpleCamera;
import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.util.EarthFlat;
import com.dfki.av.sudplan.vis.control.AdvancedOrbitBehavior;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.behaviors.vp.ViewPlatformBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.net.URL;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Node;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
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
 */
public class VisualisationComponentPanel extends javax.swing.JPanel implements VisualisationComponent {

    private final Logger logger = LoggerFactory.getLogger(VisualisationComponentPanel.class);
    /*ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: Not a perfect solution if the users leaves the sphere no control 
     *  will be possible
     */
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: seems it would be a good idea to define the earth as bounds
    private BoundingSphere behaviourBounding = new BoundingSphere(new Point3d(), 1000000.0);
    private BoundingSphere lightBounds = new BoundingSphere(new Point3d(), 1000000.0);
    private BoundingSphere backgroundBounds = new BoundingSphere(new Point3d(), 1000000.0);
    private SimpleUniverse universe;
    private BranchGroup sceneGraph;
    private final AmbientLight al = new AmbientLight(new Color3f(0.7f, 0.7f, 0.7f));
    DirectionalLight dl;
//    private Vector3d home = new Vector3d(0.0, 0.0, 50.0);
//    private Vector3d home = new Vector3d(16.0, 65.0, 5.0);
    private final Vector3d home = new Vector3d(2007.0, 6609.0, 800.0);
    private final GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
    private final Canvas3D canvas3D = new Canvas3D(config);
    private Camera camera3D;
    private final Canvas3D canvas2D = new Canvas3D(config);
    private Camera2D camera2D;
    private final JPanel panel2d = new JPanel(new BorderLayout());

    /** Creates new form VisualisationComponentPanel */
    public VisualisationComponentPanel() {
        logger.debug("{} Constructor() call", VisualisationComponentPanel.class.toString());
        initComponents();
        createUniverse();
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:does not work
//        setDoubleBuffered(false);
//        canvas3D.setDoubleBufferEnable(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    mainPanel = new javax.swing.JPanel();

    setLayout(new java.awt.BorderLayout());

    mainPanel.setPreferredSize(new java.awt.Dimension(800, 600));
    mainPanel.setLayout(new java.awt.BorderLayout());
    add(mainPanel, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel mainPanel;
  // End of variables declaration//GEN-END:variables

    private BranchGroup createSceneGraph() {
        BranchGroup sceneRoot = new BranchGroup();
        return sceneRoot;
    }

    private void createUniverse() {
        /*ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: clipping should be handeld in general therefore it was set by
         * default to 10
         */
        universe = new SimpleUniverse(canvas3D);
        canvas3D.getView().setBackClipDistance(30000);
        canvas3D.getView().setFrontClipDistance(0.1);
        canvas3D.setPreferredSize(new Dimension(800, 600));
        sceneGraph = createSceneGraph();
        configure2dView();
        canvas2D.getView().setBackClipDistance(30000);
        canvas2D.getView().setFrontClipDistance(0.1);
        canvas2D.setPreferredSize(new Dimension(800, 600));
        mainPanel.add(canvas3D, BorderLayout.CENTER);

        sceneGraph.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        sceneGraph.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        sceneGraph.setCapability(BranchGroup.ALLOW_DETACH);
        setBackground();
        configureLight();
        createWorld();
        createOrthobox();
//    loadASCGeom();
//    loadASCGeom2();
//    initNavigation();

        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this must also be proportional

//        behavior.setMinRadius(1);
//        AdvancedOrbitBehavior.NOMINAL_TRANS_FACTOR = 1.0;                
        final Transform3D viewTransfrom = new Transform3D();
        universe.getViewingPlatform().getViewPlatformTransform().getTransform(viewTransfrom);
        final Vector3d position = new Vector3d();
        viewTransfrom.get(position);
        if (logger.isDebugEnabled()) {
            logger.debug("Viewing Position: " + position);
        }
        camera3D = new SimpleCamera(universe.getViewer(), sceneGraph);
        camera3D.setCameraBounds(behaviourBounding);
        camera3D.addCameraListner(camera2D);
//        camera3D.setCameraPosition(new Point3d(home));
//        behavior.setRotationCenter(new Point3d(position));
//        behavior.setPointOnEarth(universe.getViewingPlatform().get);       
//        universe.getViewingPlatform().setViewPlatformBehavior(camera3D.getViewBehavior());
        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
//    universe.getViewingPlatform().setNominalViewingTransform();        
        universe.addBranchGraph(sceneGraph);
        gotoToHome();
    }

    public void configure2dView() {
        ViewingPlatform vwp;
        vwp = new ViewingPlatform(1);
        vwp.setUniverse(universe);
        Viewer[] viewer = new Viewer[1];
        viewer[0] = new Viewer(canvas2D);
        viewer[0].setViewingPlatform(vwp);
        sceneGraph.addChild(vwp);
//        Transform3D viewTransformation = new Transform3D();
//        viewTransformation.setTranslation(home);
//        vwp.getViewPlatformTransform().setTransform(viewTransformation);
        panel2d.add(canvas2D, BorderLayout.CENTER);
//        final AdvancedOrbitBehavior behavior2d = new AdvancedOrbitBehavior(canvas2D, sceneGraph, OrbitBehavior.REVERSE_ALL | OrbitBehavior.STOP_ZOOM);
//        behavior2d.setProportionalZoom(true);
//        behavior2d.setProportionalTranslate(true);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this must also be proportional
//        behavior2d.setTransFactors(10, 10);
//        behavior.setMinRadius(1);
//        AdvancedOrbitBehavior.NOMINAL_TRANS_FACTOR = 1.0;
//        behavior2d.setRotationCenter(new Point3d(home.x, home.y, 0.0));
//        behavior2d.setInteractionMode(AdvancedOrbitBehavior.TRANSLATE);
//        behavior2d.setSchedulingBounds(behaviourBounding);
//        vwp.setViewPlatformBehavior(behavior2d);
        camera2D = new Camera2D(viewer[0], sceneGraph);
        camera2D.setCameraBounds(behaviourBounding);
//        camera2D.setCameraPosition(new Point3d(home));
    }

    public JPanel getPanel2d() {
        return panel2d;
    }

    private void createWorld() {

//    Box world = new Box(40075016.6856f, 34261226.9711f, 1.0f, Box.GENERATE_NORMALS, worldAppearance);
        final EarthFlat earth = new EarthFlat(ComponentBroker.getInstance().getScalingFactor());
        if (logger.isDebugEnabled()) {
            logger.debug("earth extends: " + earth.EARTH_EXTENDS);
            logger.debug("earth bounds: " + new BoundingBox(earth.getGeometry().getBounds()));
        }
        Transform3D worldTransformation = new Transform3D();
        worldTransformation.setTranslation(new Vector3f(0.0f, 0.0f, -0.08f));
        TransformGroup worldGroup = new TransformGroup(
                worldTransformation);
        worldGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        worldGroup.addChild(earth.getGeometry());
        sceneGraph.addChild(worldGroup);
    }

    private void createOrthobox() {
        final float xOff = 0.0034f;
        final float yOff = 0.00047f;
        final BoundingBox orthoExtends = new BoundingBox(
                //            EarthFlat.geodeticToCartesian(new Point3d(59.29252, 17.998819, 0.0), EarthFlat.PLATE_CARREE_PROJECTION),
                //            EarthFlat.geodeticToCartesian(new Point3d(59.327, 18.144483, 0.0), EarthFlat.PLATE_CARREE_PROJECTION));
                EarthFlat.geodeticToCartesian(new Point3d(18.008895 + xOff, 59.297637 + yOff, 0.0), EarthFlat.PLATE_CARREE_PROJECTION),
                EarthFlat.geodeticToCartesian(new Point3d(18.118415 + xOff, 59.327774 + yOff, 0.0), EarthFlat.PLATE_CARREE_PROJECTION));
//        URL imageURL = this.getClass().getClassLoader().getResource(
//                "sodermalm.png");
        URL imageURL = this.getClass().getClassLoader().getResource(
                "osm_basic.png");
//    URL imageURL = this.getClass().getClassLoader().getResource(
//            "kl_air.jpg");
        TextureLoader textureLoader = new TextureLoader(imageURL, ComponentBroker.getInstance().getMainFrame());
        ImageComponent2D image = textureLoader.getImage();
        Texture2D worldTexture = new Texture2D(
                Texture2D.BASE_LEVEL,
                Texture2D.RGBA,
                image.getWidth(),
                image.getHeight());
        worldTexture.setImage(0, image);
        worldTexture.setEnable(true);
        Appearance worldAppearance = new Appearance();
//          Material material = new Material();
//        material.setDiffuseColor(new Color3f(0.3f, 0.3f, 0.3f));
//        material.setAmbientColor(new Color3f(0.3f, 0.3f, 0.3f));
        TextureAttributes texAttribtues = new TextureAttributes();
//        texAttribtues.setTextureMode(TextureAttributes.DECAL);
//        worldAppearance.setMaterial(material);
        worldAppearance.setTextureAttributes(texAttribtues);
        worldTexture.setMagFilter(Texture2D.BASE_LEVEL_LINEAR);
        worldTexture.setMinFilter(Texture2D.BASE_LEVEL_LINEAR);
        worldAppearance.setTexture(worldTexture);
//        worldAppearance.setMaterial(new Material(new Color3f(1.0f, 1.0f, 0.0f),
//                new Color3f(0, 0, 0), new Color3f(0.0f, 0.0f, 0.0f),
//                new Color3f(0.0f, 0.0f, 0.0f), 100f));

        Transform3D scaling = new Transform3D();

        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:bad design this is actually transforming the bounds not creating a new scaled object.
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: Better to centralise the scaling --> one place then everywhere in the code
        scaling.setScale(ComponentBroker.getInstance().getScalingFactor());
        orthoExtends.transform(scaling);
        if (logger.isDebugEnabled()) {
            logger.debug("Ortho boundings scaled: " + orthoExtends);
        }
        final Point3d lower = new Point3d();
        final Point3d upper = new Point3d();
        orthoExtends.getLower(lower);
        orthoExtends.getUpper(upper);
        if (logger.isDebugEnabled()) {
            logger.debug("distance x: " + (float) (upper.getX() - lower.getX()));
            logger.debug("distance y: " + (float) (upper.getY() - lower.getY()));
        }
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:extends bounds with distance
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: divided by 2 because the box doubles by default.
        final Box geometry = new Box(
                (float) (((upper.getX() - lower.getX()) / 2)),
                (float) (((upper.getY() - lower.getY()) / 2)),
                -0.04f, Box.GENERATE_TEXTURE_COORDS, new Appearance());
//        this.geometry = new Box(
//                3.0f,
//                2.0f,
//                0.0f, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, new Appearance());
//        this.geometry = new Box();
        if (logger.isDebugEnabled()) {
            logger.debug("Ortho box bounds: " + new BoundingBox(geometry.getBounds()));
            logger.debug("Ortho xdim: " + geometry.getXdimension());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Ortho extends: " + orthoExtends);
        }
        geometry.setAppearance(Box.FRONT, worldAppearance);
        Transform3D orthoTransformation = new Transform3D();
        if (logger.isDebugEnabled()) {
            logger.debug("lower x: " + lower.x + " y: " + lower.y);
        }
        double x = lower.x + geometry.getXdimension();
        double y = lower.y + geometry.getYdimension();
        if (logger.isDebugEnabled()) {
            logger.debug("lower x: " + x + " y: " + y);
        }
        orthoTransformation.setTranslation(new Vector3d(x, y, 0.0));
//        orthoTransformation.setTranslation(new Vector3d(lower));
        //        orthoTransformation.setTranslation(new Vector3f((float)lower.x/2,(float)lower.y/2,0.0f));
        TransformGroup orthoGroup = new TransformGroup(
                orthoTransformation);
        orthoGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        orthoGroup.addChild(geometry);
        sceneGraph.addChild(orthoGroup);
        if (logger.isDebugEnabled()) {
            logger.debug("Postion: " + getPositionOfObject(geometry));
        }
        final Point3f point1 = EarthFlat.geodeticToCartesian(new Point3f(18.028919047425f, 59.3046022310815f, 0.0f), EarthFlat.PLATE_CARREE_PROJECTION);
        final Point3f point2 = EarthFlat.geodeticToCartesian(new Point3f(18.028919047425f, 59.3046022310815f, 0.0f), EarthFlat.PLATE_CARREE_PROJECTION);
        scalePoint(point1, ComponentBroker.getInstance().getScalingFactor());
        scalePoint(point2, ComponentBroker.getInstance().getScalingFactor());
        if (logger.isDebugEnabled()) {
            logger.debug("Building Postion1 : " + point1);
            logger.debug("Building Postion2 : " + point2);
        }
        final Point3f point3 = EarthFlat.geodeticToCartesian(new Point3f(18.027139322596f, 59.302444439500f, 0.0f), EarthFlat.PLATE_CARREE_PROJECTION);
        if (logger.isDebugEnabled()) {
            logger.debug("DEM Postion3 unscaled: " + point3);
            scalePoint(point3, ComponentBroker.getInstance().getScalingFactor());
            logger.debug("DEM Postion3 scaled: " + point3);
        }
        final Point3f point4 = EarthFlat.geodeticToCartesian(new Point3f(17.998819f, 59.29252f, 0.0f), EarthFlat.PLATE_CARREE_PROJECTION);
        if (logger.isDebugEnabled()) {
            logger.debug("Ortho Postion4 unscaled: " + point4);
            scalePoint(point4, ComponentBroker.getInstance().getScalingFactor());
            logger.debug("Ortho Postion4 scaled: " + point4);
        }
        final Point3f point5 = EarthFlat.geodeticToCartesian(new Point3f(0.000080294914256f, 0.0f, 0.0f), EarthFlat.PLATE_CARREE_PROJECTION);
        if (logger.isDebugEnabled()) {
            logger.debug("Cellsize unscaled: " + point5);
            scalePoint(point5, ComponentBroker.getInstance().getScalingFactor());
            logger.debug("Cellsize scaled: " + point5);
        }
    }

    private Point3d getPositionOfObject(final Node node) {
        final Point3d position = new Point3d(0.0, 0.0, 0.0);
        Transform3D transformPos = new Transform3D();
        node.getLocalToVworld(transformPos);
        Vector3d translation = new Vector3d();
        transformPos.get(translation);
        if (logger.isDebugEnabled()) {
            logger.debug("transform: " + translation);
        }
        transformPos.transform(position);
        return position;
    }

    private Point3f scalePoint(final Point3f point, final double scalingFactor) {
        point.x *= scalingFactor;
        point.y *= scalingFactor;
        point.z *= scalingFactor;
        return point;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:directed light does not shine to soedermalm destination
    private void configureLight() {
//        final TransformGroup lightTG = new TransformGroup();
//        final Transform3D lightT = new Transform3D();
//        final Vector3d translate = new Vector3d(home.x,home.y,home.z+500);
//        lightTG.getTransform(lightT);
//        lightT.setTranslation(translate);
//        lightTG.setTransform(lightT);
        al.setInfluencingBounds(lightBounds);
        al.setCapability(AmbientLight.ALLOW_STATE_WRITE);
        al.setCapability(AmbientLight.ALLOW_STATE_READ);
        sceneGraph.addChild(al);

//        Color3f light1Color = new Color3f(0.5f, 0.5f, 0.5f);
        Vector3f light1Direction = new Vector3f(2011.058f, 6603.77f, -10.0f);
//        dl = new DirectionalLight(light1Color, light1Direction);
        dl = new DirectionalLight();
        dl.setDirection(light1Direction);
        dl.setCapability(DirectionalLight.ALLOW_STATE_WRITE);
        dl.setCapability(DirectionalLight.ALLOW_STATE_READ);
        dl.setInfluencingBounds(lightBounds);
        sceneGraph.addChild(dl);
//        sceneGraph.addChild(lightTG);
    }

    private void setBackground() {
        Background background = new Background(0.50f, 0.85f, 0.98f);
        background.setApplicationBounds(backgroundBounds);
        sceneGraph.addChild(background);
    }

//    private void loadASCGeom() {
//    try {
//      InputStream input = this.getClass().getClassLoader().getResourceAsStream("sodermalm_5m.asc");
////    InputStream input = this.getClass().getClassLoader().getResourceAsStream("basedem.asc");
//      InputStreamReader isr = new InputStreamReader(new BufferedInputStream(input));
//      ArcGridParser loader = new ArcGridParser(isr);
//      GeometryInfo geoInfo = loader.parseArcGrid();
//      logger.debug("coordinates length: "+geoInfo.getCoordinates().length);
//      geoInfo.compact();
//      NormalGenerator nGenerator = new NormalGenerator();
//      nGenerator.generateNormals(geoInfo);
//      Stripifier stripifier = new Stripifier();
//      stripifier.stripify(geoInfo);
//      Appearance landscapeAppearance = new Appearance();
//      PolygonAttributes pa = new PolygonAttributes();
//      pa.setCullFace(PolygonAttributes.CULL_NONE);
//      landscapeAppearance.setPolygonAttributes(pa);
//      Shape3D landscape = new Shape3D();
//      landscape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
//      landscape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
//      landscape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
//      landscape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
//      landscape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
//      landscape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
//      landscape.addGeometry(geoInfo.getIndexedGeometryArray());
//      landscape.setAppearance(landscapeAppearance);
//      sceneGraph.addChild(landscape);
//    } catch (Exception ex) {
//      logger.error("Error while building geometry: ", ex);
//    }
//    }
//  private void loadASCGeom2() {
//    DEMLoader loader = new DEMLoader();
//    try {
//      Scene loadedScene = loader.load(new BufferedReader(
    //new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("sodermalm_5m.asc"))));
////      Scene loadedScene = loader.load(new BufferedReader(
//    new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("basedem.asc"))));
//      logger.debug("loadedScene"+loadedScene);
//      sceneGraph.addChild(loadedScene.getSceneGroup());
//    } catch (Exception ex) {
//      logger.error("Error while loading Asc geometry: ",ex);
//    }
//  }
    @Override
    public Component getDnDComponent() {
        return canvas3D;
    }

    @Override
    public void addContent(final Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof Scene) {
            sceneGraph.addChild(((Scene) object).getSceneGroup());
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("Not possible to add content unkown type: " + object.getClass());
            }
        }
        if (logger.isDebugEnabled()) {
//            logger.debug("First child: " + scene.getSceneGroup().getChild(0));
//            logger.debug("scene : " + sceneGraph.isLive());
//            Shape3D test = new Shape3D();
//            logger.debug("Position of first child: " + getPositionOfObject(scene.getSceneGroup().getChild(0)));
        }
    }

    @Override
    public void removContent(final Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof Scene) {
            sceneGraph.removeChild(((Scene) object).getSceneGroup());
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("Not possible to add content unkown type: " + object.getClass());
            }
        }
    }

    @Override
    public void enableDirectedLight(boolean enabled) {
        dl.setEnable(enabled);
    }

//    @Override
//    public void gotoBoundingBox(BoundingBox boundingBox) {
//        if (logger.isDebugEnabled()) {
//            logger.debug("Goto BoundingBox: "+boundingBox);
//        }
//        if(boundingBox != null && !boundingBox.isEmpty()){
//            final Point3d lower = new Point3d();
//            final Point3d upper = new Point3d();
//            boundingBox.getLower(lower);
//            boundingBox.getUpper(upper);
//            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:not fixed but so that all is seen
//            gotoPoint(new Point3d(lower.x+((upper.x-lower.x)/2),lower.y+((upper.y-lower.y)/2),20));
//        }
//    }
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:duplicated code
    @Override
    public void setModeZoom() {
        final ViewPlatformBehavior viewBehavior = camera3D.getViewingPlatform().getViewPlatformBehavior();
        if (viewBehavior != null && viewBehavior instanceof AdvancedOrbitBehavior) {
            final AdvancedOrbitBehavior behavior = (AdvancedOrbitBehavior) viewBehavior;
            behavior.setInteractionMode(2);//behavior.ZOOM);
        }
    }

    @Override
    public void setModePan() {
        final ViewPlatformBehavior viewBehavior = camera3D.getViewingPlatform().getViewPlatformBehavior();
        if (viewBehavior != null && viewBehavior instanceof AdvancedOrbitBehavior) {
            final AdvancedOrbitBehavior behavior = (AdvancedOrbitBehavior) viewBehavior;
            behavior.setInteractionMode(1);//behavior.TRANSLATE);
        }
    }

    @Override
    public void setModeRotate() {
        final ViewPlatformBehavior viewBehavior = camera3D.getViewingPlatform().getViewPlatformBehavior();
        if (viewBehavior != null && viewBehavior instanceof AdvancedOrbitBehavior) {
            final AdvancedOrbitBehavior behavior = (AdvancedOrbitBehavior) viewBehavior;
            behavior.setInteractionMode(0);//behavior.ROTATE);
        }
    }

    @Override
    public void setModeCombined() {
        final ViewPlatformBehavior viewBehavior = camera3D.getViewingPlatform().getViewPlatformBehavior();
        if (viewBehavior != null && viewBehavior instanceof AdvancedOrbitBehavior) {
            final AdvancedOrbitBehavior behavior = (AdvancedOrbitBehavior) viewBehavior;
            behavior.setInteractionMode(3);//behavior.COMBINED);
        }
    }

    @Override
    public Camera get2dCamera() {
        return camera2D;
    }

    @Override
    public Camera get3dCamera() {
        return camera3D;
    }

    public void gotoToHome() {
        camera3D.setCameraPosition(new Point3d(home));
    }
}
