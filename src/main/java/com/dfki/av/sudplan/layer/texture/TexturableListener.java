/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.layer.texture;

import javax.media.j3d.Texture;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface TexturableListener {
    public void textureAdded(final Object source, final Texture addedTexture);
    public void textureRemoved(final Object source, final Texture removedTexture);
}
