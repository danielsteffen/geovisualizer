/*
 * Pins.java
 *
 * Created by DFKI AV on 09.09.2013.
 * Copyright (c) 2011-2014 DFKI GmbH, Kaiserslautern. All rights reserved.
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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a {@link Renderable} for a list of pins. This object uses vertex
 * buffer objects and {@link GL#GL_POINTS} to render the points.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class Pins implements Renderable, OrderedRenderable {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Pins.class);
    /**
     * The {@link List} of {@link Position} elements.
     */
    private List<Position> positionList;
    /**
     * The {@link List} of {@link Color} for the positions.
     */
    private List<Color> colorList;
    /**
     * The id of the vertex buffer object for the points.
     */
    private int vboPointID;
    /**
     * The id of the vertex buffer object for the lines.
     */
    private int vboLineID;
    /**
     * The id of the color buffer for the points.
     */
    private int cboPointID;
    /**
     * The id of the color buffer for the lines.
     */
    private int cboLineID;
    /**
     * Whether the OpenGL elements have been initialized or not.
     */
    private boolean initialized;
    /**
     * The distance to the eye.
     */
    private double eyeDistance;
    /**
     * See example usage.
     */
    private long frameTimestamp;
    /**
     * The update flag used by the rendering loop.
     */
    private boolean update;
    /**
     * The {@link ByteBuffer}.
     */
    private ByteBuffer byteBuffer;
    /**
     * The vertex {@link FloatBuffer}.
     */
    private FloatBuffer pointVertices;
    /**
     * The vertex {@link FloatBuffer}.
     */
    private FloatBuffer lineVertices;

    /**
     * Constructor.
     */
    public Pins() {
        this.positionList = new ArrayList<>();
        this.colorList = new ArrayList<>();
        this.eyeDistance = 0;
        this.frameTimestamp = -1L;
        this.initialized = false;
        this.update = false;
    }

    /**
     * Initialize the pin geometry for the {@link #positionList}.
     *
     * @param dc the {@link DrawContext}
     */
    private void initialize(DrawContext dc) {

        GL gl = dc.getGL();
        Globe globe = dc.getGlobe();

        // In order to set the points relative to the ground
        double[] elevations = new double[positionList.size()];
        Sector sector = Sector.boundingSector(positionList);
        double resolution = globe.getElevations(sector, positionList, 0.00001, elevations);
        LOG.debug("Resolution for terrain elevations (in radians) {}", resolution);

        // Calculate and convert point and line coordinates
        int i = 0;
        pointVertices = Buffers.newDirectFloatBuffer(positionList.size() * 3);
        lineVertices = Buffers.newDirectFloatBuffer(positionList.size() * 3 * 2);

        for (Iterator<Position> it = positionList.iterator(); it.hasNext(); i++) {
            Position position = it.next();
            // Compute vertices for the points
            Vec4 vec0 = globe.computePointFromPosition(
                    position.latitude,
                    position.longitude,
                    position.elevation + elevations[i]);
            pointVertices.put((float) vec0.x);
            pointVertices.put((float) vec0.y);
            pointVertices.put((float) vec0.z);

            // Compute vertices and indices for the lines
            // ... head of pin
            lineVertices.put((float) vec0.x);
            lineVertices.put((float) vec0.y);
            lineVertices.put((float) vec0.z);
            // ... bottom of pin
            Vec4 vec1 = globe.computePointFromPosition(
                    position.latitude,
                    position.longitude,
                    elevations[i]);
            lineVertices.put((float) vec1.x);
            lineVertices.put((float) vec1.y);
            lineVertices.put((float) vec1.z);
        }

        pointVertices.rewind();
        lineVertices.rewind();

        // Calc colors for point and lines
        FloatBuffer pointColors = Buffers.newDirectFloatBuffer(colorList.size() * 4);
        FloatBuffer lineColors = Buffers.newDirectFloatBuffer(colorList.size() * 4 * 2);

        for (Iterator<Color> it = colorList.iterator(); it.hasNext();) {
            Color color = it.next();
            float[] values = color.getRGBColorComponents(null);
            pointColors.put(values[0]);
            pointColors.put(values[1]);
            pointColors.put(values[2]);
            pointColors.put(1.0f);
            float[] twoRGBAValues = new float[]{
                0.7f, 0.7f, 0.7f, 0.5f,
                0.7f, 0.7f, 0.7f, 0.5f};
            lineColors.put(twoRGBAValues);
        }
        pointColors.rewind();
        lineColors.rewind();

        // Create the buffer objects
        int[] buffers = new int[4];
        gl.glGenBuffers(4, buffers, 0);
        vboPointID = buffers[0];
        cboPointID = buffers[1];
        vboLineID = buffers[2];
        cboLineID = buffers[3];

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboPointID);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,
                pointVertices.capacity() * Buffers.SIZEOF_FLOAT,
                pointVertices,
                GL2.GL_STREAM_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        pointVertices.clear();

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboPointID);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,
                pointColors.capacity() * Buffers.SIZEOF_FLOAT,
                pointColors,
                GL2.GL_STATIC_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        pointColors.clear();

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboLineID);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,
                lineVertices.capacity() * Buffers.SIZEOF_FLOAT,
                lineVertices,
                GL2.GL_STREAM_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        lineVertices.clear();

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboLineID);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,
                lineColors.capacity() * Buffers.SIZEOF_FLOAT,
                lineColors,
                GL2.GL_STATIC_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        lineColors.clear();

        initialized = true;
    }

    /**
     * Return the number of points.
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
     * Draws the ordered renderable.
     *
     * @param dc the {@link DrawContext} to use.
     */
    protected void drawOrderedRenderable(DrawContext dc) {
        if (dc == null) {
            String message = "dc == null";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        GL gl = dc.getGL();
        GL2 gl2 = gl.getGL2();

        if (!initialized) {
            initialize(dc);
        }

        gl2.glPushAttrib(GL2.GL_ENABLE_BIT | GL2.GL_CURRENT_BIT);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glPushMatrix();

        gl2.glPointSize(5.0f);

        // Draw the head of the pin
        gl2.glEnable(GL2.GL_POINT_SMOOTH);
        gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);

        gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboPointID);
        gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
        gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboPointID);
        gl2.glColorPointer(4, GL2.GL_FLOAT, 0, 0);
        gl2.glDrawArrays(GL2.GL_POINTS, 0, positionList.size() * 3);
        gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glDisable(GL2.GL_POINT_SMOOTH);

        // Draw the sting of the pin
        gl2.glEnable(GL2.GL_LINES);
        gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);

        gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboLineID);
        gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
        gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboLineID);
        gl2.glColorPointer(4, GL2.GL_FLOAT, 0, 0);
        gl2.glDrawArrays(GL2.GL_LINES, 0, positionList.size() * 3 * 2);
        gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        if (this.update) {
            updateVBO(dc);
        }

        gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glDisable(GL2.GL_LINES);

        gl2.glPopMatrix();
        gl2.glPopAttrib();
    }

    /**
     * Creates the ordered Renderable.
     *
     * @param dc the {@link DrawContext} to use.
     */
    protected void makeOrderedRenderable(DrawContext dc) {
        if (dc.getFrameTimeStamp() != this.frameTimestamp) {
            Vec4 eyePoint = dc.getView().getEyePoint();
            Globe globe = dc.getGlobe();
            Position firstPosition = this.positionList.get(0);
            Vec4 placePoint = globe.computePointFromPosition(firstPosition);
            this.eyeDistance = eyePoint.distanceTo3(placePoint);
            this.frameTimestamp = dc.getFrameTimeStamp();
        }
        dc.addOrderedRenderable(this);
    }

    /**
     * Adds a {@link Position} and an associated {@link Color} to this pins
     * collection.
     *
     * @param p the {@link Position} for the pin to add.
     * @param c the {@link Color} of the pin head for this {@link Position}.
     * @throws IllegalArgumentException if {@code position == null} or
     * {@code color == null}
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

    /**
     * Set the {@link #update} flag to {@code true}.
     */
    public void update() {
        this.update = true;
    }

    /**
     * Update the vertex buffer objects {@link #vboLineID}, {@link #vboPointID}
     * and color buffer object.
     *
     * @param dc the {@link DrawContext}
     */
    private void updateVBO(DrawContext dc) {

        GL2 gl = dc.getGL().getGL2();
        Globe globe = dc.getGlobe();

        // Update the vertex buffer...
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboPointID);
        byteBuffer = gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_READ_WRITE);
        double[] elevations = new double[positionList.size()];

        if (byteBuffer != null) {
            pointVertices = byteBuffer.asFloatBuffer();
            pointVertices.rewind();

            for (int i = 0; i < positionList.size(); i++) {
                Position position = positionList.get(i);
                elevations[i] = globe.getElevation(position.latitude, position.longitude);
                Vec4 vec0 = globe.computePointFromPosition(position.latitude,
                        position.longitude, elevations[i] + position.elevation);
                pointVertices.put(i * 3 + 0, (float) vec0.x);
                pointVertices.put(i * 3 + 1, (float) vec0.y);
                pointVertices.put(i * 3 + 2, (float) vec0.z);
            }
            pointVertices.rewind();
        }

        gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        // Update the line vertex buffer...
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboLineID);
        byteBuffer = gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_READ_WRITE);

        if (byteBuffer != null) {
            lineVertices = byteBuffer.asFloatBuffer();
            lineVertices.rewind();

            for (int i = 0; i < positionList.size(); i++) {
                Position position = positionList.get(i);
                Vec4 vec0 = globe.computePointFromPosition(position.latitude,
                        position.longitude, elevations[i] + position.elevation);
                Vec4 vec1 = globe.computePointFromPosition(position.latitude,
                        position.longitude, elevations[i]);
                lineVertices.put(i * 6 + 0, (float) vec0.x);
                lineVertices.put(i * 6 + 1, (float) vec0.y);
                lineVertices.put(i * 6 + 2, (float) vec0.z);
                lineVertices.put(i * 6 + 3, (float) vec1.x);
                lineVertices.put(i * 6 + 4, (float) vec1.y);
                lineVertices.put(i * 6 + 5, (float) vec1.z);
            }
            lineVertices.rewind();
        }

        gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        this.update = false;
    }
}
