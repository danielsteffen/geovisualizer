/*
 *  Created by DFKI AV on 05/09/2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplanX.test;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.cache.BasicMemoryCache;
import gov.nasa.worldwind.cache.Cacheable;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.retrieve.HTTPRetriever;
import gov.nasa.worldwind.retrieve.RetrievalPostProcessor;
import gov.nasa.worldwind.retrieve.Retriever;
import gov.nasa.worldwind.retrieve.URLRetriever;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
//import si.xlab.gaea.formats.kml.styles.KMLStyleException;
//import si.xlab.gaea.formats.kml.styles.Style;

public abstract class AbstractWFSCopy extends AbstractLayer {

    public static final Angle DEFAULT_TILE_DELTA = Angle.fromDegrees(0.5D);
    private final WFSServiceCopy wfsService;
    private final Tile[] tiles;
    private PriorityBlockingQueue<Runnable> requestQ = new PriorityBlockingQueue(64);
    private Vec4 referencePoint;
    private final Object fileLock = new Object();
//  protected Style defaultStyle;

    public AbstractWFSCopy(WFSServiceCopy paramWFSService, String paramString) {
        Logging.logger().log(Level.INFO, "AbstractWFSCopy constructor:{0},{1}", new Object[]{paramWFSService, paramString});
        if (paramWFSService == null) {
            String str = Logging.getMessage("nullValue.wfsServiceIsNull");
            Logging.logger().fine(str);
            throw new IllegalArgumentException(str);
        }
        this.wfsService = paramWFSService.deepCopy();
        setName(paramString);
        this.tiles = buildTiles(this.wfsService);
        Logging.logger().log(Level.INFO, "getName:{0}", new Object[]{Tile.class.getName()});
        if (!WorldWind.getMemoryCacheSet().containsCache(Tile.class.getName())) {
            Logging.logger().log(Level.INFO, "memoryCache not set:");
            long l = Configuration.getLongValue("si.xlab.xgiswind.avkey.WFSLayerCacheSize", Long.valueOf(50000000L)).longValue();
            Logging.logger().log(Level.INFO, " l={0}", new Object[]{l});
            BasicMemoryCache localBasicMemoryCache = new BasicMemoryCache((long) (0.85D * l), l);
            localBasicMemoryCache.setName("WFS Tiles");
            WorldWind.getMemoryCacheSet().addCache(Tile.class.getName(), localBasicMemoryCache);
        }
//    this.defaultStyle = KSSClient.getInstance().getLayerDefaultStyle(getName());
    }

    public final WFSServiceCopy getWFSService() {
        return this.wfsService;
    }

    private PriorityBlockingQueue<Runnable> getRequestQ() {
        return this.requestQ;
    }

    private Tile[] buildTiles(WFSServiceCopy paramWFSService) {
        Logging.logger().log(Level.INFO, "BuildTiles:{0}", new Object[]{paramWFSService});
        Sector localSector = paramWFSService.getSector();
        Angle dLat = paramWFSService.getTileDelta().getLatitude();
        Angle dLon = paramWFSService.getTileDelta().getLongitude();
        int i = (int) Math.floor(Tile.computeRow(dLat, localSector.getMinLatitude()));
        int j = (int) Math.floor(Tile.computeColumn(dLon, localSector.getMinLongitude()));
        int k = (int) Math.ceil(Tile.computeRow(dLat, localSector.getMaxLatitude().subtract(dLat)));
        int m = (int) Math.ceil(Tile.computeColumn(dLon, localSector.getMaxLongitude().subtract(dLon)));
        int n = k - i + 1;
        int i1 = m - j + 1;
        Logging.logger().severe("buildTiles: i=" + i + " j=" + j + " k=" + k + " m=" + m);
        Logging.logger().severe("delta " + dLat + ", ntiles " + i1 + "x" + n);
        Tile[] arrayOfTile = new Tile[n * i1];
        Object localObject1 = Tile.computeRowLatitude(i, dLat);
        for (int i2 = 0; i2 <= k - i; i2++) {
            Angle localAngle3 = ((Angle) localObject1).add(dLat);
            Object localObject2 = Tile.computeColumnLongitude(j, dLon);
            for (int i3 = 0; i3 <= m - j; i3++) {
                Angle localAngle4 = ((Angle) localObject2).add(dLon);
                arrayOfTile[(i3 + i2 * i1)] = new Tile(paramWFSService, this, new Sector((Angle) localObject1, localAngle3, (Angle) localObject2, localAngle4), i2, i3);
                localObject2 = localAngle4;
            }
            localObject1 = localAngle3;
        }
        return arrayOfTile;
    }

    protected void doRender(DrawContext paramDrawContext) {
        Logging.logger().log(Level.INFO, "doRender():{0}", new Object[]{paramDrawContext});
        this.referencePoint = computeReferencePoint(paramDrawContext);
        if (!isServiceVisible(paramDrawContext)) {
            return;
        }
        double d1 = this.wfsService.getMinDisplayDistance();
        double d2 = this.wfsService.getMaxDisplayDistance();
        double d3 = d1 * d1;
        double d4 = d2 * d2;
        if (isSectorVisible(paramDrawContext, this.wfsService.getSector(), d3, d4)) {
            Tile[] arrayOfTile1 = this.tiles;
            for (Tile localTile : arrayOfTile1) {
                try {
                    if (!isTileVisible(paramDrawContext, localTile, d3, d4)) {
                        continue;
                    }
                    if (localTile.isTileInMemory()) {
                        doRenderTile(paramDrawContext, localTile);
                    } else if (!getWFSService().isResourceAbsent(getWFSService().getTileNumber(localTile.row, localTile.column))) {
                        requestTile(paramDrawContext, localTile);
                    }
                } catch (Exception localException) {
                    Logging.logger().severe("WFSLayer: Exception Rendering Tile: " + localException);
                    localException.printStackTrace();
                }
            }
        }
        sendRequests();
        this.requestQ.clear();
    }

    protected void doPreRender(DrawContext paramDrawContext) {
        Logging.logger().log(Level.INFO, "doPreRender():{0}", new Object[]{paramDrawContext});
        this.referencePoint = computeReferencePoint(paramDrawContext);
        if (!isServiceVisible(paramDrawContext)) {
            return;
        }
        double d1 = this.wfsService.getMinDisplayDistance();
        double d2 = this.wfsService.getMaxDisplayDistance();
        double d3 = d1 * d1;
        double d4 = d2 * d2;
//        Logging.logger().log(Level.INFO, "d1={0},d2={1},d3={2},d4={3}", new Object[]{d1, d2, d3, 4});
        if (isSectorVisible(paramDrawContext, this.wfsService.getSector(), d3, d4)) {
//            Logging.logger().log(Level.INFO, "sector is visible");
            Tile[] arrayOfTile1 = this.tiles;
            for (Tile localTile : arrayOfTile1) {
//                Logging.logger().log(Level.INFO, "tile check");
                try {
                    if ((isTileVisible(paramDrawContext, localTile, d3, d4)) && (!localTile.isTileInMemory())) {
                        continue;
                    }
//                    Logging.logger().log(Level.INFO, "tile is Visible");
                    doPreRenderTile(paramDrawContext, localTile);
                } catch (Exception localException) {
                    Logging.logger().log(Level.INFO, "doPreRender", localException);
                }
            }
        }
        sendRequests();
        this.requestQ.clear();
    }

    protected void doPick(DrawContext paramDrawContext, Point paramPoint) {
        Logging.logger().log(Level.INFO, "doPick():{0},{1}", new Object[]{paramDrawContext, paramPoint});
        this.referencePoint = computeReferencePoint(paramDrawContext);
        if (!isServiceVisible(paramDrawContext)) {
            return;
        }
        double d1 = this.wfsService.getMinDisplayDistance();
        double d2 = this.wfsService.getMaxDisplayDistance();
        double d3 = d1 * d1;
        double d4 = d2 * d2;
        if (isSectorVisible(paramDrawContext, this.wfsService.getSector(), d3, d4)) {
            Tile[] arrayOfTile1 = this.tiles;
            for (Tile localTile : arrayOfTile1) {
                try {
                    if ((isTileVisible(paramDrawContext, localTile, d3, d4)) && (!localTile.isTileInMemory())) {
                        continue;
                    }
                    doPickTile(paramDrawContext, localTile, paramPoint);
                } catch (Exception localException) {
                    Logging.logger().log(Level.INFO, "doPickException", localException);
                }
            }
        }
        sendRequests();
        this.requestQ.clear();
    }

    protected abstract void doRenderTile(DrawContext paramDrawContext, Tile paramTile);

    protected abstract void doPreRenderTile(DrawContext paramDrawContext, Tile paramTile);

    protected abstract void doPickTile(DrawContext paramDrawContext, Tile paramTile, Point paramPoint);

    private Vec4 computeReferencePoint(DrawContext paramDrawContext) {
        Logging.logger().log(Level.INFO, "computeReferencePoint():{0}", new Object[]{paramDrawContext});
        if (paramDrawContext.getViewportCenterPosition() != null) {
            return paramDrawContext.getGlobe().computePointFromPosition(paramDrawContext.getViewportCenterPosition());
        }
        Rectangle localRectangle = paramDrawContext.getView().getViewport();
        int i = (int) localRectangle.getWidth() / 2;
        for (int j = (int) (0.5D * localRectangle.getHeight()); j >= 0; j--) {
            Position localPosition = paramDrawContext.getView().computePositionFromScreenPoint(i, j);
            if (localPosition != null) {
                return paramDrawContext.getGlobe().computePointFromPosition(localPosition.getLatitude(), localPosition.getLongitude(), 0.0D);
            }
        }
        return null;
    }

    protected Vec4 getReferencePoint() {
        return this.referencePoint;
    }

    private boolean isServiceVisible(DrawContext paramDrawContext) {
        if (!this.wfsService.isEnabled()) {
            return false;
        }
        if ((paramDrawContext.getVisibleSector() != null) && (!this.wfsService.getSector().intersects(paramDrawContext.getVisibleSector()))) {
            return false;
        }
        return this.wfsService.getExtent(paramDrawContext).intersects(paramDrawContext.getView().getFrustumInModelCoordinates());
    }

    private static boolean isSectorVisible(DrawContext paramDrawContext, Sector paramSector, double paramDouble1, double paramDouble2) {
        Logging.logger().log(Level.INFO, "isSectorVisible():{0},{1},{2},{3}", new Object[]{paramDrawContext, paramSector, paramDouble1, paramDouble2});
        View localView = paramDrawContext.getView();
        Position localPosition = localView.getEyePosition();
        if (localPosition == null) {
            return false;
        }
        Angle localAngle1 = clampAngle(localPosition.getLatitude(), paramSector.getMinLatitude(), paramSector.getMaxLatitude());
        Angle localAngle2 = clampAngle(localPosition.getLongitude(), paramSector.getMinLongitude(), paramSector.getMaxLongitude());
        Vec4 localVec4 = paramDrawContext.getGlobe().computePointFromPosition(localAngle1, localAngle2, 0.0D);
        double d = paramDrawContext.getView().getEyePoint().distanceToSquared3(localVec4);
        return (paramDouble1 <= d) && (paramDouble2 >= d);
    }

    private static boolean isTileVisible(DrawContext paramDrawContext, Tile paramTile, double paramDouble1, double paramDouble2) {
//        Logging.logger().log(Level.INFO, "isTileVisible():{0},{1},{2},{3}", new Object[]{paramDrawContext,paramTile,paramDouble1,paramDouble2});
        if (!paramTile.getSector().intersects(paramDrawContext.getVisibleSector())) {
            return false;
        }
        View localView = paramDrawContext.getView();
        Position localPosition = localView.getEyePosition();
        if (localPosition == null) {
            return false;
        }
        Angle localAngle1 = clampAngle(localPosition.getLatitude(), paramTile.getSector().getMinLatitude(), paramTile.getSector().getMaxLatitude());
        Angle localAngle2 = clampAngle(localPosition.getLongitude(), paramTile.getSector().getMinLongitude(), paramTile.getSector().getMaxLongitude());
        Vec4 localVec4 = paramDrawContext.getGlobe().computePointFromPosition(localAngle1, localAngle2, 0.0D);
        double d = paramDrawContext.getView().getEyePoint().distanceToSquared3(localVec4);
        return (paramDouble1 <= d) && (paramDouble2 >= d);
    }

    protected boolean isPositionVisible(DrawContext paramDrawContext, Position paramPosition) {
        Logging.logger().log(Level.INFO, "isPositionVisible():{0},{1}", new Object[]{paramDrawContext, paramPosition});
        double d1 = paramDrawContext.getVerticalExaggeration() * paramPosition.getElevation();
        Vec4 localVec41 = paramDrawContext.getGlobe().computePointFromPosition(paramPosition.getLatitude(), paramPosition.getLongitude(), d1);
        Vec4 localVec42 = paramDrawContext.getView().getEyePoint();
        double d2 = localVec42.distanceTo3(localVec41);
        return (d2 >= getWFSService().getMinDisplayDistance()) && (d2 <= getWFSService().getMaxDisplayDistance());
    }

    private static Angle clampAngle(Angle paramAngle1, Angle paramAngle2, Angle paramAngle3) {
//        Logging.logger().log(Level.INFO, "clampAngle():{0},{1},{2}", new Object[]{paramAngle1,paramAngle2,paramAngle3});
        double d1 = paramAngle1.degrees;
        double d2 = paramAngle2.degrees;
        double d3 = paramAngle3.degrees;
        return Angle.fromDegrees(d1 > d3 ? d3 : d1 < d2 ? d2 : d1);
    }

    private void requestTile(DrawContext paramDrawContext, Tile paramTile) {
        Logging.logger().log(Level.INFO, "requestTile():{0},{1}", new Object[]{paramDrawContext, paramTile});
        Vec4 localVec4 = paramTile.getCentroidPoint(paramDrawContext.getGlobe());
        if (getReferencePoint() != null) {
            paramTile.setPriority(localVec4.distanceTo3(getReferencePoint()));
        }
        RequestTask localRequestTask = new RequestTask(paramTile, this);
        getRequestQ().add(localRequestTask);
    }

    private void sendRequests() {
        Logging.logger().log(Level.INFO, "sendRequests(), requests:{0}", new Object[]{requestQ.size()});
        for (Runnable localRunnable = (Runnable) this.requestQ.poll(); localRunnable != null; localRunnable = (Runnable) this.requestQ.poll()) {
            if (WorldWind.getTaskService().isFull()) {
                continue;
            }
            Logging.logger().log(Level.INFO, "runnable():{0}", new Object[]{localRunnable});
            WorldWind.getTaskService().addTask(localRunnable);
        }
    }

    private boolean loadTile(Tile paramTile, URL paramURL) {
        Logging.logger().log(Level.INFO, "loadTile():{0},{1}", new Object[]{paramTile, paramURL});
        Object localObject1;
        if (WWIO.isFileOutOfDate(paramURL, getWFSService().getExpiryTime())) {
            WorldWind.getDataFileStore().removeFile(paramURL);
            localObject1 = Logging.getMessage("loadTile Exception", new Object[]{paramURL});
            Logging.logger().fine((String) localObject1);
            return false;
        }
        synchronized (this.fileLock) {
            localObject1 = readTileData(paramTile, paramURL);
        }
        if (localObject1 == null) {
            return false;
        }
        addTileToCache(paramTile, (Cacheable) localObject1);
        return true;
    }

    private Cacheable readTileData(Tile paramTile, URL paramURL) {
        Logging.logger().log(Level.INFO, "readTileData():{0},{1}", new Object[]{paramTile, paramURL});
//        FileInputStream localGZIPInputStream = null;
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: not compressed
        BufferedInputStream localBufferedInputStream =null;
        try {
            String str = paramURL.getFile();
            str = str.replaceAll("%20", " ");
            FileInputStream localFileInputStream = new FileInputStream(str);
            localBufferedInputStream = new BufferedInputStream(localFileInputStream);
//            localGZIPInputStream = new GZIPInputStream(localBufferedInputStream);
            WFSSAXHandler localWFSSAXHandler = getSAXHandler(paramTile);
            SAXParserFactory.newInstance().newSAXParser().parse(localBufferedInputStream, localWFSSAXHandler);
            Cacheable localCacheable = localWFSSAXHandler.createDataChunk();
            return localCacheable;
        } catch (Exception localIOException1) {
            Logging.logger().log(Level.INFO, "WFSLayer.ExceptionAttemptingToReadFile: " + paramURL, localIOException1);
        } finally {
            try {
                if (localBufferedInputStream != null) {
                    localBufferedInputStream.close();
                }
            } catch (IOException localIOException3) {
                Logging.logger().log(Level.INFO, "WFSLayer.ExceptionAttemptingToReadFile: " + paramURL, localIOException3);
            }
        }
        return null;
    }

    protected abstract WFSSAXHandler getSAXHandler(Tile paramTile);

    private void addTileToCache(Tile paramTile, Cacheable paramCacheable) {
        Logging.logger().log(Level.INFO, "addTileToCache():{0},{1}", new Object[]{paramTile, paramCacheable});
        WorldWind.getMemoryCache(Tile.class.getName()).add(paramTile, paramCacheable);
    }

    private void downloadTile(Tile paramTile) {
        Logging.logger().log(Level.INFO, "downloadTile():{0}", new Object[]{paramTile});
        if (!WorldWind.getRetrievalService().isAvailable()) {
            return;
        }
        URL localURL;
        try {
            localURL = paramTile.getRequestURL();
            if (WorldWind.getNetworkStatus().isHostUnavailable(localURL)) {
                return;
            }
        } catch (MalformedURLException localMalformedURLException) {
            Logging.logger().log(Level.SEVERE, Logging.getMessage("layers.TextureLayer.ExceptionCreatingTextureUrl", new Object[]{paramTile}), localMalformedURLException);
            return;
        }
        HTTPRetriever localHTTPRetriever;
        if ("http".equalsIgnoreCase(localURL.getProtocol())) {
            localHTTPRetriever = new HTTPRetriever(localURL, new DownloadPostProcessor(this, paramTile));
        } else {
            Logging.logger().severe(Logging.getMessage("layers.TextureLayer.UnknownRetrievalProtocol", localURL.toString()));
            return;
        }
        Integer localInteger1 = AVListImpl.getIntegerValue(this, "gov.nasa.worldwind.avkey.URLConnectTimeout");
        if ((localInteger1 != null) && (localInteger1.intValue() > 0)) {
            localHTTPRetriever.setConnectTimeout(localInteger1.intValue());
        }
        Integer localInteger2 = AVListImpl.getIntegerValue(this, "gov.nasa.worldwind.avkey.URLReadTimeout");
        if ((localInteger2 != null) && (localInteger2.intValue() > 0)) {
            localHTTPRetriever.setReadTimeout(localInteger2.intValue());
        }
        Integer localInteger3 = AVListImpl.getIntegerValue(this, "gov.nasa.worldwind.avkey.RetrievalStaleRequestLimit");
        if ((localInteger3 != null) && (localInteger3.intValue() > 0)) {
            localHTTPRetriever.setStaleRequestLimit(localInteger3.intValue());
        }
        Logging.logger().log(Level.INFO, "runretriever():{0}", new Object[]{localHTTPRetriever});
        WorldWind.getRetrievalService().runRetriever(localHTTPRetriever, paramTile.getPriority());
    }

    private void saveBuffer(ByteBuffer paramByteBuffer, File paramFile)
            throws IOException {
        synchronized (this.fileLock) {
            WWIO.saveBuffer(paramByteBuffer, paramFile);
        }
    }

//  protected Style getStyle(String paramString)
//  {
//    Style localStyle;
//    if (null == paramString)
//      localStyle = this.defaultStyle;
//    else if (paramString.startsWith("#"))
//      localStyle = KSSClient.getInstance().getStyle(paramString.substring(1));
//    else
//      try
//      {
//        localStyle = new Style(paramString, this.defaultStyle);
//      }
//      catch (KMLStyleException localKMLStyleException)
//      {
//        Logging.logger().warning("Error parsing style: error is " + localKMLStyleException.getMessage() + "; style is " + paramString);
//        localStyle = this.defaultStyle;
//      }
//    return localStyle;
//  }
    public String toString() {
        return getName();
    }

    private static class DownloadPostProcessor
            implements RetrievalPostProcessor {

        final AbstractWFSCopy layer;
        final Tile tile;

        private DownloadPostProcessor(AbstractWFSCopy paramAbstractWFSLayer, Tile paramTile) {
            Logging.logger().log(Level.INFO, "DownloadPostProcessor");
            this.layer = paramAbstractWFSLayer;
            this.tile = paramTile;
        }

        public ByteBuffer run(Retriever paramRetriever) {
            Logging.logger().log(Level.INFO, "DownloadPostProcessor run():{0}", new Object[]{paramRetriever});
            Object localObject1;
            if (paramRetriever == null) {
                localObject1 = Logging.getMessage("nullValue.RetrieverIsNull");
                Logging.logger().fine((String) localObject1);
                throw new IllegalArgumentException((String) localObject1);
            }
            try {
                if (!paramRetriever.getState().equals("gov.nasa.worldwind.RetrieverStatusSuccessful")) {
                    return null;
                }
                localObject1 = (URLRetriever) paramRetriever;
                ByteBuffer localByteBuffer = ((URLRetriever) localObject1).getBuffer();
                if ((paramRetriever instanceof HTTPRetriever)) {
                    HTTPRetriever localObject2 = (HTTPRetriever) paramRetriever;
                    if (((HTTPRetriever) localObject2).getResponseCode() == 204) {
                        this.tile.getWFSService().markResourceAbsent(this.tile.getWFSService().getTileNumber(this.tile.row, this.tile.column));
                        return null;
                    }
                    if (((HTTPRetriever) localObject2).getResponseCode() != 200) {
                        this.tile.getWFSService().markResourceAbsent(this.tile.getWFSService().getTileNumber(this.tile.row, this.tile.column));
                        return null;
                    }
                }
                Object localObject2 = WorldWind.getDataFileStore().newFile(this.tile.getFileCachePath());
                Logging.logger().log(Level.INFO, "New File :{0}", new Object[]{localObject2});
                if (localObject2 == null) {
                    return null;
                }
                if (((File) localObject2).exists()) {
                    return localByteBuffer;
                }
                if (localByteBuffer != null) {
                    String str = paramRetriever.getContentType();
                    if (str == null) {
                        return null;
                    }
                    this.layer.saveBuffer(localByteBuffer, (File) localObject2);
                    this.layer.firePropertyChange("gov.nasa.worldwind.avkey.LayerObject", null, this);
                    return localByteBuffer;
                }
            } catch (IOException localIOException) {
                this.tile.getWFSService().markResourceAbsent(this.tile.getWFSService().getTileNumber(this.tile.row, this.tile.column));
                Logging.logger().log(Level.FINE, Logging.getMessage("WFSLayer.ExceptionSavingRetrievedFile", this.tile.getWFSService()), localIOException);
            }
            return (ByteBuffer) (ByteBuffer) null;
        }
    }

    protected static abstract class WFSSAXHandler extends DefaultHandler {

        static final String GML_FEATURE_MEMBER = "gml:featureMember";
        final LinkedList<String> internedQNameStack = new LinkedList();
        String currentFeatureType = null;

        abstract Cacheable createDataChunk();

        abstract void beginFeature();

        abstract void endFeature();

        static LatLon parseCoords(String paramString)
                throws NumberFormatException {
            String[] arrayOfString = paramString.split(",");
            if (arrayOfString.length != 2) {
                throw new NumberFormatException(arrayOfString.length + " coordinates; expected 2");
            }
            double d1 = Double.parseDouble(arrayOfString[0]);
            double d2 = Double.parseDouble(arrayOfString[1]);
            if ((-90.0D <= d2) && (d2 <= 90.0D) && (-180.0D <= d1) && (d1 <= 180.0D)) {
                return new LatLon(Angle.fromDegrees(d2), Angle.fromDegrees(d1));
            }
            throw new NumberFormatException("Latitude or longitude out of range");
        }

        public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) {
            String str = paramString3.intern();
            if ((this.currentFeatureType == null) && (this.internedQNameStack.size() > 0) && ("gml:featureMember" == this.internedQNameStack.getFirst())) {
                this.currentFeatureType = str;
                beginFeature();
            }
            this.internedQNameStack.addFirst(str);
        }

        public void endElement(String paramString1, String paramString2, String paramString3) {
            String str = paramString3.intern();
            if (str == this.currentFeatureType) {
                endFeature();
                this.currentFeatureType = null;
            }
            this.internedQNameStack.removeFirst();
        }
    }

    private static class RequestTask
            implements Runnable, Comparable<RequestTask> {

        private final AbstractWFSCopy layer;
        private final Tile tile;

        RequestTask(Tile paramTile, AbstractWFSCopy paramAbstractWFSLayer) {
            this.layer = paramAbstractWFSLayer;
            this.tile = paramTile;
        }

        public void run() {
            Logging.logger().log(Level.INFO, "RequestTask run()");
            if (this.tile.isTileInMemory()) {
                return;
            }
            URL localURL = WorldWind.getDataFileStore().findFile(this.tile.getFileCachePath(), false);
            if (localURL != null) {
                if (this.layer.loadTile(this.tile, localURL)) {
                    this.tile.getWFSService().unmarkResourceAbsent(this.tile.getWFSService().getTileNumber(this.tile.row, this.tile.column));
                    this.layer.firePropertyChange("gov.nasa.worldwind.avkey.LayerObject", null, this);
                    return;
                }
                WorldWind.getDataFileStore().removeFile(localURL);
                this.tile.getWFSService().markResourceAbsent(this.tile.getWFSService().getTileNumber(this.tile.row, this.tile.column));
                String str = Logging.getMessage("generic.DeletedCorruptDataFile", new Object[]{localURL});
                Logging.logger().info(str);
            }
            this.layer.downloadTile(this.tile);
        }

        public int compareTo(RequestTask paramRequestTask) {
            if (paramRequestTask == null) {
                String str = Logging.getMessage("nullValue.RequestTaskIsNull");
                Logging.logger().severe(str);
                throw new IllegalArgumentException(str);
            }
            return this.tile.getPriority() < paramRequestTask.tile.getPriority() ? -1 : this.tile.getPriority() == paramRequestTask.tile.getPriority() ? 0 : 1;
        }

        public boolean equals(Object paramObject) {
            if (this == paramObject) {
                return true;
            }
            if ((paramObject == null) || (getClass() != paramObject.getClass())) {
                return false;
            }
            RequestTask localRequestTask = (RequestTask) paramObject;
            return this.tile != null ? this.tile.equals(localRequestTask.tile) : localRequestTask.tile == null;
        }

        public int hashCode() {
            return this.tile != null ? this.tile.hashCode() : 0;
        }

        public String toString() {
            return this.tile.toString();
        }
    }

    protected static class Tile {

        final WFSServiceCopy wfsService;
        final AbstractWFSCopy layer;
        final Sector sector;
        final int row;
        final int column;
        final int hash;
        String fileCachePath = null;
        Extent extent = null;
        double extentVerticalExaggeration = 4.9E-324D;
        private Vec4 centroid;
        private double priority = 1.7976931348623157E+308D;

        private static double computeRow(Angle paramAngle1, Angle paramAngle2) {
            if ((paramAngle1 == null) || (paramAngle2 == null)) {
                String str = Logging.getMessage("nullValue.AngleIsNull");
                Logging.logger().severe(str);
                throw new IllegalArgumentException(str);
            }
            return (paramAngle2.getDegrees() + 90.0D) / paramAngle1.getDegrees();
        }

        private static double computeColumn(Angle paramAngle1, Angle paramAngle2) {
            if ((paramAngle1 == null) || (paramAngle2 == null)) {
                String str = Logging.getMessage("nullValue.AngleIsNull");
                Logging.logger().severe(str);
                throw new IllegalArgumentException(str);
            }
            return (paramAngle2.getDegrees() + 180.0D) / paramAngle1.getDegrees();
        }

        private static Angle computeRowLatitude(int paramInt, Angle paramAngle) {
            if (paramAngle == null) {
                String str = Logging.getMessage("nullValue.AngleIsNull");
                Logging.logger().severe(str);
                throw new IllegalArgumentException(str);
            }
            return Angle.fromDegrees(-90.0D + paramAngle.getDegrees() * paramInt);
        }

        private static Angle computeColumnLongitude(int paramInt, Angle paramAngle) {
            if (paramAngle == null) {
                String str = Logging.getMessage("nullValue.AngleIsNull");
                Logging.logger().severe(str);
                throw new IllegalArgumentException(str);
            }
            return Angle.fromDegrees(-180.0D + paramAngle.getDegrees() * paramInt);
        }

        private Tile(WFSServiceCopy paramWFSService, AbstractWFSCopy paramAbstractWFSLayer, Sector paramSector, int paramInt1, int paramInt2) {
            this.wfsService = paramWFSService;
            this.layer = paramAbstractWFSLayer;
            this.sector = paramSector;
            this.row = paramInt1;
            this.column = paramInt2;
            this.hash = computeHash();
        }

        int computeHash() {
            return getFileCachePath() != null ? getFileCachePath().hashCode() : 0;
        }

        public boolean equals(Object paramObject) {
            if (this == paramObject) {
                return true;
            }
            if ((paramObject == null) || (getClass() != paramObject.getClass())) {
                return false;
            }
            Tile localTile = (Tile) paramObject;
            if (this.layer != localTile.layer) {
                return false;
            }
            return getFileCachePath() != null ? getFileCachePath().equals(localTile.getFileCachePath()) : localTile.getFileCachePath() == null;
        }

        private Extent getExtent(DrawContext paramDrawContext) {
            if (paramDrawContext == null) {
                String str = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.logger().fine(str);
                throw new IllegalArgumentException(str);
            }
            if ((this.extent == null) || (this.extentVerticalExaggeration != paramDrawContext.getVerticalExaggeration())) {
                this.extentVerticalExaggeration = paramDrawContext.getVerticalExaggeration();
                this.extent = Sector.computeBoundingCylinder(paramDrawContext.getGlobe(), this.extentVerticalExaggeration, this.sector);
            }
            return this.extent;
        }

        private String getFileCachePath() {
            if (this.fileCachePath == null) {
                this.fileCachePath = this.wfsService.createFileCachePathFromTile(this.row, this.column);
            }
            return this.fileCachePath;
        }

        private WFSServiceCopy getWFSService() {
            return this.wfsService;
        }

        private URL getRequestURL()
                throws MalformedURLException {
            return this.wfsService.createServiceURLFromSector(this.sector);
        }

        protected Sector getSector() {
            return this.sector;
        }

        public int hashCode() {
            return this.hash;
        }

        private boolean isTileInMemory() {
            return WorldWind.getMemoryCache(Tile.class.getName()).getObject(this) != null;
        }

        Cacheable getData() {
            return (Cacheable) WorldWind.getMemoryCache(Tile.class.getName()).getObject(this);
        }

        public Vec4 getCentroidPoint(Globe paramGlobe) {
            Object localObject;
            if (paramGlobe == null) {
                localObject = Logging.getMessage("nullValue.GlobeIsNull");
                Logging.logger().severe((String) localObject);
                throw new IllegalArgumentException((String) localObject);
            }
            if (this.centroid == null) {
                localObject = getSector().getCentroid();
                this.centroid = paramGlobe.computePointFromPosition(((LatLon) localObject).getLatitude(), ((LatLon) localObject).getLongitude(), 0.0D);
            }
            return (Vec4) this.centroid;
        }

        public double getPriority() {
            return this.priority;
        }

        public void setPriority(double paramDouble) {
            this.priority = paramDouble;
        }
    }
}

/* Location:           C:\develop\sourcen\gaea.jar
 * Qualified Name:     si.xlab.gaea.core.layers.wfs.AbstractWFSLayer
 * JD-Core Version:    0.6.0
 */
