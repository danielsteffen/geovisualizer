/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.layer;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface LayerStateListener {

    public void layerAdded(final Layer addedLayer);
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:idefication for event (Timestamp --> therefore use events instead of methods)
    public void layerNotAdded(LayerStateEvent event);
    public void layerRemoved(final Layer addedLayer);
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:nothing done event for example layer already added vs. exception
}
