/*
 *  Created by DFKI AV on 05/09/2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplanX.test;

import com.dfki.av.sudplanX.test.FogLayer;
import com.dfki.av.sudplanX.test.ElevatedImage;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.data.BufferWrapperRaster;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.WMSLayersPanel;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurface;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurfaceAttributes;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurfaceLegend;
import gov.nasa.worldwind.formats.worldfile.WorldFile;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.util.BufferWrapper;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWBufferUtil;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWMath;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class POILayerTest extends ApplicationTemplate {

//    private final static Logger logger = LoggerFactory.getLogger(POILayerTest.class);
    private static final String TEST_IMAGE_PATH = "src/main/java/com/dfki/av/textureLayer/rooftop.png";

    public static class AppFrame extends ApplicationTemplate.AppFrame {

        private final Dimension wmsPanelSize = new Dimension(400, 600);
        protected static final double HUE_BLUE = 240d / 360d;
        protected static final double HUE_RED = 0d / 360d;
        protected RenderableLayer analyticSurfaceLayer;
        private static final String[] servers = new String[]{
            /*"http://localhost:8080/geoserver/wms",*/"http://pc-2162:8080/geoserver/ows?service=wms&version=1.1.1&request=GetCapabilities"};

        public AppFrame() {
            super(true, true, false);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            SLF4JBridgeHandler.install();
            initWMS();
            setUpFogLayer();
//            setUpNewYorkPOILayer();
//            setUpOrthoLayer();
            setUpSoedermalmPOILayer();
            setUpSoedermalmBuildingLayer();
//            setUpRooftopResultsOnGroundTest();
            setUpFloodLayer();
//            setUpRooftopResults();
            layerPanel.update(getWwd());
            getWwd().getView().goTo(Position.fromDegrees(59.328, 18.047, 10000d), 10000d);
        }

        public void setUpNewYorkPOILayer() {
            WFSServiceCopy localWFSService = new WFSServiceCopy("http://pc-2162:8080/geoserver/wfs", "tiger:poi", Sector.FULL_SPHERE, AbstractWFSCopy.DEFAULT_TILE_DELTA);
            localWFSService.setMinDisplayDistance(0.0D);
            localWFSService.setMaxDisplayDistance(300000.0D);
            localWFSService.setEnabled(true);
            WFSPOILayerCopy localWFSPOILayer = new WFSPOILayerCopy(localWFSService, "New York POI", null);
            localWFSPOILayer.setEnabled(true);
            localWFSPOILayer.setMaxActiveAltitude(300000.0D);
//            getWwd().getModel().getLayers().add(localWFSPOILayer);
            insertAfterPlacenames(getWwd(), localWFSPOILayer);

        }

        public void setUpSoedermalmPOILayer() {
            WFSServiceCopy localWFSService = new WFSServiceCopy("http://pc-2162:8080/geoserver/wfs", "dfki:poi", Sector.FULL_SPHERE, AbstractWFSCopy.DEFAULT_TILE_DELTA);
            localWFSService.setMinDisplayDistance(0.0D);
            localWFSService.setMaxDisplayDistance(300000.0D);
            localWFSService.setEnabled(true);
            WFSPOILayerCopy localWFSPOILayer = new WFSPOILayerCopy(localWFSService, "Soedermalm POI", null);
            localWFSPOILayer.setEnabled(true);
            localWFSPOILayer.setMaxActiveAltitude(300000.0D);
//            getWwd().getModel().getLayers().add(localWFSPOILayer);
            insertAfterPlacenames(getWwd(), localWFSPOILayer);
        }

        private void setUpSoedermalmBuildingLayer() {
            WFSServiceCopy localWFSService = new WFSServiceCopy("http://pc-2162:8080/geoserver/wfs", "dfki:building", Sector.FULL_SPHERE, AbstractWFSCopy.DEFAULT_TILE_DELTA);
            localWFSService.setMinDisplayDistance(0.0D);
            localWFSService.setMaxDisplayDistance(3000.0D);
            localWFSService.setEnabled(true);
            WFSMultirenderableLayerCopy2 localWFSBuildingLayer = new WFSMultirenderableLayerCopy2(localWFSService, "Soedermalm Building 1");
            WFSMultirenderableLayerCopy localWFSBuildingLayer1 = new WFSMultirenderableLayerCopy(localWFSService, "Soedermalm Building 2");
            localWFSBuildingLayer.setEnabled(true);
            localWFSBuildingLayer.setMaxActiveAltitude(300000.0D);
            localWFSBuildingLayer1.setEnabled(true);
            localWFSBuildingLayer1.setMaxActiveAltitude(300000.0D);
//            getWwd().getModel().getLayers().add(localWFSPOILayer);
            insertAfterPlacenames(getWwd(), localWFSBuildingLayer);
            insertAfterPlacenames(getWwd(), localWFSBuildingLayer1);
        }

        private void setUpFogLayer() {
            FogLayer layer = new FogLayer();
            insertBeforeLayerName(getWwd(), layer, "Blue Marble");
        }
