/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.io.texture;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.io.AbstractFileLoader;
import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:difference to other loader problem this are all loaders ?? different Interface etc.
public class GeographicTextureLoader extends AbstractFileLoader{
    private final static Logger logger = LoggerFactory.getLogger(GeographicTextureLoader.class);
    GeographicTexture texture = null;

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: loader Exception Handling
    @Override
    protected Object loadImpl() throws Exception {
//        TextureLoader textureLoader = new TextureLoader(file.toURI().toURL(), ComponentBroker.getInstance().getMainFrame());
//         final ImageComponent2D image = textureLoader.getImage();
            texture = GeographicTexture.createGeographicTexture(file);
//            if (logger.isDebugEnabled()) {
//                logger.debug("image.getWidth(): " + image.getWidth());
//                logger.debug("image.getHeight(): " + image.getHeight());
//            }
//            texture.setImage(0, image);
            texture.setMagFilter(Texture2D.BASE_LEVEL_LINEAR);
            texture.setMinFilter(Texture2D.BASE_LEVEL_LINEAR);
            texture.setCapability(Texture2D.ALLOW_ENABLE_READ);
            texture.setCapability(Texture2D.ALLOW_ENABLE_WRITE);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:what does this mean ?
//        texture.setEnable(true);
            TextureAttributes attrib = new TextureAttributes();
            attrib.setTextureMode(TextureAttributes.BLEND);

            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this has to be done in the shape
//            landscapeAppearance.setTextureAttributes(attrib);
//            landscapeAppearance.setTexture(demTexture);
        return texture;
    }


}
