/*
 *  WMSControlLayer.java
 *
 *  Created by DFKI AV on 22.08.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.wms;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.util.OGLStackHandler;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;

/**
 * ControlLayer that controls which WMS layer should be displayed.
 *
 * Based on {@code de.dfki.av.geovisualizer.vis.mc.ControlLayer}.
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class WMSControlLayer extends RenderableLayer {

    /**
     * Array to store color transformation data
     */
    private final float[] compArray = new float[4];
    /**
     * Size of the fonts
     */
    public final static float FONT_SIZE = 12.f;
    /**
     * The {@link Font} used by the {@link WMSControlLayer}.
     */
    public final static Font FONT = new Font("Arial", Font.BOLD, (int) FONT_SIZE);
    /**
     * File path of the icon used for switching to the layer above.
     * btn-sqare-top.png 147 x 23
     */
    private final static String IMAGE_UP = "icons/btn-sqare-top.png";
    /**
     * File path of the icon used for switching to the layer below.
     * btn-sqare-down.png 147 x 23
     */
    private final static String IMAGE_DOWN = "icons/btn-sqare-down.png";
    /**
     * File path of the icon used for starting the self-played animation.
     * btn-play.png 22 x 22
     */
    private final static String IMAGE_PLAY = "icons/btn-play.png";
    /**
     * File path of the icon used for stopping the self-played animation.
     * btn-pause.png 22 x 22
     */
    private final static String IMAGE_PAUSE = "icons/btn-pause.png";
    /**
     * Empty string for screen annotations without text
     */
    private final static String NOTEXT = "";
    /**
     * Origin point
     */
    private final static Point ORIGIN = new Point(0, 0);
    /**
     * List of screen annotations
     * {@link gov.nasa.worldwind.render.ScreenAnnotation} for time steps.
     */
    private ScreenAnnotation[] steps;
    /**
     * Screen annotation {@link gov.nasa.worldwind.render.ScreenAnnotation} for
     * the increasing the iso-value of time step.
     */
    private ScreenAnnotation controlUp;
    /**
     * Screen annotation {@link gov.nasa.worldwind.render.ScreenAnnotation} for
     * the decreasing the iso-value of time step.
     */
    private ScreenAnnotation controlDpwn;
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
    private List<String> stepsRange;
    /**
     * North AVKey value.
     */
    private String position;
    /**
     * Horizontal AVKey value.
     */
    private String layout;
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
     * Boolean flag indicating initialization.
     */
    private boolean initialized;
    /**
     * Rectangle object of view port.
     */
    private boolean controlsShow;
    /**
     * List of {@link ElevatedRenderableLayer} which the {@link WMSControlLayer}
     * can control.
     */
    private final List<ElevatedRenderableLayer> layers;
    /**
     * Control layer title annotation
     */
    private ScreenAnnotation controlTitle;
    /**
     * Id to handle multiple instances of {@link WMSControlLayer}
     */
    private final String id;

    /**
     * Creates an instance of {@link WMSControlLayer} to switch between a list
     * of {@link ElevatedRenderableLayer}.
     *
     * @param layerList
     */
    public WMSControlLayer(List<ElevatedRenderableLayer> layerList) {
        initAttributes();
        id = String.valueOf(System.currentTimeMillis());
        stepsRange = new ArrayList<>();
        for (ElevatedRenderableLayer layer : layerList) {
            String[] parts = layer.getName().split(" ");
            if (parts.length > 1) {
                String suffix = parts[1];
                String[] suffixParts = suffix.split("_");
                if (suffixParts.length > 0) {
                    stepsRange.add(suffix.split("_")[0]);
                }
            } else {
                stepsRange.add("");
            }
        }
        this.layers = layerList;
        this.steps = new ScreenAnnotation[stepsRange.size()];
        this.valSelected = new boolean[stepsRange.size()];

        AnnotationAttributes ca = new AnnotationAttributes();
        ca.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
        ca.setBackgroundColor(new Color(0, 0, 0, 0));
        ca.setTextColor(Color.LIGHT_GRAY);
        ca.setHighlightScale(2d);
        ca.setInsets(new Insets(0, 0, 0, 0));
        ca.setBorderWidth(0);
        ca.setCornerRadius(0);
        ca.setFont(WMSControlLayer.FONT);
        ca.setSize(new Dimension(size.width, (int) (FONT_SIZE * 1.6)));
        ca.setImageOpacity(.8);
        ca.setScale(scale);

        for (int i = 0; i < steps.length; i++) {
            steps[i] = new ScreenAnnotation(stepsRange.get(i),
                    ORIGIN, ca);
            steps[i].setValue(id + WMSControlType.CONTROL_ACTION, stepsRange.get(i));
            if (!this.controlsShow) {
                steps[i].getAttributes().setTextColor(Color.WHITE);
            }
            this.addRenderable(steps[i]);
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
        AnnotationAttributes ca0 = new AnnotationAttributes();
        ca0.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
        ca0.setInsets(new Insets(0, 0, 0, 0));
        ca0.setBorderWidth(0);
        ca0.setCornerRadius(0);
        ca0.setSize(new Dimension(buttonSize, buttonSize));
        ca0.setBackgroundColor(new Color(0, 0, 0, 0));
        ca0.setImageOpacity(.5);
        ca0.setScale(scale);

        // Setup user interface - common default attributes
        AnnotationAttributes ca = new AnnotationAttributes();
        ca.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
        ca.setInsets(new Insets(0, 0, 0, 0));
        ca.setBorderWidth(0);
        ca.setCornerRadius(0);
        ca.setSize(new Dimension(147, 23));
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
        ca2.setFont(WMSControlLayer.FONT);
        ca2.setSize(new Dimension(size.width, (int) (FONT_SIZE * 1.6)));
        ca2.setImageOpacity(.8);
        ca2.setScale(scale);

        controlTitle = new ScreenAnnotation(this.getName(), ORIGIN, ca2);
        controlTitle.getAttributes().setTextColor(Color.WHITE);
        controlTitle.getAttributes().setSize(new Dimension(size.width, (int) (FONT_SIZE * 1.7f)));
        controlTitle.getAttributes().setFont(FONT);
        this.addRenderable(controlTitle);

        if (this.isControlsShow()) {
            controlUp = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
            controlUp.setValue(id + WMSControlType.CONTROL_ACTION, WMSControlType.CONTROL_UP);
            controlUp.getAttributes().setImageSource(IMAGE_UP);

            this.addRenderable(controlUp);

            controlDpwn = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
            controlDpwn.setValue(id + WMSControlType.CONTROL_ACTION, WMSControlType.CONTROL_DOWN);
            controlDpwn.getAttributes().setImageSource(IMAGE_DOWN);

            this.addRenderable(controlDpwn);


            animPlay = new ScreenAnnotation(NOTEXT, ORIGIN, ca0);
            animPlay.setValue(id + WMSControlType.CONTROL_ACTION, WMSControlType.CONTROL_PLAY);
            animPlay.getAttributes().setImageSource(IMAGE_PLAY);

            this.addRenderable(animPlay);


            animStop = new ScreenAnnotation(NOTEXT, ORIGIN, ca0);
            animStop.setValue(id + WMSControlType.CONTROL_ACTION, WMSControlType.CONTROL_PAUSE);
            animStop.getAttributes().setImageSource(IMAGE_PAUSE);

            this.addRenderable(animStop);
        }
        // Place controls according to layout and viewport dimension
        updatePositions(dc);

        this.initialized = true;
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
        int xOffset = 90;
        int yOffset = - 150;

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
        int xOrigin = locationSW.x;
        int yOrigin = horizontalLayout ? locationSW.y : locationSW.y + height;

        int x = xOrigin + halfButtonSize + xOffset;
        int y = yOrigin + yOffset;
        AnnotationAttributes attributes;

        if (this.isControlsShow()) {
            controlTitle.setScreenPoint(new Point(x, y));
            attributes = controlTitle.getAttributes();
            y -= (int) (attributes.getSize().height * attributes.getScale());
            y -= halfButtonSize;
            animPlay.setScreenPoint(new Point(x + (int) (buttonSize * scale), y));
            animStop.setScreenPoint(new Point(x - (int) (buttonSize * scale), y));
            y -= (int) (buttonSize * scale);
            controlUp.setScreenPoint(new Point(x, y));
        }

        y = y - (int) (buttonSize * scale);
        if (getSteps() != null) {
            for (ScreenAnnotation annotation : getSteps()) {
                annotation.setScreenPoint(new Point(x, y));
                attributes = annotation.getAttributes();
                y -= (int) (attributes.getSize().height * attributes.getScale());
            }
        }
        if (this.isControlsShow()) {
            y -= (int) (FONT_SIZE * 1.2f);
            controlDpwn.setScreenPoint(new Point(x, y));
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
        switch (this.position) {
            case AVKey.NORTHEAST:
                x = viewport.getWidth() - controls.width - this.borderWidth;
                y = viewport.getHeight() - controls.height - this.borderWidth;
                break;
            case AVKey.SOUTHEAST:
                x = viewport.getWidth() - controls.width - this.borderWidth;
                y = 0d + this.borderWidth;
                break;
            case AVKey.NORTHWEST:
                x = 0d + this.borderWidth;
                y = viewport.getHeight() - controls.height - this.borderWidth;
                break;
            case AVKey.SOUTHWEST: 
            default:
                x = 0d + this.borderWidth;
                y = 0d + this.borderWidth;
                break;
        }
        return new Point((int) x, (int) y);
    }

    /**
     * Clears all the controls and removes all the renderable objects.
     */
    protected void clearControls() {
        this.removeAllRenderables();
        this.controlUp = null;
        this.controlDpwn = null;
        this.animPlay = null;
        this.animStop = null;
        this.initialized = false;
    }

    /**
     * Returns the id of the control layer.
     *
     * @return the id of the control layer
     */
    public String getId() {
        return id;
    }

    /**
     * Initialize attributes to default values and is alway called by the
     * constructor.
     */
    private void initAttributes() {
        size = new Dimension(185, 300);
        color = Color.white;
        position = AVKey.NORTH;
        layout = AVKey.HORIZONTAL;
        scale = 1;
        borderWidth = 20;
        buttonSize = 22;
        initialized = false;
        controlsShow = true;
    }

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

    /**
     * Returns the list of {@link ElevatedRenderableLayer} which can be
     * controlled by the {@link WMSControlLayer}.
     *
     * @return list of {@link ElevatedRenderableLayer}s
     */
    public List<ElevatedRenderableLayer> getLayers() {
        return layers;
    }

    /**
     * Accesses the time steps screen annotations
     * {@link gov.nasa.worldwind.render.ScreenAnnotation}.
     *
     * @return List of time steps screen annotations
     */
    public ScreenAnnotation[] getSteps() {
        return steps.clone();
    }

    /**
     * Accesses the arrayList of the time steps.
     *
     * @return ArrayList of Strings of the time steps names.
     */
    public List<String> getStepsRange() {
        return stepsRange;
    }

    /**
     * Gets the boolean array in which the true indexes the selected screen
     * annotation.
     *
     * @return Array of booleans.
     */
    public boolean[] getValSelected() {
        return valSelected.clone();
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
                font = font.deriveFont(FONT_SIZE);
                currentControl.getAttributes().setFont(font);
                currentControl.getAttributes().setTextColor(Color.WHITE);
            }
        }
        if (this.currentControl == null && getSteps() != null) {
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
                
                colorRGB = this.color.getRGBColorComponents(null);
                gl.glColor4d(colorRGB[0], colorRGB[1], colorRGB[2],
                        this.getOpacity());

                // Draw label
                gl.glLoadIdentity();
                gl.glDisable(GL.GL_CULL_FACE);

            }
        } finally {
            gl.glColor4d(1d, 1d, 1d, 1d); // restore the default OpenGL color
            gl.glEnable(GL.GL_DEPTH_TEST);

            gl.glBlendFunc(GL.GL_ONE, GL.GL_ZERO); // restore to default blend function
            gl.glDisable(GL.GL_BLEND); // restore to default blend state

            ogsh.pop(gl);
        }
    }

    @Override
    public void setEnabled(boolean bln) {
        super.setEnabled(bln);
        for (ElevatedRenderableLayer l : getLayers()) {
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
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
