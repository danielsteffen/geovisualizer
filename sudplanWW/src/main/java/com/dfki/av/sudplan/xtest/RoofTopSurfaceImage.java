/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
 */
package com.dfki.av.sudplan.xtest;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.formats.tiff.GeotiffImageReaderSpi;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;

import gov.nasa.worldwindx.examples.ApplicationTemplate;
import java.awt.Color;
import javax.imageio.spi.IIORegistry;
import java.util.*;

/**
 * This example demonstrates how to use the {@link gov.nasa.worldwind.render.SurfaceImage} class to place images on the
 * surface of the globe.
 *
 * @author tag
 * @version $Id: SurfaceImages.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class RoofTopSurfaceImage extends ApplicationTemplate {

//    static {
//        IIORegistry reg = IIORegistry.getDefaultInstance();
//        reg.registerServiceProvider(GeotiffImageReaderSpi.inst());
//    }

    public static class AppFrame extends ApplicationTemplate.AppFrame {

        protected RenderableLayer analyticSurfaceLayer;

        public AppFrame() {
            super(true, true, false);

            try {

//                this.getLayerPanel().update(this.getWwd());
//                getWwd().setModel(new BasicModel());
//                View view = getWwd().getView();
//                view.goTo(Position.fromDegrees(59.328, 18.047, 10000), 10000);
                initStockholmRoofTopResults();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        private void initStockholmRoofTopResults() {
//            double latOffset = 0.00053;
//            double longOffset = 0.00342;
//            String roofTopResultImage = "roofTop.png";
//            SurfaceImage si = new SurfaceImage(roofTopResultImage, new ArrayList<LatLon>(Arrays.asList(LatLon.fromDegrees(59.2941 - latOffset, 17.985 - longOffset),
//                    LatLon.fromDegrees(59.2941 - latOffset, 18.112 - longOffset),
//                    LatLon.fromDegrees(59.359 - latOffset, 18.112 - longOffset),
//                    LatLon.fromDegrees(59.359 - latOffset, 17.985 - longOffset))));
//
////            Polyline boundary = new Polyline(si.getCorners(), 0);
////            boundary.setFollowTerrain(true);
////            boundary.setClosed(true);
////            boundary.setPathType(Polyline.RHUMB_LINE);
////            boundary.setColor(new Color(0, 255, 0));
//
//            RenderableLayer layer = new RenderableLayer();
//            layer.setName("Roof Top Results (Stockholm)");
//            layer.setPickEnabled(false);
//            layer.addRenderable(si);
//            layer.setOpacity(0.5);
////            layer.addRenderable(boundary);
//
//            insertBeforeCompass(this.getWwd(), layer);
//        }
        private void initStockholmRoofTopResults() {
            List corners = Arrays.asList(
                    LatLon.fromDegrees(59.2941, 17.985),
                    LatLon.fromDegrees(59.2941, 18.112),
                    LatLon.fromDegrees(59.359, 18.112),
                    LatLon.fromDegrees(59.359, 17.985));
            Sector imageSector = Sector.boundingSector(corners);
            String roofTopResultImage = "roofTop.png";
            ElevatedImage ei1 = new ElevatedImage(roofTopResultImage, imageSector);
            ei1.setElevation(100.0);
            ei1.setOpacity(0.4);

            RenderableLayer layer = new RenderableLayer();
            layer.setName("Surface Images");
            layer.setPickEnabled(false);
            layer.addRenderable(ei1);

            insertBeforeCompass(this.getWwd(), layer);
            this.getLayerPanel().update(this.getWwd());
        }

    }

    public static void main(String[] args) {
        ApplicationTemplate.start("World Wind Surface Images", RoofTopSurfaceImage.AppFrame.class);
    }
}
