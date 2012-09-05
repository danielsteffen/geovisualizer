/*
 *  ControlListener.java
 *
 *  Created by DFKI AV on 22.08.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;
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
 * Listens to the changes in the controlLayer
 * {@link com.dfki.av.sudplan.vis.mc.ControlLayer} and takes the corresponding
 * action.
 *
 * Based on com.dfki.av.sudplan.vis.mc.ControlListener
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class WMSControlListener extends AbstractLayer implements SelectListener {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(WMSControlListener.class);
    /**
     * Default timer delay.
     */
    private static final int DEFAULT_TIMER_DELAY = 50;
    /**
     * ControlLayer {@link com.dfki.av.sudplan.vis.mc.ControlLayer} instance
     * which this listener listens to.
     */
    private WMSControlLayer viewControlsLayer;
    /**
     * List of triangle grids {@link com.dfki.av.sudplan.vis.mc.TriangleGrid generated.
     */
    private ArrayList<ElevatedRenderableLayer> layers;
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
     * Duration of one animation frame in ms 40 ms -> 25 fps
     */
    private final static int INTERVALL = 500;
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
     * Font size
     */
    private final static float FONT_SIZE = 10.f;

    /**
     * Creates an instance of {@link WMSControlListener} which handles the input
     * from the {@link WMSControlLayer}.
     *
     * @param layer the corresponding {@link WMSControlLayer}
     * @param layers the list of {@link ElevatedRenderableLayer} which are
     * controlled by the {@link WMSControlLayer}
     */
    public WMSControlListener(WMSControlLayer layer, ArrayList<ElevatedRenderableLayer> layers) {
        if (layer == null) {
            String msg = Logging.getMessage("nullValue.LayerIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.viewControlsLayer = layer;
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
     * Sets the animation durion.
     *
     * @param duration Duration of the fade animation in ms.
     */
    public void setAnimationDuration(int duration) {
        this.animationDuration = duration;
    }

    /**
     * Updates the renderable layer
     * {@link gov.nasa.worldwind.layers.RenderableLayer}with the triangle grid
     * suitable to what was pressed.
     *
     * @param control The screen annotation pressed, throws a
     * nullPointerException if null.
     * @param controlType The control type of the screen annotation pressed.
     */
    protected void updateView(ScreenAnnotation control, String controlType) {
        if (getViewControlsLayer().getSteps() != null) {
            for (ScreenAnnotation pressed : getViewControlsLayer().getSteps()) {
                if (control.equals(pressed)) {
                    String x = pressed.getText();
                    for (int i = 0;
                            i < this.getViewControlsLayer().getStepsRange().size();
                            i++) {
                        if (x.equals(this.getViewControlsLayer().getStepsRange().get(i))) {
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
        if (controlType.equals("ISO_PLUS")) {
            checkAnimationTimer();
            animFlag = false;
            if (getViewControlsLayer().getSteps() != null) {
                if (id != 0) {
                    id--;
                }
            }
        } else if (controlType.equals("ISO_MINUS")) {
            checkAnimationTimer();
            animFlag = false;
            if (getViewControlsLayer().getSteps() != null) {
                if (id != this.getViewControlsLayer().getStepsRange().size() - 1) {
                    id++;
                }
            } else {
                if (id != 0) {
                    id--;
                }
            }
        } else if (controlType.equals("ANIM_PLAY")) {
            if (!animFlag) {
                animFlag = true;
                animate(getLayers());
            }
        } else if (controlType.equals("ANIM_STOP")) {
            checkAnimationTimer();
            if (animFlag) {
                animFlag = false;
            }
        }
        if (!animFlag) {
            mark();
            if (getLayers().get(id) != null) {
                if (getLayers().get(id).getOpacity() < 1.0d) {
                    change(getLayers().get(id));
                }
            }
        }

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
     * Highlights the currently selected annotation when pressed and retains it.
     */
    public void mark() {
        if (getViewControlsLayer().getSteps() != null) {
            for (ScreenAnnotation pressed : getViewControlsLayer().getSteps()) {
                if (this.getViewControlsLayer().getStepsRange().get(
                        id).equals(pressed.getText())) {
                    viewControlsLayer.getValSelected()[
                            getViewControlsLayer().getStepsRange().indexOf(
                            pressed.getText())] = true;
                    Font font = pressed.getAttributes().getFont();
                    font = font.deriveFont(FONT_SIZE);
                    pressed.getAttributes().setFont(font);
                    pressed.getAttributes().setTextColor(Color.WHITE);
                } else {
                    viewControlsLayer.getValSelected()[
                            getViewControlsLayer().getStepsRange().indexOf(
                            pressed.getText())] = false;
                    Font font = pressed.getAttributes().getFont();
                    font = font.deriveFont(FONT_SIZE);
                    pressed.getAttributes().setFont(font);
                    pressed.getAttributes().setTextColor(Color.LIGHT_GRAY);
                }
            }
        }
        firePropertyChange(EventHolder.WWD_REDRAW, null, null);
    }

    /**
     * Hides the previous layer and shows the next layer.
     *
     * @param layer the next layer which was selected on the {@link WMSControlLayer}
     */
    private void change(final ElevatedRenderableLayer layer) {
        List<ElevatedRenderableLayer> oldLayers = new ArrayList<ElevatedRenderableLayer>();
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

    @Override
    public void selected(SelectEvent event) {
        if (this.getViewControlsLayer().getHighlightedObject() != null) {
            this.getViewControlsLayer().highlight(null);
        }

        if (event.getMouseEvent() != null && event.getMouseEvent().isConsumed()) {
            return;
        }
        if (event.getTopObject() == null || !(event.getTopObject() instanceof AVList)) {
            return;
        }
        AVList av = ((AVList) event.getTopObject());
        String controlType = av.getStringValue("ISO_OPERATION");
        if (controlType == null) {
            return;
        }

        ScreenAnnotation selectedObject = (ScreenAnnotation) event.getTopObject();

        if (event.getEventAction().equals(SelectEvent.ROLLOVER)) {
            // Highlight on rollover
            this.getViewControlsLayer().highlight(selectedObject);
        } else if (event.getEventAction().equals(SelectEvent.HOVER)) {
            // Highlight on hover
            this.getViewControlsLayer().highlight(selectedObject);
        } else if (event.getEventAction().equals(SelectEvent.LEFT_CLICK)
                || event.getEventAction().equals(SelectEvent.LEFT_DOUBLE_CLICK)) {
            // Release pressed control
            this.pressedControl = null;
            this.pressedControlType = null;
        } // Keep pressed control highlighted - overrides rollover
        //non currently pressed controls
        else if (event.getEventAction().equals(SelectEvent.LEFT_PRESS)) {
            // Handle left press on controls
            this.pressedControl = selectedObject;
            this.pressedControlType = controlType;
            event.consume();
        }


        // Keep pressed control highlighted - overrides rollover non 
        //currently pressed controls
        if (this.pressedControl != null) {
            this.getViewControlsLayer().highlight(this.pressedControl);
        }
    }

    /**
     * Animates the volumes automatically with a play and stop buttons. TODO:
     * Fade animation
     *
     * @param layer renderable layer containing all the triangle grid to be
     * displayed, a nullPointerException is thrown if null.
     * @param layers all the triangle grids generated from the marching cubes
     * algorithm, a nullPointerException is thrown if null.
     */
    private void animate(final ArrayList<ElevatedRenderableLayer> layers) {
        checkAnimationTimer();
        animTimer = new Timer(INTERVALL, new ActionListener() {

            boolean forward = true;
            boolean fadein = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                change(layers.get(id));
                next();
            }

            private void next() {
                fadein = true;
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

            private void fadein() {
                if (layers.get(id) != null) {
                    ElevatedRenderableLayer layer = layers.get(id);
                    if (layer.getOpacity() + STEP > 1.0d) {
                        layer.setOpacity(1.0d);
                        fadein = false;
                    } else {
                        layer.setOpacity(layer.getOpacity() + STEP);
                    }
                } else {
                    next();
                }
            }

            private void fadeout() {
                boolean change = false;
                for (ElevatedRenderableLayer l : layers) {
                    if (l.getOpacity() > 0.0 && !l.equals(layers.get(id))) {
                        if (l.getOpacity() - STEP < 0.0d) {
                            l.setOpacity(0.0d);
                        } else {
                            l.setOpacity(l.getOpacity() - STEP);
                            change = true;
                        }
                    }
                }
                if (!change) {
                    next();
                }
            }
        });
        animTimer.start();

    }

    /**
     * Accesses the Control Layer
     * {@link com.dfki.av.sudplan.vis.mc.ControlLayer} associated with the
     * listener.
     *
     * @return Control Layer this listener is assigned to.
     */
    public WMSControlLayer getViewControlsLayer() {
        return viewControlsLayer;
    }

    /**
     * Accesses the list of grids
     * {@link com.dfki.av.sudplan.vis.mc.TriangleGrid} generated.
     *
     * @return ArrayList of the generated triangle grids.
     */
    public ArrayList<ElevatedRenderableLayer> getLayers() {
        return layers;
    }

    /**
     * Accesses the repeat timer responsible for fetching events and selected
     * annotations.
     *
     * @return Timer object associated with this listener.
     */
    public Timer getRepeatTimer() {
        return repeatTimer;
    }

    /**
     * Sets the repeat timer responsible for fetching events and selected
     * annotations.
     *
     * @param repeatTimer Timer object to be set as the repeat timer.
     */
    public void setRepeatTimer(Timer repeatTimer) {
        this.repeatTimer = repeatTimer;
    }

    @Override
    protected void doRender(DrawContext dc) {
    }

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
        updateView(viewControlsLayer.getSteps()[0], "");
    }
}
