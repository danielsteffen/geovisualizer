/*
 *  Created by DFKI AV on 05/09/2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wfs.copy;

import gov.nasa.worldwind.cache.Cacheable;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.util.Logging;
import java.awt.Point;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;

public class WFSMultirenderableLayerCopy2 extends AbstractWFSCopy {

    public static final Angle DEFAULT_TILE_DELTA = Angle.fromDegrees(0.01D);

    public WFSMultirenderableLayerCopy2(WFSServiceCopy paramWFSService, String paramString) {
        super(paramWFSService, paramString);
    }

    public String toString() {
        return getName();
    }

    protected void doRenderTile(DrawContext paramDrawContext, AbstractWFSCopy.Tile paramTile) {
        Logging.logger().log(Level.INFO, "doRenderTile():{0}", new Object[]{paramTile});
        DataChunk data = (DataChunk) paramTile.getData();
        ArrayList<ExtrudedPolygon> polys = data.getPolys();
//        localMultiRenderable.render(paramDrawContext, getOpacity());
        for (ExtrudedPolygon currentPoly : polys) {
            currentPoly.render(paramDrawContext);
        }
    }

    protected void doPreRenderTile(DrawContext paramDrawContext, AbstractWFSCopy.Tile paramTile) {
    }

    protected void doPickTile(DrawContext paramDrawContext, AbstractWFSCopy.Tile paramTile, Point paramPoint) {
    }

    protected AbstractWFSCopy.WFSSAXHandler getSAXHandler(AbstractWFSCopy.Tile paramTile) {
        return new WFSMultirenderableSAXHandler(paramTile);
    }

    protected static class WFSMultirenderableSAXHandler extends AbstractWFSCopy.WFSSAXHandler {

        static final String GML_POINT = "gml:Point";
        static final String GML_LINEAR_RING = "gml:LinearRing";
        static final String GML_COORDINATES = "gml:coordinates";
        final Sector sector;
        static Hashtable<String, Class<?>> featureClasses = null;
        ArrayList<ExtrudedPolygon> parts;
        Hashtable<String, StringBuilder> buffers;

        public WFSMultirenderableSAXHandler(AbstractWFSCopy.Tile paramTile) {
            if (featureClasses == null) {
                featureClasses = new Hashtable();
            }
            this.sector = paramTile.getSector();
            this.parts = new ArrayList();
        }

        Cacheable createDataChunk() {
//            return new MultiRenderable(this.parts);
//            return parts;
            return new DataChunk(parts);
        }

        void beginFeature() {
            this.buffers = new Hashtable();
        }

        void endFeature() {
            try {
                LatLon localLatLon = null;
                ArrayList<LatLon> localArrayList = null;
//                if (this.buffers.get("gml:Point") != null) {
//                    localLatLon = parseCoords(new String((StringBuilder) this.buffers.get("gml:Point")));
//                }
                if (this.buffers.get("gml:LinearRing") != null) {
                    String[] localObject1 = new String((StringBuilder) this.buffers.get("gml:LinearRing")).split(" ");
                    localArrayList = new ArrayList<LatLon>();
                    for (String str : localObject1) {
                        LatLon localLatLon1 = parseCoords(str);
                        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:ugly Offset correction

                        localArrayList.add(LatLon.fromDegrees(localLatLon1.getLatitude().degrees - 0.00053, localLatLon1.getLongitude().degrees - 0.00342));
                    }
                }
                //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: performace issue, not good to do this in geographical coordinates
                int nCoordinates = 0;
                double centroidX = 0.0;
                double centroidY = 0.0;
                for (LatLon curLatLon : localArrayList) {
                    nCoordinates++;
                    centroidX += curLatLon.getLatitude().getDegrees();
                    centroidY += curLatLon.getLongitude().getDegrees();
                }
                if (nCoordinates != 0) {
                    centroidX = centroidX / nCoordinates;
                    centroidY = centroidY / nCoordinates;
                    localLatLon = LatLon.fromDegrees(centroidX, centroidY);
                }
//                Logging.logger().log(Level.INFO, "center: {0} ", new Object[]{localLatLon});
//                Object localObject1 = (Class) featureClasses.get(this.currentFeatureType);
//                if (localObject1 == null) {
////                    Class[] classes = new Class[]{Building.class};
////                    for (Class localObject4 : classes) {
////                        Method localMethod = localObject4.getMethod("implementsWFSType", new Class[]{String.class});
////                        if (localMethod == null) {
////                            continue;
////                        }
////                        if (!((Boolean) localMethod.invoke(null, new Object[]{this.currentFeatureType})).booleanValue()) {
////                            continue;
////                        }
////                        featureClasses.put(this.currentFeatureType, localObject4);
////                        break;
////                    }
//                    featureClasses.put(this.currentFeatureType, Building.class);
//                    localObject1=Building.class;
//                }
//                if (localObject1 == null) {
//                    throw new Exception("No class found for this feature type");
//                }
//                Constructor con = ((Class) localObject1).getConstructor(new Class[]{this.buffers.getClass(), LatLon.class, ArrayList.class});
                ExtrudedPolygon polygon = new ExtrudedPolygon(localArrayList, 10d);
//                String fid = new String(buffers.get("dfki:fid").toString());
//                double z_max = Double.parseDouble(new String((StringBuilder) buffers.get("dfki:z_max")));
//                Logging.logger().log(Level.INFO, "wfs params: {0} ", new Object[]{z_max});
//                MultiRenderable.Part part = (MultiRenderable.Part) (con).newInstance(new Object[]{this.buffers, localLatLon, localArrayList});
//                if (this.sector.contains(((MultiRenderable.Part) part).getCenter())) {
                this.parts.add(polygon);
//                }
            } catch (Exception localException) {
                Logging.logger().log(Level.INFO, "Error parsing feature type " + this.currentFeatureType, localException);
            } finally {
                this.buffers = null;
            }
        }

        public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
            if (this.currentFeatureType == null) {
                return;
            }
            String str1 = (String) this.internedQNameStack.getFirst();
            String str2 = null;
            if (("gml:coordinates" == str1) && (this.internedQNameStack.contains("gml:Point"))) {
                str2 = "gml:Point";
            } else if (("gml:coordinates" == str1) && (this.internedQNameStack.contains("gml:LinearRing"))) {
                str2 = "gml:LinearRing";
            } else {
                str2 = str1;
            }
            StringBuilder localStringBuilder = (StringBuilder) this.buffers.get(str2);
            if (localStringBuilder == null) {
                localStringBuilder = new StringBuilder();
                this.buffers.put(str2, localStringBuilder);
            }
            localStringBuilder.append(paramArrayOfChar, paramInt1, paramInt2);
        }
    }

    protected static class DataChunk implements Cacheable {

        private final ArrayList<ExtrudedPolygon> polys;

        public DataChunk(ArrayList<ExtrudedPolygon> polys) {
            this.polys = polys;
        }

        @Override
        public long getSizeInBytes() {
            return 10;
        }

        public ArrayList<ExtrudedPolygon> getPolys() {
            return polys;
        }
    }
}

/* Location:           C:\develop\sourcen\gaea.jar
 * Qualified Name:     si.xlab.gaea.core.layers.wfs.WFSMultirenderableLayer
 * JD-Core Version:    0.6.0
 */
