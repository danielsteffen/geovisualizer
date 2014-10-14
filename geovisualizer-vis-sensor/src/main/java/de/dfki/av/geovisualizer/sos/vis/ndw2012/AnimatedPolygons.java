/*
 *  AnimatedPolygons.java 
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
import gov.nasa.worldwindx.examples.util.PowerOfTwoPaddedImage;
import java.awt.*;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class AnimatedPolygons implements Renderable {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(AnimatedPolygons.class);
    // Animation configuration parameters begin
    /**
     * Transparency flag
     */
    private static final boolean TRANSPARENCY = false;
    /**
     * Amount of steps until bar has max size
     */
    private final static int ANIMATION_DELAY = 20;
    // Animation configuration parameters end
    /**
     * The {@link List} of {@link LatLon} elements.
     */
    private List<LatLon> latlonList;
    /**
     * The {@link List} of {@link List} of elevations for the
     * {@link #latlonList}.
     */
    private List<Float> elevationsList;
    private List<String> timecodeList;
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
     * Whether this points are initialized or not.
     */
    private boolean initialized;
    private int vertexPerPolygon = 2;
    /**
     *
     */
    private long lasttime = -1L;
    private int index = 1;
    private List<Integer> stressList;
    private boolean pause;
    private HighResolutionTerrain terrain;
    private PowerOfTwoPaddedImage noStress;
    private PowerOfTwoPaddedImage stress;
    private GlobeAnnotation gaNoStress;
    private GlobeAnnotation gaStress;
    private GlobeAnnotation gaTime;

    /**
     * Constructor.
     */
    public AnimatedPolygons(RenderableLayer layer) {
        this.latlonList = new ArrayList<>();
        this.elevationsList = new ArrayList<>();
        this.colorList = new ArrayList<>();
        this.stressList = new ArrayList<>();
        this.timecodeList = new ArrayList<>();
        this.initialized = false;
        try {
            noStress = PowerOfTwoPaddedImage.fromBufferedImage(
                    ImageIO.read(this.getClass().getClassLoader().getResource("icons/nostress.png")));
        } catch (IOException ex) {
            log.error("", ex);
        }
        try {
            stress = PowerOfTwoPaddedImage.fromBufferedImage(
                    ImageIO.read(this.getClass().getClassLoader().getResource("icons/stress.png")));
        } catch (IOException ex) {
            log.error("", ex);
        }
        // Create default attributes
        AnnotationAttributes defaultAttributes = new AnnotationAttributes();
        defaultAttributes.setBackgroundColor(new Color(0f, 0f, 0f, 0f));
        defaultAttributes.setTextAlign(AVKey.RIGHT);
        defaultAttributes.setTextColor(Color.WHITE);
        defaultAttributes.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        defaultAttributes.setEffect(AVKey.TEXT_EFFECT_OUTLINE);
        defaultAttributes.setDrawOffset(new Point(0, 0));
        defaultAttributes.setImageOffset(new Point(0, 0));
        defaultAttributes.setCornerRadius(0);
        defaultAttributes.setInsets(new Insets(0, 0, 0, 0));
        defaultAttributes.setDistanceMinScale(.1);
        defaultAttributes.setDistanceMaxScale(1);
        defaultAttributes.setDistanceMinOpacity(.5);
        defaultAttributes.setBorderWidth(0);
        defaultAttributes.setImageRepeat(AVKey.REPEAT_NONE);
        defaultAttributes.setSize(new Dimension(noStress.getOriginalWidth(),
                noStress.getOriginalHeight()));
        defaultAttributes.setImageOpacity(0.8f);
        gaNoStress = new GlobeAnnotation("", Position.ZERO, defaultAttributes);
        gaNoStress.getAttributes().setImageSource(noStress.getPowerOfTwoImage());
        layer.addRenderable(gaNoStress);
        gaStress = new GlobeAnnotation("", Position.ZERO, defaultAttributes);
        gaStress.getAttributes().setImageSource(stress.getPowerOfTwoImage());
        layer.addRenderable(gaStress);
        gaNoStress.getAttributes().setImageOpacity(0.8f);
        gaStress.getAttributes().setImageOpacity(0.0f);
        gaTime = new GlobeAnnotation("", Position.ZERO, defaultAttributes);
        gaTime.getAttributes().setSize(new Dimension(noStress.getOriginalWidth(),
                noStress.getOriginalHeight() + 30));
        layer.addRenderable(gaTime);
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

        if (TRANSPARENCY) {
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        }

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_COLOR_ARRAY);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID);
        gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboID);
        gl.glColorPointer(4, GL2.GL_FLOAT, 0, 0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        gl.glEnable(GL2.GL_POLYGON_SMOOTH);
        gl.glBegin(GL2.GL_TRIANGLE_STRIP);
        if (index < latlonList.size()) {
            if (System.currentTimeMillis() - lasttime > ANIMATION_DELAY) {
                int increase = 1;
                if (ANIMATION_DELAY < 30) {
                    increase = (int) 30 / ANIMATION_DELAY;
                }
                lasttime = System.currentTimeMillis();
                index += increase;
            }
        } else {
            index = latlonList.size() - 1;
        }
        for (int i = 0; i < index; i++) {
            int vertexIndex = i * vertexPerPolygon;
            gl.glArrayElement(vertexIndex + 0);
            gl.glArrayElement(vertexIndex + 1);
        }
        gl.glEnd();
        gl.glDisable(GL2.GL_POLYGON_SMOOTH);
        int start = latlonList.size() * vertexPerPolygon;


        gl.glEnable(GL2.GL_LINE_SMOOTH);
        gl.glLineWidth(5);
        gl.glBegin(GL2.GL_LINE_STRIP);
        for (int i = 0; i < index; i++) {
            int vertexIndex = i * 1;
            vertexIndex += start;
            gl.glArrayElement(vertexIndex + 0);
        }
        gl.glEnd();
        gl.glDisable(GL2.GL_LINE_SMOOTH);
        gl.glLineWidth(1);
        gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        if (TRANSPARENCY) {
            gl.glDisable(GL2.GL_BLEND);
        }

        gl.glPopMatrix();
        gl.glPopAttrib();
        updateAnnotations();
    }

    /**
     * Returns true if animation is completed
     *
     * @return {@link Boolean}
     */
    public boolean animationCompleted() {
        return !(index < latlonList.size());
    }

    /**
     * Initialize the point to be used by OpenGL2.
     *
     * @param dc the {@link DrawContext}
     */
    private void initialize(DrawContext dc) {
        lasttime = System.currentTimeMillis();
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
        FloatBuffer vertices = Buffers.newDirectFloatBuffer(latlonList.size() * 12);
        for (int i = 0; i < latlonList.size(); i++) {
            LatLon latLon = latlonList.get(i);
            double elevation = terrainElevation[i];
            Vec4 bottom = globe.computePointFromPosition(latLon, elevation);
            elevation += elevationsList.get(i);
            Vec4 top = globe.computePointFromPosition(latLon, elevation);
            List<Vec4> vectors = new ArrayList<>();
            vectors.add(top);
            vectors.add(bottom);
            for (Vec4 vec : vectors) {
                vertices.put((float) vec.x);
                vertices.put((float) vec.y);
                vertices.put((float) vec.z);
            }
        }
        for (int i = 0; i < latlonList.size(); i++) {
            LatLon latLon = latlonList.get(i);
            double elevation = terrainElevation[i];
            elevation += elevationsList.get(i);
            List<Vec4> vectors;
            Vec4 top = globe.computePointFromPosition(latLon, elevation);
            vectors = new ArrayList<>();
            vectors.add(top);
            for (Vec4 vec : vectors) {
                vertices.put((float) vec.x);
                vertices.put((float) vec.y);
                vertices.put((float) vec.z);
            }
        }
        vertices.rewind();

        FloatBuffer colors = Buffers.newDirectFloatBuffer(colorList.size() * 4 * 3);
        // Side color
        for (int i = 0; i < latlonList.size(); i++) {
            Color color = colorList.get(i);
            float[] values = new float[4];
            color.getRGBColorComponents(values);
            for (int j = 0; j < 2; j++) {
                colors.put(values[0]);
                colors.put(values[1]);
                colors.put(values[2]);
                colors.put(0.75f);
            }
        }
        // Top Color
        for (int i = 0; i < latlonList.size(); i++) {
            Color color = colorList.get(i);
            float[] values = new float[4];
            color.getRGBColorComponents(values);
            colors.put(values[0]);
            colors.put(values[1]);
            colors.put(values[2]);
            colors.put(0.75f);
        }
        colors.rewind();

        // Create the vbo 
        int[] buffers = new int[2];
        gl.glGenBuffers(2, buffers, 0);
        vboID = buffers[0];
        cboID = buffers[1];

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.capacity() * Buffers.SIZEOF_FLOAT, vertices, GL2.GL_STATIC_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        vertices.clear();

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, cboID);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, colors.capacity() * Buffers.SIZEOF_FLOAT, colors, GL2.GL_STATIC_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        colors.clear();

        initialized = true;
    }

    /**
     * Adds a new position and the corresponding values
     *
     * @param latLon
     * @param timecode
     * @param elevation
     * @param stress
     * @param color
     */
    public void add(LatLon latLon, String timecode, float elevation,
            Number stress, Color color) {
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
        int stressLevel;
        if (stress == null) {
            stressLevel = 0;
        } else {
            stressLevel = stress.intValue();
        }
        this.latlonList.add(latLon);
        this.timecodeList.add(timecode);
        this.elevationsList.add(elevation);
        this.stressList.add(stressLevel);
        this.colorList.add(color);
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
     * Resets the animation values
     */
    public void resetAnimation() {
        pause = true;
        index = 1;
        pause = false;
    }

    /**
     * Updates the annotations
     */
    private void updateAnnotations() {
        if (index < elevationsList.size()) {
            if (gaTime != null) {
                gaTime.setPosition(new Position(latlonList.get(index - 1), elevationsList.get(index - 1)));
                gaTime.setText(timecodeList.get(index - 1));
            }
            if (gaNoStress != null && gaStress != null && gaTime != null) {
                gaNoStress.setPosition(new Position(latlonList.get(index - 1), elevationsList.get(index - 1)));
                gaStress.setPosition(new Position(latlonList.get(index - 1), elevationsList.get(index - 1)));
                if (stressList.get(index - 1) > 0) {
                    gaStress.getAttributes().setImageOpacity(0.8f);
                } else {
                    boolean fade = false;
                    for (int i = 0; i < 25; i++) {
                        if (index - i > -1 && stressList.get(index - i) > 0) {
                            fade = true;
                            double opac = 0.8f / (float) i;
                            if (opac > 0 && opac < 1) {
                                gaStress.getAttributes().setImageOpacity(opac);
                            }
                            break;
                        }
                        if (index + i < stressList.size()
                                && stressList.get(index + i) > 0) {
                            fade = true;
                            double opac = 0.8f / (float) i;
                            if (opac > 0 && opac < 1) {
                                gaStress.getAttributes().setImageOpacity(opac);
                            }
                            break;
                        }
                    }
                    if (!fade) {
                        gaStress.getAttributes().setImageOpacity(0.0f);
                    }
                }
            }
        }
    }
}
