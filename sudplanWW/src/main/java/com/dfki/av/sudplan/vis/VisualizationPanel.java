/*
 *  VisualizationPanel.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import com.dfki.av.sudplan.camera.*;
import com.dfki.av.sudplan.vis.algorithm.IVisAlgorithm;
import com.dfki.av.sudplan.vis.algorithm.VisCreateTexture;
import com.dfki.av.sudplan.vis.algorithm.VisWorker;
import com.dfki.av.sudplan.vis.algorithm.Visualization;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.terrain.SectorGeometryList;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JPanel;
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
    }

    /**
     * Returns the
     * <code>WorldWindowGLCanvas</code>.
     *
     * @return the
     * <code>WorldWindowGLCanvas</code> to return.
     */
    public WorldWindowGLCanvas getWwd() {
        return this.wwd;
    }

    @Override
    public void addLayer(Object data) {
        addLayer(data, Visualization.POINT_CLOUD, null);
    }

    /**
     *
     * @param data
     * @param vis
     * @param attributes
     */
    public void addLayer(Object data, IVisAlgorithm vis, Object[] attributes) {
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
        Position p = this.wwd.getView().getEyePosition();
        Vector3D vec = new Vector3D(wwd.getView().getForwardVector());
        return new SimpleCamera(p, vec);
    }

    @Override
    public void setCamera(Camera c) {
        // TODO <steffen>: Move behaviour into camera classes.
        if (c == null) {
            throw new IllegalArgumentException("Parameter Camera is null.");
        }
        if (c instanceof AnimatedCamera) {
            View view = this.wwd.getView();
            view.goTo(Position.fromDegrees(c.getLatitude(), c.getLongitude()), c.getAltitude());
        } else if (c instanceof SimpleCamera) {
            View view = this.wwd.getView();
            view.setEyePosition(Position.fromDegrees(c.getLatitude(), c.getLongitude(), c.getAltitude()));
            wwd.redraw();
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
        log.debug("Not supported!");
    }

    public void addTimeseries() {
        try {
            URL url = new URL("http://sudplan.kl.dfki.de/testdata/ts_nox_2m.zip");
            log.debug("URL to load from: {}", url.toString());
            String[] attributes = new String[]{"Val_200503", "Val_200501"};
            addLayer(url, Visualization.TIMESERIES, attributes);
        } catch (MalformedURLException ex) {
            log.error(ex.toString());
        }
    }

    public void removeTimeseries() {
        LayerList layerList = this.wwd.getModel().getLayers();
        for (Object object : layerList) {
            Layer layer = (Layer) object;
            if (layer.getName().startsWith("ts_nox_2m")) {
                log.debug("Removing layer: {}", layer.getName());
                removeLayer(layer);
            }
        }
    }

    public void addBuildings() {
        try {
            URL url = new URL("http://sudplan.kl.dfki.de/testdata/Buildings.zip");
            log.debug("URL to load from: {}", url.toString());
            String[] attributes = new String[]{"Elevation"};
            addLayer(url, Visualization.EXTRUDE_POLYGON, attributes);
        } catch (MalformedURLException ex) {
            log.error(ex.toString());
        }
    }

    public void removeBuildings() {
        LayerList layerList = this.wwd.getModel().getLayers();
        for (Object object : layerList) {
            Layer layer = (Layer) object;
            // TODO <steffen>: Check usage of World Wind constants here.
            if (layer.getName().startsWith("Building")) {
                log.debug("Removing layer: {}", layer.getName());
                removeLayer(layer);
            }
        }
    }

    public void addRooftopResults() {
        try {
            URL url = new URL("http://sudplan.kl.dfki.de/testdata/rooftop3.tiff");
            log.debug("URL to load from: {}", url.toString());
            addLayer(url, new VisCreateTexture(), null);
        } catch (MalformedURLException ex) {
            log.error(ex.toString());
        }
    }

    public void removeRooftopResults() {
        LayerList layerList = this.wwd.getModel().getLayers();
        for (Object object : layerList) {
            Layer layer = (Layer) object;
            // TODO <steffen>: Check usage of World Wind constants here.
            if (layer.getName().endsWith("tiff")) {
                log.debug("Removing layer: {}", layer.getName());
                removeLayer(layer);
            }
        }
    }

    public void addStreetLevelResults() {
        try {
            URL url = new URL("http://sudplan.kl.dfki.de/testdata/AirQualityStreetLevel.zip");
            String[] attributes = new String[]{"Perc98d", "NrVehTot"};
            addLayer(url, Visualization.EXTRUDE_POLYLINE, attributes);
        } catch (MalformedURLException ex) {
            log.error(ex.toString());
        }
    }

    public void removeStreetLevelResults() {
        LayerList layerList = this.wwd.getModel().getLayers();
        for (Object object : layerList) {
            Layer layer = (Layer) object;
            // TODO <steffen>: Check usage of World Wind constants here.
            if (layer.getName().startsWith("AirQuality")) {
                log.debug("Removing layer: {}", layer.getName());
                removeLayer(layer);
            }
        }
    }
}
