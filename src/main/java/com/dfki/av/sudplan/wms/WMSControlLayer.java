/*
 *  ControlLayer.java
 *
 *  Created by DFKI AV on 22.08.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.util.OGLStackHandler;
import java.awt.*;
import java.util.ArrayList;
import javax.media.opengl.GL;

/**
 * ControlLayer that controls which wms layer should be displayed.
 *
 * Based on com.dfki.av.sudplan.vis.mc.ControlLayer
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class WMSControlLayer extends RenderableLayer {

    /**
     * File path of the icon used for incrementing iso-value or time step.
     */
    private final static String IMAGE_PLUS = "icons/arrow_up-32x32.png";
    /**
     * File path of the icon used for decrementing iso-value or time step.
     */
    private final static String IMAGE_MINUS = "icons/arrow_down-32x32.png";
    /**
     * File path of the icon used for starting the self-played animation.
     */
    private final static String IMAGE_PLAY = "icons/play-32x32.png";
    /**
     * File path of the icon used for stopping the self-played animation.
     */
    private final static String IMAGE_STOP = "icons/pause-32x32.png";
    private final static float FONT_SIZE = 10.f;
    /**
     * List of screen annotations
     * {@link gov.nasa.worldwind.render.ScreenAnnotation} for time steps.
     */
    private ScreenAnnotation[] steps;
    /**
     * Screen annotation {@link gov.nasa.worldwind.render.ScreenAnnotation} for
     * the increasing the iso-value of time step.
     */
    private ScreenAnnotation controlPlus;
    /**
     * Screen annotation {@link gov.nasa.worldwind.render.ScreenAnnotation} for
     * the decreasing the iso-value of time step.
     */
    private ScreenAnnotation controlMinus;
    /**
     * Screen annotation {@link gov.nasa.worldwind.render.ScreenAnnotation} for
     * starting the self-played animation.
     */
    private ScreenAnnotation animPlay;
    /**
     * Screen annotation {@link gov.nasa.worldwind.render.ScreenAnnotation} for
     * stopping the self-played animation.
     */
    private ScreenAnnotation animStop;
    /**
     * Screen annotation {@link gov.nasa.worldwind.render.ScreenAnnotation}
     * currently in use.
     */
    private ScreenAnnotation currentControl;
    /**
     * Screen annotation {@link gov.nasa.worldwind.render.ScreenAnnotation} of
     * the title.
     */
    private ScreenAnnotation title;
    /**
     * Boolean array indicating which screen annotation
     * {@link gov.nasa.worldwind.render.ScreenAnnotation} of iso-values or time
     * steps is selected.
     */
    private Rectangle referenceViewport;
    /**
     * Dimension object for size.
     */
    private Dimension size;
    /**
     * Color instance.
     */
    private Color color;
    /**
     * List time steps names.
     */
    private ArrayList<String> stepsRange;
    /**
     * North AVKey value.
     */
    private String position;
    /**
     * Horizontal AVKey value.
     */
    private String layout;
    /**
     * Vector {@link gov.nasa.worldwind.geom.Vec4} indicating the center
     * location.
     */
    private Vec4 locationCenter;
    /**
     * Vector {@link gov.nasa.worldwind.geom.Vec4} indicating the offset from
     * the center.
     */
    private Vec4 locationOffset;
    /**
     * Boolean flag to whether show controls or not.
     */
    private boolean[] valSelected;
    /**
     * Scale set to 1 (no scaling).
     */
    private double scale;
    /**
     * Width of borders.
     */
    private int borderWidth;
    /**
     * Size of buttons.
     */
    private int buttonSize;
    /**
     * Number of intervals in the side bar.
     */
    private int intervalCnt;
    /**
     * Boolean flag indicating initialization.
     */
    private boolean initialized;
    /**
     * Rectangle object of view port.
     */
    private boolean controlsShow;
    /**
     * Ordered icon {@link com.dfki.av.sudplan.vis.mc.OrderedIcon} object for
     * the side bar.
     */
    private OrderedIcon orderedImage;
    /**
     * List of {@link ElevatedSurfaceLayer} which the {@link WMSControlLayer}
     * can control.
     */
    private final ArrayList<ElevatedSurfaceLayer> layers;

    /**
     * Creates an instance of {@link WMSControlLayer} to switch between a list
     * of {@link ElevatedSurfaceLayer}.
     *
     * @param layerList
     */
    public WMSControlLayer(ArrayList<ElevatedSurfaceLayer> layerList) {
        initAttributes();
        stepsRange = new ArrayList<String>();
        for (ElevatedSurfaceLayer layer : layerList) {
            stepsRange.add(layer.getName().split(" ")[1]);
        }
        this.layers = layerList;
    }

    /**
     * Initialize attributes to default values and is alway called by the
     * constructor.
     */
    private void initAttributes() {
        size = new Dimension(150, 10);
        color = Color.white;
        position = AVKey.NORTH;
        layout = AVKey.HORIZONTAL;
        locationCenter = null;
        locationOffset = null;
        scale = 1;
        borderWidth = 20;
        buttonSize = 32;
        initialized = false;
        controlsShow = true;
        orderedImage = new OrderedIcon(this);
    }

    /**
     * Returns the list of {@link ElevatedSurfaceLayer} which can be controlled
     * by the {@link WMSControlLayer}.
     *
     * @return list of {@link ElevatedSurfaceLayer}s
     */
    public ArrayList<ElevatedSurfaceLayer> getLayers() {
        return layers;
    }

    /**
     * Accesses the time steps screen annotations
     * {@link gov.nasa.worldwind.render.ScreenAnnotation}.
     *
     * @return List of time steps screen annotations
     */
    public ScreenAnnotation[] getSteps() {
        return steps;
    }

    /**
     * Sets the time steps list of screen annotations
     * {@link gov.nasa.worldwind.render.ScreenAnnotation} to the input
     * parameter.
     *
     * @param steps List of screen annotations
     */
    private void setSteps(ScreenAnnotation[] steps) {
        this.steps = steps;
    }

    /**
     * Accesses the arrayList of the time steps.
     *
     * @return ArrayList of Strings of the time steps names.
     */
    public ArrayList<String> getStepsRange() {
        return stepsRange;
    }

    /**
     * Gets the boolean array in which the true indexes the selected screen
     * annotation.
     *
     * @return Array of booleans.
     */
    public boolean[] getValSelected() {
        return valSelected;
    }

    /**
     * Sets the ValSelected attribute to the input parameter. (The valSelected
     * is a boolean array in which the true indexes the selected screen
     * annotation.)
     *
     * @param valSelected Array of boolean must have the same size as the
     * annotations list (iso-values or time steps).
     */
    public void setValSelected(boolean[] valSelected) {
        this.valSelected = valSelected;
    }

    /**
     * Gets the flag indicating whether to display controls or not.
     *
     * @return True if the controls are to be shown and false if not.
     */
    public boolean isControlsShow() {
        return controlsShow;
    }

    /**
     * Sets the flag indicating whether to display controls or not.
     *
     * @param controlsShow If true controls are shown if not controls are not
     * shown.
     */
    public void setControlsShow(boolean controlsShow) {
        this.controlsShow = controlsShow;
    }

    /**
     * Indicates the currently highlighted control, if any.
     *
     * @return the currently highlighted control, or null if no control is
     * highlighted.
     */
    public Object getHighlightedObject() {
        return this.currentControl;
    }

    /**
     * Specifies the control to highlight. Any currently highlighted control is
     * un-highlighted.
     *
     * @param control The control to highlight.
     */
    public void highlight(Object control) {
        // Manage highlighting of controls.
        if (this.currentControl == control) {
            return; // same thing selected
        }
        // Turn off highlight if on.
        if (this.currentControl != null) {
            int index = -1;
            if (getSteps() != null) {
                for (int i = 0; i < getSteps().length; i++) {
                    if (currentControl.getText().equals(getSteps()[i].getText())) {
                        index = i;
                    }
                }
            }
            this.currentControl.getAttributes().setImageOpacity(-1); // use default opacity     
            if (index != -1 && !getValSelected()[index]) {
                Font font = currentControl.getAttributes().getFont();
                font = font.deriveFont(FONT_SIZE);
                currentControl.getAttributes().setFont(font);
                currentControl.getAttributes().setTextColor(Color.LIGHT_GRAY);
            }
            this.currentControl = null;

        }

        // Turn on highlight if object selected.
        if (control != null && control instanceof ScreenAnnotation) {
            this.currentControl = (ScreenAnnotation) control;
            this.currentControl.getAttributes().setImageOpacity(1);
            if (currentControl.getText() != null) {
                Font font = currentControl.getAttributes().getFont();
                if (getSteps() == null) {
                    font = font.deriveFont(FONT_SIZE);
                } else {
                    font = font.deriveFont(FONT_SIZE);
                }
                currentControl.getAttributes().setFont(font);
                currentControl.getAttributes().setTextColor(Color.WHITE);
            }
        }
        if (this.currentControl == null) {

            if (getSteps() != null) {
                for (ScreenAnnotation current : getSteps()) {
                    if (current.getText() != null
                            && !getValSelected()[stepsRange.indexOf(
                            current.getText())]) {
                        Font font = current.getAttributes().getFont();
                        font = font.deriveFont(FONT_SIZE);
                        current.getAttributes().setFont(font);
                        current.getAttributes().setTextColor(Color.LIGHT_GRAY);
                    }

                    current = null;
                }
            }
        }
    }

    /**
     * Checks whether current ContorlLayer is initialized or not.
     *
     * @return Boolean value of the initialized flag.
     */
    protected boolean isInitialized() {
        return initialized;
    }

    /**
     * Initialize current ControlLayer if not initialized with all its
     * components and their attributes.
     *
     * @param dc DrawContext used.
     */
    protected void initialize(DrawContext dc) {
        if (this.initialized) {
            return;
        }

        // Setup user interface - common default attributes
        AnnotationAttributes ca = new AnnotationAttributes();
        ca.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
        ca.setInsets(new Insets(0, 0, 0, 0));
        ca.setBorderWidth(0);
        ca.setCornerRadius(0);
        ca.setSize(new Dimension(buttonSize, buttonSize));
        ca.setBackgroundColor(new Color(0, 0, 0, 0));
        ca.setImageOpacity(.5);
        ca.setScale(scale);


        AnnotationAttributes ca2 = new AnnotationAttributes();
        ca2.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
        ca2.setBackgroundColor(new Color(0, 0, 0, 0));
        ca2.setTextColor(Color.LIGHT_GRAY);
        ca2.setHighlightScale(2d);
        ca2.setInsets(new Insets(0, 0, 0, 0));
        ca2.setBorderWidth(0);
        ca2.setCornerRadius(0);
        ca2.setFont(new Font("Book Antiqua", Font.BOLD, (int) FONT_SIZE));
        ca2.setSize(new Dimension(buttonSize, 20));
        ca2.setImageOpacity(.8);
        ca2.setScale(scale);

        final String NOTEXT = "";
        final Point ORIGIN = new Point(0, 0);

        if (getStepsRange() != null) {
            setSteps(new ScreenAnnotation[getStepsRange().size()]);
            setValSelected(new boolean[getStepsRange().size()]);

            for (int i = 0; i < getSteps().length; i++) {
                getSteps()[i] = new ScreenAnnotation(getStepsRange().get(i),
                        ORIGIN, ca2);
                getSteps()[i].setValue("ISO_OPERATION", getStepsRange().get(i));
                if (!this.isControlsShow()) {
                    getSteps()[i].getAttributes().setTextColor(Color.WHITE);
                }
                this.addRenderable(getSteps()[i]);
            }

        }

        if (this.isControlsShow()) {
            controlPlus = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
            controlPlus.setValue("ISO_OPERATION", "ISO_PLUS");
            controlPlus.getAttributes().setImageSource(
                    getImageSource("ISO_PLUS"));
            controlPlus.setScreenPoint(new Point(50, 100));

            this.addRenderable(controlPlus);

            controlMinus = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
            controlMinus.setValue("ISO_OPERATION", "ISO_MINUS");
            controlMinus.getAttributes().setImageSource(
                    getImageSource("ISO_MINUS"));

            this.addRenderable(controlMinus);


            animPlay = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
            animPlay.setValue("ISO_OPERATION", "ANIM_PLAY");
            animPlay.getAttributes().setImageSource(IMAGE_PLAY);

            this.addRenderable(animPlay);


            animStop = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
            animStop.setValue("ISO_OPERATION", "ANIM_STOP");
            animStop.getAttributes().setImageSource(IMAGE_STOP);

            this.addRenderable(animStop);
        }



        if (!this.isControlsShow()) {
            title = new ScreenAnnotation("IsoValue ", ORIGIN, ca2);
            title.getAttributes().setTextColor(Color.WHITE);
            title.getAttributes().setFont(new Font(
                    "Book Antiqua", Font.BOLD, 4));
            this.addRenderable(title);
        }
        // Place controls according to layout and viewport dimension
        updatePositions(dc);

        this.initialized = true;
    }

    /**
     * Get a control image source and returns null if the parameter is not
     * recognized.
     *
     * @param control the control type. Can be one of {"ISO_PLUS"} or
     * {"ISO_MINUS"}.
     *
     * @return the image source associated with the given control type.
     */
    protected Object getImageSource(String control) {
        if (control.equals("ISO_PLUS")) {
            return IMAGE_PLUS;
        } else if (control.equals("ISO_MINUS")) {
            return IMAGE_MINUS;
        }
        return null;
    }

    /**
     * Sets controls positions according to layout and viewport dimension.
     *
     * @param dc Draw context currently used.
     */
    protected void updatePositions(DrawContext dc) {
        boolean horizontalLayout = this.layout.equals(AVKey.HORIZONTAL);

        int width = 192;
        int height = buttonSize;
        width = (int) (width * scale);
        height = (int) (height * scale);
        int xOffset = 0;
        int yOffset = 0;

        if (!horizontalLayout) {
            int temp = height;
            height = width;
            width = temp;
            xOffset = 0;
            yOffset = 0;
        }

        int halfButtonSize = (int) (buttonSize * scale / 2);

        Rectangle controlsRectangle = new Rectangle(width, height);
        Point locationSW = computeLocation(dc.getView().getViewport(),
                controlsRectangle);

        // Layout start point
        int x = locationSW.x;
        int y = horizontalLayout ? locationSW.y : locationSW.y + height;

        if (this.isControlsShow()) {
            if (!horizontalLayout) {
                y -= (int) (buttonSize * scale);
            }
            if (this.isControlsShow()) {
                controlPlus.setScreenPoint(new Point(x + halfButtonSize
                        + xOffset + 140, y + yOffset - 120));
            }

            if (horizontalLayout) {
                x += (int) (buttonSize * scale);
            }
        } else {
            title.setScreenPoint(new Point(x + halfButtonSize
                    + xOffset + 110, y + yOffset - 130));
        }

        int i = 0;
        if (getSteps() != null) {
            for (i = 0; i < getSteps().length; i++) {
                getSteps()[i].setScreenPoint(new Point(x + halfButtonSize
                        + xOffset + 100, y - 150 - (int) (FONT_SIZE * 1.1f) * i));
            }
        }
        if (this.isControlsShow()) {
            controlMinus.setScreenPoint(new Point(x + halfButtonSize
                    + 105, y - 175 - (int) (FONT_SIZE * 1.1f) * i));
            animPlay.setScreenPoint(new Point(x + halfButtonSize + 60,
                    y + - 175 - (int) (FONT_SIZE * 1.1f) * i));
            animStop.setScreenPoint(new Point(x + halfButtonSize + 150,
                    y + - 175 - (int) (FONT_SIZE * 1.1f) * i));
        }
        this.referenceViewport = dc.getView().getViewport();
    }

    /**
     * Compute the screen location of the controls overall rectangle bottom
     * right corner according to either the location center if not null, or the
     * screen position.
     *
     * @param viewport the current viewport rectangle.
     * @param controls the overall controls rectangle
     *
     * @return the screen location of the bottom left corner - south west
     * corner.
     */
    protected Point computeLocation(Rectangle viewport, Rectangle controls) {
        double x;
        double y;

        if (this.locationCenter != null) {
            x = this.locationCenter.x - controls.width / 2;
            y = this.locationCenter.y - controls.height / 2;
        } else if (this.position.equals(AVKey.NORTHEAST)) {
            x = viewport.getWidth() - controls.width - this.borderWidth;
            y = viewport.getHeight() - controls.height - this.borderWidth;
        } else if (this.position.equals(AVKey.SOUTHEAST)) {
            x = viewport.getWidth() - controls.width - this.borderWidth;
            y = 0d + this.borderWidth;
        } else if (this.position.equals(AVKey.NORTHWEST)) {
            x = 0d + this.borderWidth;
            y = viewport.getHeight() - controls.height - this.borderWidth;
        } else if (this.position.equals(AVKey.SOUTHWEST)) {
            x = 0d + this.borderWidth;
            y = 0d + this.borderWidth;
        } else // use North East as default
        {
            x = viewport.getWidth() - controls.width - this.borderWidth;
            y = viewport.getHeight() - controls.height - this.borderWidth;
        }

        if (this.locationOffset != null) {
            x += this.locationOffset.x;
            y += this.locationOffset.y;
        }

        return new Point((int) x, (int) y);
    }

    /**
     * Clears all the controls and removes all the renderable objects.
     */
    protected void clearControls() {
        this.removeAllRenderables();
        this.controlPlus = null;
        this.controlMinus = null;
        this.animPlay = null;
        this.animStop = null;
        this.initialized = false;
    }

    /**
     * Renders or draw the layer.
     *
     * @param dc Draw context used.
     */
    public void draw(DrawContext dc) {
        GL gl = dc.getGL();

        OGLStackHandler ogsh = new OGLStackHandler();

        try {
            ogsh.pushAttrib(gl, GL.GL_TRANSFORM_BIT);

            gl.glDisable(GL.GL_DEPTH_TEST);

            double width = this.size.width;
            double height = this.size.height;
            java.awt.Rectangle viewport = dc.getView().getViewport();
            ogsh.pushProjectionIdentity(gl);
            double maxwh = width > height ? width : height;
            gl.glOrtho(0d, viewport.width, 0d, viewport.height, -0.6 * maxwh,
                    0.6 * maxwh);

            ogsh.pushModelviewIdentity(gl);

            // Compute scale size in real world
            Position referencePosition = dc.getViewportCenterPosition();
            if (referencePosition != null) {
                // Draw scale
                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

                // Set color using current layer opacity
                Color backColor = this.getBackgroundColor(this.color);
                float[] colorRGB = backColor.getRGBColorComponents(null);
                gl.glColor4d(colorRGB[0], colorRGB[1], colorRGB[2],
                        (double) backColor.getAlpha() / 255d
                        * this.getOpacity());
                this.drawScale(dc);

                colorRGB = this.color.getRGBColorComponents(null);
                gl.glColor4d(colorRGB[0], colorRGB[1], colorRGB[2],
                        this.getOpacity());
                this.drawScale(dc);

                // Draw label
                gl.glLoadIdentity();
                gl.glDisable(GL.GL_CULL_FACE);

            } else {
                this.drawScale(dc);
            }
        } finally {
            gl.glColor4d(1d, 1d, 1d, 1d); // restore the default OpenGL color
            gl.glEnable(GL.GL_DEPTH_TEST);

            gl.glBlendFunc(GL.GL_ONE, GL.GL_ZERO); // restore to default blend function
            gl.glDisable(GL.GL_BLEND); // restore to default blend state

            ogsh.pop(gl);
        }
    }

    /**
     * Renders or draws the side bar in white color.
     *
     * @param dc Draw context used.
     */
    private void drawScale(DrawContext dc) {
        if (intervalCnt == 0) {
            return;
        }
        java.awt.Rectangle rect = dc.getView().getViewport();
        GL gl = dc.getGL();
        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glVertex3d(rect.width - 30, rect.height - 190, 0);
        gl.glVertex3d(rect.width - 20, rect.height - 190, 0);
        gl.glVertex3d(rect.width - 20, rect.height - 190 - 40 * (intervalCnt - 1),
                0);
        gl.glVertex3d(rect.width - 30, rect.height - 190 - 40 * (intervalCnt - 1),
                0);
        gl.glEnd();
        double intervalHeight = 40;
        for (int i = 1; i <= intervalCnt - 2; i++) {
            gl.glBegin(GL.GL_LINE_STRIP);
            gl.glVertex3d(rect.width - 30, rect.height - 190 - i * intervalHeight,
                    0);
            gl.glVertex3d(rect.width - 20, rect.height - 190 - i * intervalHeight,
                    0);
            gl.glEnd();
        }
    }
    private final float[] compArray = new float[4];

    /**
     * Compute background color for best contrast.
     *
     * @param color input color
     * @return color that gives the best contrast with the input color
     */
    private Color getBackgroundColor(Color color) {
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(),
                compArray);
        if (compArray[2] > 0.5) {
            return new Color(0, 0, 0, 0.7f);
        } else {
            return new Color(1, 1, 1, 0.7f);
        }
    }

    @Override
    public void setEnabled(boolean bln) {
        super.setEnabled(bln);
        for (ElevatedSurfaceLayer l : getLayers()) {
            l.setEnabled(bln);
        }
    }

    @Override
    public void doRender(DrawContext dc) {
        if (!this.initialized) {
            initialize(dc);
        }

        if (!this.referenceViewport.equals(dc.getView().getViewport())) {
            updatePositions(dc);
        }

        super.doRender(dc);
        dc.addOrderedRenderable(this.orderedImage);
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public void doPick(DrawContext dc, Point pickPoint) {
        super.doPick(dc, pickPoint);
        dc.addOrderedRenderable(this.orderedImage);
    }
}
