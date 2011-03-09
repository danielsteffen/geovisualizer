/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.camera;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface TransformationListener {
    public void translated(final TransformationEvent transEvent);
    public void rotated(final TransformationEvent transEvent);
}
