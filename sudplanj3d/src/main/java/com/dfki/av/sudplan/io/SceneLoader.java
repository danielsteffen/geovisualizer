/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.io;

import com.sun.j3d.loaders.Scene;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface SceneLoader {
    public Scene load(final Object source) throws LoadingNotPossibleException;
}
