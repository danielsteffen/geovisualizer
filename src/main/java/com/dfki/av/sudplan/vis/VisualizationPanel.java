/*
 *  VisualizationPanel.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import com.dfki.av.sudplan.camera.*;
import com.dfki.av.sudplan.vis.basic.VisPointCloud;
import com.dfki.av.sudplan.vis.core.IVisAlgorithm;
import com.dfki.av.sudplan.vis.core.VisWorker;
import com.dfki.av.sudplan.vis.spi.VisAlgorithmFactory;
import com.dfki.av.sudplan.vis.wiz.VisWiz;
import com.dfki.av.sudplan.wms.ElevatedSurfaceLayer;
import com.dfki.av.sudplan.wms.LayerInfo;
import com.dfki.av.sudplan.wms.EventHolder;
import com.dfki.av.sudplan.wms.WMSUtils;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwind.terrain.SectorGeometryList;
import gov.nasa.worldwind.util.StatusBar;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing the {@link WorldWindowGLCanvas} to render the virtual globe.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisualizationPanel extends JPanel implements VisualizationComponent {

    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(VisualizationPanel.class);
    /**
     * The world wind GL canvas.
     */
    private WorldWindowGLCanvas wwd;
    /**
     * The {@link StatusBar} attached to this panel.
     */
    private StatusBar statusBar;
    /**
     * The {@link JProgressBar} added to the {@link #statusBar}.
     */
    private JProgressBar progressBar;
    /**
     * Support for publishing progress support to the universe :)
     */
    private PropertyChangeSupport progressChange;
    /**
     * A {@link LayerPanel} panel to manage the layer settings.
     */
    private LayerPanel layerPanel;

    /**
     * Constructs a visualization panel of the defined
     * <code>Dimension</code>.
     *
     * @param canvasSize size of the
     * <code>WorldWindowGLCanvas</code>.
     */
    public VisualizationPanel(Dimension canvasSize) {
        super(new BorderLayout());

        this.wwd = new WorldWindowGLCanvas();
        this.wwd.setPreferredSize(canvasSize);

        // Create the default model as described in the current worldwind properties.
        Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        this.wwd.setModel(m);

        // Setup a select listener for the worldmap click-and-go feature
        this.wwd.addSelectListener(new ClickAndGoSelectListener(this.wwd, WorldMapLayer.class));

        ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
        this.wwd.getModel().getLayers().add(viewControlsLayer);
        this.wwd.addSelectListener(new ViewControlsSelectListener(this.wwd, viewControlsLayer));
        this.add(this.wwd, BorderLayout.CENTER);

        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setVisible(false);

        this.statusBar = new StatusBar();
        this.statusBar.setEventSource(wwd);
        this.statusBar.add(progressBar);
        this.add(statusBar, BorderLayout.SOUTH);

        this.progressChange = new PropertyChangeSupport(this);

        this.layerPanel = new LayerPanel(this.wwd);
        this.wwd.getModel().addPropertyChangeListener(layerPanel);
    }

    /**
     * Returns the {@link WorldWindowGLCanvas} used by the {@link VisualizationPanel}
     *
     * @return the {@link WorldWindowGLCanvas} to return.
     */
    public WorldWindowGLCanvas getWwd() {
        return this.wwd;
    }

    /**
     * Contect to the WMS at the given {@link URI}. Adds all available layers to
     * the {@link WorldWindow}. The layers are disabled by default.
     *
     * @param uri the server string to be parsed into a {@link URI}.
     * @throws IllegalArgumentException if uri is {@code null} or is empty.
     */
    public void addWMS(String uri) {
        if (uri == null) {
            String msg = "URI is null. No valid URI for WMS.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (uri.isEmpty()) {
            String msg = "URI is empty. No valid URI for WMS.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try {
            final URI serverURI = new URI(uri.trim());
            Thread loadingThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    WMSCapabilities caps;
                    final ArrayList<LayerInfo> layerInfos = new ArrayList<LayerInfo>();
                    try {
                        caps = WMSCapabilities.retrieve(serverURI);
                        caps.parse();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        return;
                    }

                    // Gather up all the named layers and make a world wind layer for each.
                    final List<WMSLayerCapabilities> namedLayerCaps = caps.getNamedLayers();
                    if (namedLayerCaps == null) {
                        log.debug("No named layers available for server: {}.", serverURI);
                        return;
                    }

                    try {
                        for (WMSLayerCapabilities lc : namedLayerCaps) {
                            Set<WMSLayerStyle> styles = lc.getStyles();
                            if (styles == null || styles.isEmpty()) {
                                LayerInfo layerInfo = new LayerInfo(caps, lc, null);
                                layerInfos.add(layerInfo);
                            } else {
                                for (WMSLayerStyle style : styles) {
                                    LayerInfo layerInfo = new LayerInfo(caps, lc, style);
                                    layerInfos.add(layerInfo);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        return;
                    }

                    // Add the layers to the world window
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            for (LayerInfo layerInfo : layerInfos) {
                                Object component = LayerInfo.createComponent(layerInfo.caps, layerInfo.params);
                                if (component instanceof Layer) {
                                    Layer layer = (Layer) component;
                                    LayerList layers = wwd.getModel().getLayers();
                                    layer.setEnabled(false);
                                    if (!layers.contains(layer)) {
                                        ApplicationTemplate.insertBeforePlacenames(wwd, layer);
                                    }
                                }
                            }
                        }
                    });
                }
            });
            loadingThread.setPriority(Thread.MIN_PRIORITY);
            loadingThread.start();
        } catch (URISyntaxException ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public void addLayer(Object data) {
        IVisAlgorithm algo = VisAlgorithmFactory.newInstance(VisPointCloud.class.getName());
        if (algo != null) {
            addLayer(data, algo, null);
        } else {
            log.error("VisAlgorithm {} not supported.", VisPointCloud.class.getName());
        }
    }

    /**
     * Add a layer visualization to the {@link WorldWindowGLCanvas}. The
     * visualization is created by using the {@code data}, the {@link IVisAlgorithm},
     * and the {@code attributes}.
     *
     * @param data the data source for the visualization.
     * @param vis the visualization technique.
     * @param attributes the attributes of the data source to be visualized.
     */
    public void addLayer(Object data, IVisAlgorithm vis, Object[] attributes) {
        vis.addPropertyChangeListener(this);
        VisWorker producer = new VisWorker(data, vis, attributes, wwd);
        producer.execute();
    }

    @Override
    public void removeLayer(Object source) {
        if (source == null) {
            log.warn("Object sourc equals to null.");
            throw new IllegalArgumentException("Parameter 'layer' is null.");
        }

        if (source instanceof Layer) {
            Layer layer = (Layer) source;
            this.wwd.getModel().getLayers().remove(layer);
        } else {
            log.warn("Can't remove object.");
        }
    }

    /**
     * Removes all layers from the World Wind visualization component. <p> Note:
     * This implementation keeps the following layers: <ul> <li>Atmosphere</li>
     * <li>NASA Blue Marble Image</li> <li>Blue Marble (WMS) 2004</li>
     * <li>i-cubed Landsat</li> <li>Place Names</li> <li>Scale bar</li>
     * <li>Compass</li> <li>View Controls</li> </ul>
     */
    @Override
    public void removeAllLayers() {
        LayerList layerList = this.wwd.getModel().getLayers();
        for (Object object : layerList) {
            Layer layer = (Layer) object;
            // TODO <steffen>: Check usage of World Wind constants here.
            if (layer.getName().equalsIgnoreCase("Atmosphere")
                    || layer.getName().equalsIgnoreCase("NASA Blue Marble Image")
                    || layer.getName().equalsIgnoreCase("Blue Marble (WMS) 2004")
                    || layer.getName().equalsIgnoreCase("i-cubed Landsat")
                    || layer.getName().equalsIgnoreCase("Place Names")
                    || layer.getName().equalsIgnoreCase("Scale bar")
                    || layer.getName().equalsIgnoreCase("Compass")
                    || layer.getName().equalsIgnoreCase("View Controls")) {
                log.debug("Not removing layer: {}", layer.getName());
                continue;
            } else {
                log.debug("Removing layer: {}", layer.getName());
                removeLayer(layer);
            }
        }
    }

    @Override
    public Camera getCamera() {
        Camera camera = null;
        View view = this.wwd.getView();
        if (view != null) {
            Position p = view.getEyePosition();
            Angle heading = view.getHeading();
            Angle roll = view.getRoll();
            Angle pitch = view.getPitch();
            Vector3D vector = new Vector3D(roll.getRadians(),
                    pitch.getRadians(),
                    heading.getRadians());
            camera = new SimpleCamera(p, vector);
        } else {
            log.debug("No view available. Could not init camera object.");
        }
        return camera;
    }

    @Override
    public void setCamera(Camera c) {
        if (c == null) {
            throw new IllegalArgumentException("Parameter Camera is null.");
        }

        View view = this.wwd.getView();
        if (view != null) {

            Angle roll;
            Angle pitch;
            Angle heading;

            Vector3D vector = c.getViewingDirection();

            if (vector != null) {
                roll = Angle.fromRadians(vector.getX());
                pitch = Angle.fromRadians(vector.getY());
                heading = Angle.fromRadians(vector.getZ());
            } else {
                log.warn("No vector defined for camera. Using zero vector.");
                roll = pitch = heading = Angle.ZERO;
            }

            view.setRoll(roll);
            view.setPitch(pitch);
            view.setHeading(heading);

            if (c instanceof AnimatedCamera) {
                Position pos = Position.fromDegrees(c.getLatitude(), c.getLongitude());
                view.goTo(pos, c.getAltitude());
            } else if (c instanceof SimpleCamera) {
                Position pos = Position.fromDegrees(c.getLatitude(), c.getLongitude(), c.getAltitude());
                view.setEyePosition(pos);
                wwd.redraw();
            }
        }
    }

    @Override
    public synchronized void addCameraListener(CameraListener cl) {
        if (cl == null) {
            throw new IllegalArgumentException("Parameter CameraListener is null.");
        }
        this.wwd.getView().addPropertyChangeListener("gov.nasa.worldwind.avkey.ViewObject", cl);
    }

    @Override
    public synchronized void removeCameraListener(CameraListener cl) {
        if (cl == null) {
            throw new IllegalArgumentException("Parameter CameraListener is null.");
        }
        this.wwd.getView().removePropertyChangeListener("gov.nasa.worldwind.avkey.ViewObject", cl);
    }

    @Override
    public void setBoundingVolume(BoundingVolume bv) {
        if (bv == null) {
            throw new IllegalArgumentException("Parameter BoundingVolume is null.");
        }

        Sector sector = bv.getSector();
        Box extent = Sector.computeBoundingBox(wwd.getModel().getGlobe(),
                wwd.getSceneController().getVerticalExaggeration(), sector);
        Angle fov = wwd.getView().getFieldOfView();
        double zoom = extent.getRadius() / fov.cosHalfAngle() / fov.tanHalfAngle();

        LatLon latLon = sector.getCentroid();
        AnimatedCamera ac = new AnimatedCamera(latLon.getLatitude().getDegrees(),
                latLon.getLongitude().getDegrees(), zoom);
        setCamera(ac);
    }

    @Override
    public BoundingVolume getBoundingVolume() {
        SectorGeometryList sectorGeometryList = wwd.getSceneController().getTerrain();
        Sector sector = sectorGeometryList.getSector();
        return new BoundingBox(sector);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase(IVisAlgorithm.PROGRESS_PROPERTY)) {
            Integer i = (Integer) evt.getNewValue();
            if (i.intValue() <= 0 || i.intValue() >= 100) {
                progressBar.setVisible(false);
            } else {
                progressBar.setVisible(true);
            }
            progressBar.setValue(i.intValue());
            progressChange.firePropertyChange(evt);
        }
        if (evt.getPropertyName().equals(EventHolder.WWD_REDRAW)) {
            wwd.redraw();
        }
        if (evt.getPropertyName().equals(EventHolder.WMS_DOWNLOAD_ACTIVE)) {
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
        }
        if (evt.getPropertyName().equals(EventHolder.WMS_DOWNLAOD_COMPLETE)) {
            progressBar.setVisible(false);
            progressBar.setIndeterminate(false);
        }
    }

    @Override
    public void addProgressListener(PropertyChangeListener listener) {
        this.progressChange.addPropertyChangeListener(IVisAlgorithm.PROGRESS_PROPERTY, listener);
    }

    @Override
    public void removeProgressListener(PropertyChangeListener listener) {
        this.progressChange.removePropertyChangeListener(IVisAlgorithm.PROGRESS_PROPERTY, listener);
    }

    /**
     * Run the {@link VisWiz} and add its result to the {@link #wwd}.
     */
    public void runVisWiz() {
        VisWiz.execute(wwd, this);
    }

    /**
     * Adds a WMS Layer from the given parameters.
     *
     * @param caps the wms capabilities
     * @param lcaps the wms layer capabilities
     * @param params the wms paramters
     * @param elevation the elevation (meters above sea level) for the result
     * layer (0 = mapped to terrain)
     * @param opacity the opacity for the result layer (1.0 : full transparent)
     */
    public void addWMSHeightLayer(WMSCapabilities caps, WMSLayerCapabilities lcaps, AVList params, double elevation, double opacity) {
        ElevatedSurfaceLayer sul = new ElevatedSurfaceLayer(caps, params, elevation, opacity, lcaps.getGeographicBoundingBox());
        sul.addPropertyChangeListener(this);
        sul.setName(params.getStringValue(AVKey.DISPLAY_NAME) + "_" + elevation);
        ApplicationTemplate.insertBeforePlacenames(wwd, sul);
        ApplicationTemplate.insertBeforePlacenames(wwd, sul.getSupportLayer());
    }

    /**
     * 
     * @param layerInfo
     * @param elevation
     * @param opacity 
     */
    public void addWMSHeightLayer(LayerInfo layerInfo, double elevation, double opacity) {
        if (opacity > 1.0 || opacity < 0.0) {
            log.warn("The content of the \"opacity\" "
                    + "component must be a double value between."
                    + "0.0 and 100.0");
            opacity = 0.0;
        }
        addWMSHeightLayer(layerInfo.caps, layerInfo.layerCaps, 
                layerInfo.params, elevation, opacity);
    }

    /**
     * Adds a WMS Layer from the given url request (
     * <code>request</code>).
     *
     * @param request the request url for the wms layer
     * @param elevation the elevation (meters above sea level) for the result
     * layer (0 = mapped to terrain)
     * @param opacity the opacity for the result layer (1.0 : full transparent)
     */
    public void addWMSHeightLayer(String request, double elevation, double opacity) {
        LayerInfo li = null;
        try {
            li = WMSUtils.parseWMSRequest(request);
        } catch (Exception ex) {
            log.warn("" + ex);
        }
        if (li != null) {
            addWMSHeightLayer(li.caps, li.layerCaps, li.params, elevation, opacity);
        }
    }

    /**
     * Returns the {@link LayerPanel} that manages all currently available layer
     * elements. The structure for the UI is currently a tree.
     *
     * @return the {@link LayerPanel} to return.
     */
    public LayerPanel getLayerPanel() {
        return this.layerPanel;
    }
}
