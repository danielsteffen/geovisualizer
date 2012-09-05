/*
 *  ElevatedRenderableSupportLayer.java 
 *
 *  Created by DFKI AV on 01.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.layers.TextureTile;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support layer for a {@link ElevatedRenderableLayer} which provides the wms
 * data.
 * 
* @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class ElevatedRenderableSupportLayer extends WMSTiledImageLayer {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ElevatedRenderableSupportLayer.class);
    private final ElevatedRenderableLayer layer;

    /**
     * Creates a {@link ElevatedRenderableSupportLayer} with the defined
     * {@link WMSCapabilities} caps and the {@link AVList} params.
     *
     * @param caps the {@link WMSCapabilities}
     * @param params the parameters as {@link AVList}
     */
    public ElevatedRenderableSupportLayer(WMSCapabilities caps, AVList params, ElevatedRenderableLayer layer) {
        super(wmsGetParamsFromCapsDoc(caps, params));
        this.setOpacity(0.0);
        this.layer = layer;
    }

    @Override
    protected void addTile(DrawContext dc, TextureTile tile) {
        super.addTile(dc, tile);
        layer.addImage(tile.getTileKey(), tile.getSector());
        layer.cleanUp();
    }
}
