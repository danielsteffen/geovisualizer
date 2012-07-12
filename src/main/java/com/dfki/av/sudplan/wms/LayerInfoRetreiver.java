/*
 *  LayerInfoRetreiver.java 
 *
 *  Created by DFKI AV on 15.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 * SwingWorker for the retreival of {@link LayerInfo} for a given wms request
 * url
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class LayerInfoRetreiver extends SwingWorker<LayerInfo, Void> {

    /**
     * wms request url as {@link String}
     */
    private String wmsURL;

    /**
     * Creates a {@link LayerInfoRetreiver} with the defined wms source {@item
     * String} wmsURL.
     *
     * @param wmsURL wms request url as {@link String}
     */
    public LayerInfoRetreiver(String wmsURL) {
        this.wmsURL = wmsURL;
    }

    /**
     * Retreives a {@link LayerInfo} from the defined wms source
     * ({@link String} wmsURL})
     *
     * @param wmsURL
     * @return parsed {@link LayerInfo} of the data returned from the wms server
     * @throws URISyntaxException if the parsing of the {@link String} wmsURL to {@link URI}
     * failed.
     * @throws Exception if the parsing of the layer info failed
     */
    private LayerInfo retreiveLayerInfo(String wmsURL) throws URISyntaxException, Exception {
        return WMSUtils.parseWMSRequest(wmsURL);
    }

    @Override
    protected LayerInfo doInBackground() throws URISyntaxException, Exception {
        return retreiveLayerInfo(wmsURL);
    }

    @Override
    protected void done() {
        LayerInfo li;
        try {
            li = get();
        } catch (InterruptedException ex) {
            firePropertyChange(PropertyChangeEventHolder.LAYERINFO_RETREIVAL_FAILED, this, wmsURL);
            Exceptions.printStackTrace(ex);
            return;
        } catch (ExecutionException ex) {
            firePropertyChange(PropertyChangeEventHolder.LAYERINFO_RETREIVAL_FAILED, this, wmsURL);
            Exceptions.printStackTrace(ex);
            return;
        }
        if (li == null) {
            firePropertyChange(PropertyChangeEventHolder.LAYERINFO_RETREIVAL_FAILED, this, wmsURL);
        } else {
            firePropertyChange(PropertyChangeEventHolder.LAYERINFO_RETREIVAL_COMPLETE, this, li);

        }
    }
}