//        private void setUpOrthoLayer() {
//            WMSCapabilities caps;
//
//            try {
//                caps = WMSCapabilities.retrieve(this.serverURI);
//                caps.parse();
//            } catch (Exception e) {
//                e.printStackTrace();
//                return;
//            }
//        }
        private JTabbedPane tabbedPane;
        private int previousTabIndex;

        public void initWMS() {
            this.tabbedPane = new JTabbedPane();

            this.tabbedPane.add(new JPanel());
            this.tabbedPane.setTitleAt(0, "+");
            this.tabbedPane.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent changeEvent) {
                    if (tabbedPane.getSelectedIndex() != 0) {
                        previousTabIndex = tabbedPane.getSelectedIndex();
                        return;
                    }

                    String server = JOptionPane.showInputDialog("Enter wms server URL");
                    if (server == null || server.length() < 1) {
                        tabbedPane.setSelectedIndex(previousTabIndex);
                        return;
                    }

                    // Respond by adding a new WMSLayerPanel to the tabbed pane.
                    if (addTab(tabbedPane.getTabCount(), server.trim()) != null) {
                        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                    }
                }
            });

            // Create a tab for each server and add it to the tabbed panel.
            for (int i = 0; i < servers.length; i++) {
                this.addTab(i + 1, servers[i]); // i+1 to place all server tabs to the right of the Add Server tab
            }

            // Display the first server pane by default.
            this.tabbedPane.setSelectedIndex(this.tabbedPane.getTabCount() > 0 ? 1 : 0);
            this.previousTabIndex = this.tabbedPane.getSelectedIndex();

            // Add the tabbed pane to a frame separate from the world window.
            JFrame controlFrame = new JFrame();
            controlFrame.getContentPane().add(tabbedPane);
            controlFrame.pack();
            controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            controlFrame.setVisible(true);
        }

        private WMSLayersPanel addTab(int position, String server) {
            // Add a server to the tabbed dialog.
            try {
                WMSLayersPanel layersPanel = new WMSLayersPanel(AppFrame.this.getWwd(), server, wmsPanelSize);
                this.tabbedPane.add(layersPanel, BorderLayout.CENTER);
                String title = layersPanel.getServerDisplayString();
                this.tabbedPane.setTitleAt(position, title != null && title.length() > 0 ? title : server);

                // Add a listener to notice wms layer selections and tell the layer panel to reflect the new state.
                layersPanel.addPropertyChangeListener("LayersPanelUpdated", new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        AppFrame.this.getLayerPanel().update(AppFrame.this.getWwd());
                    }
                });

                return layersPanel;
            } catch (URISyntaxException e) {
                JOptionPane.showMessageDialog(null, "Server URL is invalid", "Invalid Server URL",
                        JOptionPane.ERROR_MESSAGE);
                tabbedPane.setSelectedIndex(previousTabIndex);
                return null;
            }
        }

        private void setUpRooftopResultsOnGroundTest() {
//            localArrayList.add(LatLon.fromDegrees(localLatLon1.getLatitude().degrees-0.00053, localLatLon1.getLongitude().degrees-0.00342));
            double latOffset = 0.00053;
            double longOffset = 0.00342;
            List corners = Arrays.asList(
                    LatLon.fromDegrees(59.2941 - latOffset, 17.985 - longOffset),
                    LatLon.fromDegrees(59.2941 - latOffset, 18.112 - longOffset),
                    LatLon.fromDegrees(59.359 - latOffset, 18.112 - longOffset),
                    LatLon.fromDegrees(59.359 - latOffset, 17.985 - longOffset));
            Sector imageSector = Sector.boundingSector(corners);
//            SurfaceImage si1 = new SurfaceImage(TEST_IMAGE_PATH, new ArrayList<LatLon>(Arrays.asList(
//                    LatLon.fromDegrees(59.2941, 17.985),
//                    LatLon.fromDegrees(59.2941, 18.112),
//                    LatLon.fromDegrees(59.359, 18.112),
//                    LatLon.fromDegrees(59.359, 17.985)
//                    )));
            SurfaceImage si1 = new SurfaceImage(TEST_IMAGE_PATH, imageSector);
            si1.setOpacity(0.4);
            Polyline boundary = new Polyline(si1.getCorners(), 0);
            boundary.setFollowTerrain(true);
            boundary.setClosed(true);
            boundary.setPathType(Polyline.RHUMB_LINE);
            boundary.setColor(new Color(0, 255, 0));

            RenderableLayer layer = new RenderableLayer();
            layer.setName("Surface Images");
            layer.setPickEnabled(false);
            layer.addRenderable(si1);
            layer.addRenderable(boundary);

            insertBeforeCompass(this.getWwd(), layer);
            this.getLayerPanel().update(this.getWwd());
        }

        private void setUpRooftopResults() {
            List corners = Arrays.asList(
                    LatLon.fromDegrees(59.2941, 17.985),
                    LatLon.fromDegrees(59.2941, 18.112),
                    LatLon.fromDegrees(59.359, 18.112),
                    LatLon.fromDegrees(59.359, 17.985));
            Sector imageSector = Sector.boundingSector(corners);

            ElevatedImage ei1 = new ElevatedImage(TEST_IMAGE_PATH, imageSector);
            ei1.setElevation(10.0);
            Polyline boundary = new Polyline(ei1.getCorners(), 0);
            boundary.setFollowTerrain(true);
            boundary.setClosed(true);
            boundary.setPathType(Polyline.RHUMB_LINE);
            boundary.setColor(new Color(0, 255, 0));

            RenderableLayer layer = new RenderableLayer();
            layer.setName("Surface Images");
            layer.setPickEnabled(false);
            layer.addRenderable(ei1);
            layer.addRenderable(boundary);

            insertBeforeCompass(this.getWwd(), layer);
            this.getLayerPanel().update(this.getWwd());
        }

        private void setUpFloodLayer() {
            this.analyticSurfaceLayer = new RenderableLayer();
            this.analyticSurfaceLayer.setPickEnabled(false);
            this.analyticSurfaceLayer.setName("Analytic Surfaces");
            insertBeforePlacenames(this.getWwd(), this.analyticSurfaceLayer);
            this.getLayerPanel().update(this.getWwd());

//            createRandomAltitudeSurface(HUE_BLUE, HUE_RED, 40, 40, this.analyticSurfaceLayer);
//            createRandomColorSurface(HUE_BLUE, HUE_RED, 40, 40, this.analyticSurfaceLayer);
//            createWaterLevel(HUE_BLUE, HUE_RED, 40, 40, analyticSurfaceLayer);
            // Load the static precipitation data. Since it comes over the network, load it in a separate thread to
            // avoid blocking the example if the load is slow or fails.
            Thread t = new Thread(new Runnable() {

                public void run() {
//                    createPrecipitationSurface(HUE_BLUE, HUE_RED, analyticSurfaceLayer);
                    createWaterLevel2(HUE_BLUE, HUE_RED, WIDTH, WIDTH, analyticSurfaceLayer);
                }
            });
            t.start();
        }
    }

    public static void main(String[] args) {
        ApplicationTemplate.start("World Wind Place Names", AppFrame.class);
    }

    public static Iterable<? extends AnalyticSurface.GridPointAttributes> createColorGradientValues(
            BufferWrapper values, double missingDataSignal, double minValue, double maxValue, double minHue, double maxHue) {
        if (values == null) {
            String message = Logging.getMessage("nullValue.BufferIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        ArrayList<AnalyticSurface.GridPointAttributes> attributesList = new ArrayList<AnalyticSurface.GridPointAttributes>();

        double oldMax = maxValue;
        double oldMin = minValue;
        int offset = 20;
        minValue = (minValue / maxValue) + offset;
        maxValue = (maxValue / maxValue) + offset;
        for (int i = 0; i < values.length(); i++) {
            double value = values.getDouble(i);

            double normalized = minValue;
            if (Double.compare(value, missingDataSignal) == 0) {
                value = minValue;
            } else {
                normalized = (value / oldMax);
                normalized += offset;
            }

//            System.out.println("value: " + value + ", normalized: " + normalized);
//            System.out.println("min: " + minValue + ", max: " + maxValue+" minH: "+minHue+" maxH "+maxHue);
            attributesList.add(AnalyticSurface.createColorGradientAttributes(normalized, minValue, maxValue, minHue, maxHue));
        }
        return attributesList;
    }

    protected static void createWaterLevel2(double minHue, double maxHue, int width, int height,
            final RenderableLayer outLayer) {
        BufferWrapperRaster raster = loadZippedBILData(
                "http://worldwind.arc.nasa.gov/java/demos/data/wa-precip-24hmam.zip");
        if (raster == null) {
            return;
        }
        double[] extremes = WWBufferUtil.computeExtremeValues(raster.getBuffer(), raster.getTransparentValue());
        if (extremes == null) {
            return;


        }
        final AnalyticSurface surface = new AnalyticSurface();

          double latOffset = 0.01083;
            double longOffset = 0.02042;
//            List corners = Arrays.asList(
//                    LatLon.fromDegrees(59.2941 - latOffset, 17.985 - longOffset),
//            LatLon.fromDegrees(59.359, 18.112),
//        surface.setSector(Sector.fromDegrees(51.228388, 51.275662, 7.084579, 7.224655));
            surface.setSector(Sector.fromDegrees(59.2941-latOffset, 59.359-latOffset, 17.985+longOffset, 18.112+longOffset));

        surface.setDimensions(raster.getWidth(), raster.getHeight());
        System.out.println("Extremes: " + extremes[0] + "," + extremes[1]);
        surface.setValues(createColorGradientValues(
                raster.getBuffer(), raster.getTransparentValue(), extremes[0], extremes[1], minHue, maxHue));
        surface.setVerticalScale(1);
        AnalyticSurfaceAttributes attr = new AnalyticSurfaceAttributes();
        attr.setDrawOutline(false);
        attr.setDrawShadow(false);
        attr.setInteriorOpacity(0.6);
        surface.setSurfaceAttributes(attr);

        Format legendLabelFormat = new DecimalFormat("# ft") {

            public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition) {
                double valueInFeet = number * WWMath.METERS_TO_FEET;
                return super.format(valueInFeet, result, fieldPosition);
            }
        };

        final AnalyticSurfaceLegend legend = AnalyticSurfaceLegend.fromColorGradient(extremes[0], extremes[1],
                minHue, maxHue,
                AnalyticSurfaceLegend.createDefaultColorGradientLabels(extremes[0], extremes[1], legendLabelFormat),
                AnalyticSurfaceLegend.createDefaultTitle("Annual Precipitation"));
        legend.setOpacity(0.8);
        legend.setScreenLocation(new Point(100, 300));

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                surface.setClientLayer(outLayer);
                outLayer.addRenderable(surface);
                outLayer.addRenderable(createLegendRenderable(surface, 300, legend));
            }
        });
    }

    protected static Renderable createLegendRenderable(final AnalyticSurface surface, final double surfaceMinScreenSize,
            final AnalyticSurfaceLegend legend) {
        return new Renderable() {

            public void render(DrawContext dc) {
                Extent extent = surface.getExtent(dc);
                if (!extent.intersects(dc.getView().getFrustumInModelCoordinates())) {
                    return;


                }
                if (WWMath.computeSizeInWindowCoordinates(dc, extent) < surfaceMinScreenSize) {
                    return;


                }
                legend.render(dc);
            }
        };
    }

    protected static BufferWrapperRaster loadZippedBILData(String uriString) {
        try {
            File zipFile = File.createTempFile("data", ".zip");
            File hdrFile = File.createTempFile("data", ".hdr");
            File blwFile = File.createTempFile("data", ".blw");
            zipFile.deleteOnExit();
            hdrFile.deleteOnExit();
            blwFile.deleteOnExit();

            ByteBuffer byteBuffer = WWIO.readURLContentToBuffer(new URI(uriString).toURL());
            WWIO.saveBuffer(byteBuffer, zipFile);

            ZipFile zip = new ZipFile(zipFile);
            ByteBuffer dataBuffer = unzipEntryToBuffer(zip, "data.bil");
            WWIO.saveBuffer(unzipEntryToBuffer(zip, "data.hdr"), hdrFile);
            WWIO.saveBuffer(unzipEntryToBuffer(zip, "data.blw"), blwFile);
            zip.close();

            AVList params = new AVListImpl();
            WorldFile.decodeWorldFiles(new File[]{hdrFile, blwFile}, params);
            params.setValue(AVKey.DATA_TYPE, params.getValue(AVKey.PIXEL_TYPE));

            Double missingDataSignal = (Double) params.getValue(AVKey.MISSING_DATA_REPLACEMENT);
            if (missingDataSignal == null) {
                missingDataSignal = Double.NaN;


            }
            Sector sector = (Sector) params.getValue(AVKey.SECTOR);
            int[] dimensions = (int[]) params.getValue(WorldFile.WORLD_FILE_IMAGE_SIZE);
            BufferWrapper buffer = BufferWrapper.wrap(dataBuffer, params);

            BufferWrapperRaster raster = new BufferWrapperRaster(dimensions[0], dimensions[1], sector, buffer);
            raster.setTransparentValue(missingDataSignal);
            return raster;
        } catch (Exception e) {
            String message = Logging.getMessage("generic.ExceptionAttemptingToReadFrom", uriString);
            Logging.logger().severe(message);
            return null;
        }
    }

    protected static ByteBuffer unzipEntryToBuffer(ZipFile zipFile, String entryName) throws IOException {
        ZipEntry entry = zipFile.getEntry(entryName);
        InputStream is = zipFile.getInputStream(entry);
        return WWIO.readStreamToBuffer(is);
    }
}
