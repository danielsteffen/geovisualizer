package com.dfki.av.sudplan.io;

import javax.vecmath.Color4f;

import com.sun.j3d.utils.geometry.GeometryInfo;

/**
 * @author Puhl,Sebastian Scholl,Martin This class extends GeometryInfo to hold
 *         additional to the geometry info multiple colorsets
 * 
 */
public class LandscapeGeometryInfo extends GeometryInfo {

    private Color4f[] colorsPlain = null;

    private Color4f[] colorsStandard;

    private Color4f[] colorsElevation;

    private Color4f[] colorsSlope;

//    private int[] indices;

    /**
     * Default Contructor. The integer value determines what kind of Geometry
     * will delivered TriangleArray TriangleStripArray
     * 
     * @param primitive -
     *            The kind of Geometry
     */
    public LandscapeGeometryInfo(int primitive) {
        super(primitive);
    }

    /**
     * Constructor where the kind of geometry is specific and the three
     * colorsets + the color indices (to determine which vertex gets which
     * color)
     * 
     * @param primitive -
     *            the kind of geometry
     * @param standardColor -
     *            the standard color of the landscape
     * @param elevationColor -
     *            the elevation color set
     * @param slopeColor -
     *            the slope color set
     * @param indices-
     *            the indices
     */
    public LandscapeGeometryInfo(int primitive, Color4f[] standardColor,
            Color4f[] elevationColor, Color4f[] slopeColor, int[] indices) {
        super(primitive);
        colorsStandard = standardColor;
        colorsElevation = elevationColor;
        colorsSlope = slopeColor;
//        this.indices = indices;
        setColorIndices(indices);
        setStandardColor();
    }

//    private void setIndicies(int[] indiciesList) {
//        indices = indiciesList;
//    }

    private void setElevationColor(Color4f[] colorList) {
        colorsElevation = colorList;
    }

    private void setSlopeColor(Color4f[] colorList) {
        colorsSlope = colorList;
    }

    private void setStandardColor(Color4f[] colorList) {
        colorsStandard = colorList;
    }

    /**
     * Sets the slope colorset
     * 
     * 
     */
    public void setSlopeColor() {
        setColors(colorsSlope);
    }

    /**
     * sets the elevation colorset
     * 
     * 
     */
    public void setElevationColor() {
        setColors(colorsElevation);
    }

    /**
     * sets the standard colorset
     * 
     * 
     */
    public void setStandardColor() {
        setColors(colorsStandard);
    }
}
