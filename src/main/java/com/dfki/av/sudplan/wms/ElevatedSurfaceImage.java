/*
 *  ElevatedSurfaceImage.java 
 *
 *  Created by DFKI AV on 15.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wms;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.pick.PickSupport;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.OrderedRenderable;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.util.Logging;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import javax.media.opengl.GL;

/**
 * Extended {@link SurfaceImage} with the possibility to change the elevation of
 * the SurfaceImage
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class ElevatedSurfaceImage extends SurfaceImage implements OrderedRenderable {

    /**
     * Geographic position of the cube.
     */
    protected Position position;
    /**
     * Length of each face, in meters.
     */
    protected double size;
    /**
     * Support object to help with pick resolution.
     */
    protected PickSupport pickSupport = new PickSupport();
    /**
     * Determined each frame
     */
    protected long frameTimestamp = -1L;
    /**
     * Cartesian position of the cube, computed from {@link #position}.
     */
    protected Vec4 placePoint;
    /**
     * Distance from the eye point to the cube.
     */
    protected double eyeDistance;
    /**
     * Extend which encloses all points of the {@link ElevatedSurfaceImage}
     */
    protected Extent extent;
    /**
     * Display quality of the surface (Amount of supporting points)
     *
     */
    private int quality = 1;
    /**
     * Default elevation set to zero meters over sea level
     *
     */
    private double elevation = 0;
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
     * Flag for force update
     */
    private boolean needsUpdate = true;
    /**
     * Flag for enable floating Note: Must be enabled for elevation != 0
     */
    private boolean floating = true;
    /**
     * ID of the {@link ElevatedSurfaceImage} Note: Needed to determine the if a
     * newer version is available for a given sector
     *
     * Note: the id is set by the {@link ElevatedSurfaceLayer}, the default
     * value after creation is -1 and will be set to an value >= 0.
     */
    private int id;

    public ElevatedSurfaceImage(Object imageSource, Sector sector) {
        super(imageSource, sector);
        Double[] verticies = WMSUtils.verticies(sector);
        double max = Math.max(Math.max(verticies[0], verticies[1]), Math.max(verticies[2], verticies[3]));
        double min = Math.min(Math.min(verticies[0], verticies[1]), Math.min(verticies[2], verticies[3]));
        position = new Position(sector.getCentroid(), elevation);
        size = max * 1000;
        this.id = -1;
        // increase amount of support points if area of sector is high
        if (WMSUtils.area(sector) > 5000000) {
            quality = 256;
        } else if (WMSUtils.area(sector) > 1000000) {
            quality = 128;
        } else if (WMSUtils.area(sector) > 60000) {
            quality = 64;
        } else if (WMSUtils.area(sector) > 30000) {
            quality = 32;
        } else if (WMSUtils.area(sector) > 15000) {
            quality = 16;
        } else if (WMSUtils.area(sector) == 0) {
            quality = 0;
        }


        // Check if Sector is on noth/southpole
        if (min == 0) {
            if (max > 2000) {
                quality = 128;
            } else if (max > 800) {
                quality = 64;
            } else if (max > 400) {
                quality = 36;
            } else if (max > 200) {
                quality = 16;
            }
        }
    }

    /**
     * Return the id
     *
     * @return the id of the {@link ElevatedSurfaceImage}
     */
    public int getId() {
        return id;
    }

    public int getQuality() {
        return quality;
    }

    /**
     * Sets the id
     *
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Return the elevation
     *
     * @return the elevation of the {@link ElevatedSurfaceImage}
     */
    public double getElevation() {
        return this.elevation;
    }

    /**
     * Sets the elevation
     *
     * @param elevation the elevation to set for the {@link ElevatedSurfaceImage}
     */
    public void setElevation(double elevation) {
        this.elevation = elevation;
        this.position = new Position(getSector().getCentroid(), elevation);
        this.geometrySector = null;  // invalidate geometry
    }

    /**
     * Return the floating (true if enabled / false if disabled)
     *
     * @return true if floating (elevation) is enable for the
     * {@link ElevatedSurfaceImage}
     */
    public boolean getFloating() {
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
        this.imageOffset = offset;
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
     * Renders the surface of the {@link ElevatedSurfaceImage}
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
        this.referenceCenter = globe.computePointFromPosition(centroid.getLatitude(), centroid.getLongitude(), 0d);

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
                this.vertices.put(iv++, p.x - referenceCenter.x).put(iv++, p.y - referenceCenter.y).put(iv++, p.z - referenceCenter.z);
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
     * Forces the {@link ElevatedSurfaceImage} to refresh on the next opengl
     * update
     */
    public void refresh() {
        needsUpdate = true;
    }

    /**
     * Generates the texture coordinates fpr the
     * <code>ElevatedSurfaceImage</code>
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

        if (needsUpdate) {
            GL gl = dc.getGL();
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
            TextureData texdata;

            if (getImageSource() instanceof TextureData) {
                texdata = (TextureData) getImageSource();
            } else {
                BufferedImage src = (BufferedImage) this.getImageSource();
                texdata = new TextureData(0, 0, false, src);
            }


            gl.glTexImage2D(GL.GL_TEXTURE_2D,
                    0,
                    texdata.getInternalFormat(),
                    texdata.getWidth(),
                    texdata.getHeight(),
                    0,
                    texdata.getPixelFormat(),
                    texdata.getPixelType(),
                    texdata.getBuffer());
            needsUpdate = false;
        }
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
            this.drawOrderedRenderable(dc, this.pickSupport);
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
        // This method is called twice each frame: once during picking and once during rendering. We only need to
        // compute the placePoint and eye distance once per frame, so check the frame timestamp to see if this is a
        // new frame.
        if (dc.getFrameTimeStamp() != this.frameTimestamp) {
            // Compute a bounding box that encloses the cube. We'll use this sphere for intersection calculations to determine
            // if the cube is actually visible.            
            this.extent = computeExtent(dc);

            // Convert the cube's geographic position to a position in Cartesian coordinates.
            this.placePoint = extent.getCenter();

            // Compute the distance from the eye to the surface image position.
            this.eyeDistance = computeEyeDistance(dc);

            this.frameTimestamp = dc.getFrameTimeStamp();
        }

        // Add the cube to the ordered renderable list. The SceneController sorts the ordered renderables by eye
        // distance, and then renders them back to front. render will be called again in ordered rendering mode, and at
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

        Vec4[] points = new Vec4[8];
        points[0] = dc.getGlobe().computePointFromPosition(getSector().getCorners()[0], elevation);
        points[1] = dc.getGlobe().computePointFromPosition(getSector().getCorners()[1], elevation);
        points[2] = dc.getGlobe().computePointFromPosition(getSector().getCorners()[2], elevation);
        points[3] = dc.getGlobe().computePointFromPosition(getSector().getCorners()[3], elevation);
        points[4] = dc.getGlobe().computePointFromLocation(getSector().getCorners()[0]);
        points[5] = dc.getGlobe().computePointFromLocation(getSector().getCorners()[1]);
        points[6] = dc.getGlobe().computePointFromLocation(getSector().getCorners()[2]);
        points[7] = dc.getGlobe().computePointFromLocation(getSector().getCorners()[3]);

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
     * @param outerBoundary the shape's outer boundary.
     *
     * @return the computed extent, or null if the extent cannot be computed.
     */
    protected Extent computeExtent(DrawContext dc) {
        Vec4[] topVertices = new Vec4[4];
        topVertices[0] = dc.getGlobe().computePointFromPosition(getSector().getCorners()[0], elevation);
        topVertices[1] = dc.getGlobe().computePointFromPosition(getSector().getCorners()[1], elevation);
        topVertices[2] = dc.getGlobe().computePointFromPosition(getSector().getCorners()[2], elevation);
        topVertices[3] = dc.getGlobe().computePointFromPosition(getSector().getCorners()[3], elevation);
        Vec4[] botVertices = new Vec4[4];
        botVertices[0] = dc.getGlobe().computePointFromLocation(getSector().getCorners()[0]);
        botVertices[1] = dc.getGlobe().computePointFromLocation(getSector().getCorners()[1]);
        botVertices[2] = dc.getGlobe().computePointFromLocation(getSector().getCorners()[2]);
        botVertices[3] = dc.getGlobe().computePointFromLocation(getSector().getCorners()[3]);

        ArrayList<Vec4> allVertices = new ArrayList<Vec4>(2 * topVertices.length);
        allVertices.addAll(Arrays.asList(topVertices));
        allVertices.addAll(Arrays.asList(botVertices));

        Box boundingBox = Box.computeBoundingBox(allVertices);

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
    private void drawOrderedRenderable(DrawContext dc, PickSupport pickCandidates) {
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
                    | GL.GL_ENABLE_BIT | GL.GL_CURRENT_BIT | GL.GL_DEPTH_BUFFER_BIT // for depth func
                    | GL.GL_TEXTURE_BIT // for texture env
                    | GL.GL_TRANSFORM_BIT | GL.GL_POLYGON_BIT);
            try {
                if (!dc.isPickingMode()) {
                    double opacity = this.getOpacity();
                    gl.glColor4d(1d, 1d, 1d, opacity);
                    gl.glEnable(GL.GL_BLEND);
                    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                }


                gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
                gl.glEnable(GL.GL_CULL_FACE);
                gl.glCullFace(GL.GL_BACK);

                gl.glEnable(GL.GL_DEPTH_TEST);
                gl.glDepthFunc(GL.GL_LEQUAL);

                gl.glEnable(GL.GL_ALPHA_TEST);
                gl.glAlphaFunc(GL.GL_GREATER, 0.01f);

                gl.glActiveTexture(GL.GL_TEXTURE0);
                gl.glEnable(GL.GL_TEXTURE_2D);
                gl.glMatrixMode(GL.GL_TEXTURE);
                gl.glPushMatrix();
                if (!dc.isPickingMode()) {
                    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
                } else {
                    gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_COMBINE);
                    gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_SRC0_RGB, GL.GL_PREVIOUS);
                    gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_COMBINE_RGB, GL.GL_REPLACE);
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
