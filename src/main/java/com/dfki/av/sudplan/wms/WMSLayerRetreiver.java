/*
 *  WMSLayerRetreiver.java 
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
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class WMSLayerRetreiver extends SwingWorker<LayerInfo, Void> {

    private String wmsURL;

    private WMSLayerRetreiver() {
    }

    public WMSLayerRetreiver(String wmsURL) {
        this.wmsURL = wmsURL;
    }

    @Override
    protected LayerInfo doInBackground() throws URISyntaxException, Exception {
        return retrieveWMS(wmsURL);
    }

    private LayerInfo retrieveWMS(String wmsURL) throws URISyntaxException, Exception {
        return WMSHeightUtils.parseWMSRequest(wmsURL);
    }

    @Override
    protected void done() {
        LayerInfo li = null;
        try {
            li = get();
        } catch (InterruptedException ex) {
            firePropertyChange("WMSLayerInfoRetreiver failed", this, wmsURL);
            Exceptions.printStackTrace(ex);
            return;
        } catch (ExecutionException ex) {
            firePropertyChange("WMSLayerInfoRetreiver failed", this, wmsURL);
            Exceptions.printStackTrace(ex);
            return;
        }
        if (li == null) {
            firePropertyChange("WMSLayerInfoRetreiver failed", this, wmsURL);
        } else {
            firePropertyChange("WMSLayerInfoRetreiver done", this, li);
            
        }
    }
}
