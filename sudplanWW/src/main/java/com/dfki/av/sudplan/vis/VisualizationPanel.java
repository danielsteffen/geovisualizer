/*
 *  VisualizationPanel.java 
 *
 *  Created by DFKI AV on 15.09.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import com.dfki.av.sudplan.camera.AnimatedCamera;
import com.dfki.av.sudplan.camera.BoundingVolume;
import com.dfki.av.sudplan.camera.Camera;
import com.dfki.av.sudplan.camera.CameraListener;
import com.dfki.av.sudplan.camera.SimpleCamera;
import com.dfki.av.sudplan.camera.Vector3D;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;
import gov.nasa.worldwind.layers.WorldMapLayer;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing the {@link WorldWindowGLCanvas} to render the virtual
 * globe.
 * 
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class VisualizationPanel extends JPanel implements VisualizationComponent {

    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());
    /**
     * The world wind GL canvas.
     */
    private WorldWindowGLCanvas wwd;

    /**
     * Constructs a visualization panel of the defined <code>Dimension</code>.
     * 
     * @param canvasSize size of the <code>WorldWindowGLCanvas</code>.
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
     * Returns the <code>WorldWindowGLCanvas</code>.
     * 
     * @return the <code>WorldWindowGLCanvas</code> to return.
     */
    public WorldWindowGLCanvas getWwd() {
        return this.wwd;
    }

    @Override
    public void addLayer(Object source) {

        if (source == null) {
            if (log.isWarnEnabled()) {
                log.warn("Parameter 'object' of method 'addLayer()' is null.");
            }
            throw new IllegalArgumentException("Parameter 'ocject' of method "
                    + "'addLayer()' is null.");
        }

        LayerWorker worker = new LayerWorker(source, wwd);
        worker.execute();
    }

    @Override
    public void removeLayer(Object source) {
        if (source == null) {
            if (log.isWarnEnabled()) {
                log.warn("Object trying to add equals to null.");
            }
            throw new IllegalArgumentException("Parameter 'layer' is null.");
        }

        if (source instanceof Layer) {
            Layer layer = (Layer) source;
            this.wwd.getModel().getLayers().remove(layer);
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Can't remove object. Currently, only objects of type Layer are supported.");
            }
        }
    }

    /**
     * Removes all layers from the World Wind visualization component.
     * <p>
     * Note: This implementation keeps the following layers: 
     * <ul> <li>Atmosphere</li> <li>NASA Blue Marble Image</li> <li>Blue Marble (WMS) 2004</li> 
     * <li>i-cubed Landsat</li> <li>Place Names</li> <li>Scale bar</li>  <li>Compass</li> 
     * <li>View Controls</li> </ul>
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
                if (log.isDebugEnabled()) {
                    log.debug("Not removing layer: {}", layer.getName());
                }
                continue;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Removing layer: {}", layer.getName());
                }
                removeLayer(layer);
            }
        }
    }

    @Override
    public Camera getCamera() {
        Sector s = new Sector(Angle.ZERO, Angle.ZERO, Angle.ZERO, Angle.ZERO);
        Position p = this.wwd.getView().getEyePosition();
        Vector3D vec = new Vector3D(wwd.getView().getForwardVector());
        return new SimpleCamera(p, vec);
    }

    @Override
    public void setCamera(Camera c) {
        if (c == null) {
            if (log.isWarnEnabled()) {
                log.warn("Camera trying to add equals to null.");
            }
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
    public void propertyChange(PropertyChangeEvent evt) {
        if(log.isDebugEnabled()){
            log.debug("Not supported!");
        }
    }
}
