/*
 * Point.java
 *
 * Created by DFKI AV on 30.08.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.vis.basic;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import java.awt.Color;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a renderable point. The primitive uses {@link GL#GL_POINTS} to render
 * the point.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class Point implements Renderable {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Point.class);
    /**
     * The {@link Position} of the {@link Point}.
     */
    private Position position;
    /**
     * The color of the {@link Point}.
     */
    private float[] color;

    /**
     * Constructor.
     *
     * @param pos the {@link Position} of the {@link Point}.
     * @throws IllegalArgumentException if position is set to {@code null}.
     */
    public Point(Position pos) {
        if (pos == null) {
            String message = "Center is set to null";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        this.position = pos;
        this.color = new float[]{0.0f, 1.0f, 0.0f};
    }

    @Override
    public void render(DrawContext dc) {
        if (dc == null) {
            String message = "DrawContext is set to null";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        Globe globe = dc.getGlobe();
        Vec4 vec4Center = globe.computePointFromPosition(position);
        GL gl = dc.getGL();
        GL2 gl2 = gl.getGL2();
                
        gl2.glPushAttrib(GL2.GL_ENABLE_BIT | GL2.GL_CURRENT_BIT);

        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glPushMatrix();
        gl2.glTranslated(vec4Center.x, vec4Center.y, vec4Center.z);

        gl2.glPointSize(5.0f);
        gl2.glEnable(GL2.GL_POINT_SMOOTH);
        {
            gl2.glBegin(GL.GL_POINTS);
            {
                gl2.glColor3f(color[0], color[1], color[2]);
                gl2.glVertex3d(0.0, 0.0, 0.0);
            }
            gl2.glEnd();
        }
        gl2.glDisable(GL2.GL_POINT_SMOOTH);
        gl2.glPopMatrix();

        gl2.glPopAttrib();
    }

    /**
     * Sets the {@link Color} for this {@link Point}.
     *
     * @param c the {@link Color} to set.
     * @throws IllegalArgumentException if color is set to {@code Null}.
     */
    public void setColor(Color c) {
        if (c == null) {
            throw new IllegalArgumentException("No valid color value.");
        }
        this.color = c.getRGBColorComponents(null);
    }
}
