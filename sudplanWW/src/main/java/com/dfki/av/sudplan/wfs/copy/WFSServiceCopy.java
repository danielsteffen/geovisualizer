/*
 *  Created by DFKI AV on 05/09/2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wfs.copy;


/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.AbsentResourceList;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.Tile;
import gov.nasa.worldwind.util.WWIO;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

public class WFSServiceCopy {

    private final String service;
    private final String dataset;
    private final String fileCachePath;
    private long expiryTime = 0L;
    private static final String FORMAT_SUFFIX = ".xml.gz";
    private final Sector sector;
    private final LatLon tileDelta;
    private Extent extent = null;
    private double extentVerticalExaggeration = 4.9E-324D;
    private boolean enabled;
    private double minDisplayDistance;
    private double maxDisplayDistance;
    private int numColumns;
    private static final int MAX_ABSENT_TILE_TRIES = 2;
    private static final int MIN_ABSENT_TILE_CHECK_INTERVAL = 10000;
    private final AbsentResourceList absentTiles = new AbsentResourceList(2, 10000);

    public WFSServiceCopy(String paramString1, String paramString2, Sector paramSector, Angle paramAngle) {
        this.service = paramString1;
        this.dataset = paramString2;
        URI localURI = null;
        try {
            localURI = new URI(paramString1);
        } catch (URISyntaxException localURISyntaxException) {
            String str2 = Logging.getMessage("WFSService.URISyntaxException: ", new Object[]{localURISyntaxException});
            Logging.logger().severe(str2);
        }
        if (localURI != null) {
            this.fileCachePath = WWIO.formPath(new String[]{localURI.getAuthority(), localURI.getPath(), paramString2});
        } else {
            this.fileCachePath = WWIO.formPath(new String[]{paramString1});
        }
        this.sector = paramSector;
        this.tileDelta = new LatLon(paramAngle, paramAngle);
        this.enabled = true;
        this.minDisplayDistance = 4.9E-324D;
        this.maxDisplayDistance = 1.7976931348623157E+308D;
        String str1 = validate();
        if (str1 != null) {
            Logging.logger().severe(str1);
            throw new IllegalArgumentException(str1);
        }
        this.numColumns = numColumnsInLevel();
        Logging.logger().severe("Number of Columns: "+numColumns);
    }

    public String createFileCachePathFromTile(int paramInt1, int paramInt2) {
        if ((paramInt1 < 0) || (paramInt2 < 0)) {
            String localObject = Logging.getMessage("PlaceNameService.RowOrColumnOutOfRange", new Object[]{Integer.valueOf(paramInt1), Integer.valueOf(paramInt2)});
            Logging.logger().severe((String) localObject);
            throw new IllegalArgumentException((String) localObject);
        }
        Object localObject = new StringBuilder(this.fileCachePath);
        ((StringBuilder) localObject).append(File.separator).append(this.dataset);
        ((StringBuilder) localObject).append(File.separator).append(paramInt1);
        ((StringBuilder) localObject).append(File.separator).append(paramInt1).append('_').append(paramInt2);
        ((StringBuilder) localObject).append(".xml.gz");
        String str = ((StringBuilder) localObject).toString();
        return (String) str.replaceAll("[:*?<>|]", "");
    }

    private int numColumnsInLevel() {
        int i = Tile.computeColumn(this.tileDelta.getLongitude(), this.sector.getMinLongitude(), Angle.NEG180);
        int j = Tile.computeColumn(this.tileDelta.getLongitude(), this.sector.getMaxLongitude().subtract(this.tileDelta.getLongitude()), Angle.NEG180);
        return j - i + 1;
    }

    public long getTileNumber(int paramInt1, int paramInt2) {
        return paramInt1 * this.numColumns + paramInt2;
    }

    public URL createServiceURLFromSector(Sector paramSector)
            throws MalformedURLException {
        if (paramSector == null) {
            String localObject = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe((String) localObject);
            throw new IllegalArgumentException((String) localObject);
        }
        Object localObject = new StringBuilder(this.service);
        Logging.logger().log(Level.INFO, "service={0}", new Object[]{service});
        if (((StringBuilder) localObject).charAt(((StringBuilder) localObject).length() - 1) != '?') {
            ((StringBuilder) localObject).append('?');
        }
        ((StringBuilder) localObject).append("version=1.0.0&TypeName=").append(this.dataset);
        ((StringBuilder) localObject).append("&Request=GetFeature");
        ((StringBuilder) localObject).append("&Service=WFS");
        ((StringBuilder) localObject).append("&OUTPUTFORMAT=GML2");
        ((StringBuilder) localObject).append("&BBOX=");
        ((StringBuilder) localObject).append(paramSector.getMinLongitude().getDegrees()).append(',');
        ((StringBuilder) localObject).append(paramSector.getMinLatitude().getDegrees()).append(',');
        ((StringBuilder) localObject).append(paramSector.getMaxLongitude().getDegrees()).append(',');
        ((StringBuilder) localObject).append(paramSector.getMaxLatitude().getDegrees());
        return (URL) new URL(((StringBuilder) localObject).toString());
    }

    public final synchronized WFSServiceCopy deepCopy() {
        WFSServiceCopy localWFSService = new WFSServiceCopy(this.service, this.dataset, this.sector, this.tileDelta.getLatitude());
        localWFSService.enabled = this.enabled;
        localWFSService.minDisplayDistance = this.minDisplayDistance;
        localWFSService.maxDisplayDistance = this.maxDisplayDistance;
        localWFSService.expiryTime = this.expiryTime;
        return localWFSService;
    }

    public final long getExpiryTime() {
        return this.expiryTime;
    }

    public final void setExpiryTime(long paramLong) {
        this.expiryTime = paramLong;
    }

    public boolean equals(Object paramObject) {
        if (this == paramObject) {
            return true;
        }
        if ((paramObject == null) || (getClass() != paramObject.getClass())) {
            return false;
        }
        WFSServiceCopy localWFSService = (WFSServiceCopy) paramObject;
        if (this.service != null ? !this.service.equals(localWFSService.service) : localWFSService.service != null) {
            return false;
        }
        if (this.dataset != null ? !this.dataset.equals(localWFSService.dataset) : localWFSService.dataset != null) {
            return false;
        }
        if (this.sector != null ? !this.sector.equals(localWFSService.sector) : localWFSService.sector != null) {
            return false;
        }
        if (this.tileDelta != null ? !this.tileDelta.equals(localWFSService.tileDelta) : localWFSService.tileDelta != null) {
            return false;
        }
        if (this.minDisplayDistance != localWFSService.minDisplayDistance) {
            return false;
        }
        return this.maxDisplayDistance == localWFSService.maxDisplayDistance;
    }

    public final String getDataset() {
        return this.dataset;
    }

    public final Extent getExtent(DrawContext paramDrawContext) {
        if (paramDrawContext == null) {
            String str = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(str);
            throw new IllegalArgumentException(str);
        }
        if ((this.extent == null) || (this.extentVerticalExaggeration != paramDrawContext.getVerticalExaggeration())) {
            this.extentVerticalExaggeration = paramDrawContext.getVerticalExaggeration();
            this.extent = Sector.computeBoundingCylinder(paramDrawContext.getGlobe(), this.extentVerticalExaggeration, this.sector);
        }
        return this.extent;
    }

    public final String getFileCachePath() {
        return this.fileCachePath;
    }

    public final synchronized double getMaxDisplayDistance() {
        return this.maxDisplayDistance;
    }

    public final synchronized double getMinDisplayDistance() {
        return this.minDisplayDistance;
    }

    public final LatLon getTileDelta() {
        return this.tileDelta;
    }

    public final Sector getSector() {
        return this.sector;
    }

    public final String getService() {
        return this.service;
    }

    public int hashCode() {
        int i = this.service != null ? this.service.hashCode() : 0;
        i = 29 * i + (this.dataset != null ? this.dataset.hashCode() : 0);
        i = 29 * i + (this.fileCachePath != null ? this.fileCachePath.hashCode() : 0);
        i = 29 * i + (this.sector != null ? this.sector.hashCode() : 0);
        i = 29 * i + (this.tileDelta != null ? this.tileDelta.hashCode() : 0);
        i = 29 * i + Double.valueOf(this.minDisplayDistance).hashCode();
        i = 29 * i + Double.valueOf(this.maxDisplayDistance).hashCode();
        return i;
    }

    public final synchronized boolean isEnabled() {
        return this.enabled;
    }

    public final synchronized void setEnabled(boolean paramBoolean) {
        this.enabled = paramBoolean;
    }

    public final synchronized void setMaxDisplayDistance(double paramDouble) {
        if (paramDouble < this.minDisplayDistance) {
            String str = Logging.getMessage("PlaceNameService.MaxDisplayDistanceLessThanMinDisplayDistance", new Object[]{Double.valueOf(paramDouble), Double.valueOf(this.minDisplayDistance)});
            Logging.logger().severe(str);
            throw new IllegalArgumentException(str);
        }
        this.maxDisplayDistance = paramDouble;
    }

    public final synchronized void setMinDisplayDistance(double paramDouble) {
        if (paramDouble > this.maxDisplayDistance) {
            String str = Logging.getMessage("PlaceNameService.MinDisplayDistanceGrtrThanMaxDisplayDistance", new Object[]{Double.valueOf(paramDouble), Double.valueOf(this.maxDisplayDistance)});
            Logging.logger().severe(str);
            throw new IllegalArgumentException(str);
        }
        this.minDisplayDistance = paramDouble;
    }

    public final synchronized void markResourceAbsent(long paramLong) {
        this.absentTiles.markResourceAbsent(paramLong);
    }

    public final synchronized boolean isResourceAbsent(long paramLong) {
        return this.absentTiles.isResourceAbsent(paramLong);
    }

    public final synchronized void unmarkResourceAbsent(long paramLong) {
        this.absentTiles.unmarkResourceAbsent(paramLong);
    }

    public final String validate() {
        String str = "";
        if (this.service == null) {
            str = str + Logging.getMessage("nullValue.ServiceIsNull") + ", ";
        }
        if (this.dataset == null) {
            str = str + Logging.getMessage("nullValue.DataSetIsNull") + ", ";
        }
        if (this.fileCachePath == null) {
            str = str + Logging.getMessage("nullValue.FileCachePathIsNull") + ", ";
        }
        if (this.sector == null) {
            str = str + Logging.getMessage("nullValue.SectorIsNull") + ", ";
        }
        if (this.tileDelta == null) {
            str = str + Logging.getMessage("nullValue.TileDeltaIsNull") + ", ";
        }
        if (str.length() == 0) {
            return null;
        }
        return str;
    }
}
