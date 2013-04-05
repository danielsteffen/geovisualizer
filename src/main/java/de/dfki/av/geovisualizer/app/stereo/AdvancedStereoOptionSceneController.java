/*
 * AdvancedStereoOptionSceneController.java
 *
 * Created by DFKI AV on 01.08.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.stereo;

/*
 * Copyright (C) 2011 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration. All
 * Rights Reserved.
 */
import gov.nasa.worldwind.StereoOptionSceneController;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This file is updated to implement "correct" stereo, as described at:
 * http://www.orthostereo.com/geometryopengl.html
 *
 * This class extends StereoOptionSceneController with all its functionalities
 * and adds to it advanced side by side stereo mode by overriding only the draw
 * method. The underlying approach used it the render-to-texture approach to
 * take draw the scene to a texture for the left eye, apply the asymmetric
 * frustum shift, draw the scene to a texture for the right eye, and finally
 * swap both texture buffers with the frame display buffers of the 2 screens.
 *
 * @author tarek
 */
public class AdvancedStereoOptionSceneController
        extends StereoOptionSceneController {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(AdvancedStereoOptionSceneController.class);
    /**
     * Index of the frame buffer object.
     */
    private int fbo;
    /**
     * Index of the texture buffer for the left eye.
     */
    private int texLeftEye;
    /**
     * Index of the texture buffer for the right eye.
     */
    private int texRightEye;
    /**
     * Index of the depth buffer.
     */
    private int depthBuffer;
    /**
     * Width of the screen.
     */
    private int width;
    /**
     * Height of the screen.
     */
    private int height;
    /**
     * Translation of one view away from the other.
     */
    private float modeltranslation;
    /**
     * Left side of the viewing frustum.
     */
    private double leftfrustum;
    /**
     * Right side of the viewing frustum.
     */
    private double rightfrustum;
    /**
     * Bottom side of the viewing frustum.
     */
    private double bottomfrustum;
    /**
     * Top side of the viewing frustum.
     */
    private double topfrustum;
    /**
     * Aspect ratio of the screen.
     */
    private double aspect;
    /**
     * Near side of the viewing frustum.
     */
    private double nearZ;
    /**
     * Far side of the viewing frustum.
     */
    private double farZ;
    /**
     * Depth of the screen plane. (Distance between the screen and the viewer.)
     */
    private double screenZ;
    /**
     * Intra ocular distance. (Eye separation length).
     */
    private double iod;
    /**
     * Indicates the first time to enter the side by side mode when its value is
     * true.
     */
    private boolean firstTime;
    /**
     * Set to true to run in side by side stereo mode.
     */
    private boolean sideBySide;

    /**
     * Constructs a new instance of the advancedStereoOptionSceneController. It
     * initializes the super class as well (StereoOptionSceneController) and
     * initializes the variables IOD, screenZ, sideBySide, and first time to
     * default values 0.5, 150, false, and true respectively.
     */
    public AdvancedStereoOptionSceneController() {
        super();
        iod = 0.5;
        screenZ = 150;
        sideBySide = false;
        firstTime = true;
    }

    /**
     * Checks if side by side stereo mode is activated or not. If yes, it call
     * draw side by side method. If no, it proceeds with the same behavior of
     * the draw method in the super class StereoOptionSceneController.
     *
     * @param dc Draw context, nullPointerExeption is thrown if null.
     */
    @Override
    public void draw(DrawContext dc) {
        if (dc == null) {
            throw new NullPointerException("DrawContext is null in draw method");
        }
        if (sideBySide) {
            this.doDrawSideBySide(dc);
            return;
        }
        super.draw(dc);
    }

    /**
     * Checks if side by side stereo mode is activated or not.
     *
     * @return True if side by side stereo mode is activated and false if not.
     */
    public boolean isSideBySide() {
        return sideBySide;
    }

    /**
     * Activates or deactivates the side by side stereo mode according to the
     * input parameter.
     *
     * @param sideBySide True to activate and false to deactivate the side by
     * side stereo mode.
     */
    public void setSideBySide(boolean sideBySide) {
        this.sideBySide = sideBySide;
    }

    /**
     * Render the side by side stereo following the render to texture approach
     * for left and right eyes and then swap buffers with the screen buffers.
     *
     * @param dc the {@link DrawContext}
     * @throws NullPointerException if dc == null.
     */
    private void doDrawSideBySide(DrawContext dc) {
        if (dc == null) {
            String msg = "DrawContext == null";
            LOG.error(msg);
            throw new NullPointerException(msg);
        }
        GL gl = dc.getGL();
        if (firstTime) {
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDepthFunc(GL.GL_LEQUAL);

            // Enable smooth shading.
            gl.glShadeModel(GL.GL_SMOOTH);

            // Define "clear" color.
            gl.glClearColor(0f, 0f, 0f, 0f);

            // Nice perspective.
            gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

            // Create texture for left eye
            final int[] tmpLeft = new int[1];
            gl.glGenTextures(1, tmpLeft, 0);
            texLeftEye = tmpLeft[0];
            gl.glBindTexture(GL.GL_TEXTURE_2D, tmpLeft[0]);
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                    GL.GL_NEAREST);
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
                    GL.GL_NEAREST);
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
                    GL.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
                    GL.GL_CLAMP_TO_EDGE);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA8, (int) getWidth(),
                    (int) getHeight(), 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

            // Create texture for right eye
            final int[] tmpRight = new int[1];
            gl.glGenTextures(1, tmpRight, 0);
            texRightEye = tmpRight[0];
            gl.glBindTexture(GL.GL_TEXTURE_2D, tmpRight[0]);
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                    GL.GL_NEAREST);
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
                    GL.GL_NEAREST);
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
                    GL.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
                    GL.GL_CLAMP_TO_EDGE);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA8, (int) getWidth(),
                    (int) getHeight(), 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

            // Create a framebufferobject
            int[] array = new int[1];
            IntBuffer ib = IntBuffer.wrap(array);
            gl.glGenFramebuffersEXT(1, ib);
            this.fbo = ib.get(0);

            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fbo);

            int[] array1 = new int[1];
            IntBuffer ib1 = IntBuffer.wrap(array1);
            gl.glGenRenderbuffersEXT(1, ib1);
            this.depthBuffer = ib.get(0);

            gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, depthBuffer);
            gl.glRenderbufferStorageEXT(GL.GL_RENDERBUFFER_EXT,
                    GL.GL_DEPTH_STENCIL_NV, getWidth(), getHeight());

            gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT,
                    GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D, texLeftEye, 0);
            gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT,
                    GL.GL_COLOR_ATTACHMENT1_EXT, GL.GL_TEXTURE_2D, texRightEye, 0);
            gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT,
                    GL.GL_DEPTH_ATTACHMENT_EXT, GL.GL_RENDERBUFFER_EXT,
                    depthBuffer);
            gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT,
                    GL.GL_STENCIL_ATTACHMENT_EXT, GL.GL_RENDERBUFFER_EXT,
                    depthBuffer);
            // Reset framebuffer.
            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);

        }

        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fbo);

        gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
        gl.glViewport(0, 0, getWidth(), getHeight());
        gl.glPushMatrix();
        gl.glDrawBuffer(GL.GL_COLOR_ATTACHMENT0_EXT);
        renderScene(gl, !isSwapEyes());
        gl.glPopMatrix();
        gl.glPopAttrib();

        gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
        gl.glViewport(0, 0, getWidth(), getHeight());
        gl.glPushMatrix();
        gl.glDrawBuffer(GL.GL_COLOR_ATTACHMENT1_EXT);
        renderScene(gl, isSwapEyes());
        gl.glPopMatrix();
        gl.glPopAttrib();

        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);


        // Clear screen.
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // Set camera.
        // Change to projection matrix.
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        // Ortho.
        gl.glOrtho(0.0, getWidth() * 2, 0.0, getHeight(), -1.0, 1.0);

        // Change back to model view matrix.
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texLeftEye);
        // Write triangle.
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex2f(0.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex2f(getWidth(), 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex2f(getWidth(), getHeight());
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex2f(0.0f, getHeight());
        gl.glEnd();
        gl.glDisable(GL.GL_TEXTURE_2D);

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texRightEye);
        // Write triangle.
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex2f(getWidth(), 0.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex2f(getWidth() * 2, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex2f(getWidth() * 2, getHeight());
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex2f(getWidth(), getHeight());
        gl.glEnd();
        gl.glDisable(GL.GL_TEXTURE_2D);
    }

    /**
     * Renders the actual scene with the asymmetric frustum shift stated in the
     * paper mentioned above.
     *
     * @param gl {@link GL} context derived from current draw context,
     * @param flag Indicates which eye is currently drawn; {@code true} for the
     * left eye and{@code false} for the right eye.
     * @throws NullPointerException if gl == null
     */
    private void renderScene(GL gl, boolean flag) {

        if (gl == null) {
            String msg = "GL == null";
            LOG.error(msg);
            throw new NullPointerException(msg);
        }

        super.initializeFrame(dc);
        try {
            super.applyView(dc);
            super.createTerrain(dc);
            super.clearFrame(dc);
            super.pick(dc);
            super.clearFrame(dc);

            BasicOrbitView currentView = (BasicOrbitView) dc.getView();
            if (firstTime) {
                firstTime = false;
                currentView.setZoom(currentView.getZoom() * 2);
                currentView.apply(dc);
            }
            nearZ = currentView.getNearClipDistance() + screenZ;
            farZ = currentView.getFarClipDistance() + screenZ;
            double angle = 0.0174532925 * currentView.getFieldOfView().degrees;
            double right = nearZ * Math.tan(angle / 2.0);
            double top = right / this.getAspect();
            double frustumShift = (getIOD() / 2.0) * nearZ / screenZ;
            topfrustum = top / 2.0;
            bottomfrustum = -top / 2.0;
            if (flag) { // Left eye
                leftfrustum = (-right + frustumShift) / 2.0;
                rightfrustum = (right + frustumShift) / 2.0;
                modeltranslation = (float) (getIOD() / 2.0);
            } else { // Right eye
                leftfrustum = (-right - frustumShift) / 2.0;
                rightfrustum = (right - frustumShift) / 2.0;
                modeltranslation = (float) (-getIOD() / 2.0);
            }
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glFrustum(leftfrustum, rightfrustum, bottomfrustum,
                    topfrustum, nearZ, farZ);
            gl.glTranslatef(modeltranslation, 0f, 0f);
            gl.glMatrixMode(GL.GL_MODELVIEW);

            super.draw(dc);

        } finally {
            super.finalizeFrame(dc);
        }
    }

    /**
     * Gets the intra ocular distance (eye separation distance)
     *
     * @return IOD Intra ocular distance (eye separation distance).
     */
    public double getIOD() {
        return iod;
    }

    /**
     * Sets the eye separation (intra ocular) distance to the input parameter.
     *
     * @param IOD Eye separation distance.
     */
    public void setIOD(double IOD) {
        this.iod = IOD;
    }

    /**
     * Gets the width of the screen.
     *
     * @return Width of the screen in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the screen.
     *
     * @return Height of the screen in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the width of the screen to the input parameter value.
     *
     * @param width Width of the screen in pixels.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Sets the height of the screen to the input parameter value.
     *
     * @param height Height of the screen in pixels.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the aspect ratio of the screen dimensions (ratio of screen width to
     * screen height).
     *
     * @return Aspect ratio of the screen.
     */
    public double getAspect() {
        return aspect;
    }

    /**
     * Sets the aspect ratio of the screen to the input parameter value.
     *
     * @param aspect The aspect ratio of the screen.
     */
    public void setAspect(double aspect) {
        this.aspect = aspect;
    }
}
