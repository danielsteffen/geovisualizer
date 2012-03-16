/*
 *  Created by DFKI AV on 05/09/2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplanX.test;

import gov.nasa.worldwind.cache.Cacheable;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.IconRenderer;
import gov.nasa.worldwind.render.UserFacingIcon;
import gov.nasa.worldwind.render.WWIcon;
import gov.nasa.worldwind.util.Logging;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;

public class WFSPOILayerCopy extends AbstractWFSCopy {

    public static final Angle DEFAULT_TILE_DELTA = Angle.fromDegrees(0.5D);
    private IconRenderer iconRenderer;
    protected final String defaultIcon;

    public WFSPOILayerCopy(WFSServiceCopy paramWFSService, String paramString1, String paramString2) {
        super(paramWFSService, paramString1);
        this.defaultIcon = paramString2;
        this.iconRenderer = new IconRenderer();
//    this.defaultStyle = getStyle("<Style id=\"LAYER_DEFAULT_" + paramString1 + "\">" + "<IconStyle><Icon><href>" + paramString2 + "</href></Icon></IconStyle></Style>");
    }

    public void setMaxActiveAltitude(double paramDouble) {
//    this.iconRenderer.setMaxVisibleDistance(paramDouble);
        super.setMaxActiveAltitude(paramDouble);
    }

    protected void doRenderTile(DrawContext paramDrawContext, AbstractWFSCopy.Tile paramTile) {
        Logging.logger().log(Level.INFO, "doRenderTile():{0},{1}", new Object[]{paramDrawContext, paramTile});
        
        WFSPOIChunk localWFSPOIChunk = (WFSPOIChunk) paramTile.getData();
        if(localWFSPOIChunk != null){
            Iterator<WWIcon> it = localWFSPOIChunk.getIcons().iterator();
            if(it.hasNext()){
                Logging.logger().log(Level.INFO, "localWFSPOIChunk:{0}", new Object[]{it.next().getPosition()});
                this.iconRenderer.render(paramDrawContext, localWFSPOIChunk.getIcons());
            }
        }                
    }

    protected void doPreRenderTile(DrawContext paramDrawContext, AbstractWFSCopy.Tile paramTile) {
    }

    protected void doPickTile(DrawContext paramDrawContext, AbstractWFSCopy.Tile paramTile, Point paramPoint) {
        //ToDo why does this not work ??
//        Logging.logger().log(Level.INFO, "doPickTile():{0},{1}", new Object[]{paramDrawContext, paramTile});
        WFSPOIChunk localWFSPOIChunk = (WFSPOIChunk) paramTile.getData();
        if (localWFSPOIChunk != null) {
//    Logging.logger().log(Level.INFO, "iconRenderer:{0},{1},{2},{3}", new Object[]{this.iconRenderer, paramDrawContext, localWFSPOIChunk, paramPoint});
//    Logging.logger().log(Level.INFO, "iconRenderer:{0}", new Object[]{this.iconRenderer.hashCode()});
            this.iconRenderer.pick(paramDrawContext, localWFSPOIChunk.getIcons(), paramPoint, this);
        }
    }

    protected AbstractWFSCopy.WFSSAXHandler getSAXHandler(AbstractWFSCopy.Tile paramTile) {
        return new WFSPOISAXHandler(paramTile);
    }

    protected class WFSPOISAXHandler extends AbstractWFSCopy.WFSSAXHandler {

        static final String TOPP_FULL_NAME = "topp:full_name";
        static final String TOPP_DESCRIPTION = "topp:description";
        static final String GML_POINT = "gml:Point";
        static final String GML_COORDINATES = "gml:coordinates";
        StringBuilder nameBuffer;
        StringBuilder descBuffer;
        StringBuilder coordsBuffer;
        ArrayList<String> names = new ArrayList();
        ArrayList<String> descs = new ArrayList();
        ArrayList<LatLon> positions = new ArrayList();

        public WFSPOISAXHandler(AbstractWFSCopy.Tile arg2) {
        }

        Cacheable createDataChunk() {
            ArrayList localArrayList = new ArrayList();
            for (int i = 0; i < this.names.size(); i++) {
//        HTMLIcon localHTMLIcon = new HTMLIcon(WFSPOILayerCopy.this.getStyle(null), new Position((LatLon)this.positions.get(i), 0.0D), (String)this.names.get(i), (String)this.descs.get(i), null, 1.0D);
                UserFacingIcon localIcon = new UserFacingIcon("src/main/java/32x32-icon-nasa.png", new Position((LatLon) this.positions.get(i), 0.0D));
                localIcon.setVisible(true);
                localIcon.setShowToolTip(true);
                localIcon.setToolTipText("lala");
                localArrayList.add(localIcon);
            }
            return new WFSPOILayerCopy.WFSPOIChunk(localArrayList);
        }

        void beginFeature() {
            this.nameBuffer = new StringBuilder();
            this.descBuffer = new StringBuilder();
            this.coordsBuffer = new StringBuilder();
        }

        void endFeature() {
            if ((null == this.nameBuffer) || (null == this.coordsBuffer)) {
                Logging.logger().warning("Missing name or coords in feature");
                return;
            }
            String str = new String(this.coordsBuffer);
            try {
                this.positions.add(parseCoords(str));
                this.names.add(new String(this.nameBuffer));
                this.descs.add(new String(this.descBuffer));
            } catch (NumberFormatException localNumberFormatException) {
                Logging.logger().warning("Error parsing coordinates <" + str + ">\n, error is: " + localNumberFormatException.getMessage());
            }
        }

        public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
            if (this.currentFeatureType == null) {
                return;
            }
            String str = (String) this.internedQNameStack.getFirst();
            StringBuilder localStringBuilder = null;
            if ("topp:full_name" == str) {
                localStringBuilder = this.nameBuffer;
            } else if ("topp:description" == str) {
                localStringBuilder = this.descBuffer;
            } else if (("gml:coordinates" == str) && (this.internedQNameStack.contains("gml:Point"))) {
                localStringBuilder = this.coordsBuffer;
            }
            if (localStringBuilder != null) {
                localStringBuilder.append(paramArrayOfChar, paramInt1, paramInt2);
            }
        }
    }

    public static class WFSPOIChunk
            implements Cacheable {

        private final ArrayList<WWIcon> icons;
        private final long estimatedMemorySize;

        public WFSPOIChunk(ArrayList<UserFacingIcon> paramArrayList) {
            long l = 1L;
            this.icons = new ArrayList();
            Iterator localIterator = paramArrayList.iterator();
            while (localIterator.hasNext()) {
                UserFacingIcon localSelectableIcon = (UserFacingIcon) localIterator.next();
                this.icons.add(localSelectableIcon);
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:fatal                
            }
            this.estimatedMemorySize = l;
        }

        public long getSizeInBytes() {
            return this.estimatedMemorySize;
        }

        public Iterable<WWIcon> getIcons() {
            return Collections.unmodifiableCollection(this.icons);
        }
    }
}

/* Location:           C:\develop\sourcen\gaea.jar
 * Qualified Name:     si.xlab.gaea.core.layers.wfs.WFSPOILayer
 * JD-Core Version:    0.6.0
 */
