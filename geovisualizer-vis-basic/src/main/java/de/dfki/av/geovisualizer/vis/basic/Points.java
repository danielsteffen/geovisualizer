/*
 * Points.java
 *
 * Created by DFKI AV on 04.09.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.vis.basic;

import com.jogamp.common.nio.Buffers;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.OrderedRenderable;
import gov.nasa.worldwind.render.Renderable;
import java.awt.Color;
import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a {@link Renderable} for a list of points. This object uses vertex
 * buffer objects and {@link GL#GL_POINTS} to render the points.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class Points implements Renderable, OrderedRenderable {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Points.class);
    /**
     * The {@link List} of {@link Position} elements.
     */
    private List<Position> positionList;
    /**
     * The {@link List} of {@link Color} for the positions.
     */
    private List<Color> colorList;
    /**
     * The id of the vertex buffer object.
     */
    private int vboID;
    /**
     * The id of the color buffer.
     */
    private int cboID;
    /**
     * Whether the OpenGL things have been initialized or not.
     */
    private boolean firstTime;
    /**
     * The distance to the eye.
     */
    private double eyeDistance;
    /**
     * See example usage
     */
    private long frameTimestamp;

    /**
     * Constructor.
     */
    public Points() {
        this.positionList = new ArrayList<>();
        this.colorList = new ArrayList<>();
        this.firstTime = true;
        this.eyeDistance = 0;
        this.frameTimestamp = -1L;
    }

    /**
     * Init the Points.
     *
     * @param dc the {@link DrawContext}
     */
    private void init(DrawContext dc) {

        GL gl = dc.getGL();
//        GL2 gl2 = gl.getGL2();
        Globe globe = dc.getGlobe();

        // In order to set the points relative to the ground
        double[] elevations = new double[positionList.size()];
        Sector sector = Sector.boundingSector(positionList);
        double resolution = globe.getElevations(sector, positionList, 0.001, elevations);
        LOG.debug("Resolution for terrain elevations (in radians) {}", resolution);

        // setup vertices
        int i = 0;
        FloatBuffer vertices = Buffers.newDirectFloatBuffer(positionList.size() * 3);
        for (Iterator<Position> it = positionList.iterator(); it.hasNext();) {
            Position position = it.next();
            Vec4 vec4 = globe.computePointFromPosition(position.latitude, position.longitude, position.elevation + elevations[i]);
            vertices.put((float) vec4.x);
            vertices.put((float) vec4.y);
            vertices.put((float) vec4.z);
            i++;
        }
        vertices.rewind();

        FloatBuffer colors = Buffers.newDirectFloatBuffer(colorList.size() * 4);
        for (Iterator<Color> it = colorList.iterator(); it.hasNext();) {
            Color color = it.next();
            float[] values = color.getRGBColorComponents(null);
            colors.put(values[0]);
            colors.put(values[1]);
            colors.put(values[2]);
            colors.put(1.0f);
        }
        colors.rewind();

        // Create the vbo
        int[] buffers = new int[2];
        gl.glGenBuffers(2, buffers, 0);
        vboID = buffers[0];
        cboID = buffers[1];

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboID);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.capacity() * Buffers.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        vertices.clear();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, cboID);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, colors.capacity() * Buffers.SIZEOF_FLOAT, colors, GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        colors.clear();
    }

    /**
     * Return the number of points
     *
     * @return the number of points
     */
    public int size() {
        return positionList.size();
    }

    @Override
    public void render(DrawContext dc) {
        if (dc.isOrderedRenderingMode()) {
            this.drawOrderedRenderable(dc);
        } else {
            this.makeOrderedRenderable(dc);
        }
    }

    /**
     *
     * @param dc
     */
    protected void drawOrderedRenderable(DrawContext dc) {
        if (dc == null) {
            String message = "DrawContext is set to null";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        GL gl = dc.getGL();
        GL2 gl2 = gl.getGL2();
        
        if (firstTime) {
            init(dc);
            firstTime = false;
        }

        gl2.glPushAttrib(GL2.GL_ENABLE_BIT | GL2.GL_CURRENT_BIT);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glPushMatrix();

        gl2.glPointSize(5.0f);
//        gl.glEnable(GL.GL_BLEND);
//        OGLUtil.applyBlending(gl, false);
        gl2.glEnable(GL2.GL_POINT_SMOOTH);
        gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);

        gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID);
        gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
        gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboID);
        gl2.glColorPointer(4, GL2.GL_FLOAT, 0, 0);
        gl2.glDrawArrays(GL2.GL_POINTS, 0, positionList.size() * 3);
        gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glDisable(GL2.GL_POINT_SMOOTH);
//        gl.glDisable(GL.GL_BLEND);
        gl2.glPopMatrix();
        gl2.glPopAttrib();
    }

    /**
     *
     * @param dc
     */
    protected void makeOrderedRenderable(DrawContext dc) {
        if (dc.getFrameTimeStamp() != this.frameTimestamp) {
            Vec4 eyePoint = dc.getView().getEyePoint();
            Globe globe = dc.getGlobe();
            Vec4 placePoint = globe.computePointFromPosition(this.positionList.get(0));
            this.eyeDistance = eyePoint.distanceTo3(placePoint);
            this.frameTimestamp = dc.getFrameTimeStamp();
        }
        dc.addOrderedRenderable(this);
    }

    /**
     *
     * @param p
     */
    public void add(Position p, Color c) {
        if (p == null) {
            String msg = "Position set to null. No valid argument";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (c == null) {
            String msg = "Color set to null. No valid argument";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.positionList.add(p);
        this.colorList.add(c);
    }

    @Override
    public double getDistanceFromEye() {
        return this.eyeDistance;
    }

    @Override
    public void pick(DrawContext dc, Point point) {
    }
}
