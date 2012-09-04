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
import javax.swing.Timer;

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

    /**
     * Default timer delay.
     */
    private static final int DEFAULT_TIMER_DELAY = 90;
    /**
     * ControlLayer {@link com.dfki.av.sudplan.vis.mc.ControlLayer} instance
     * which this listener listens to.
     */
    private WMSControlLayer viewControlsLayer;
    /**
     * List of triangle grids {@link com.dfki.av.sudplan.vis.mc.TriangleGrid generated.
     */
    private ArrayList<ElevatedSurfaceLayer> layers;
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
     * Duration of one animation frame in ms
     */
    private final static int INTERVALL = 30;
    /**
     * Duration of the fade animation in ms
     */
    private final static int FADETIME = 400;
    /**
     * Amount of frames per frame animation
     */
    private final static int STEPS = FADETIME / INTERVALL;
    /**
     * Procentual change of the opacity per frame
     */
    private final static double STEP = 1.0d / (double) STEPS;
    /**
     * Font size
     */
    private final static float FONT_SIZE = 10.f;

    /**
     * Creates an instance of {@link WMSControlListener} which handles the input
     * from the {@link WMSControlLayer}.
     *
     * @param layer the corresponding {@link WMSControlLayer}
     * @param layers the list of {@link ElevatedSurfaceLayer} which are
     * controlled by the {@link WMSControlLayer}
     */
    public WMSControlListener(WMSControlLayer layer, ArrayList<ElevatedSurfaceLayer> layers) {
        if (layer == null) {
            String msg = Logging.getMessage("nullValue.LayerIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.viewControlsLayer = layer;
        this.layers = layers;

        this.repeatTimer = new Timer(DEFAULT_TIMER_DELAY, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                if (pressedControl != null) {
                    updateView(pressedControl, pressedControlType);
                }
            }
        });
        this.repeatTimer.start();
        init();
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
                            id = i;
                            animFlag = false;
                            checkAnimationTimer();
                            break;
                        }
                    }
                    break;
                }
            }
        }

        if (controlType.equals("ISO_PLUS")) {
            if (getViewControlsLayer().getSteps() != null) {
                if (id != 0) {
                    id--;
                }
            }
            animFlag = false;
            checkAnimationTimer();
        } else if (controlType.equals("ISO_MINUS")) {
            if (getViewControlsLayer().getSteps() != null) {
                if (id != this.getViewControlsLayer().getStepsRange().size() - 1) {
                    id++;
                }
            } else {
                if (id != 0) {
                    id--;
                }
            }
            animFlag = false;
            checkAnimationTimer();
        } else if (controlType.equals("ANIM_PLAY")) {
            if (!animFlag) {
                animFlag = true;
                animate(getLayers());
            }
        } else if (controlType.equals("ANIM_STOP")) {
            if (animFlag) {
                animFlag = false;
                checkAnimationTimer();
            }
        }

        mark();
        if (!animFlag) {
            if (getLayers().get(id) != null) {
                if (getLayers().get(id).getOpacity() < 1.0d) {
                    fade(getLayers().get(id));
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
     * Fades the previous layer out and the next layer in.
     *
     * @param layer the next layer which was selected on the {@link WMSControlLayer}
     */
    private void fade(final ElevatedSurfaceLayer layer) {
        animTimer = new Timer(INTERVALL, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (layer.getOpacity() + STEP > 1.0d) {
                    layer.setOpacity(1.0d);
                    boolean change = false;
                    for (ElevatedSurfaceLayer l : layers) {
                        if (l.getOpacity() > 0.0d && !l.equals(layer)) {
                            if (l.getOpacity() - STEP > 0.0d) {
                                l.setOpacity(l.getOpacity() - STEP);
                                change = true;
                            } else {
                                l.setOpacity(0.0d);
                            }
                        }
                    }
                    if (!change) {
                        animTimer.stop();
                    }
                } else {
                    layer.setOpacity(layer.getOpacity() + STEP);
                }
                firePropertyChange(EventHolder.WWD_REDRAW, null, null);
            }
        });
        animTimer.start();
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
     * Animates the volumes automatically with a play and stop buttons.
     *
     * @param layer renderable layer containing all the triangle grid to be
     * displayed, a nullPointerException is thrown if null.
     * @param layers all the triangle grids generated from the marching cubes
     * algorithm, a nullPointerException is thrown if null.
     */
    private void animate(final ArrayList<ElevatedSurfaceLayer> layers) {
        animTimer = new Timer(INTERVALL, new ActionListener() {

            boolean forward = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (layers.get(id) != null) {
                    ElevatedSurfaceLayer layer = layers.get(id);
                    if (layer.getOpacity() + STEP > 1.0d) {
                        layer.setOpacity(1.0d);
                        boolean change = false;
                        for (ElevatedSurfaceLayer l : layers) {
                            if (l.getOpacity() > 0.0d && !l.equals(layer)) {
                                if (l.getOpacity() - STEP > 0.0d) {
                                    l.setOpacity(l.getOpacity() - STEP);
                                    change = true;
                                } else {
                                    l.setOpacity(0.0d);
                                }
                            }
                        }
                        if (!change) {
                            next();
                        }
                    } else {
                        layer.setOpacity(layer.getOpacity() + STEP);
                    }
                    firePropertyChange(EventHolder.WWD_REDRAW, null, null);
                }

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
    public ArrayList<ElevatedSurfaceLayer> getLayers() {
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

    private void init() {
        fade(layers.get(0));
        mark();
    }

    @Override
    protected void doRender(DrawContext dc) {
    }
}
