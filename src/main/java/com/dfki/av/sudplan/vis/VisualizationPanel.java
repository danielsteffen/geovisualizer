/*
 *  VisualizationPanel.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import com.dfki.av.sudplan.Configuration;
import com.dfki.av.sudplan.camera.*;
import com.dfki.av.sudplan.stereo.SideBySideStereoSetup;
import com.dfki.av.sudplan.vis.basic.VisPointCloud;
import com.dfki.av.sudplan.vis.core.IVisAlgorithm;
import com.dfki.av.sudplan.vis.core.VisConfiguration;
import com.dfki.av.sudplan.vis.core.VisWorker;
import com.dfki.av.sudplan.vis.io.IOUtils;
import com.dfki.av.sudplan.vis.spi.VisAlgorithmFactory;
import com.dfki.av.sudplan.vis.wiz.AttributeSelectionPanel;
import com.dfki.av.sudplan.vis.wiz.VisWiz;
import com.dfki.av.sudplan.wms.*;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.terrain.SectorGeometryList;
import gov.nasa.worldwind.util.StatusBar;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.apache.commons.configuration.XMLConfiguration;
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

        initCustomElevationModels();
    }

    /**
     * Initialize custom elevation models. Currently, only the Wuppertal
     * elevation model for the area of Lüntenbeck.
     */
    private void initCustomElevationModels() {
        XMLConfiguration xmlConfig = Configuration.getXMLConfiguration();
        String key = "sudplan3D.wuppertal.localElevationModel.enabled";
        if (xmlConfig.containsKey(key)) {
            String value = xmlConfig.getString(key);
            if (Boolean.valueOf(value)) {
                log.info("Wuppertal elevation model enabled.");
                Globe globe = this.wwd.getModel().getGlobe();
                String pathKey = "sudplan3D.wuppertal.localElevationModel.path";
                if (xmlConfig.containsKey(pathKey)) {
                    String path = xmlConfig.getString(pathKey);
                    ElevationsLoader loader = new ElevationsLoader(globe, path);
                    loader.execute();
                } else {
                    log.debug("No <path> tag. Wuppertal elevation model disabled.");
                }
            } else {
                log.debug("Wuppertal elevation model disabled.");
            }
        } else {
            log.debug("No <enabled> tag. Wuppertal elevation model disabled.");
        }
    }

    /**
     * Returns the {@link WorldWindowGLCanvas} used by the
     * {@link VisualizationPanel}
     *
     * @return the {@link WorldWindowGLCanvas} to return.
     */
    public WorldWindowGLCanvas getWwd() {
        return this.wwd;
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
     * visualization is created by using the {@code data}, the
     * {@link IVisAlgorithm}, and the {@code attributes}.
     *
     * @param data the data source for the visualization.
     * @param vis the visualization technique.
     * @param attributes the attributes of the data source to be visualized.
     */
    public void addLayer(Object data, IVisAlgorithm vis, Object[] attributes) {
        vis.addPropertyChangeListener(this);
        VisConfiguration config = new VisConfiguration(vis, data, attributes);
        VisWorker producer = new VisWorker(config, wwd);
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
            String msg = "camera == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
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
            String msg = "CameraListener == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.wwd.getView().addPropertyChangeListener("gov.nasa.worldwind.avkey.ViewObject", cl);
    }

    @Override
    public synchronized void removeCameraListener(CameraListener cl) {
        if (cl == null) {
            String msg = "CameraListener == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.wwd.getView().removePropertyChangeListener("gov.nasa.worldwind.avkey.ViewObject", cl);
    }

    @Override
    public void setBoundingVolume(BoundingVolume bv) {
        if (bv == null) {
            String msg = "BoundingVolume == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
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
            wwd.redrawNow();
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
     * Run the {@link VisWiz} and add its result to the {@link #wwd}. Starts the
     * wizard with data selection panel.
     */
    public void runVisWiz() {
        runVisWiz(null);
    }

    /**
     * Run the {@link VisWiz} and add its result to the {@link WorldWindowGLCanvas}.
     * Starts the wizard with the {@link AttributeSelectionPanel} in case the
     * {@code data} is a valid data type (see {@link IOUtils#Read(java.lang.Object)
     * }) or not {code null}.
     *
     * @param data the pre-selected data source to visualize.
     * @see IOUtils#Read(java.lang.Object)
     */
    public void runVisWiz(Object data) {
        VisWiz.execute(wwd, this, data);
    }

    /**
     * Adds all available layers of the WMS at {@link URI} to the
     * {@link WorldWindow}. The layers are disabled by default.
     *
     * @param uri the server {@link URI}.
     * @throws IllegalArgumentException if uri == null
     */
    public void addAllWMSLayer(URI uri) {
        if (uri == null) {
            String msg = "URI == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try {
            List<LayerInfo> layerInfos = WMSUtils.getLayerInfos(uri);
            if (layerInfos != null) {
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
            } else {
                String msg = "No valid URI: " + uri;
                log.error(msg);
                throw new Exception(msg);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Add the {@code layerName} layer of the WMS server at {@link URI} with the
     * opacity {@code opacity} to the {@link WorldWindowGLCanvas}. The layer is
     * enabled per default.
     *
     * @param uri the {@link URI} to the WMS
     * @param layerName the name of the Layer to add
     * @param opacity the opacity to set
     * @throws IllegalArgumentException if uri == null or layerName == null or
     * {@code opacity < 0} or {@code opacity > 1.0}.
     */
    public void addWMSLayer(URI uri, String layerName, double opacity) {
        if (uri == null) {
            String msg = "uri == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (layerName == null) {
            String msg = "layerName == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (opacity < 0.0 || opacity > 1.0) {
            String msg = "opacity out of range";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        LayerInfo layerInfo = WMSUtils.getLayerInfo(uri, layerName);
        Object component = LayerInfo.createComponent(layerInfo.caps, layerInfo.params);
        if (component instanceof Layer) {
            Layer layer = (Layer) component;
            layer.setEnabled(true);
            layer.setOpacity(opacity);
            LayerList layers = wwd.getModel().getLayers();
            if (!layers.contains(layer)) {
                ApplicationTemplate.insertBeforePlacenames(wwd, layer);
            }
        }
    }

    /**
     * Adds a WMS Layer from the given parameters.
     *
     * Note that if the layerName contains '[]' a time series of wms layer will
     * be added, including a {@link WMSControlLayer}.
     *
     * @param uri {@link URI} from the wms server
     * @param layerName name of the requested layer or name of the top layer of
     * a time series which must contain a '[]' in the layer name
     * @param elevation the elevation (meters above sea level) for the result
     * layer
     * @param opacity the opacity for the result layer (1.0 : full transparent)
     */
    public void addWMSHeightLayer(URI uri, String layerName, double elevation, double opacity) {
        if (layerName.contains("[]")) {
            List<LayerInfo> layerInfos = WMSUtils.getLayerInfos(uri);
            List<ElevatedRenderableLayer> layers = new ArrayList<ElevatedRenderableLayer>();
            boolean start = false;
            String prefix = null;
            for (LayerInfo layerInfo : layerInfos) {
                if (start) {
                    String[] parts = layerInfo.getTitle().split(" ");
                    if (prefix == null) {
                        prefix = parts[0];
                    }
                    if (parts.length > 1) {
                        if (parts[0].equals(prefix)) {
                            ElevatedRenderableLayer layer = addWMSHeightLayer(layerInfo.caps,
                                    layerInfo.layerCaps, layerInfo.params, elevation, opacity);
                            layer.setSlave(true);
                            layer.setOpacity(0.0d);
                            layers.add(layer);
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (layerInfo.getTitle().equals(layerName)) {
                    start = true;
                }
            }
            if (!layers.isEmpty()) {
                WMSControlLayer cl = new WMSControlLayer(layers);
                cl.setName(layerName);
                WMSControlListener clistener = new WMSControlListener(cl, layers);
                boolean[] valSelected = new boolean[layers.size()];
                valSelected[0] = true;
                wwd.addSelectListener(clistener);
                clistener.addPropertyChangeListener(this);
                ApplicationTemplate.insertBeforePlacenames(wwd, cl);
            }
            log.debug("No layer found for the time series");
        } else {
            LayerInfo layerInfo = WMSUtils.getLayerInfo(uri, layerName);
            addWMSHeightLayer(layerInfo.caps,
                    layerInfo.layerCaps, layerInfo.params, elevation, opacity);
        }
    }

    /**
     * Adds a WMS Layer from the given parameters.
     *
     * @param caps the WMS capabilities
     * @param lcaps the WMS layer capabilities
     * @param params the WMS parameters
     * @param elevation the elevation (meters above sea level) for the result
     * layer
     * @param opacity the opacity for the result layer (1.0 : full transparent)
     */
    private ElevatedRenderableLayer addWMSHeightLayer(WMSCapabilities caps, WMSLayerCapabilities lcaps, AVList params, double elevation, double opacity) {
        ElevatedRenderableLayer sul = new ElevatedRenderableLayer(caps, params, elevation, opacity, lcaps.getGeographicBoundingBox());
        sul.setName(params.getStringValue(AVKey.DISPLAY_NAME) + "_" + elevation);
        sul.addPropertyChangeListener(this);
        ApplicationTemplate.insertBeforePlacenames(wwd, sul);
        ApplicationTemplate.insertBeforePlacenames(wwd, sul.getSupportLayer());
        return sul;
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

    /**
     * Switches to stereoscopic side-by-side mode in full screen.
     *
     * @param parent the {@link JFrame} as parent frame.
     * @throws IllegalArgumentException if parent == null
     */
    public void startStereo(JFrame parent) {
        if (parent == null) {
            String msg = "parent == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);

        }
        SideBySideStereoSetup stereoSetup = new SideBySideStereoSetup(parent, wwd);
        stereoSetup.start();
    }
}
