/*
 *  AnimatedBars.java 
 *
 *  Created by DFKI AV on 05.10.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.sos.vis.ndw2012;

import com.jogamp.common.nio.Buffers;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.terrain.HighResolutionTerrain;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
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
public class AnimatedBars implements Renderable {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(AnimatedBars.class);
    // Animation configuration parameters begin
    /**
     * Amount of steps until bar has max size
     */
    private static final int RISING_STEPS = 15;
    /**
     * Bar size in degrees
     */
    private static final double BAR_SIZE = 0.00009d;
    /**
     * Amount of movement steps {@code BAR_SIZE} / {@code MOVEMENT_STEPS} =
     * movement delta in degrees
     *
     * @see #BAR_SIZE
     */
    private static final int MOVEMENT_STEPS = 15;
    /**
     * Amount of bars which are displayed during the animation
     */
    private static final int BARS_DISPLAYED = 15;
    /**
     * Displaying a gap between bars (on/off flag)
     */
    private static final boolean BAR_GAP_ENABLED = true;
    // Animation configuration parameters end
    /**
     * Transparency flag
     */
    private boolean transparency;
    /**
     * The {@link List} of {@link LatLon} elements.
     */
    private List<LatLon> latlonList;
    private List<String> eventList;
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
     * Amount of vertecies per bar (Don't change this value)
     */
    private final int vertexPerBar = 8;
    /**
     * Index for indicating first bar which should be displayed
     */
    private int index = 1;
    /**
     * Index for indicating last bar which should be displayed
     */
    private int startIndex = 0;
    /**
     * List of colors for top of bar
     */
    private List<Color> colorTopList;
    /**
     * if true animation is paused
     */
    private boolean pause;
    /**
     * {@link HighResolutionTerrain} for calculation terrain elevations
     */
    private HighResolutionTerrain terrain;
    /**
     * Time annotation
     */
    private final GlobeAnnotation gaTime;
    /**
     * Event annotation
     */
    private final GlobeAnnotation gaEvent;
    /**
     * max elevation of the bars in the animation
     */
    private double maxElevation;
    /**
     * List of time codes as {@link String}
     */
    private List<String> timecodeList;
    private Position annotationPosition;
    private LatLon barsOrigin;

    /**
     * Constructor.
     */
    public AnimatedBars(RenderableLayer layer) {
        this.timecodeList = new ArrayList<>();
        this.eventList = new ArrayList<>();
        this.offsetList = new HashMap<>();
        this.latlonList = new ArrayList<>();
        this.elevationsList = new ArrayList<>();
        this.currentElevationList = new HashMap<>();
        this.colorList = new ArrayList<>();
        this.colorTopList = new ArrayList<>();
        this.initialized = false;
        this.update = false;
        // Create default attributes
        AnnotationAttributes defaultAttributes = new AnnotationAttributes();
        defaultAttributes.setEffect(AVKey.TEXT_EFFECT_OUTLINE);
        defaultAttributes.setBackgroundColor(new Color(0f, 0f, 0f, 0f));
        defaultAttributes.setTextColor(Color.WHITE);
        defaultAttributes.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        defaultAttributes.setDrawOffset(new Point(0, 0));
        defaultAttributes.setImageOffset(new Point(0, 0));
        defaultAttributes.setCornerRadius(0);
        defaultAttributes.setInsets(new Insets(0, 0, 0, 0));
        defaultAttributes.setDistanceMinScale(.1);
        defaultAttributes.setDistanceMaxScale(1);
        defaultAttributes.setDistanceMinOpacity(.5);
        defaultAttributes.setBorderWidth(0);
        gaTime = new GlobeAnnotation("", Position.ZERO, defaultAttributes);
        gaTime.getAttributes().setScale(1.2d);
        layer.addRenderable(gaTime);
        gaEvent = new GlobeAnnotation("", Position.ZERO, defaultAttributes);
        gaEvent.getAttributes().setDrawOffset(new Point(0, 60));
        gaEvent.getAttributes().setScale(1.2d);
        layer.addRenderable(gaEvent);
    }

    @Override
    public void render(DrawContext dc) {
        if (pause) {
            return;
        }
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

        if (transparency) {
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        }
        gl.glEnable(GL2.GL_POLYGON_SMOOTH);
        for (int i = startIndex + 1; i < index; i++) {
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
        }
        int start = latlonList.size() * vertexPerBar;
        for (int i = startIndex + 1; i < index; i++) {
            int vertexIndex = i * 4;
            vertexIndex = vertexIndex + start;
            gl.glBegin(GL2.GL_TRIANGLE_STRIP);
            // triangle top
            gl.glArrayElement(vertexIndex + 3);
            gl.glArrayElement(vertexIndex + 1);
            gl.glArrayElement(vertexIndex + 0);
            gl.glArrayElement(vertexIndex + 2);
            gl.glEnd();
        }
        gl.glDisable(GL2.GL_POLYGON_SMOOTH);

        start = start + latlonList.size() * 4;
        gl.glEnable(GL2.GL_LINE_SMOOTH);
        gl.glLineWidth(0.5f);
        for (int i = startIndex + 1; i < index; i++) {
            int vertexIndex = i * 8;
            vertexIndex += start;
            gl.glBegin(GL2.GL_LINE_STRIP);
            gl.glArrayElement(vertexIndex + 3);
            gl.glArrayElement(vertexIndex + 0);
            gl.glArrayElement(vertexIndex + 6);
            gl.glArrayElement(vertexIndex + 7);
            gl.glArrayElement(vertexIndex + 2);
            gl.glArrayElement(vertexIndex + 1);
            gl.glArrayElement(vertexIndex + 6);
            gl.glArrayElement(vertexIndex + 4);
            gl.glArrayElement(vertexIndex + 5);
            gl.glArrayElement(vertexIndex + 0);
            gl.glArrayElement(vertexIndex + 5);
            gl.glArrayElement(vertexIndex + 1);
            gl.glEnd();
        }
        gl.glDisable(GL2.GL_LINE_SMOOTH);
        gl.glLineWidth(1);
        if (transparency) {
            gl.glDisable(GL2.GL_BLEND);
        }
        update();
        if (this.update) {
            updateVBO(dc);
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
        maxElevation = 0.0d;
        FloatBuffer vertices = Buffers.newDirectFloatBuffer(latlonList.size() * 60);
        for (int i = 0; i < latlonList.size(); i++) {
            LatLon latLon = latlonList.get(i);
            double elevation = terrainElevation[i];
            List<Vec4> vectors = new ArrayList<>();
            for (int j = 0; j < vertexPerBar; j++) {
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
            for (int j = 0; j < 4; j++) {
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
            for (int j = 0; j < vertexPerBar; j++) {
                vectors.add(globe.computePointFromPosition(latLon, elevation));
            }
            for (Vec4 vec : vectors) {
                vertices.put((float) vec.x);
                vertices.put((float) vec.y);
                vertices.put((float) vec.z);
            }
        }
        vertices.rewind();

        FloatBuffer colors = Buffers.newDirectFloatBuffer(colorList.size() * 4 * 22);
        // Side color
        for (int i = 0; i < latlonList.size(); i++) {
            Color color = colorList.get(i);
            float[] values = new float[4];
            color.getRGBColorComponents(values);
            for (int j = 0; j < vertexPerBar; j++) {
                colors.put(values[0]);
                colors.put(values[1]);
                colors.put(values[2]);
                colors.put(0.65f);
            }
        }
        // Top Color
        for (int i = 0; i < latlonList.size(); i++) {
            Color color = colorTopList.get(i);
            float[] values = new float[4];
            color.getRGBColorComponents(values);
            for (int j = 0; j < 4; j++) {
                colors.put(values[0]);
                colors.put(values[1]);
                colors.put(values[2]);
                colors.put(0.65f);
            }
        }
        // Line Color
        for (int i = 0; i < latlonList.size(); i++) {
            Color color = Color.BLACK;
            float[] values = new float[4];
            color.getRGBColorComponents(values);
            for (int j = 0; j < 8; j++) {
                colors.put(values[0]);
                colors.put(values[1]);
                colors.put(values[2]);
                colors.put(0.8f);
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
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, colors.capacity() * Buffers.SIZEOF_FLOAT, colors, GL2.GL_STREAM_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        colors.clear();

        initialized = true;
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
        LatLon deltaLat = LatLon.fromDegrees(BAR_SIZE / 2.0d, 0.000d);
        LatLon deltaLon = LatLon.fromDegrees(0.000d, BAR_SIZE / 2.0d);
        if (byteBuffer != null) {

            FloatBuffer vertices = byteBuffer.asFloatBuffer();
            vertices.rewind();
            Globe globe = dc.getGlobe();
            for (int i = startIndex; i < index; i++) {
                int vboIndex = i * 24;
                int offset = 0;
                if (offsetList.containsKey(i)) {
                    offset = offsetList.get(i);
                }
                LatLon latLon = latlonList.get(i);
                if (barsOrigin != null) {
                    latLon = barsOrigin;
                }
                double movementDelta = offset / 2.d * (BAR_SIZE / MOVEMENT_STEPS);
                if (BAR_GAP_ENABLED) {
                    movementDelta = offset / 1.5d * (BAR_SIZE / MOVEMENT_STEPS);
                }
                LatLon delta = LatLon.fromDegrees(movementDelta, movementDelta);
                LatLon latLonTranslated = new LatLon(latLon.add(delta));
                double elevation = terrainElevation[i];
                double elevationTop = elevation + currentElevationList.get(i);
                List<Vec4> vectors = new ArrayList<>();

                LatLon l1 = new LatLon(latLonTranslated);
                l1 = l1.add(deltaLat);
                vectors.add(globe.computePointFromPosition(l1, elevationTop));
                LatLon l2 = new LatLon(latLonTranslated);
                l2 = l2.subtract(deltaLat);
                vectors.add(globe.computePointFromPosition(l2, elevationTop));
                LatLon l3 = new LatLon(latLonTranslated);
                l3 = l3.subtract(deltaLat);
                vectors.add(globe.computePointFromPosition(l3, elevation));
                LatLon l4 = new LatLon(latLonTranslated);
                l4 = l4.add(deltaLat);
                vectors.add(globe.computePointFromPosition(l4, elevation));

                LatLon l5 = new LatLon(latLonTranslated);
                l5 = l5.add(deltaLon);
                vectors.add(globe.computePointFromPosition(l5, elevation));
                LatLon l6 = new LatLon(latLonTranslated);
                l6 = l6.add(deltaLon);
                vectors.add(globe.computePointFromPosition(l6, elevationTop));
                LatLon l7 = new LatLon(latLonTranslated);
                l7 = l7.subtract(deltaLon);
                vectors.add(globe.computePointFromPosition(l7, elevationTop));
                LatLon l8 = new LatLon(latLonTranslated);
                l8 = l8.subtract(deltaLon);
                vectors.add(globe.computePointFromPosition(l8, elevation));

                for (Vec4 vec : vectors) {
                    vertices.put(vboIndex, (float) vec.x);
                    vboIndex++;
                    vertices.put(vboIndex, (float) vec.y);
                    vboIndex++;
                    vertices.put(vboIndex, (float) vec.z);
                    vboIndex++;
                }
            }
            for (int i = startIndex; i < index; i++) {
                int vboIndex = latlonList.size() * 24 + i * 12;
                int offset = 0;
                if (offsetList.containsKey(i)) {
                    offset = offsetList.get(i);
                }
                double movementDelta = offset / 2.d * (BAR_SIZE / MOVEMENT_STEPS);
                if (BAR_GAP_ENABLED) {
                    movementDelta = offset / 1.5d * (BAR_SIZE / MOVEMENT_STEPS);
                }
                LatLon delta = LatLon.fromDegrees(movementDelta, movementDelta);
                LatLon latLon = latlonList.get(i);
                if (barsOrigin != null) {
                    latLon = barsOrigin;
                }
                LatLon latLonTranslated = new LatLon(latLon.add(delta));
                double elevation = terrainElevation[i];
                double elevationTop = elevation + currentElevationList.get(i);

                List<Vec4> vectors = new ArrayList<>();
                LatLon l1 = new LatLon(latLonTranslated);
                l1 = l1.add(deltaLat);
                vectors.add(globe.computePointFromPosition(l1, elevationTop));
                LatLon l2 = new LatLon(latLonTranslated);
                l2 = l2.subtract(deltaLat);
                vectors.add(globe.computePointFromPosition(l2, elevationTop));
                LatLon l6 = new LatLon(latLonTranslated);
                l6 = l6.add(deltaLon);
                vectors.add(globe.computePointFromPosition(l6, elevationTop));
                LatLon l7 = new LatLon(latLonTranslated);
                l7 = l7.subtract(deltaLon);
                vectors.add(globe.computePointFromPosition(l7, elevationTop));

                for (Vec4 vec : vectors) {
                    vertices.put(vboIndex, (float) vec.x);
                    vboIndex++;
                    vertices.put(vboIndex, (float) vec.y);
                    vboIndex++;
                    vertices.put(vboIndex, (float) vec.z);
                    vboIndex++;
                }
            }
            for (int i = startIndex; i < index; i++) {
                int vboIndex = latlonList.size() * 36 + i * 24;
                int offset = 0;
                if (offsetList.containsKey(i)) {
                    offset = offsetList.get(i);
                }
                double movementDelta = offset / 2.d * (BAR_SIZE / MOVEMENT_STEPS);
                if (BAR_GAP_ENABLED) {
                    movementDelta = offset / 1.5d * (BAR_SIZE / MOVEMENT_STEPS);
                }
                LatLon delta = LatLon.fromDegrees(movementDelta, movementDelta);
                LatLon latLon = latlonList.get(i);
                if (barsOrigin != null) {
                    latLon = barsOrigin;
                }
                LatLon latLonTranslated = new LatLon(latLon.add(delta));
                double elevation = terrainElevation[i];
                double elevationTop = elevation + currentElevationList.get(i);

                List<Vec4> vectors = new ArrayList<>();

                LatLon l1 = new LatLon(latLonTranslated);
                l1 = l1.add(deltaLat);
                vectors.add(globe.computePointFromPosition(l1, elevationTop));
                LatLon l2 = new LatLon(latLonTranslated);
                l2 = l2.subtract(deltaLat);
                vectors.add(globe.computePointFromPosition(l2, elevationTop));
                LatLon l3 = new LatLon(latLonTranslated);
                l3 = l3.subtract(deltaLat);
                vectors.add(globe.computePointFromPosition(l3, elevation));
                LatLon l4 = new LatLon(latLonTranslated);
                l4 = l4.add(deltaLat);
                vectors.add(globe.computePointFromPosition(l4, elevation));

                LatLon l5 = new LatLon(latLonTranslated);
                l5 = l5.add(deltaLon);
                vectors.add(globe.computePointFromPosition(l5, elevation));
                LatLon l6 = new LatLon(latLonTranslated);
                l6 = l6.add(deltaLon);
                vectors.add(globe.computePointFromPosition(l6, elevationTop));
                LatLon l7 = new LatLon(latLonTranslated);
                l7 = l7.subtract(deltaLon);
                vectors.add(globe.computePointFromPosition(l7, elevationTop));
                LatLon l8 = new LatLon(latLonTranslated);
                l8 = l8.subtract(deltaLon);
                vectors.add(globe.computePointFromPosition(l8, elevation));

                for (Vec4 vec : vectors) {
                    vertices.put(vboIndex, (float) vec.x);
                    vboIndex++;
                    vertices.put(vboIndex, (float) vec.y);
                    vboIndex++;
                    vertices.put(vboIndex, (float) vec.z);
                    vboIndex++;
                }
            }
        }

        gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboID);
        byteBuffer = gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_READ_WRITE);
        if (byteBuffer != null) {
            FloatBuffer colors = byteBuffer.asFloatBuffer();
            colors.rewind();
            for (int i = startIndex; i < index; i++) {
                int vboIndex = i * 32;
                Color color = colorList.get(i);
                float[] values = new float[4];
                color.getRGBColorComponents(values);
                int offset = 0;
                if (offsetList.containsKey(i)) {
                    offset = offsetList.get(i);
                }
                for (int j = 0; j < vertexPerBar; j++) {
                    colors.put(vboIndex, values[0]);
                    vboIndex++;
                    colors.put(vboIndex, values[1]);
                    vboIndex++;
                    colors.put(vboIndex, values[2]);
                    vboIndex++;
                    colors.put(vboIndex, (1 - ((float) offset
                            / (float) (MOVEMENT_STEPS * BARS_DISPLAYED))));
                    vboIndex++;
                }
            }
            // Top Color
            for (int i = 0; i < latlonList.size(); i++) {
                int vboIndex = latlonList.size() * 32 + i * 16;
                Color color = colorTopList.get(i);
                float[] values = new float[4];
                color.getRGBColorComponents(values);
                int offset = 0;
                if (offsetList.containsKey(i)) {
                    offset = offsetList.get(i);
                }
                for (int j = 0; j < 4; j++) {
                    colors.put(vboIndex, values[0]);
                    vboIndex++;
                    colors.put(vboIndex, values[1]);
                    vboIndex++;
                    colors.put(vboIndex, values[2]);
                    vboIndex++;
                    colors.put(vboIndex, (1 - ((float) offset
                            / (float) (MOVEMENT_STEPS * BARS_DISPLAYED))));
                    vboIndex++;
                }
            }
            // Line Color
            for (int i = 0; i < latlonList.size(); i++) {
                int vboIndex = latlonList.size() * 48 + i * 32;
                Color color = Color.BLACK;
                float[] values = new float[4];
                color.getRGBColorComponents(values);
                int offset = 0;
                if (offsetList.containsKey(i)) {
                    offset = offsetList.get(i);
                }
                for (int j = 0; j < 8; j++) {
                    colors.put(vboIndex, values[0]);
                    vboIndex++;
                    colors.put(vboIndex, values[1]);
                    vboIndex++;
                    colors.put(vboIndex, values[2]);
                    vboIndex++;
                    colors.put(vboIndex, 1 - ((float) offset
                            / (float) (MOVEMENT_STEPS * BARS_DISPLAYED)));
                    vboIndex++;
                }
            }
        }
        gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        this.update = false;
    }

    /**
     * Adds a new position and the corresponding values
     *
     * @param latLon position in {@link LatLon}
     * @param timecode time code as {@link String}
     * @param elevation elevation
     * @param color color
     */
    public void add(LatLon latLon, String timecode, String event, float elevation, Color color) {
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
        if (elevation > maxElevation) {
            maxElevation = elevation;
        }
        this.eventList.add(event);
        this.latlonList.add(latLon);
        this.timecodeList.add(timecode);
        this.elevationsList.add(elevation);
        this.currentElevationList.put(elevationsList.size() - 1, 0.f);
        this.colorTopList.add(color);
        this.colorList.add(color);
    }

    public void setAnnotationPosition(Position position) {
        annotationPosition = position;
    }

    public void setBarsOrigin(LatLon latLon) {
        barsOrigin = latLon;
    }

    public void setTransparency(boolean enabled) {
        transparency = enabled;
    }

    public double getMaxElevation() {
        return maxElevation;
    }

    /**
     * Set the {@link #update} flag to {@code true}.
     */
    public void update() {
        if (!pause) {
            this.update = true;
            for (int i = 0; i < index; i++) {
                float current = currentElevationList.get(i);
                if (current != elevationsList.get(i)) {
                    if (current + elevationsList.get(i) / RISING_STEPS
                            < elevationsList.get(i)) {
                        currentElevationList.put(i, current
                                + elevationsList.get(i) / RISING_STEPS);
                    } else {
                        currentElevationList.put(i, elevationsList.get(i));
                    }
                }
                if (offsetList.containsKey(i)) {
                    if (offsetList.get(i) < MOVEMENT_STEPS * BARS_DISPLAYED) {
                        offsetList.put(i, offsetList.get(i) + 1);
                    } else {
                        offsetList.put(i, MOVEMENT_STEPS * BARS_DISPLAYED);
                        if (i > startIndex) {
                            startIndex = i;
                        }
                    }
                    if (offsetList.get(i) == MOVEMENT_STEPS) {
                        if (index < elevationsList.size()) {
                            Position positon;
                            if (annotationPosition != null) {
                                positon = annotationPosition;
                            } else {
                                LatLon delta = LatLon.fromDegrees(
                                        BAR_SIZE * 4,
                                        BAR_SIZE * 4);
                                LatLon latLon = new LatLon(
                                        latlonList.get(index - 1).subtract(delta));
                                positon = new Position(latLon, 10);
                            }
                            gaTime.setPosition(positon);
                            gaTime.setText(timecodeList.get(index - 1));
                            gaEvent.setPosition(positon);
                            gaEvent.setText(eventList.get(index - 1));
                            index++;
                            update();
                        } else {
                            resetAnimation();
                        }
                    }
                } else {
                    offsetList.put(i, 0);
                }
            }
        }
    }

    /**
     * Return the number of positions
     *
     * @return the number of positions
     */
    public int size() {
        return latlonList.size();
    }

    /**
     * Returns true if animation is completed
     *
     * @return true if animation is completed
     */
    public boolean animationCompleted() {
        return !(index < latlonList.size() - 1);
    }

    /**
     * Resets the animation values
     */
    public void resetAnimation() {
        pause = true;
        index = 1;
        startIndex = 0;
        for (int i = 0; i < elevationsList.size(); i++) {
            currentElevationList.put(i, 0f);
        }
        offsetList.clear();
        pause = false;
    }
}
