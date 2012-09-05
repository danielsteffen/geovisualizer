/*
 *  ElevatedTileImage.java 
 *
 *  Created by DFKI AV on 15.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.OrderedRenderable;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.TileKey;
import java.awt.Point;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;

/**
 * Extended {@link SurfaceImage} with the possibility to change the elevation of
 * the SurfaceImage
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class ElevatedTileImage extends SurfaceImage implements OrderedRenderable {

    /**
     * Determined each frame
     */
    private long frameTimestamp;
    /**
     * Distance from the eye point to the cube.
     */
    private double eyeDistance;
    /**
     * Extend which encloses all points of the {@link ElevatedTileImage}
     */
    private Extent extent;
    /**
     * Display quality of the surface (Amount of supporting points) which
     * determines how much polygons will be rendered to display the surface of
     * the {@link ElevatedTileImage}.
     */
    private int quality;
    /**
     * Default elevation set to zero meters over sea level
     */
    private double elevation;
    /**
     * If true image will be repeated on the surface
     */
    private boolean imageRepeat;
    /**
     * {@link Point} which defines the image offset for the rendering on the
     * surface
     */
    private Point imageOffset;
    /**
     * Bounding box for the modified (changed elevation) geometry
     */
    private Sector geometrySector;
    /**
     * Reference center of the modified (changed elevation) geometry
     */
    private Vec4 referenceCenter;
    /**
     * Vertices of the modified (changed elevation) geometry
     */
    private DoubleBuffer vertices;
    /**
     * Coordinates of the modified (changed elevation) geometry
     */
    private DoubleBuffer texCoords;
    /**
     * Triangles indices of the modified (changed elevation) geometry
     */
    private IntBuffer indices;
    /**
     * Flag for enable floating Note: Must be enabled for elevation != 0
     */
    private boolean floating;
    /**
     * Maximum quality of the image (support points)
     */
    private static int MAXQUALITY = 64;
    /**
     * ID of the {@link ElevatedTileImage} Note: Needed to determine the if a
     * newer version is available for a given sector
     *
     * Note: the id is set by the {@link ElevatedSurfaceLayer}, the default
     * value after creation is -1 and will be set to an value >= 0.
     */
    private final long updateTime;

    /**
     * Creates a {@link ElevatedTileImage}, which is an extended version of a {@link SurfaceImage}
     * with the possibility to chenge the elevation.
     *
     * @param tileKey {@link TileKey} for Texture retreival
     * @param sector {@link Sector} of the image
     */
    public ElevatedTileImage(TileKey tileKey, Sector sector, double elevation) {
        super(tileKey, sector);
        this.floating = true;
        this.elevation = elevation;
        this.quality = 1;
        this.updateTime = System.currentTimeMillis();
        this.frameTimestamp = -1L;
        initialization(sector);
    }

    /**
     * Initialize the {@link ElevatedTileImage} parameters
     *
     * @param sector {@link Sector} for the initialize calculation
     */
    private void initialization(Sector sector) {
        Double[] v = WMSUtils.verticies(sector);
        double max = Math.max(Math.max(v[0], v[1]),
                Math.max(v[2], v[3]));
        double min = Math.min(Math.min(v[0], v[1]),
                Math.min(v[2], v[3]));
        // increase amount of support points if area of sector is high
        if (WMSUtils.area(sector) > 5000000) {
            quality = MAXQUALITY;
        } else if (WMSUtils.area(sector) > 1000000) {
            quality = MAXQUALITY / 2;
        } else if (WMSUtils.area(sector) > 60000) {
            quality = MAXQUALITY / 4;
        } else if (WMSUtils.area(sector) > 30000) {
            quality = MAXQUALITY / 8;
        } else if (WMSUtils.area(sector) > 15000) {
            quality = MAXQUALITY / 16;
        } else if (WMSUtils.area(sector) == 0) {
            quality = 0;
        }

        // Check if Sector is on noth/southpole
        if (min == 0) {
            if (max > 2000) {
                quality = MAXQUALITY / 2;
            } else if (max > 800) {
                quality = MAXQUALITY / 4;
            } else if (max > 400) {
                quality = MAXQUALITY / 6;
            } else if (max > 200) {
                quality = MAXQUALITY / 8;
            } else if (max > 100) {
                quality = MAXQUALITY / 16;
            } else {
                quality = MAXQUALITY / 32;
            }
        }
    }

    /**
     * Return the id Note: Through the id the version of the {@link ElevatedTileImage}
     * is determined.
     *
     * @return the id of the {@link ElevatedTileImage}
     */
    public long getUpdateTime() {
        return updateTime;
    }

    /**
     * Return the quality value Note: Display quality of the surface (Amount of
     * supporting points), which determines how much polygons will be rendered
     * to display the surface of the {@link ElevatedTileImage}.
     *
     * @return the quality value of the {@link ElevatedTileImage}
     */
    public int getQuality() {
        return quality;
    }

    /**
     * Return the elevation
     *
     * @return the elevation of the {@link ElevatedTileImage}
     */
    public double getElevation() {
        return this.elevation;
    }

    /**
     * Sets the elevation
     *
     * @param elevation the elevation to set for the {@link ElevatedTileImage}
     */
    public void setElevation(double elevation) {
        this.elevation = elevation;
        this.geometrySector = null;  // invalidate geometry
    }

    /**
     * Return the floating (true if enabled / false if disabled)
     *
     * @return true if floating (elevation) is enable for the
     * {@link ElevatedTileImage}
     */
    public boolean isFloating() {
        return floating;
    }

    /**
     * Sets the floating (enable / disable)
     *
     * @param f true for enable floating / false for disable floating
     */
    public void setFloating(boolean f) {
        floating = f;
    }

    /**
     * Sets the imageRepeat (enable / disable)
     *
     * @param imageRepeat true for setting image repeating to enabled
     */
    public void setImageRepeat(boolean imageRepeat) {
        this.imageRepeat = imageRepeat;
    }

    /**
     * Return the imageRepeat (true if enabled / false if disabled)
     *
     * @return true if image repeating is enabled
     */
    public boolean getImageRepeat() {
        return this.imageRepeat;
    }

    /**
     * Sets the image offset
     *
     * @param offset the offset to set as {@link Point}
     */
    public void setImageOffset(Point offset) {
        if (offset != null) {
            this.imageOffset = offset;
        }
    }

    /**
     * Return the image offset
     *
     * @return the image offset as {@link Point}
     */
    public Point getImageOffset() {
        return this.imageOffset;
    }

    /**
     * Renders the surface of the {@link ElevatedTileImage}
     *
     * @param dc the binded {@link DrawContext}
     */
    private void renderSurface(DrawContext dc) {
        if (!getSector().equals(this.geometrySector)) {
            buildGeometry(dc);
        }

        GL gl = dc.getGL();

        // Save
        dc.getView().pushReferenceCenter(dc, this.referenceCenter);
        gl.glPushClientAttrib(GL.GL_CLIENT_VERTEX_ARRAY_BIT);

        // Setup
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL.GL_DOUBLE, 0, this.vertices.rewind());

        gl.glClientActiveTexture(GL.GL_TEXTURE0);
        gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(2, GL.GL_DOUBLE, 0, this.texCoords.rewind());

        // Draw
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, this.indices.limit(),
                GL.GL_UNSIGNED_INT, this.indices.rewind());

        // Restore
        gl.glPopClientAttrib();
        dc.getView().popReferenceCenter(dc);
    }

    /**
     * Builds the modified (through elevation of the image) geometry
     *
     * @param dc the binded {@link DrawContext}
     */
    private void buildGeometry(DrawContext dc) {
        Globe globe = dc.getGlobe();
        Sector sector = getSector();

        LatLon centroid = sector.getCentroid();
        this.geometrySector = sector;
        this.referenceCenter = globe.computePointFromPosition(centroid, 0d);

        Angle dLat = sector.getDeltaLat().divide(quality);
        Angle dLon = sector.getDeltaLon().divide(quality);

        // Compute vertices
        int numVertices = (quality + 1) * (quality + 1);
        this.vertices = BufferUtil.newDoubleBuffer(numVertices * 3);
        int iv = 0;
        Angle lat = sector.getMinLatitude();
        for (int j = 0; j <= quality; j++) {
            Angle lon = sector.getMinLongitude();
            for (int i = 0; i <= quality; i++) {
                Vec4 p = globe.computePointFromPosition(lat, lon, elevation);
                Vec4 res = p.subtract3(referenceCenter);
                vertices.put(iv++, res.x);
                vertices.put(iv++, res.y);
                vertices.put(iv++, res.z);
                lon = lon.add(dLon);
            }
            lat = lat.add(dLat);
        }

        // Compute indices
        if (this.indices == null) {
            this.indices = getIndices(quality);
        }
        // Compute texture coordinates
        if (this.texCoords == null) {
            this.texCoords = getTextureCoordinates(quality);
        }
    }

    /**
     * Generates the texture coordinates fpr the
     * <code>ElevatedTileImage</code>
     *
     * @param quality Amount of supporting points
     * @return Texture coordinates as {@link DoubleBuffer}
     */
    private static DoubleBuffer getTextureCoordinates(int quality) {
        if (quality < 1) {
            quality = 1;
        }

        int coordCount = (quality + 1) * (quality + 1);
        DoubleBuffer p = BufferUtil.newDoubleBuffer(2 * coordCount);
        double delta = 1d / quality;
        int k = 0;
        for (int j = 0; j <= quality; j++) {
            double v = j * delta;
            for (int i = 0; i <= quality; i++) {
                p.put(k++, i * delta); // u
                p.put(k++, v);
            }
        }
        return p;
    }

    /**
     * Compute indices for triangle strips
     *
     * @param quality Amount of supporting points
     * @return Indices as {@link IntBuffer}
     */
    private static IntBuffer getIndices(int quality) {
        if (quality < 1) {
            quality = 1;
        }

        int sideSize = quality;

        int indexCount = 2 * sideSize * sideSize + 4 * sideSize - 2;
        IntBuffer buffer = BufferUtil.newIntBuffer(indexCount);
        int k = 0;
        for (int i = 0; i < sideSize; i++) {
            buffer.put(k);
            if (i > 0) {
                buffer.put(++k);
                buffer.put(k);
            }

            if (i % 2 == 0) // even
            {
                buffer.put(++k);
                for (int j = 0; j < sideSize; j++) {
                    k += sideSize;
                    buffer.put(k);
                    buffer.put(++k);
                }
            } else // odd
            {
                buffer.put(--k);
                for (int j = 0; j < sideSize; j++) {
                    k -= sideSize;
                    buffer.put(k);
                    buffer.put(--k);
                }
            }
        }
        return buffer;
    }

    @Override
    public boolean bind(DrawContext dc) {
        boolean returnValue = super.bind(dc);
        GL gl = dc.getGL();
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
                GL.GL_NEAREST);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                GL.GL_NEAREST);
        return returnValue;
    }

    @Override
    public void render(DrawContext dc) {
        // Render is called three times:
        // 1) During picking. The cube is drawn in a single color.
        // 2) As a normal renderable. The cube is added to the ordered renderable queue.
        // 3) As an OrderedRenderable. The cube is drawn.

        if (this.extent != null) {
            if (!this.intersectsFrustum(dc)) {
                return;
            }

            // If the shape is less that a pixel in size, don't render it.
            if (dc.isSmall(this.extent, 1)) {
                return;
            }
        }

        if (dc.isOrderedRenderingMode()) {
            this.drawOrderedRenderable(dc);
        } else {
            this.makeOrderedRenderable(dc);
        }
    }

    /**
     * Compute per-frame attributes, and add the ordered renderable to the
     * ordered renderable list.
     *
     * @param dc Current draw context.
     */
    protected void makeOrderedRenderable(DrawContext dc) {
        if (dc.getFrameTimeStamp() != this.frameTimestamp) {
            // Compute a bounding box that encloses the image.
            this.extent = computeExtent(dc);

            // Compute the distance from the eye to the surface image position.
            this.eyeDistance = computeEyeDistance(dc);

            this.frameTimestamp = dc.getFrameTimeStamp();
        }

        // Add the cube to the ordered renderable list. 
        // The SceneController sorts the ordered renderables by eye
        // distance, and then renders them back to front. 
        // Render will be called again in ordered rendering mode, and at
        // that point we will actually draw the cube.
        dc.addOrderedRenderable(this);
    }

    /**
     * Computes the minimum distance between this shape and the eye point.
     *
     * @param dc the draw context.
     *
     * @return the minimum distance from the shape to the eye point.
     */
    protected double computeEyeDistance(DrawContext dc) {
        double minDistance = Double.MAX_VALUE;
        Vec4 eyePoint = dc.getView().getEyePoint();

        List<Vec4> points = new ArrayList<Vec4>();
        List<LatLon> cornerList = getCorners();

        for (LatLon c : cornerList) {
            points.add(dc.getGlobe().computePointFromPosition(c, elevation));
            points.add(dc.getGlobe().computePointFromLocation(c));
        }

        for (Vec4 point : points) {
            double d = point.distanceTo3(eyePoint);
            if (d < minDistance) {
                minDistance = d;
            }
        }

        return minDistance;
    }

    /**
     * Computes this shapes extent. If a reference point is specified, the
     * extent is translated to that reference point.
     *
     * @param dc the current {@link DrawContext}
     *
     * @return the computed extent, or null if the extent cannot be computed.
     */
    protected Extent computeExtent(DrawContext dc) {
        List<Vec4> points = new ArrayList<Vec4>();
        List<LatLon> cornerList = getCorners();

        for (LatLon c : cornerList) {
            points.add(dc.getGlobe().computePointFromPosition(c, elevation));
            points.add(dc.getGlobe().computePointFromLocation(c));
        }

        Box boundingBox = Box.computeBoundingBox(points);

        // The bounding box is computed relative to the polygon's reference point, so it needs to be translated to
        // model coordinates in order to indicate its model-coordinate extent.
        return boundingBox;
    }

    /**
     * Set up drawing state, and draw the cube. This method is called when the
     * cube is rendered in ordered rendering mode.
     *
     * @param dc Current draw context.
     */
    private void drawOrderedRenderable(DrawContext dc) {
        if (!floating) {
            super.render(dc);
            return;
        }

        if (dc == null) {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        if (!this.getSector().intersects(dc.getVisibleSector())) {
            return;
        }

        if (this.bind(dc)) {
            Texture texture = (Texture) dc.getTextureCache().get(getImageSource());
            if (texture == null) {
                return;
            }

            GL gl = dc.getGL();

            gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT // for alpha func
                    | GL.GL_ENABLE_BIT
                    | GL.GL_CURRENT_BIT
                    | GL.GL_DEPTH_BUFFER_BIT // for depth func
                    | GL.GL_TEXTURE_BIT // for texture env
                    | GL.GL_TRANSFORM_BIT
                    | GL.GL_POLYGON_BIT);
            try {
                if (!dc.isPickingMode()) {
                    double opacity = this.getOpacity();
                    gl.glColor4d(1d, 1d, 1d, opacity);
                    gl.glEnable(GL.GL_BLEND);
                    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                }


                gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);

                gl.glEnable(GL.GL_DEPTH_TEST);
                gl.glDepthFunc(GL.GL_LEQUAL);

                gl.glEnable(GL.GL_ALPHA_TEST);
                gl.glAlphaFunc(GL.GL_GREATER, 0.01f);

                gl.glActiveTexture(GL.GL_TEXTURE0);
                gl.glEnable(GL.GL_TEXTURE_2D);
                gl.glMatrixMode(GL.GL_TEXTURE);
                gl.glPushMatrix();
                if (!dc.isPickingMode()) {
                    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,
                            GL.GL_MODULATE);
                } else {
                    gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,
                            GL.GL_COMBINE);
                    gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_SRC0_RGB, GL.GL_PREVIOUS);
                    gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_COMBINE_RGB,
                            GL.GL_REPLACE);
                }

                // Texture transforms
                super.applyInternalTransform(dc, true);

                if (imageRepeat) {
                    texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
                } else {
                    texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
                }
                if (imageRepeat) {
                    texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
                } else {
                    texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
                }

                // Draw
                renderSurface(dc);

                gl.glActiveTexture(GL.GL_TEXTURE0);
                gl.glMatrixMode(GL.GL_TEXTURE);
                gl.glPopMatrix();
                gl.glDisable(GL.GL_TEXTURE_2D);
            } finally {
                gl.glPopAttrib();
            }
        }

    }

    /**
     * Determines whether the cube intersects the view frustum.
     *
     * @param dc the current draw context.
     *
     * @return true if this cube intersects the frustum, otherwise false.
     */
    protected boolean intersectsFrustum(DrawContext dc) {
        if (this.extent == null) {
            return true; // don't know the visibility, shape hasn't been computed yet
        }
        if (dc.isPickingMode()) {
            return dc.getPickFrustums().intersectsAny(this.extent);
        }

        return dc.getView().getFrustumInModelCoordinates().intersects(this.extent);
    }

    @Override
    public double getDistanceFromEye() {
        return this.eyeDistance;
    }

    @Override
    public void pick(DrawContext dc, Point point) {
        // Use same code for rendering and picking.
        this.render(dc);
    }
}
