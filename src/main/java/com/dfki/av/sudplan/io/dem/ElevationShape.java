/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io.dem;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.sun.j3d.utils.image.TextureLoader;
import gov.nasa.worldwind.geom.Sphere;
import java.net.URL;
import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ElevationShape extends Shape3D {

    Appearance landscapeAppearance = new Appearance();
    private final static Logger logger = LoggerFactory.getLogger(ElevationShape.class);

    public ElevationShape(final Geometry geometry) {
        super(geometry);
        landscapeAppearance.setCapability(Appearance.ALLOW_TEXTURE_READ);
        landscapeAppearance.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
        setAppearance(landscapeAppearance);
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: this has to be configurable with a default value        
        PolygonAttributes pa = new PolygonAttributes();

        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:wrong triangulation ??
//    pa.setCullFace(PolygonAttributes.CULL_NONE);
        landscapeAppearance.setPolygonAttributes(pa);
        URL imageURL = this.getClass().getClassLoader().getResource(
                "dem.png");
//    URL imageURL = this.getClass().getClassLoader().getResource(
//            "kl_air.jpg");
//        TextureLoader textureLoader = new TextureLoader(imageURL, ComponentBroker.getInstance().getMainFrame());
////        TexCoordGeneration tcg = new TexCoordGeneration(TexCoordGeneration.OBJECT_LINEAR,
////                                                        TexCoordGeneration.TEXTURE_COORDINATE_2);
////       landscapeAppearance.setTexCoordGeneration(tcg);
//        ImageComponent2D image = textureLoader.getImage();
//        demTexture = new Texture2D(
//                Texture2D.BASE_LEVEL,
//                Texture2D.RGBA,
//                image.getWidth(),
//                image.getHeight());
//        if (logger.isDebugEnabled()) {
//            logger.debug("image.getWidth(): " + image.getWidth());
//            logger.debug("image.getHeight(): " + image.getHeight());
//        }
//        demTexture.setImage(0, image);
//        demTexture.setMagFilter(Texture2D.BASE_LEVEL_LINEAR);
//        demTexture.setMinFilter(Texture2D.BASE_LEVEL_LINEAR);
//        demTexture.setCapability(Texture2D.ALLOW_ENABLE_READ);
//        demTexture.setCapability(Texture2D.ALLOW_ENABLE_WRITE);
//        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:what does this mean ?
//        demTexture.setEnable(true);
//        TextureAttributes attrib = new TextureAttributes();
////        TextureUnitState texture = new TextureUnitState(demTexture, attrib,
////                new TexCoordGeneration(TexCoordGeneration.OBJECT_LINEAR,
////                        TexCoordGeneration.TEXTURE_COORDINATE_2));
////        texture.setCapability(TextureUnitState.ALLOW_STATE_WRITE);
//        Material material = new Material();
//        material.setDiffuseColor(new Color3f(0.2f, 0.2f, 0.2f));
//        material.setShininess(50.0f);
//        Sphere lala = new com.sun.j3d.utils.geometry.Sphere()
//        material.setAmbientColor(new Color3f(0.1f, 0.1f, 0.1f));
        Color3f aColor = new Color3f(0.1f, 0.1f, 0.1f);
        Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f dColor = new Color3f(0.5f, 0.5f, 0.5f);
        Color3f sColor = new Color3f(0.6f, 0.6f, 0.6f);
        Material m = new Material(aColor, eColor, dColor, sColor, 10.0f);
        landscapeAppearance.setMaterial(m);
        TextureAttributes texAttrib = new TextureAttributes();
        texAttrib.setTextureMode(TextureAttributes.MODULATE);
//        texAttrib.setTextureBlendColor(new Color4f(0.6f, 0.6f, 0.6f, 1.0f));
        final Transform3D texTransform = new Transform3D();
        texAttrib.getTextureTransform(texTransform);
        Vector3d translate = new Vector3d();
        if (logger.isDebugEnabled()) {
            logger.debug("translation: " + translate);
        }
        translate.x += 150;
        translate.y += 150;
        if (logger.isDebugEnabled()) {
            logger.debug("translation: " + translate);
        }
        texTransform.set(translate);
        texTransform.setScale(new Vector3d(0.1, 0.26, 0.0));
        texTransform.get(translate);
        texAttrib.setTextureTransform(texTransform);
        landscapeAppearance.setTextureAttributes(texAttrib);
//        landscapeAppearance.setTextureUnitState(new TextureUnitState[]{texture});        
        this.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        this.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        this.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
        this.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
        this.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        this.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        this.addGeometry(geometry);
        this.setAppearance(landscapeAppearance);
    }
//    public void enableTexture(final boolean enabled){
//        if(enabled){
//            landscapeAppearance.setTexture(demTexture);
//        } else {
//            if (logger.isDebugEnabled()) {
//                logger.debug("texture off");
//            }
//            landscapeAppearance.setTexture(null);
//        }
//
//    }
}
