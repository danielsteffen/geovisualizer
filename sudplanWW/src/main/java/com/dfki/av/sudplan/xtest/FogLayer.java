/*
 *  Created by DFKI AV on 05/09/2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.xtest;

import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.DrawContext;

import javax.media.opengl.GL;
import java.awt.*;
/**
 * From Patrick Murris
 */
public class FogLayer  extends RenderableLayer {



/**
 * Sets fog range/density according to view altitude
 * Author: Patrick Murris
 */


    private float fogColor[] = new float[] { 0.66f, 0.70f, 0.81f, 1.0f };
    private float nearFactor = 1.0f;  // Applies to the view altitude
    private float farFactor = 1.0f;   // Applies to the distance to the horizon

    /**
     * Sets fog range/density according to view altitude
     */
    public FogLayer() {
        this.setName("Fog"); // TODO: get as resource string
    }

    // Public properties

    /**
     * Get the fog color
     * @return the fog color
     */
    public Color getColor()
    {
        return new Color(this.fogColor[0], this.fogColor[1], this.fogColor[2], this.fogColor[3]);
    }

    /**
     * Set the fog color
     * @param color the fog color
     */
    public void setColor(Color color)
    {
        color.getColorComponents(this.fogColor);
    }

    /**
     * Get the near distance factor that is applied to the view altitude.
     * @return the near factor
     */
    public float getNearFactor()
    {
        return this.nearFactor;
    }

    /**
     * Set the near distance factor applied to the view altitude
     * @param factor the factor to apply to the view altitude
     */
    public void setNearFactor(float factor)
    {
        this.nearFactor = factor;
    }

    /**
     * Get the far distance factor that is applied to the eye distance to the horizon.
     * @return the far factor
     */
    public float getFarFactor()
    {
        return this.farFactor;
    }

    /**
     * Set the far distance factor applied to the eye distance to the horizon
     * @param factor the factor to apply to the eye distance to the horizon
     */
    public void setFarFactor(float factor)
    {
        this.farFactor = factor;
    }

    // Rendering

    /**
     * Setup fog
     * @param dc the current DrawContext
     */
    @Override
    public void doRender(DrawContext dc)
    {
        Position eyePos = dc.getView().getEyePosition();
        if (eyePos == null)
            return;
        // View altitude
        float alt = (float)eyePos.getElevation();
        alt = alt < 100 ? 100 : alt;   // Clamp altitudes below 100m
        // Start based on view altitude
        float start = alt * this.nearFactor;
        // End based on distance to horizon
        float end = 5.0f;
//        float end = (float)(dc.getView().computeHorizonDistance() * this.farFactor);
        // Set GL fog
        GL gl = dc.getGL();
        gl.glFogfv(GL.GL_FOG_COLOR, fogColor, 0);  // Set fog color
        gl.glFogi(GL.GL_FOG_MODE, GL.GL_LINEAR);   // Set fog mode
        gl.glFogf(GL.GL_FOG_START, start);         // Set fog start distance
        gl.glFogf(GL.GL_FOG_END, end);             // Set fog end distance
        gl.glHint(GL.GL_FOG_HINT, GL.GL_DONT_CARE);// Set fog hint
        gl.glEnable(GL.GL_FOG);                    // Enable fog

    }

    @Override
    public String toString()
    {
        return this.getName();
    }
}
