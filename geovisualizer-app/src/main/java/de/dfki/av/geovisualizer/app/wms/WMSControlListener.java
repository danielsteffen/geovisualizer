/*
 *  ontrolListener.java
 *
 * Created by DFKI AV on 22.08.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.wms;

import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.util.Logging;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens to the changes in the controlLayer {@link WMSControlLayer} and takes
 * the corresponding action.
 *
 * Note that this class is based on
 * {@code de.dfki.av.geovisualizer.vis.mc.ControlListener}.
 */
public class WMSControlListener extends WWObjectImpl implements SelectListener {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(WMSControlListener.class);
    /**
     * Default timer delay.
     */
    private static final int DEFAULT_TIMER_DELAY = 50;
    /**
     * Duration of one animation frame in ms 40 ms results in 25 fps.
     */
    private final static int INTERVALL = 500;
    /**
     * ControlLayer {@link WMSControlLayer} instance which this listener listens
     * to.
     */
    private WMSControlLayer wmsControlLayer;
    /**
     * List of {@link ElevatedRenderableLayer}
     */
    private List<ElevatedRenderableLayer> layers;
    /**
     * Screen annotation {@link gov.nasa.worldwind.render.ScreenAnnotation}
     * currently pressed.
     */
    private ScreenAnnotation pressedControl;
    /**
     * Timer object to fetch pressed controls.
     */
    private Timer repeatTimer;
    /**
     * Timer object responsible for self-played animation.
     */
    private Timer animTimer;
    /**
     * Boolean flag to control self-played animation.
     */
    private boolean animFlag = false;
    /**
     * Type of the currently pressed annotation.
     */
    private String pressedControlType;
    /**
     * Current selected layer id.
     */
    private int id = 0;
    /**
     * Duration of the fade animation in ms.
     */
    private int animationDuration = 400;
    /**
     * Amount of frames per frame animation
     */
    private int STEPS = (animationDuration / INTERVALL) / 2;
    /**
     * Procentual change of the opacity per frame
     */
    private double STEP = 1.0d / (double) STEPS;

