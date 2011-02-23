/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.layer;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public interface LayerManager extends PropertyChangeListener{

    public void addLayer(final Layer layer);

    public void removeLayers(final ArrayList<Layer> layer);
    public void removeLayer(final Layer layer);

    public void scrollToLayer(final Layer layer);

    public Layer getLayer(final int index);
    public List<Layer> getLayers();

    public void addLayerListener(final LayerListener listener);
    public void removeLayerListener(final LayerListener listener);
    
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:interface FileLayerManager
    public void addLayerFromFile(final File file);
    public void addLayerFromFile(final String filename);
    public void addLayerFromFile(final URL fileURL);

    void addLayersFromFile(final List<File> files);
}
