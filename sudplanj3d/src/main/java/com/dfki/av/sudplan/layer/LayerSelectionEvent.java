/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.layer;

import java.util.ArrayList;
import java.util.EventObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class LayerSelectionEvent extends EventObject{
    private final static Logger logger = LoggerFactory.getLogger(LayerSelectionEvent.class);

    private boolean layersSelected;
    private ArrayList<Layer> selectedLayer;

    public LayerSelectionEvent(Object source) {
        super(source);        
    }

    public ArrayList<Layer> getSelectedLayer() {
        return selectedLayer;
    }

    public boolean isLayersSelected() {
        return layersSelected;
    }

    public void setLayersSelected(boolean layersSelected) {
        this.layersSelected = layersSelected;
    }

    public void setSelectedLayer(ArrayList<Layer> selectedLayer) {
        this.selectedLayer = selectedLayer;
    }
}