    /**
     * Creates an instance of {@link WMSControlListener} which handles the input
     * from the {@link WMSControlLayer}.
     *
     * @param layer the corresponding {@link WMSControlLayer}
     * @param layers the list of {@link ElevatedRenderableLayer} which are
     * controlled by the {@link WMSControlLayer}
     */
    public WMSControlListener(WMSControlLayer layer, List<ElevatedRenderableLayer> layers) {
        if (layer == null) {
            String msg = Logging.getMessage("nullValue.LayerIsNull");
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.wmsControlLayer = layer;
        this.layers = layers;
        initialize();
    }

    /**
     * Creates an instance of {@link WMSControlListener} which handles the input
     * from the {@link WMSControlLayer}.
     *
     * @param layer the corresponding {@link WMSControlLayer}
     * @param layers the list of {@link ElevatedRenderableLayer} which are
     * controlled by the {@link WMSControlLayer}
     * @param duration Duration of the fade animation in ms.
     */
    public WMSControlListener(WMSControlLayer layer, ArrayList<ElevatedRenderableLayer> layers, int duration) {
        this(layer, layers);
        this.animationDuration = duration;
    }

    /**
     * Set up the {@link #repeatTimer}.
     */
    private void initialize() {
        this.repeatTimer = new Timer(DEFAULT_TIMER_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (pressedControl != null) {
                    updateView(pressedControl, pressedControlType);
                }
            }
        });
        this.repeatTimer.start();
        updateView(wmsControlLayer.getSteps()[0], "");
    }

    /**
     * Checks if a animation timer is running and cancles if any timer is
     * running.
     */
    private void checkAnimationTimer() {
        if (animTimer != null) {
            animTimer.stop();
            animTimer.removeActionListener(animTimer.getActionListeners()[0]);
            animTimer = null;
        }
    }

    /**
     * Hides the previous layer and shows the next layer.
     *
     * @param layer the next {@link ElevatedRenderableLayer} which was selected
     * on the {@link WMSControlLayer}
     */
    private void change(final ElevatedRenderableLayer layer) {
        List<ElevatedRenderableLayer> oldLayers = new ArrayList<>();
        for (ElevatedRenderableLayer l : layers) {
            if (l.getOpacity() > 0.0d && !l.equals(layer)) {
                oldLayers.add(l);
            }
        }
        layer.setOpacity(1.0d);
        for (ElevatedRenderableLayer l : oldLayers) {
            l.setOpacity(0.0d);
        }

        firePropertyChange(EventHolder.WWD_REDRAW, null, null);
    }

    /**
     * Animates the volumes automatically with a play and stop buttons.
     *
     * Note that currently no fading between the WMS layers is supported.
     *
     * @param layers the {@link ElevatedRenderableLayer} to animate
     */
    private void animate(final List<ElevatedRenderableLayer> layers) {
        checkAnimationTimer();
        animTimer = new Timer(INTERVALL, new ActionListener() {
            boolean forward = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                change(layers.get(id));
                next();
            }

            private void next() {
                mark();
                if (forward) {
                    if (id == layers.size() - 1) {
                        id--;
                        forward = false;
                    } else {
                        id++;
                    }
                } else {
                    if (id == 0) {
                        id++;
                        forward = true;
                    } else {
                        id--;
                    }
                }
                if (!animFlag) {
                    animTimer.stop();
                }
            }
        });
        animTimer.start();
    }

    /**
     * Updates the renderable layer with the triangle grid suitable to what was
     * pressed.
     *
     * @param control the {@link ScreenAnnotation} pressed, throws a
     * {@link NullPointerException} if {@code null}.
     * @param controlType The control type of the screen annotation pressed.
     * @see WMSControlType
     * @throws NullPointerException if {@code control == null}
     */
    protected void updateView(ScreenAnnotation control, String controlType) {
        if (getWMSControlLayer().getSteps() != null) {
            for (ScreenAnnotation pressed : getWMSControlLayer().getSteps()) {
                if (control.equals(pressed)) {
                    String x = pressed.getText();
                    for (int i = 0;
                            i < this.getWMSControlLayer().getStepsRange().size();
                            i++) {
                        if (x.equals(this.getWMSControlLayer().getStepsRange().get(i))) {
                            checkAnimationTimer();
                            animFlag = false;
                            id = i;
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (controlType.equals(WMSControlType.CONTROL_UP)) {
            checkAnimationTimer();
            animFlag = false;
            if (getWMSControlLayer().getSteps() != null && id != 0) {
                id--;
            }
        } else if (controlType.equals(WMSControlType.CONTROL_DOWN)) {
            checkAnimationTimer();
            animFlag = false;
            if (getWMSControlLayer().getSteps() != null
                    && id != getWMSControlLayer().getStepsRange().size() - 1) {
                id++;
            } else if (id != 0) {
                id--;
            }
        } else if (controlType.equals(WMSControlType.CONTROL_PLAY) && !animFlag) {
            animFlag = true;
            animate(getLayers());
        } else if (controlType.equals(WMSControlType.CONTROL_PAUSE)) {
            checkAnimationTimer();
            if (animFlag) {
                animFlag = false;
            }
        }

        if (!animFlag) {
            mark();
            ElevatedRenderableLayer elevatedRenderableLayer = getLayers().get(id);
            if (elevatedRenderableLayer != null && elevatedRenderableLayer.getOpacity() < 1.0d) {
                change(elevatedRenderableLayer);
            }
        }
    }

    /**
     * Sets the animation duration for the time series animation.
     *
     * @param duration animation duration in ms
     */
    public void setAnimationDuration(int duration) {
        this.animationDuration = duration;
    }

    /**
     * Highlights the currently selected annotation when pressed and retains it.
     */
    public void mark() {
        if (getWMSControlLayer().getSteps() != null) {
            for (ScreenAnnotation pressed : getWMSControlLayer().getSteps()) {
                if (this.getWMSControlLayer().getStepsRange().get(
                        id).equals(pressed.getText())) {
                    wmsControlLayer.getValSelected()[
                            getWMSControlLayer().getStepsRange().indexOf(
                            pressed.getText())] = true;
                    Font font = pressed.getAttributes().getFont();
                    font = font.deriveFont(WMSControlLayer.FONT_SIZE);
                    pressed.getAttributes().setFont(font);
                    pressed.getAttributes().setTextColor(Color.WHITE);
                } else {
                    wmsControlLayer.getValSelected()[
                            getWMSControlLayer().getStepsRange().indexOf(
                            pressed.getText())] = false;
                    Font font = pressed.getAttributes().getFont();
                    font = font.deriveFont(WMSControlLayer.FONT_SIZE);
                    pressed.getAttributes().setFont(font);
                    pressed.getAttributes().setTextColor(Color.LIGHT_GRAY);
                }
            }
        }
        firePropertyChange(EventHolder.WWD_REDRAW, null, null);
    }

    @Override
    public void selected(SelectEvent event) {
        if (this.getWMSControlLayer().getHighlightedObject() != null) {
            this.getWMSControlLayer().highlight(null);
        }

        if (event.getMouseEvent() != null && event.getMouseEvent().isConsumed()) {
            return;
        }
        if (event.getTopObject() == null || !(event.getTopObject() instanceof AVList)) {
            return;
        }
        AVList av = ((AVList) event.getTopObject());
        String controlType = av.getStringValue(getWMSControlLayer().getId()
                + WMSControlType.CONTROL_ACTION);
        if (controlType == null) {
            return;
        }

        ScreenAnnotation selectedObject = (ScreenAnnotation) event.getTopObject();
        switch (event.getEventAction()) {
            case SelectEvent.ROLLOVER:
            case SelectEvent.HOVER:
                // Highlight on hover or rollover
                this.getWMSControlLayer().highlight(selectedObject);
                break;
            case SelectEvent.LEFT_CLICK:
            case SelectEvent.LEFT_DOUBLE_CLICK:
                // Release pressed control
                this.pressedControl = null;
                this.pressedControlType = null;
                break;
            case SelectEvent.LEFT_PRESS:
                // Handle left press on controls
                this.pressedControl = selectedObject;
                this.pressedControlType = controlType;
                event.consume();
                break;
            default:
                break;
        }

        // Keep pressed control highlighted - overrides rollover non 
        //currently pressed controls
        if (this.pressedControl != null) {
            this.getWMSControlLayer().highlight(this.pressedControl);
        }
    }

    /**
     * Accesses the {@link WMSControlLayer} associated with the listener.
     *
     * @return the {@link WMSControlLayer} this listener is assigned to.
     */
    public WMSControlLayer getWMSControlLayer() {
        return wmsControlLayer;
    }

    /**
     * Accesses the list of grids {@link ElevatedRenderableLayer} generated.
     *
     * @return {@link ArrayList} of {@link ElevatedRenderableLayer}.
     */
    public List<ElevatedRenderableLayer> getLayers() {
        return layers;
    }
}
