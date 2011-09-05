/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.layer.texture;

import java.util.List;
import javax.media.j3d.Texture;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface Texturable {

    public void addTextureProvider(final TextureProvider provider);

    public void removeTextureProvider(final TextureProvider provider);

    public TextureProvider getTextureProvider(final Texture texture);

//    public void addTexture(final Texture textureToAdd);
//
//    public void replaceTexture(final int index);
//
//    public void removeTexture(final Texture textureToRemove);
//
//    public void removeTexture(final int index);

    public List<Texture> getTextures();

    public boolean isTextureVisible(final Texture texture);

    public void setTextureVisible(final int index, final boolean isVisible);

    public void setTextureVisible(final Texture texture, final boolean isVisible);
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:advanced functionallity only partly texture;

    public boolean isTextureIntersecting(final Texture textureToTest);

    public void addTextureListener(final TexturableListener listener);

    public void reomveTextureListener(final TexturableListener listener);
}
