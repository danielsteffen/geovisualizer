/*
 *  AnimatedPole.java 
 *
 *  Created by DFKI AV on 05.10.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.sos.vis.ndw2012;

import com.jogamp.common.nio.Buffers;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class AnimatedPole implements Renderable {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(AnimatedPole.class);
    /**
     * The {@link List} of {@link LatLon} elements.
     */
    private List<LatLon> latlonList;
    /**
     * The different heights for the
     */
    private List<Float> heightList;
    /**
     * The {@link List} of {@link Color} for the positions.
     */
    private List<Color> colorList;
    /**
     * The id of the vertex buffer object
     */
    private int vboID;
    /**
     * The id of the color buffer object
     */
    private int cboID;
    /**
     * The id of the indices buffer
     */
    private int iboID;
    /**
     * The {@link ByteBuffer}.
     */
    private ByteBuffer byteBuffer;
    /**
     * The vertex {@link FloatBuffer}.
     */
    private FloatBuffer vertices;
    /**
     * The color {@link FloatBuffer}.
     */
    private FloatBuffer colors;
    /**
     *
     */
    private IntBuffer indices;
    /**
     * The update flag used by the rendering loop.
     */
    private boolean update;
    /**
     * Whether this points are initialized or not.
     */
    private boolean initialized;
    /**
     * The id of the time step to render.
     */
    private int timeStepID;

    /**
     * Constructor.
     */
    public AnimatedPole() {
        this.latlonList = new ArrayList<>();
        this.heightList = new ArrayList<>();
        this.colorList = new ArrayList<>();
        this.initialized = false;
        this.update = false;
        this.timeStepID = 0;
    }

    @Override
    public void render(DrawContext dc) {
        if (dc == null) {
            String message = "DrawContext is set to null";
            log.error(message);
            throw new IllegalArgumentException(message);
        }

        GL2 gl = dc.getGL().getGL2();

        if (!initialized) {
            initialize(dc);
        }

        gl.glPushAttrib(GL2.GL_ENABLE_BIT | GL2.GL_CURRENT_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();

//        gl.glPointSize(5.0f);
//        gl.glLineWidth(2.0f);
//        gl.glEnable(GL2.GL_POINT_SMOOTH);
//        gl.glEnable(GL2.GL_LINE_SMOOTH);
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_COLOR_ARRAY);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID);
        gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboID);
        gl.glColorPointer(4, GL2.GL_FLOAT, 0, 0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        gl.glDrawArrays(GL2.GL_TRIANGLE_FAN, 0, 5 * 3);
        gl.glDrawArrays(GL2.GL_LINE_STRIP, 0, 5 * 3);
//        gl.glDrawElements(, vboID, vboID, vboID);

        if (this.update) {
            updateVBO(dc);
        }

        gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
//        gl.glDisable(GL2.GL_POINT_SMOOTH);
//        gl.glDisable(GL2.GL_LINE_SMOOTH);
        gl.glLineWidth(1.0f);
        gl.glPopMatrix();
        gl.glPopAttrib();
    }

    /**
     * Initialize the point to be used by OpenGL2.
     *
     * @param dc the {@link DrawContext}
     */
    private void initialize(DrawContext dc) {

        GL2 gl = dc.getGL().getGL2();
        Globe globe = dc.getGlobe();
        LatLon centerLatLon = latlonList.get(timeStepID);

        // Compute sector with 2.0m radius
        Sector sector = Sector.boundingSector(globe, centerLatLon, 20.0);
        LatLon[] cornerLatLons = sector.getCorners();
        log.debug("Size of corner points: {}", cornerLatLons.length);

        List<LatLon> latLons = Arrays.asList(cornerLatLons);
        int numPyramidVertices = 6;
        vertices = Buffers.newDirectFloatBuffer(numPyramidVertices * 3);
        double[] elevations = new double[cornerLatLons.length];
        globe.getElevations(sector, latLons, 0.0001, elevations);

        double elevation = globe.getElevation(centerLatLon.latitude, centerLatLon.longitude);
        double absoluteHeight = elevation + heightList.get(timeStepID);

        // setup vertices
        Vec4 vertex = globe.computePointFromPosition(centerLatLon.latitude, centerLatLon.longitude, absoluteHeight);
        vertices.put((float) vertex.x);
        vertices.put((float) vertex.y);
        vertices.put((float) vertex.z);

        for (int i = 0; i < cornerLatLons.length; i++) {
            vertex = globe.computePointFromLocation(cornerLatLons[i]);
            vertices.put((float) vertex.x);
            vertices.put((float) vertex.y);
            vertices.put((float) vertex.z);
        }
        vertex = globe.computePointFromLocation(cornerLatLons[0]);
        vertices.put((float) vertex.x);
        vertices.put((float) vertex.y);
        vertices.put((float) vertex.z);
        vertices.rewind();

        // setup colors
        colors = Buffers.newDirectFloatBuffer(numPyramidVertices * 4);
        Color color = colorList.get(timeStepID);
        float[] values = color.getRGBColorComponents(null);
        for (int i = 0; i < 6; i++) {
            colors.put(i * 4, values[0]);
            colors.put(i * 4 + 1, values[1]);
            colors.put(i * 4 + 2, values[2]);
            colors.put(i * 4 + 3, 1.0f);
        }
        colors.rewind();

        // setup index array
        indices = Buffers.newDirectIntBuffer(8 * 2);
        //
        indices.put(0);
        indices.put(1);
        //
        indices.put(0);
        indices.put(2);
        //
        indices.put(0);
        indices.put(3);
        //
        indices.put(0);
        indices.put(4);
        //
        indices.put(1);
        indices.put(2);
        //
        indices.put(2);
        indices.put(3);
        //
        indices.put(3);
        indices.put(4);
        //
        indices.put(4);
        indices.put(1);
        indices.rewind();

        // Create the vbo 
        int[] buffers = new int[3];
        gl.glGenBuffers(3, buffers, 0);
        vboID = buffers[0];
        cboID = buffers[1];
        iboID = buffers[2];

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.capacity() * Buffers.SIZEOF_FLOAT, vertices, GL2.GL_STREAM_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        vertices.clear();

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboID);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, colors.capacity() * Buffers.SIZEOF_FLOAT, colors, GL2.GL_STREAM_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        colors.clear();

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, iboID);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, indices.capacity() * Buffers.SIZEOF_INT, indices, GL2.GL_STATIC_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        indices.clear();

        initialized = true;
    }

    /**
     * Update the vertex buffer object {@link #vboID} and color buffer object.
     *
     * @param dc the {@link DrawContext}
     */
    private void updateVBO(DrawContext dc) {
        GL2 gl = dc.getGL().getGL2();

        // Update the vertex buffer...
        int numPyramidVertices = 6;
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID);
        byteBuffer = gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_READ_WRITE);

        if (byteBuffer != null) {
            vertices = byteBuffer.asFloatBuffer();
            vertices.rewind();

            Globe globe = dc.getGlobe();
            LatLon centerLatLon = latlonList.get(timeStepID);

            // Compute sector with 2.0m radius
            Sector sector = Sector.boundingSector(globe, centerLatLon, 20.0);
            LatLon[] cornerLatLons = sector.getCorners();
            List<LatLon> latLons = Arrays.asList(cornerLatLons);
            double[] elevations = new double[cornerLatLons.length];
            globe.getElevations(sector, latLons, 0.0001, elevations);

            double elevation = globe.getElevation(centerLatLon.latitude, centerLatLon.longitude);
            double absoluteHeight = elevation + heightList.get(timeStepID);

            // Set the vertices...
            Vec4 vertex = globe.computePointFromPosition(centerLatLon.latitude, centerLatLon.longitude, absoluteHeight);
            vertices.put(0, (float) vertex.x);
            vertices.put(1, (float) vertex.y);
            vertices.put(2, (float) vertex.z);

            for (int i = 0; i < cornerLatLons.length; i++) {
                vertex = globe.computePointFromLocation(cornerLatLons[i]);
                vertices.put(i * 3 + 3, (float) vertex.x);
                vertices.put(i * 3 + 4, (float) vertex.y);
                vertices.put(i * 3 + 5, (float) vertex.z);
            }
            vertex = globe.computePointFromLocation(cornerLatLons[0]);
            vertices.put(cornerLatLons.length * 3 + 3, (float) vertex.x);
            vertices.put(cornerLatLons.length * 3 + 4, (float) vertex.y);
            vertices.put(cornerLatLons.length * 3 + 5, (float) vertex.z);
            vertices.rewind();
        }
        gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        // Update the color buffer...
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboID);
        byteBuffer = gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_READ_WRITE);
        if (byteBuffer != null) {
            colors = byteBuffer.asFloatBuffer();
            colors.rewind();
            Color color = colorList.get(timeStepID);
            float[] values = color.getRGBColorComponents(null);
            for (int i = 0; i < numPyramidVertices; i++) {
                colors.put(i * 4, values[0]);
                colors.put(i * 4 + 1, values[1]);
                colors.put(i * 4 + 2, values[2]);
                colors.put(i * 4 + 3, 1.0f);
            }
        }
        gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        timeStepID++;

        if (timeStepID >= latlonList.size()) {
            // reset the timpeStepid
            timeStepID = 0;
        }
        this.update = false;
    }

    /**
     *
     * @param latLon
     * @param height
     * @param color
     */
    public void add(LatLon latLon, Float height, Color color) {
        if (latLon == null) {
            String msg = "latLon == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (height == null) {
            String msg = "height == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (color == null) {
            String msg = "color == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.latlonList.add(latLon);
        this.heightList.add(height);
        this.colorList.add(color);
    }

    /**
     * Set the {@link #update} flag to {@code true}.
     */
    public void update() {
        this.update = true;
    }

    /**
     * Return the number of points
     *
     * @return the number of points
     */
    public int size() {
        return latlonList.size();
    }
}
