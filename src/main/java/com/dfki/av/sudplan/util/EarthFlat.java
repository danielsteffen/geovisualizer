/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.util;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.ui.vis.VisualisationComponentPanel;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;
import java.net.URL;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.Texture2D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
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
public class EarthFlat {

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this is hardcoded what if the earth is rotated;
    public static final Vector3d EARTH_UP = new Vector3d(0,0,1);
    private static final Logger logger = LoggerFactory.getLogger(VisualisationComponentPanel.class);
    public static final double WGS84_EARTH_EQUATORIAL_RADIUS = 6378137.0; // ellipsoid equatorial getRadius, in meters
    public static final int PLATE_CARREE_PROJECTION = 0;
    public static final BoundingBox EARTH_EXTENDS = new BoundingBox(
            geodeticToCartesian(new Point3d(-180.0, -90.0, 0.0), PLATE_CARREE_PROJECTION),
            geodeticToCartesian(new Point3d(180.0, 90.0, 0.0), PLATE_CARREE_PROJECTION));
    ////ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:often used for epsg 4326;    
    public static final String PLATE_CARREE_NAME = "Plate Carree";
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: remove this 
    public static final double EARTH_OFFSET = -0.06f;
    private Box geometry;

    /* ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: The projection is not good and the height is even worse.
     * Introduce proper coordinate system.
     */
    public static Point3d cartesianToGeodetic(final Point3d postionToTransform, final int projection) {
        checkPoint(postionToTransform);
        final Point3d newPosition = new Point3d();
        if (projection == PLATE_CARREE_PROJECTION) {
            newPosition.set(
                    radiansToDeegree(postionToTransform.getX()) / WGS84_EARTH_EQUATORIAL_RADIUS,
                    radiansToDeegree(postionToTransform.getY()) / WGS84_EARTH_EQUATORIAL_RADIUS,
                    postionToTransform.getZ());
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("No recognised projection. Using default " + PLATE_CARREE_NAME + ".");
            }
            return cartesianToGeodetic(postionToTransform, PLATE_CARREE_PROJECTION);
        }
        return newPosition;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: Same as above.
    public static Point3d geodeticToCartesian(final Point3d postionToTransform, final int projection) {
        checkPoint(postionToTransform);
        final Point3d newPosition = new Point3d();
        if (projection == PLATE_CARREE_PROJECTION) {
            newPosition.set(
                    deegreeToRadians(postionToTransform.getX()) * WGS84_EARTH_EQUATORIAL_RADIUS,
                    deegreeToRadians(postionToTransform.getY()) * WGS84_EARTH_EQUATORIAL_RADIUS,
                    postionToTransform.getZ());
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("No recognised projection. Using default " + PLATE_CARREE_NAME + ".");
            }
            return cartesianToGeodetic(postionToTransform, PLATE_CARREE_PROJECTION);
        }
        return newPosition;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:performance nightmare
    public static Point3f geodeticToCartesian(final Point3f postionToTransform, final int projection) {
        return new Point3f(geodeticToCartesian(new Point3d(postionToTransform), projection));
    }

    private static void checkPoint(final Point3d postionToTransform) {
        if (postionToTransform == null) {
            final String message = "Point is null.";
            if (logger.isErrorEnabled()) {
                logger.error(message);
            }
            throw new IllegalArgumentException(message);
        }
    }

    public static double deegreeToRadians(final double angleInDeegree) {
        return angleInDeegree * (Math.PI / 180.0);
    }

    public static double radiansToDeegree(final double angleRadians) {
        return angleRadians * (180.0 / Math.PI);
    }
    private double scaleFactor = 1.0;

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(final double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public EarthFlat(final double scaleFactor) {
        URL imageURL = this.getClass().getClassLoader().getResource(
                "blue_marble_small.jpg");
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
        worldTexture.setMagFilter(Texture2D.BASE_LEVEL_LINEAR);
        worldTexture.setMinFilter(Texture2D.BASE_LEVEL_LINEAR);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: switchable reference system
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this should be done somewhere else it is a layer only in the inital version
        Appearance worldAppearance = new Appearance();
        worldAppearance.setTexture(worldTexture);
//    PolygonAttributes polyAttributes = new PolygonAttributes();
//    polyAttributes.setCullFace(PolygonAttributes.CULL_NONE);
//    worldAppearance.setPolygonAttributes(polyAttributes);
        worldAppearance.setMaterial(new Material(new Color3f(1.0f, 1.0f, 0.0f),
                new Color3f(0, 0, 0), new Color3f(0.0f, 0.0f, 0.0f),
                new Color3f(0.0f, 0.0f, 0.0f), 100f));
        this.scaleFactor = scaleFactor;
        if (logger.isDebugEnabled()) {
            logger.debug("Earth boundings original: " + EARTH_EXTENDS);
        }
        Transform3D scaling = new Transform3D();
        scaling.setScale(scaleFactor);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:bad design this is actually transforming the bounds not creating a new scaled object.
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: Better to centralise the scaling --> one place then everywhere in the code
        EARTH_EXTENDS.transform(scaling);
        if (logger.isDebugEnabled()) {
            logger.debug("Earth boundings scaled: " + EARTH_EXTENDS);
        }
        final Point3d lower = new Point3d();
        final Point3d upper = new Point3d();
        EARTH_EXTENDS.getLower(lower);
        EARTH_EXTENDS.getUpper(upper);
        if (logger.isDebugEnabled()) {
            logger.debug("distance x: " + (float) (upper.getX() - lower.getX()));
            logger.debug("distance y: " + (float) (upper.getY() - lower.getY()));
        }        
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:extends bounds with distance
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: divided by 2 because the box doubles by default. 
        this.geometry = new Box(
                (float) (((upper.getX() - lower.getX()) / 2)),
                (float) (((upper.getY() - lower.getY()) / 2)),
                (float)EARTH_OFFSET, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, new Appearance());
//        this.geometry = new Box(
//                3.0f,
//                2.0f,
//                0.0f, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, new Appearance());
//        this.geometry = new Box();
        if (logger.isDebugEnabled()) {
            logger.debug("box bounds: " + new BoundingBox(geometry.getBounds()));
            logger.debug("xdim: " + geometry.getXdimension());
        }    
        geometry.setAppearance(Box.FRONT, worldAppearance);
    }

    public Box getGeometry() {
        return geometry;
    }

    public void setGeometry(final Box geometry) {
        this.geometry = geometry;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:central place transformation
    public static Point3d scalePoint3d(final Point3d point){
        point.x *= ComponentBroker.getInstance().getScalingFactor();
        point.y *= ComponentBroker.getInstance().getScalingFactor();
        point.z *= ComponentBroker.getInstance().getScalingFactor();
        return point;
    }
}
