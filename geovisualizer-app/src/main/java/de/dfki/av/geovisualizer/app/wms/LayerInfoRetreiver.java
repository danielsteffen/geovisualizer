/*
 * LayerInfoRetreiver.java
 *
 * Created by DFKI AV on 15.06.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.wms;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 * SwingWorker for the retrieval of {@link LayerInfo} for a given wms request
 * url
 */
public class LayerInfoRetreiver extends SwingWorker<List<LayerInfo>, Void> {

    /**
     * wms request url as {@link String}
     */
    private String wmsURL;
    private final boolean direktLink;

    /**
     * Creates a {@link LayerInfoRetreiver} with the defined wms source {@link
     * String} wmsURL.
     *
     * @param wmsURL wms request url as {@link String}
     * @param direktLink if true the wmsURL is handled as link to one specific
     * wms layer
     */
    public LayerInfoRetreiver(String wmsURL, boolean direktLink) {
        this.wmsURL = wmsURL;
        this.direktLink = direktLink;
    }

    /**
     * Retrieves a {@link LayerInfo} from the defined wms source ({@link String}
     * wmsURL})
     *
     * @param wmsURL the URL to the WMS.
     * @return parsed {@link LayerInfo} of the data returned from the wms server
     * @throws URISyntaxException if the parsing of the {@link String} wmsURL to
     * URI failed.
     * @throws Exception if the parsing of the layer info failed
     */
    private List<LayerInfo> retreiveLayerInfo(String wmsURL) throws URISyntaxException, Exception {
        if (direktLink) {
            List<LayerInfo> layerInfo = new ArrayList<>();
            layerInfo.add(WMSUtils.parseWMSRequest(wmsURL));
            return layerInfo;
        } else {
            URI serverURI = new URI(wmsURL.trim());
            return WMSUtils.getLayerInfos(serverURI);
        }
    }

    @Override
    protected List<LayerInfo> doInBackground() throws URISyntaxException, Exception {
        return retreiveLayerInfo(wmsURL);
    }

    @Override
    protected void done() {
        List<LayerInfo> li;
        try {
            li = get();
        } catch (InterruptedException | ExecutionException ex) {
            firePropertyChange(EventHolder.LAYERINFO_RETREIVAL_FAILED, this, wmsURL);
            Exceptions.printStackTrace(ex);
            return;
        }
        if (li == null) {
            firePropertyChange(EventHolder.LAYERINFO_RETREIVAL_FAILED, this, wmsURL);
        } else {
            firePropertyChange(EventHolder.LAYERINFO_RETREIVAL_COMPLETE, this, li);

        }
    }
}
