/*
 *  VisRasterize.java 
 *
 *  Created by DFKI AV on 01.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.rasterize;

import com.dfki.av.sudplan.vis.core.*;
import com.dfki.av.sudplan.vis.functions.ColorrampCategorization;
import com.dfki.av.sudplan.vis.functions.ColorrampClassification;
import com.dfki.av.sudplan.vis.functions.ConstantNumber;
import com.dfki.av.sudplan.vis.functions.RedGreenColorrampClassification;
import com.dfki.av.sudplan.vis.io.shapefile.Shapefile;
import com.dfki.av.sudplan.vis.rasterize.utils.BufferedImageGenerator;
import com.dfki.av.sudplan.vis.rasterize.utils.Rasterizer;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.ShapeAttributes;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 *
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public class VisRasterize extends VisAlgorithmAbstract {

    /**
     *
     */
    private NumberParameter parElevation;
    /**
     *
     */
    private ColorParameter parColor;
    /**
     *
     */
    private NumberParameter parOpacity;
    /**
     * Texture width
     */
    static int WIDTH = 1024;
    /**
     * Texture height
     */
    static int HEIGHT = 1024;
    /**
     * Texture opacity
     */
    private double opacity;
    /**
     * Texture elevation
     */
    private double elevation;

    /**
     *
     */
    public VisRasterize() {
        super("Rasterize", "Creates a rastered texture visualization from the shapefile data.",
                new ImageIcon(VisRasterize.class.getClassLoader().
                getResource("icons/VisRasterize.png")));

        this.parElevation = new NumberParameter("Layer Elevation [m]");
        this.parElevation.addTransferFunction(ConstantNumber.class.getName());
        addVisParameter(parElevation);

        this.parOpacity = new NumberParameter("Layer Opacity [%] (100% = No Opacity)");
        this.parOpacity.addTransferFunction(ConstantNumber.class.getName());
        addVisParameter(parOpacity);

        this.parColor = new ColorParameter("Color of surface");
        this.parColor.addTransferFunction(RedGreenColorrampClassification.class.getName());
        this.parColor.addTransferFunction(ColorrampClassification.class.getName());
        this.parColor.addTransferFunction(ColorrampCategorization.class.getName());
        addVisParameter(parColor);
    }

    @Override
    public List<Layer> createLayersFromData(Object data, Object[] attributes) {
        ArrayList<Layer> list = new ArrayList<Layer>();

        log.debug("Running {}", this.getClass().getSimpleName());

        List<Layer> layers = new ArrayList<Layer>();
        String attribute0 = IVisAlgorithm.NO_ATTRIBUTE;
        String attribute1 = IVisAlgorithm.NO_ATTRIBUTE;
        String attribute2 = IVisAlgorithm.NO_ATTRIBUTE;
        Shapefile shapefile;

        // 0 - Check data
        if (!(data instanceof Shapefile)) {
            log.error("Data type {} not supported for {}.",
                    data.getClass().getSimpleName(), this.getName());
            return layers;
        } else {
            shapefile = (Shapefile) data;
        }

        // 1 - Check and set all attributes
        if (attributes == null || attributes.length == 0) {
            log.warn("Attributes set to null. First and second attribute set to default.");
        } else if (attributes.length == 1) {
            log.warn("Using only one attribute. Second attribute set to default.");
            attribute0 = checkAttribute(attributes[0]);
        } else if (attributes.length == 2) {
            attribute0 = checkAttribute(attributes[0]);
            attribute1 = checkAttribute(attributes[1]);
        } else if (attributes.length == 3) {
            attribute0 = checkAttribute(attributes[0]);
            attribute1 = checkAttribute(attributes[1]);
            attribute2 = checkAttribute(attributes[2]);
        }
        log.debug("Using attributes: " + attribute0 + ", " + attribute1 + ", " + attribute2);

        // 1 - Pre-processing data
        ITransferFunction function0 = parElevation.getTransferFunction();
        log.debug("Using transfer function {} for attribute 0.", function0.getClass().getSimpleName());
        function0.preprocess(shapefile, attribute0);

        Number number0 = (Number) function0.calc(null);
        if (number0 == null) {
            log.warn("Invalid layer elevation value. Set elevation to zero.");
            elevation = 0.0;
        } else if (number0.doubleValue() < 0.0) {
            log.warn("Invalid layer elevation value. Set elevation to zero.");
            elevation = 0.0;
        } else {
            elevation = number0.doubleValue();
            log.debug("Set layer elevation to {}.", elevation);
        }

        ITransferFunction function1 = parOpacity.getTransferFunction();
        log.debug("Using transfer function {} for attribute 1.", function0.getClass().getSimpleName());
        function1.preprocess(shapefile, attribute1);

        Number number1 = (Number) function1.calc(null);
        if (number1 == null) {
            log.warn("Invalid layer opacity value({}). Disable layer opacity.", number1.doubleValue());
            opacity = 1.0;
        } else if (number1.doubleValue() < 0.0 || number1.doubleValue() > 100.0) {
            log.warn("Invalid layer opacity value({}). Disable layer opacity.", number1.doubleValue());
            opacity = 1.0;
        } else {
            opacity = number1.doubleValue() / 100.0;
            log.debug("Set layer opacity to {}%.", opacity * 100.0);
        }

        ITransferFunction function2 = parColor.getTransferFunction();
        log.debug("Using transfer function {} for attribute 2.", function0.getClass().getSimpleName());
        function2.preprocess(shapefile, attribute2);

        int[][] argb = Rasterizer.raster(shapefile, attribute2, function2, WIDTH, HEIGHT);
        double[] extend = shapefile.getExtent();
        Sector sector = Sector.fromDegrees(extend[2], extend[3], extend[0], extend[1]);
        BufferedImage image = BufferedImageGenerator.argbToImage(argb, WIDTH, WIDTH);

        list.add(createLayer(shapefile.getLayerName(), image, sector, elevation));

        log.debug("Finished {}", this.getClass().getSimpleName());

        return list;
    }

    /**
     *
     * @param name
     * @param image
     * @param sector
     * @return
     */
    private Layer createLayer(String name, BufferedImage image, Sector sector) {
        SurfaceImageLayer sul = new SurfaceImageLayer();
        sul.addImage(name, image, sector);
        sul.setOpacity(opacity);
        sul.setPickEnabled(false);
        return sul;
    }

    /**
     *
     * @param name
     * @param image
     * @param sector
     * @param height
     * @return
     */
    private Layer createLayer(String name, BufferedImage image, Sector sector, double elevation) {
        SurfaceImageLayer sul = new SurfaceImageLayer();
        if (elevation == 0) {
            return createLayer(name, image, sector);
        }
        if (elevation < 0) {
            log.warn("Invalid elevation value. Set elevation = 0.");
            return createLayer(name, image, sector);
        }
        ArrayList<LatLon> pathLocations = new ArrayList<LatLon>();
        Iterator<LatLon> iterator = sector.iterator();
        while (iterator.hasNext()) {
            pathLocations.add(iterator.next());
        }
        ExtrudedPolygon pgon = new ExtrudedPolygon(pathLocations, elevation);
        pgon.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);

        float[] corners = new float[8];
        corners[0] = 0.0f;
        corners[1] = 0.0f;
        corners[2] = 1.0f;
        corners[3] = 0.0f;
        corners[4] = 1.0f;
        corners[5] = 1.0f;
        corners[6] = 0.0f;
        corners[7] = 1.0f;

        pgon.setCapImageSource(image, corners, 4);

        ShapeAttributes capAttributes = new BasicShapeAttributes();
        capAttributes.setImageScale(1.0);
        capAttributes.setDrawInterior(true);
        capAttributes.setEnableLighting(true);
        capAttributes.setEnableAntialiasing(true);
        capAttributes.setDrawOutline(false);
        capAttributes.setInteriorOpacity(opacity);


        pgon.setEnableSides(false);
        pgon.setCapAttributes(capAttributes);
        sul.addRenderable(pgon);
        return sul;
    }
}
