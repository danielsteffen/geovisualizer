/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package com.dfki.av.sudplanX.test;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.util.Logging;

import javax.media.opengl.GL;
import java.awt.*;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Renders an image on a planar layer at a fixed elevation.
 *
 * @author Patrick Murris
 * @version $Id$
 */

public class ElevatedImage extends SurfaceImage
{
    public static final String IMAGE_REPEAT_NONE = "TexturedLayer.ImageRepeatNone";
    public static final String IMAGE_REPEAT_X = "TexturedLayer.ImageRepeatX";
    public static final String IMAGE_REPEAT_Y = "TexturedLayer.ImageRepeatY";
    public static final String IMAGE_REPEAT_XY = "TexturedLayer.ImageRepeatXY";

    private double elevation = 0;
    private String imageRepeat = IMAGE_REPEAT_NONE;
    private Point imageOffset;
    private double imageScale = 1;

    protected Sector geometrySector;
    protected Vec4 referenceCenter;
    protected DoubleBuffer vertices;
    protected DoubleBuffer texCoords;
    protected DoubleBuffer normals;
    protected IntBuffer indices;
    private int density = 256;

    // Lighting
    private Vec4 lightDirection;
    private Material material = new Material(Color.WHITE);
    private Color lightColor = Color.WHITE;
    private Color ambientColor = new Color(.3f, .3f, .3f);


    public ElevatedImage(Object imageSource, Sector sector)
    {
        super(imageSource, sector);
    }

    public double getElevation()
    {
        return this.elevation;
    }

    public void setElevation(double elevation)
    {
        this.elevation = elevation;
        this.invalidate();  // invalidate geometry
    }

    public int getDensity()
    {
        return this.density;
    }

    public void setDensity(int density)
    {
        this.density = density;
        this.invalidate();  // invalidate geometry
    }

    public void setImageRepeat(String imageRepeat)
    {
        this.imageRepeat = imageRepeat;
    }

    public String getImageRepeat()
    {
        return this.imageRepeat;
    }

    public void setImageOffset(Point offset)
    {
        this.imageOffset = offset;
    }

    public Point getImageOffset()
    {
        return this.imageOffset;
    }

    public void setImageScale(double scale)
    {
        this.imageScale = scale;
    }

    public double getImageScale()
    {
        return this.imageScale;
    }

    public void setSector(Sector sector)
    {
        super.initializeGeometry(Arrays.asList(sector.getCorners()));
    }

    public Vec4 getLightDirection()
    {
        return this.lightDirection;
    }

    public void setLightDirection(Vec4 direction)
    {
        this.lightDirection = direction;
    }

    public Color getLightColor()
    {
        return this.lightColor;
    }

