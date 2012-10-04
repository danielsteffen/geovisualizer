/*
 *  ElevatedRenderableSupportLayer.java 
 *
 *  Created by DFKI AV on 01.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.layers.TextureTile;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.PerformanceStatistic;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;
import java.util.Arrays;
import javax.media.opengl.GL;
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
    /**
     * Bounding box for the modified (changed elevation) geometry
     */
    private final ElevatedRenderableLayer layer;
    /**
     * Keeps track of the update time
     */
    private long updateTime;

    /**
     * Creates a {@link ElevatedRenderableSupportLayer} with the defined
     * {@link WMSCapabilities} caps and the {@link AVList} params.
     *
     * @param caps the {@link WMSCapabilities}
     * @param params the parameters as {@link AVList}
     */
    public ElevatedRenderableSupportLayer(WMSCapabilities caps, AVList params,
            ElevatedRenderableLayer layer) {
        super(wmsGetParamsFromCapsDoc(caps, params));
        this.layer = layer;
    }

    @Override
    protected void draw(DrawContext dc) {
        this.assembleTiles(dc); // Determine the tiles to draw.
        if (this.currentTiles.size() >= 1) {
            // Indicate that this layer rendered something this frame.
            this.setValue(AVKey.FRAME_TIMESTAMP, dc.getFrameTimeStamp());
            if (this.getScreenCredit() != null) {
                dc.addScreenCredit(this.getScreenCredit());
            }
            GL gl = dc.getGL();
            if (this.isUseTransparentTextures() || this.getOpacity() < 1) {
                gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_POLYGON_BIT | GL.GL_CURRENT_BIT);
                this.setBlendingFunction(dc);
            } else {
                gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_POLYGON_BIT);
            }
            gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
            gl.glEnable(GL.GL_CULL_FACE);
            gl.glCullFace(GL.GL_BACK);
            dc.setPerFrameStatistic(PerformanceStatistic.IMAGE_TILE_COUNT, this.tileCountName,
                    this.currentTiles.size());
            gl.glPopAttrib();
            // Check texture expiration. Memory-cached textures are checked for expiration only when an explicit,
            // non-zero expiry time has been set for the layer. If none has been set, the expiry times of the layer's
            // individual levels are used, but only for images in the local file cache, not textures in memory. This is
            // to avoid incurring the overhead of checking expiration of in-memory textures, a very rarely used feature.
            if (this.getExpiryTime() > 0 && this.getExpiryTime() <= System.currentTimeMillis()) {
                this.checkTextureExpiration(dc, this.currentTiles);
            }
            boolean rdy = false;
            for (TextureTile tile : currentTiles) {
                if (tile.isTextureInMemory(dc.getGpuResourceCache())
                        && tile.isTextureInMemory(dc.getTextureCache())) {
                    rdy = true;
                }
            }
            if (System.currentTimeMillis() - updateTime > 250 && rdy) {
                updateTime = System.currentTimeMillis();
                layer.removeAllRenderables();
                for (TextureTile tile : currentTiles) {
                    layer.addImage(new ElevatedTileImage(tile, layer.getElevation()));
                }
            }
            this.currentTiles.clear();
        }
        this.sendRequests();
        this.requestQ.clear();
    }
}
