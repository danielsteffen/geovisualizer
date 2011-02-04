/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.layer;

import com.sun.j3d.loaders.Scene;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ElevationLayer extends AbstractLayer {

    private Scene dataObject;

    public ElevationLayer(final Scene dataObject) {
        this.dataObject = dataObject;
    }

    @Override
    public Scene getDataObject() {
        return dataObject;
    }

    @Override
    public void setDataObject(final Scene dataObject) {
        this.dataObject = dataObject;
    }
}