    public void setLightColor(Color color)
    {
        if (color == null)
        {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.lightColor = color;
    }

    public Color getAmbientColor()
    {
        return this.ambientColor;
    }

    public void setAmbientColor(Color color)
    {
        if (color == null)
        {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.ambientColor = color;
    }

    public void invalidate()
    {
        this.geometrySector = null;
    }

    @Override
    public void render(DrawContext dc)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        if (!this.getSector().intersects(dc.getVisibleSector()))
            return;

        if (this.bind(dc))
        {
            Texture texture = (Texture) dc.getTextureCache().get(getImageSource());
            if (texture == null)
                return;

            if (!dc.isPickingMode() && this.lightDirection != null)
                beginLighting(dc);

            GL gl = dc.getGL();

            gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT // for alpha func
                | GL.GL_ENABLE_BIT
                | GL.GL_CURRENT_BIT
                | GL.GL_DEPTH_BUFFER_BIT // for depth func
                | GL.GL_TEXTURE_BIT // for texture env
                | GL.GL_TRANSFORM_BIT
                | GL.GL_POLYGON_BIT);
            try
            {
                if (!dc.isPickingMode())
                {
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
                if (!dc.isPickingMode())
                {
                    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
                }
                else
                {
                    gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_COMBINE);
                    gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_SRC0_RGB, GL.GL_PREVIOUS);
                    gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_COMBINE_RGB, GL.GL_REPLACE);
                }

                // Texture transforms
                applyInternalTransform(dc, true);

                if (imageRepeat.equals(IMAGE_REPEAT_X) || imageRepeat.equals(IMAGE_REPEAT_XY))
                    texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
                else
                    texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_BORDER);
                if (imageRepeat.equals(IMAGE_REPEAT_Y) || imageRepeat.equals(IMAGE_REPEAT_XY))
                    texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
                else
                    texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_BORDER);

                gl.glScaled(1d / imageScale, 1d / imageScale, 1d);
                if (imageOffset != null)
                    gl.glTranslated(-(double) imageOffset.x / texture.getWidth(),
                            -(double) imageOffset.y / texture.getHeight(), 0d);

                // Draw
                renderLayer(dc);

                gl.glActiveTexture(GL.GL_TEXTURE0);
                gl.glMatrixMode(GL.GL_TEXTURE);
                gl.glPopMatrix();
                gl.glDisable(GL.GL_TEXTURE_2D);
            }
            finally
            {
                gl.glPopAttrib();

                if (!dc.isPickingMode() && this.lightDirection != null)
                    endLighting(dc);
            }
        }

    }

    protected void renderLayer(DrawContext dc)
    {
        if (!getSector().equals(this.geometrySector))
            buildGeometry(dc);

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

        gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
        gl.glNormalPointer(GL.GL_DOUBLE, 0, this.normals.rewind());

        // Draw
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, this.indices.limit(),
                GL.GL_UNSIGNED_INT, this.indices.rewind());

        // Restore
        gl.glPopClientAttrib();
        dc.getView().popReferenceCenter(dc);
    }

    protected void beginLighting(DrawContext dc)
    {
        GL gl = dc.getGL();
        gl.glPushAttrib(GL.GL_ENABLE_BIT | GL.GL_CURRENT_BIT | GL.GL_LIGHTING_BIT);

        if (this.getOpacity() < 1)
            this.material.apply(dc.getGL(), GL.GL_FRONT, (float) this.getOpacity());
        else
            this.material.apply(dc.getGL(), GL.GL_FRONT);

        gl.glDisable(GL.GL_COLOR_MATERIAL);

        float[] lightPosition = {(float)-lightDirection.x, (float)-lightDirection.y, (float)-lightDirection.z, 0.0f};
        float[] lightDiffuse = new float[4];
        float[] lightAmbient = new float[4];
        lightColor.getRGBComponents(lightDiffuse);
        ambientColor.getRGBComponents(lightAmbient);

        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPosition, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbient, 0);

        gl.glDisable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_LIGHT1);
        gl.glEnable(GL.GL_LIGHTING);
    }

    protected void endLighting(DrawContext dc)
    {
        GL gl = dc.getGL();

        gl.glDisable(GL.GL_LIGHT1);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glDisable(GL.GL_LIGHTING);

        gl.glPopAttrib();
    }

    protected void buildGeometry(DrawContext dc)
    {
        Globe globe = dc.getGlobe();
        Sector sector = getSector();

        LatLon centroid = sector.getCentroid();
        this.geometrySector = sector;
        this.referenceCenter = globe.computePointFromPosition(centroid.getLatitude(), centroid.getLongitude(), 0d);

        Angle dLat = sector.getDeltaLat().divide(this.density);
        Angle dLon = sector.getDeltaLon().divide(this.density);

        // Compute vertices
        int numVertices = (this.density + 1) * (this.density + 1);
        this.vertices = BufferUtil.newDoubleBuffer(numVertices * 3);
        int iv = 0;
        Angle lat = sector.getMinLatitude();
        for (int j = 0; j <= this.density; j++)
        {
            Angle lon = sector.getMinLongitude();
            for (int i = 0; i <= this.density; i++)
            {
                Vec4 p = globe.computePointFromPosition(lat, lon, this.getElevation(dc, lat, lon));
                this.vertices.put(iv++, p.x - referenceCenter.x).put(iv++, p.y - referenceCenter.y)
                        .put(iv++, p.z - referenceCenter.z);
                lon = lon.add(dLon);
            }
            lat = lat.add(dLat);
        }

        // Compute indices
        if (this.indices == null)
        {
            this.indices = getIndices(this.density);
        }
        // Compute texture coordinates // TODO: allow texture coodinates for 1D textures based on elevation
        if (this.texCoords == null)
        {
            this.texCoords = getTextureCoordinates(this.density);
        }
        // Compute normals
        this.normals = getNormals(this.density, this.vertices, this.indices, this.referenceCenter);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected double getElevation(DrawContext dc, Angle lat, Angle lon)
    {
        return this.getElevation();
    }

    // Compute indices for triangle strips
    protected static IntBuffer getIndices(int density)
    {
        if (density < 1)
            density = 1;

        int sideSize = density;

        int indexCount = 2 * sideSize * sideSize + 4 * sideSize - 2;
        IntBuffer buffer = BufferUtil.newIntBuffer(indexCount);
        int k = 0;
        for (int i = 0; i < sideSize; i++)
        {
            buffer.put(k);
            if (i > 0)
            {
                buffer.put(++k);
                buffer.put(k);
            }

            if (i % 2 == 0) // even
            {
                buffer.put(++k);
                for (int j = 0; j < sideSize; j++)
                {
                    k += sideSize;
                    buffer.put(k);
                    buffer.put(++k);
                }
            }
            else // odd
            {
                buffer.put(--k);
                for (int j = 0; j < sideSize; j++)
                {
                    k -= sideSize;
                    buffer.put(k);
                    buffer.put(--k);
                }
            }
        }
        return buffer;
    }

    // Compute normalized regular 2D texture coordinates
    protected static DoubleBuffer getTextureCoordinates(int density)
    {
        if (density < 1)
            density = 1;

        int coordCount = (density + 1) * (density + 1);
        DoubleBuffer p = BufferUtil.newDoubleBuffer(2 * coordCount);
        double delta = 1d / density;
        int k = 0;
        for (int j = 0; j <= density; j++)
        {
            double v = j * delta;
            for (int i = 0; i <= density; i++)
            {
                p.put(k++, i * delta); // u
                p.put(k++, v);
            }
        }
        return p;
    }

    //computes normals for the triangle strip
    protected static DoubleBuffer getNormals(int density, DoubleBuffer vertices, IntBuffer indices,
        Vec4 referenceCenter)
    {
        int side = density + 1; // no skirts
        int numVertices = side * side;
        int numFaces = indices.limit() - 2;
        double centerX = referenceCenter.x;
        double centerY = referenceCenter.y;
        double centerZ = referenceCenter.z;

        // Create normal buffer
        java.nio.DoubleBuffer normals = BufferUtil.newDoubleBuffer(numVertices * 3);
        int[] counts = new int[numVertices];
        Vec4[] norms = new Vec4[numVertices];
        for (int i = 0; i < numVertices; i++)
            norms[i] = new Vec4(0d);

        for (int i = 0; i < numFaces; i++)
        {
            // get vertex indices
            int index0 = indices.get(i);
            int index1 = indices.get(i + 1);
            int index2 = indices.get(i + 2);

            // get verts involved in current face
            Vec4 v0 = new Vec4(vertices.get(index0 * 3) + centerX, vertices
                    .get(index0 * 3 + 1)
                    + centerY, vertices.get(index0 * 3 + 2) + centerZ);

            Vec4 v1 = new Vec4(vertices.get(index1 * 3) + centerX, vertices
                    .get(index1 * 3 + 1)
                    + centerY, vertices.get(index1 * 3 + 2) + centerZ);

            Vec4 v2 = new Vec4(vertices.get(index2 * 3) + centerX, vertices
                    .get(index2 * 3 + 1)
                    + centerY, vertices.get(index2 * 3 + 2) + centerZ);

            // get triangle edge vectors and plane normal
            Vec4 e1 = v1.subtract3(v0), e2;
            if (i % 2 == 0)
                e2 = v2.subtract3(v0);
            else
                e2 = v0.subtract3(v2);
            Vec4 N = e1.cross3(e2).normalize3(); // if N is 0, the triangle is degenerate

            if (N.getLength3() > 0)
            {
                // store the face's normal for each of the vertices that make up the face.
                norms[index0] = norms[index0].add3(N);
                norms[index1] = norms[index1].add3(N);
                norms[index2] = norms[index2].add3(N);

                // increment vertex normal counts
                counts[index0]++;
                counts[index1]++;
                counts[index2]++;
            }
        }

        // Now loop through each vertex, and average out all the normals stored.
        for (int i = 0; i < numVertices; i++)
        {
            if (counts[i] > 0)
                norms[i] = norms[i].divide3(counts[i]).normalize3();
            int index = i * 3;
            normals.put(index++, norms[i].x).put(index++, norms[i].y).put(
                    index, norms[i].z);
        }

        return normals;
    }


}
