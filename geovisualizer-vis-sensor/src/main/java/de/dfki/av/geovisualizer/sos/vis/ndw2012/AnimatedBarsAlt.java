/*
 *  AnimatedBars.java 
 *
 *  Created by DFKI AV on 05.10.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.sos.vis.ndw2012;

import com.jogamp.common.nio.Buffers;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.terrain.HighResolutionTerrain;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class AnimatedBarsAlt implements Renderable {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(AnimatedBarsAlt.class);
    private static final boolean TRANSPARENCY = true;
    /**
     * The {@link List} of {@link LatLon} elements.
     */
    private List<LatLon> latlonList;
    private Map<Integer, Integer> offsetList;
    /**
     * The {@link List} of {@link List} of elevations for the
     * {@link #latlonList}.
     */
    private List<Float> elevationsList;
    private Map<Integer, Float> currentElevationList;
    /**
     * The terrain elevation for the {@link #latlonList}.
     */
    private double[] terrainElevation;
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
     * The update flag used by the rendering loop.
     */
    private boolean update;
    /**
     * Whether this points are initialized or not.
     */
    private boolean initialized;
    /**
     * The duration of the animation.
     */
    private int duration;
    /**
     *
     */
    private int timestep;
    private int vertexPerBar = 16;
    /**
     *
     */
    private long lasttime = -1L;
    private int index;
    private List<Color> colorTopList;
    private HighResolutionTerrain terrain;
    private static final int RISING_STEPS = 10;
    /**
     * Bar size in degrees
     */
    private static final double BAR_SIZE = 0.002d;
    /**
     * Movement per Animation tick in % of {@code BAR_SIZE}
     *
     * @see #BAR_SIZE
     */
    private static final int SCALE_DELTA = 10;
    private List<String> timecodeList;

    /**
     * Constructor.
     */
    public AnimatedBarsAlt() {
        this.timecodeList = new ArrayList<>();
        this.offsetList = new HashMap<>();
        this.latlonList = new ArrayList<>();
        this.elevationsList = new ArrayList<>();
        this.currentElevationList = new HashMap<>();
        this.colorList = new ArrayList<>();
        this.colorTopList = new ArrayList<>();
        this.initialized = false;
        this.update = false;
        this.duration = 4000;
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

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_COLOR_ARRAY);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID);
        gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboID);
        gl.glColorPointer(4, GL2.GL_FLOAT, 0, 0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        if (TRANSPARENCY) {
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        }
        for (int i = 0; i < latlonList.size(); i++) {
            if (offsetList.containsKey(i) && offsetList.get(i) > 0 && offsetList.get(i) < 100) {
                gl.glBegin(GL2.GL_TRIANGLE_STRIP);
                int vertexIndex = i * vertexPerBar;
                // triangle a
                gl.glArrayElement(vertexIndex + 0);
                gl.glArrayElement(vertexIndex + 3);
                gl.glArrayElement(vertexIndex + 5);
                // triangle b
                gl.glArrayElement(vertexIndex + 4);
                // triangle c
                gl.glArrayElement(vertexIndex + 1);
                // triangle d
                gl.glArrayElement(vertexIndex + 2);
                // triangle e
                gl.glArrayElement(vertexIndex + 6);
                // triangle f
                gl.glArrayElement(vertexIndex + 7);
                // triangle g
                gl.glArrayElement(vertexIndex + 0);
                // triangle e
                gl.glArrayElement(vertexIndex + 3);
                gl.glEnd();
                gl.glBegin(GL2.GL_TRIANGLE_STRIP);
                vertexIndex = vertexIndex + 8;
                // triangle a
                gl.glArrayElement(vertexIndex + 0);
                gl.glArrayElement(vertexIndex + 3);
                gl.glArrayElement(vertexIndex + 5);
                // triangle b
                gl.glArrayElement(vertexIndex + 4);
                // triangle c
                gl.glArrayElement(vertexIndex + 1);
                // triangle d
                gl.glArrayElement(vertexIndex + 2);
                // triangle e
                gl.glArrayElement(vertexIndex + 6);
                // triangle f
                gl.glArrayElement(vertexIndex + 7);
                // triangle g
                gl.glArrayElement(vertexIndex + 0);
                // triangle e
                gl.glArrayElement(vertexIndex + 3);
                gl.glEnd();
            }
        }
        int start = latlonList.size() * vertexPerBar;
        for (int i = 0; i < latlonList.size(); i++) {
            if (offsetList.containsKey(i) && offsetList.get(i) > 0 && offsetList.get(i) < 100) {
                int vertexIndex = i * 4;
                vertexIndex += start;
                gl.glBegin(GL2.GL_TRIANGLE_STRIP);
                // triangle top
                gl.glArrayElement(vertexIndex + 0);
                gl.glArrayElement(vertexIndex + 4);
                gl.glArrayElement(vertexIndex + 1);
                gl.glArrayElement(vertexIndex + 5);
                gl.glArrayElement(vertexIndex + 3);
                gl.glArrayElement(vertexIndex + 7);
                gl.glArrayElement(vertexIndex + 2);
                gl.glArrayElement(vertexIndex + 6);
                gl.glArrayElement(vertexIndex + 0);
                gl.glArrayElement(vertexIndex + 4);
                gl.glEnd();
            }
        }

        if (this.update) {
            updateVBO(dc);
        }
        if (TRANSPARENCY) {
            gl.glDisable(GL2.GL_BLEND);
        }
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
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
        terrainElevation = new double[latlonList.size()];
        Sector sector = Sector.boundingSector(latlonList);
        double resolution = globe.getElevationModel().getBestResolution(sector);
        // Be sure to re-use the Terrain object to take advantage of its caching.
        this.terrain = new HighResolutionTerrain(globe, resolution);
        for (int i = 0; i < latlonList.size(); i++) {
            terrainElevation[i] = terrain.getElevation(latlonList.get(i));
        }
        log.debug("Used resolution for elevations (in radians): {}", resolution);
        // setup vertices
        FloatBuffer vertices = Buffers.newDirectFloatBuffer(latlonList.size() * 72);
        for (int i = 0; i < latlonList.size(); i++) {
            LatLon latLon = latlonList.get(i);
            double elevation = terrainElevation[i];
            List<Vec4> vectors = new ArrayList<>();
            for (int j = 0; j < 16; j++) {
                vectors.add(globe.computePointFromPosition(latLon, elevation));
            }
            for (Vec4 vec : vectors) {
                vertices.put((float) vec.x);
                vertices.put((float) vec.y);
                vertices.put((float) vec.z);
            }
        }
        for (int i = 0; i < latlonList.size(); i++) {
            LatLon latLon = latlonList.get(i);
            latLon = latLon.add(LatLon.fromDegrees(
                    0.005d, 0.000d));
            double elevation = terrainElevation[i];
            List<Vec4> vectors = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                vectors.add(globe.computePointFromPosition(latLon, elevation));
            }
            for (Vec4 vec : vectors) {
                vertices.put((float) vec.x);
                vertices.put((float) vec.y);
                vertices.put((float) vec.z);
            }
        }
        vertices.rewind();

        FloatBuffer colors = Buffers.newDirectFloatBuffer(colorList.size() * 4 * 24);
        // Side color
        for (int i = 0; i < latlonList.size(); i++) {
            Color color = colorList.get(i);
            float[] values = new float[4];
            color.getRGBColorComponents(values);
            for (int j = 0; j < 16; j++) {
                colors.put(values[0]);
                colors.put(values[1]);
                colors.put(values[2]);
                colors.put(0.9f);
            }
        }
        // Top Color
        for (int i = 0; i < latlonList.size(); i++) {
            Color color = colorTopList.get(i);
            float[] values = new float[4];
            color.getRGBColorComponents(values);
            for (int j = 0; j < 8; j++) {
                colors.put(values[0]);
                colors.put(values[1]);
                colors.put(values[2]);
                colors.put(0.9f);
            }
        }
        colors.rewind();
        // Create the vbo 
        int[] buffers = new int[2];
        gl.glGenBuffers(2, buffers, 0);
        vboID = buffers[0];
        cboID = buffers[1];

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.capacity() * Buffers.SIZEOF_FLOAT, vertices, GL2.GL_STREAM_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        vertices.clear();

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboID);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, colors.capacity() * Buffers.SIZEOF_FLOAT, colors, GL2.GL_STATIC_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        colors.clear();

        timestep = duration / (latlonList.size() - 1);

        initialized = true;
    }

    protected void setDepthFunc(DrawContext dc, Vec4 screenPoint) {
        GL2 gl = dc.getGL().getGL2();

        Position eyePos = dc.getView().getEyePosition();
        if (eyePos == null) {
            gl.glDepthFunc(GL2.GL_ALWAYS);
            return;
        }

        double altitude = eyePos.getElevation();
        if (altitude < (dc.getGlobe().getMaxElevation() * dc.getVerticalExaggeration())) {
            double depth = screenPoint.z - (8d * 0.00048875809d);
            depth = depth < 0d ? 0d : (depth > 1d ? 1d : depth);
            gl.glDepthFunc(GL2.GL_LESS);
            gl.glDepthRange(depth, depth);
        } else if (screenPoint.z >= 1d) {
            gl.glDepthFunc(GL2.GL_EQUAL);
            gl.glDepthRange(1d, 1d);
        } else {
            gl.glDepthFunc(GL2.GL_ALWAYS);
        }
    }

    /**
     * Update the vertex buffer object {@link #vboID}.
     *
     * @param dc the {@link DrawContext}
     */
    private void updateVBO(DrawContext dc) {
        GL2 gl = dc.getGL().getGL2();
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID);
        ByteBuffer byteBuffer = gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_READ_WRITE);
        if (byteBuffer != null) {
            FloatBuffer vertices = byteBuffer.asFloatBuffer();
            vertices.rewind();
            Globe globe = dc.getGlobe();
            for (int i = 0; i < latlonList.size(); i++) {
                int offset = 0;
                if (offsetList.containsKey(i)) {
                    offset = offsetList.get(i);
                }
                LatLon deltaLat = LatLon.fromDegrees(offset * 0.00001d, 0.000d);
                LatLon deltaLon = LatLon.fromDegrees(0.000d, offset * 0.00001d);

                LatLon deltaLatA = LatLon.fromDegrees(offset * 0.00001d, 0.000d);
                LatLon deltaLonA = LatLon.fromDegrees(0.000d, offset * 0.00001d);

                LatLon latLon = latlonList.get(i);
                double elevation = terrainElevation[i];

                double elevationTop = elevation + currentElevationList.get(i);

                List<Vec4> vectors = new ArrayList<>();

                LatLon l1 = latLon.add(deltaLat);
                vectors.add(globe.computePointFromPosition(l1.add(deltaLatA), elevationTop));
                LatLon l2 = latLon.subtract(deltaLat);
                vectors.add(globe.computePointFromPosition(l2.subtract(deltaLatA), elevationTop));
                LatLon l3 = latLon.subtract(deltaLat);
                vectors.add(globe.computePointFromPosition(l3.subtract(deltaLatA), elevation));
                LatLon l4 = latLon.add(deltaLat);
                vectors.add(globe.computePointFromPosition(l4.add(deltaLatA), elevation));

                LatLon l5 = latLon.add(deltaLon);
                vectors.add(globe.computePointFromPosition(l5.add(deltaLonA), elevation));
                LatLon l6 = latLon.add(deltaLon);
                vectors.add(globe.computePointFromPosition(l6.add(deltaLonA), elevationTop));
                LatLon l7 = latLon.subtract(deltaLon);
                vectors.add(globe.computePointFromPosition(l7.subtract(deltaLonA), elevationTop));
                LatLon l8 = latLon.subtract(deltaLon);
                vectors.add(globe.computePointFromPosition(l8.subtract(deltaLonA), elevation));

                vectors.add(globe.computePointFromPosition(latLon.add(deltaLatA), elevationTop));
                vectors.add(globe.computePointFromPosition(latLon.subtract(deltaLatA), elevationTop));
                vectors.add(globe.computePointFromPosition(latLon.subtract(deltaLatA), elevation));
                vectors.add(globe.computePointFromPosition(latLon.add(deltaLatA), elevation));

                vectors.add(globe.computePointFromPosition(latLon.add(deltaLonA), elevation));
                vectors.add(globe.computePointFromPosition(latLon.add(deltaLonA), elevationTop));
                vectors.add(globe.computePointFromPosition(latLon.subtract(deltaLonA), elevationTop));
                vectors.add(globe.computePointFromPosition(latLon.subtract(deltaLonA), elevation));

                for (Vec4 vec : vectors) {
                    vertices.put((float) vec.x);
                    vertices.put((float) vec.y);
                    vertices.put((float) vec.z);
                }
            }
            for (int i = 0; i < latlonList.size(); i++) {
                int offset = 0;
                if (offsetList.containsKey(i)) {
                    offset = offsetList.get(i);
                }
                LatLon latLon = latlonList.get(i);
                LatLon deltaLat = LatLon.fromDegrees(offset * 0.00001d, 0.000d);
                LatLon deltaLon = LatLon.fromDegrees(0.000d, offset * 0.00001d);

                LatLon deltaLatA = LatLon.fromDegrees(offset * 0.00001d, 0.000d);
                LatLon deltaLonA = LatLon.fromDegrees(0.000d, offset * 0.00001d);
                double elevation = terrainElevation[i];
                double elevationTop = elevation + currentElevationList.get(i);

                List<Vec4> vectors = new ArrayList<>();
                vectors.add(globe.computePointFromPosition(latLon.add(deltaLat).add(deltaLatA), elevationTop));
                vectors.add(globe.computePointFromPosition(latLon.subtract(deltaLon).subtract(deltaLonA), elevationTop));
                vectors.add(globe.computePointFromPosition(latLon.add(deltaLon).add(deltaLonA), elevationTop));
                vectors.add(globe.computePointFromPosition(latLon.subtract(deltaLat).subtract(deltaLatA), elevationTop));

                vectors.add(globe.computePointFromPosition(latLon.add(deltaLatA), elevationTop));
                vectors.add(globe.computePointFromPosition(latLon.subtract(deltaLonA), elevationTop));
                vectors.add(globe.computePointFromPosition(latLon.add(deltaLonA), elevationTop));
                vectors.add(globe.computePointFromPosition(latLon.subtract(deltaLatA), elevationTop));

                for (Vec4 vec : vectors) {
                    vertices.put((float) vec.x);
                    vertices.put((float) vec.y);
                    vertices.put((float) vec.z);
                }
            }
        }

        gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        this.update = false;
    }

    /**
     *
     * @param latLon
     * @param timecode
     * @param value
     * @param colorTop
     * @param color
     */
    public void add(LatLon latLon, String timecode, float value, Color colorTop, Color color) {
        if (latLon == null) {
            String msg = "latLon == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (color == null) {
            String msg = "color == null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.timecodeList.add(timecode);
        this.latlonList.add(latLon);
        this.elevationsList.add(value);
        this.currentElevationList.put(elevationsList.size() - 1, 0.f);
        this.colorTopList.add(colorTop);
        this.colorList.add(color);
    }

    /**
     * Set the {@link #update} flag to {@code true}.
     */
    public void update() {
        this.update = true;
        for (int i = 0; i < latlonList.size(); i++) {
            float current = currentElevationList.get(i);
            if (current < elevationsList.get(i)) {
//                currentElevationList.put(i, current + 10);
                currentElevationList.put(i, elevationsList.get(i));
            } else {
                if (offsetList.containsKey(i) && offsetList.get(i) < 100) {
                    int offset = offsetList.get(i);
                    if (i == 0) {
                        offsetList.put(i, offset + 1);
                    } else {
                        if (offsetList.containsKey(i - 1) && offsetList.get(i - 1) > 25) {
                            offsetList.put(i, offsetList.get(i - 1) - 25);
                        } else if (offsetList.containsKey(i - 1) && offsetList.get(i - 1) > 75) {
                            offsetList.put(i, offsetList.get(i) + 1);
                        }
                    }
                } else {
                    offsetList.put(i, 0);
                }
            }
        }
    }

    /**
     * Return the number of points
     *
     * @return the number of points
     */
    public int size() {
        return latlonList.size();
    }

    public boolean drawNewObject() {
        if (latlonList != null) {
            if (index < latlonList.size() - 1) {
                index += 1;
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    boolean animationCompleted() {
        return false;
    }

    void resetAnimation() {
    }
}
