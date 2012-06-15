package com.dfki.av.sudplan.vis;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.util.Logging;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import javax.media.opengl.GL;

public class ElevatedSurfaceImage extends SurfaceImage {

    // Display quality of the surface
    private final static int QUALITY = 1;
    // Default elevation set to zero meters over sea level
    private double elevation = 0;
    private boolean imageRepeat;
    private Point imageOffset;
    private double imageScale = 1;
    private Sector geometrySector;
    private Vec4 referenceCenter;
    private DoubleBuffer vertices;
    private DoubleBuffer texCoords;
    private IntBuffer indices;
    boolean needsUpdate = true;
    boolean floating = true;

    public ElevatedSurfaceImage(Object imageSource, Sector sector) {
        super(imageSource, sector);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        // TODO
        return false;
    }

    @Override
    public int hashCode() {
        // TODO
        int hash = 3;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.elevation) ^ (Double.doubleToLongBits(this.elevation) >>> 32));
        return hash;
    }

    public double getElevation() {
        return this.elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
        this.geometrySector = null;  // invalidate geometry
    }

    public boolean getFloating() {
        return floating;
    }

    public void setFloating(boolean f) {
        floating = f;
    }

    public void setImageRepeat(boolean imageRepeat) {
        this.imageRepeat = imageRepeat;
    }

    public boolean getImageRepeat() {
        return this.imageRepeat;
    }

    public void setImageOffset(Point offset) {
        this.imageOffset = offset;
    }

    public Point getImageOffset() {
        return this.imageOffset;
    }

    public void setImageScale(double scale) {
        this.imageScale = scale;
    }

    public double getImageScale() {
        return this.imageScale;
    }

    @Override
    public void render(DrawContext dc) {
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

//                gl.glScaled(1d / imageScale, 1d / imageScale, 1d);
//                if (imageOffset != null) {
//                    gl.glTranslated(-(double) imageOffset.x / texture.getWidth(),
//                            -(double) imageOffset.y / texture.getHeight(), 0d);
//                }

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

    private void buildGeometry(DrawContext dc) {
        Globe globe = dc.getGlobe();
        Sector sector = getSector();

        LatLon centroid = sector.getCentroid();
        this.geometrySector = sector;
        this.referenceCenter = globe.computePointFromPosition(centroid.getLatitude(), centroid.getLongitude(), 0d);

        Angle dLat = sector.getDeltaLat().divide(QUALITY);
        Angle dLon = sector.getDeltaLon().divide(QUALITY);

        // Compute vertices
        int numVertices = (QUALITY + 1) * (QUALITY + 1);
        this.vertices = BufferUtil.newDoubleBuffer(numVertices * 3);
        int iv = 0;
        Angle lat = sector.getMinLatitude();
        for (int j = 0; j <= QUALITY; j++) {
            Angle lon = sector.getMinLongitude();
            for (int i = 0; i <= QUALITY; i++) {
                Vec4 p = globe.computePointFromPosition(lat, lon, elevation);
                this.vertices.put(iv++, p.x - referenceCenter.x).put(iv++, p.y - referenceCenter.y).put(iv++, p.z - referenceCenter.z);
                lon = lon.add(dLon);
            }
            lat = lat.add(dLat);
        }

        // Compute indices
        if (this.indices == null) {
            this.indices = getIndices(QUALITY);
        }
        // Compute texture coordinates
        if (this.texCoords == null) {
            this.texCoords = getTextureCoordinates(QUALITY);
        }


    }

    // Compute indices for triangle strips
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

    public void refresh() {
        needsUpdate = true;
    }

    @Override
    public boolean bind(DrawContext dc) {
        boolean returnValue = super.bind(dc);

        if (needsUpdate) {
            GL gl = dc.getGL();
            gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_NEAREST_MIPMAP_NEAREST);
            gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_NEAREST);
            BufferedImage src = (BufferedImage) this.getImageSource();
            TextureData texdata = new TextureData(0, 0, false, src);

            gl.glTexImage2D(gl.GL_TEXTURE_2D,
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
}