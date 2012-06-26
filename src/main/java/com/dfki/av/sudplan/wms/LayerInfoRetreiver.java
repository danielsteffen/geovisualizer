/*
 *  LayerInfoRetreiver.java 
 *
 *  Created by DFKI AV on 15.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class LayerInfoRetreiver extends SwingWorker<List<LayerInfo>, Void> {

    private String wmsURL;
    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(LayerInfoRetreiver.class);
    

    public LayerInfoRetreiver(String wmsURL) {
        this.wmsURL = wmsURL;

    }

    @Override
    protected List<LayerInfo> doInBackground() throws URISyntaxException {
        return retrieveWMS(wmsURL);
    }

    private List<LayerInfo> retrieveWMS(String wmsURL) throws URISyntaxException {
        URI serverURI = new URI(wmsURL.trim());
        return WMSHeightUtils.getLayerInfos(serverURI);
    }

    @Override
    protected void done() {
        List<LayerInfo> layerInfos = null;
        try {
            layerInfos = get();
        } catch (Exception ex) {
            firePropertyChange("WMSLayerInfoRetreiver failed", this, wmsURL);
            log.error("Error: " + ex);
            return;
        }
        if (layerInfos == null) {
            firePropertyChange("WMSLayerInfoRetreiver failed", this, wmsURL);
        } else {
            firePropertyChange("WMSLayerInfoRetreiver done", this, layerInfos);
            
        }
    }
}
