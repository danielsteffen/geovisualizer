/*
 *  LayerInfoListRetreiver.java 
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
 * SwingWorker for the retreival of the list of {@link LayerInfo} for a given
 * wms url.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class LayerInfoListRetreiver extends SwingWorker<List<LayerInfo>, Void> {

    /**
     * Wms server url as {@link String}
     */
    private String wmsURL;
    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(LayerInfoListRetreiver.class);

    /**
     * Creates an {@link LayerInfoListRetreiver}, a {@link SwingWorker} for the
     * retreival of the list of {@link LayerInfo} for a given
     * {@link String} wmsUrl
     *
     * @param wmsURL wms server url as {@link String}
     */
    public LayerInfoListRetreiver(String wmsURL) {
        this.wmsURL = wmsURL;

    }

    /**
     * Retreives a list of {@link LayerInfo} for the defined wms source
     * ({@link String} wmsURL)
     *
     * @param wmsURL wms server url as {@link String}
     * @return list of {@link LayerInfo} parsed of the data returned from the
     * wms server
     * @throws URISyntaxException if the {@link String} can not be parsed
     * as {@link URI}
     */
    private List<LayerInfo> retreiveLayerInfo(String wmsURL) throws URISyntaxException {
        URI serverURI = new URI(wmsURL.trim());
        return WMSUtils.getLayerInfos(serverURI);
    }

    @Override
    protected List<LayerInfo> doInBackground() throws URISyntaxException {
        return retreiveLayerInfo(wmsURL);
    }

    @Override
    protected void done() {
        List<LayerInfo> layerInfos = null;
        try {
            layerInfos = get();
        } catch (Exception ex) {
            firePropertyChange(PropertyChangeEventHolder.LAYERINFO_RETREIVAL_FAILED, this, wmsURL);
            log.error("Error: " + ex);
            return;
        }
        if (layerInfos == null) {
            firePropertyChange(PropertyChangeEventHolder.LAYERINFO_RETREIVAL_FAILED, this, wmsURL);
        } else {
            firePropertyChange(PropertyChangeEventHolder.LAYERINFO_RETREIVAL_COMPLETE, this, layerInfos);

        }
    }
}
