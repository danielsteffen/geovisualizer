/*
 * ElevatedTileImage.java
 *
 * Created by DFKI AV on 15.06.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.wms;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.TextureTile;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.LazilyLoadedTexture;
import gov.nasa.worldwind.render.OrderedRenderable;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.util.Logging;
import java.awt.Point;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * Extended {@link SurfaceImage} with the possibility to change the elevation of
 * the SurfaceImage
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
    private static int MAXQUALITY = 32;
    /**
     * ID of the {@link ElevatedTileImage} Note: Needed to determine the if a
     * newer version is available for a given sector
     *
     * Note: the id is set by the {@link ElevatedRenderableLayer}, the default
     * value after creation is -1 and will be set to an value greater or equal 0.
     */
    private final long updateTime;
    /**
     * The {@link TextureTile}
     */
    private TextureTile tile;

    /**
     * Creates a {@link ElevatedTileImage}, which is an extended version of a
     * {@link SurfaceImage} with the possibility to change the elevation.
     *
     * @param tile the {@link TextureTile} for retrieval
     * @param elevation the elevation of the image
     */
    public ElevatedTileImage(TextureTile tile, double elevation) {
        super(tile.getTileKey(), tile.getSector());
        this.tile = tile;
        this.floating = true;
        this.elevation = elevation;
        this.quality = MAXQUALITY;
        this.updateTime = System.currentTimeMillis();
        this.frameTimestamp = -1L;
        initialization(tile.getSector());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.hashCode() == this.hashCode()) {
            return true;
        }
        ElevatedTileImage eti = (ElevatedTileImage) o;
        return this.geometrySector.equals(eti.geometrySector);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.geometrySector);
        return hash;
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
            quality = MAXQUALITY / 1;
        } else if (WMSUtils.area(sector) > 60000) {
            quality = MAXQUALITY / 2;
        } else if (WMSUtils.area(sector) > 30000) {
            quality = MAXQUALITY / 4;
        } else if (WMSUtils.area(sector) > 15000) {
            quality = MAXQUALITY / 8;
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
     * Renders the surface of the {@link ElevatedTileImage}
     *
     * @param dc the binded {@link DrawContext}
     */
    private void renderSurface(DrawContext dc) {
        if (!getSector().equals(this.geometrySector)) {
            buildGeometry(dc);
        }
        GL2 gl = dc.getGL().getGL2().getGL2();
        // Save
        dc.getView().pushReferenceCenter(dc, this.referenceCenter);
        gl.glPushClientAttrib(GL2.GL_CLIENT_VERTEX_ARRAY_BIT);
        // Setup
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL2.GL_DOUBLE, 0, this.vertices.rewind());
        gl.glClientActiveTexture(GL2.GL_TEXTURE0);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(2, GL2.GL_DOUBLE, 0, this.texCoords.rewind());
        // Draw
        gl.glDrawElements(GL2.GL_TRIANGLE_STRIP, this.indices.limit(),
                GL2.GL_UNSIGNED_INT, this.indices.rewind());
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
        this.vertices = Buffers.newDirectDoubleBuffer(numVertices * 3);
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
     * Generates the texture coordinates for the
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
        DoubleBuffer p = Buffers.newDirectDoubleBuffer(2 * coordCount);
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
        IntBuffer buffer = Buffers.newDirectIntBuffer(indexCount);
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
            GL2 gl = dc.getGL().getGL2();
            gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT // for alpha func
                    | GL2.GL_ENABLE_BIT
                    | GL2.GL_CURRENT_BIT
                    | GL2.GL_DEPTH_BUFFER_BIT // for depth func
                    | GL2.GL_TEXTURE_BIT // for texture env
                    | GL2.GL_TRANSFORM_BIT
                    | GL2.GL_POLYGON_BIT);
            try {
                if (!dc.isPickingMode()) {
                    double opacity = this.getOpacity();
                    gl.glColor4d(1d, 1d, 1d, opacity);
                    gl.glEnable(GL2.GL_BLEND);
                    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
                }
                gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
                gl.glEnable(GL2.GL_DEPTH_TEST);
                gl.glDepthFunc(GL2.GL_LEQUAL);
                gl.glEnable(GL2.GL_ALPHA_TEST);
                gl.glAlphaFunc(GL2.GL_GREATER, 0.01f);
                gl.glActiveTexture(GL2.GL_TEXTURE0);
                gl.glEnable(GL2.GL_TEXTURE_2D);
                gl.glMatrixMode(GL2.GL_TEXTURE);
                gl.glPushMatrix();
                if (!dc.isPickingMode()) {
                    gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE,
                            GL2.GL_MODULATE);
                } else {
                    gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE,
                            GL2.GL_COMBINE);
                    gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SRC0_RGB, GL2.GL_PREVIOUS);
                    gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB,
                            GL2.GL_REPLACE);
                }
                // Texture transforms
                super.applyInternalTransform(dc, true);
                if (imageRepeat) {
                    texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
                } else {
                    texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
                }
                if (imageRepeat) {
                    texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
                } else {
                    texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
                }
                // Draw
                renderSurface(dc);
                gl.glActiveTexture(GL2.GL_TEXTURE0);
                gl.glMatrixMode(GL2.GL_TEXTURE);
                gl.glPopMatrix();
                gl.glDisable(GL2.GL_TEXTURE_2D);
            } finally {
                gl.glPopAttrib();
            }
        }
    }

    /**
     * Compute per-frame attributes, and add the ordered renderable to the
     * ordered renderable list.
     *
     * @param dc Current draw context.
     */
    private void makeOrderedRenderable(DrawContext dc) {
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
    private double computeEyeDistance(DrawContext dc) {
        double minDistance = Double.MAX_VALUE;
        Vec4 eyePoint = dc.getView().getEyePoint();
        List<Vec4> points = new ArrayList<>();
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
    private Extent computeExtent(DrawContext dc) {
        List<Vec4> points = new ArrayList<>();
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
     * Determines whether the cube intersects the view frustum.
     *
     * @param dc the current draw context.
     *
     * @return true if this cube intersects the frustum, otherwise false.
     */
    private boolean intersectsFrustum(DrawContext dc) {
        if (this.extent == null) {
            return true; // don't know the visibility, shape hasn't been computed yet
        }
        if (dc.isPickingMode()) {
            return dc.getPickFrustums().intersectsAny(this.extent);
        }
        return dc.getView().getFrustumInModelCoordinates().intersects(this.extent);
    }

    /**
     * Return the id Note: Through the id the version of the
     * {@link ElevatedTileImage} is determined.
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
     * Clears all variables
     */
    public void clear() {
        if (vertices != null) {
            vertices.clear();
        }
        if (corners != null) {
            corners.clear();
        }
        if (indices != null) {
            indices.clear();
        }
        if (texCoords != null) {
            texCoords.clear();
        }
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
     * Returns the {@link TextureTile}
     *
     * @return the {@link TextureTile}
     */
    public TextureTile getTile() {
        return tile;
    }

    @Override
    public boolean bind(DrawContext dc) {
        boolean returnValue = false;
        GL gl = dc.getGL().getGL2();
        if (this.generatedTexture != null) {
            if (generatedTexture instanceof LazilyLoadedTexture) {
                LazilyLoadedTexture tex = (LazilyLoadedTexture) generatedTexture;
                if (tex.isTextureInitializationFailed()) {
                    return false;
                }
                if (dc == null) {
                    String message = Logging.getMessage("nullValue.DrawContextIsNull");
                    Logging.logger().severe(message);
                    throw new IllegalStateException(message);
                }
                if (tile.bind(dc)) {
                    Texture texture = null;
                    if (tile.getTextureData() != null) {
                        texture = TextureIO.newTexture(tile.getTextureData());
                    }
                    if (texture == null) {
                        texture = tile.getTexture(dc.getGpuResourceCache());
                    }
                    if (texture == null) {
                        texture = tile.getTexture(dc.getTextureCache());
                    }
                    if (texture == null) {
                        texture = dc.getGpuResourceCache().getTexture(tile.getTileKey());
                    }
                    if (texture == null) {
                        dc.getTextureCache().getTexture(tile.getTileKey());
                    }
                    if (texture == null && tile.getFallbackTile().getTextureData() != null) {
                        TextureIO.newTexture(tile.getFallbackTile().getTextureData());
                    }
                    if (texture == null) {
                        tile.getFallbackTile().getTexture(dc.getGpuResourceCache());
                    }
                    if (texture == null) {
                        tile.getFallbackTile().getTexture(dc.getTextureCache());
                    }
                    if (texture != null) {
                        texture.bind(gl);
                        returnValue = true;
                    } else {
                        returnValue = false;
                    }
                } else {
                    return false;
                }
            } else {
                returnValue = generatedTexture.bind(dc);
            }
        }
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
                GL2.GL_NEAREST);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
                GL2.GL_NEAREST);
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
