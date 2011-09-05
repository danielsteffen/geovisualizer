/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.layer.texture;

import com.dfki.av.sudplan.layer.Layer;
import javax.media.j3d.Texture;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface TextureProvider extends Layer{
    public Texture getTexture();
}
