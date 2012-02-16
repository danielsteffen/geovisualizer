package com.dfki.av.sudplan.vis.algorithm;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
import com.dfki.av.sudplan.io.shapefile.ShapefileUtils;
import com.dfki.av.utils.ColorUtils;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.WWUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.gdal.ogr.Geometry;

/**
 * This visualization technique enables to visualize a shapefile of type
 * Polyline. 2 attributes have to be defined.
 *
 * @author steffen
 */
public class VisExtrudePolyline extends VisAlgorithm {

    /**
     *
     */
    private static final int DEFAULT_NUM_CLASSES = 4;
    /**
     *
     */
    protected String[] attributes;
    /**
     *
     */
    private double[] boundaries;
    /**
     *
     */
    private Material[] materials;

    /**
     *
     * @param attr
     */
    public VisExtrudePolyline(String[] attr) {
        super("Extrude polyline visualization");

        if (attr == null || attr.length != 2) {
            throw new IllegalArgumentException("Need at 2 attributes "
                    + "to define visualization parameters.");
        }

        this.attributes = new String[2];
        for (int i = 0; i < attr.length; i++) {
            if (attr[i] == null || attr[i].isEmpty()) {
                throw new IllegalArgumentException("Parameter" + i + " no valid argument.");
            }
            this.attributes[i] = attr[i];
        }
    }

    @Override
    public List<Layer> createLayersFromData(Object data) {

        List<Layer> layers = new ArrayList<Layer>();

        if (data instanceof Shapefile) {
            Shapefile shapefile = (Shapefile) data;

            // Pre-processing data
            this.boundaries = ShapefileUtils.AutoClassificationOfNumberAttribute(shapefile, attributes[0], DEFAULT_NUM_CLASSES);
            Color[] colors = ColorUtils.GetRedGreenColorGradient(DEFAULT_NUM_CLASSES);

            this.materials = new Material[DEFAULT_NUM_CLASSES];
            for (int i = 0; i < materials.length; i++) {
                materials[i] = new Material(colors[i]);
            }

            // Create visualization
            if (Shapefile.isPolylineType(shapefile.getShapeType())) {
                RenderableLayer layer = new RenderableLayer();
                for (int i = 0; i < shapefile.getFeatureCount(); i++) {
                    Renderable r = createPolyline(shapefile, i);
                    layer.addRenderable(r);
                }
                layer.setName(shapefile.getLayerName());
                layers.add(layer);
            } else {
                log.warn("Extrude Polyline Visualization does not support shape type {}.", shapefile.getShapeType());
            }
        } else {
            log.debug("Data type not supported.");
        }

        return layers;
    }

    /**
     *
     * @param shpfile
     * @param featureId
     * @return
     */
    private Renderable createPolyline(Shapefile shpfile, int featureId) {

        //
        // Mapping for visualization parameter COLOR
        //
        Object object0 = shpfile.getAttributeOfFeature(featureId, attributes[0]);
        Double value0 = null;
        if (object0 instanceof Number) {
            value0 = ((Number) object0).doubleValue();
        }

        if (object0 instanceof String) {
            value0 = WWUtil.convertStringToDouble(object0.toString());
        }

        ShapeAttributes sa = new BasicShapeAttributes();
        sa.setOutlineMaterial(new Material(Color.BLACK));
        sa.setOutlineWidth(0.3);
        sa.setInteriorOpacity(0.6);
        sa.setOutlineOpacity(0.3);

        if (value0 != null) {
            for (int i = 1; i < boundaries.length; i++) {
                if (value0 < boundaries[i]) {
                    sa.setInteriorMaterial(materials[i - 1]);
                    break;
                }
            }
        } else {
            log.debug("Setting attribute 'color' to gray.");
            sa.setInteriorMaterial(Material.GRAY);
        }

        //
        // Mapping for visiulization parameter HEIGHT
        //
        Object object1 = shpfile.getAttributeOfFeature(featureId, attributes[1]);
        Double value1 = null;
        if (object1 instanceof Number) {
            value1 = ((Number) object1).doubleValue();
        }

        if (object1 instanceof String) {
            value1 = WWUtil.convertStringToDouble(object1.toString());
        }

        if (value1 == null) {
            log.warn("Value for ExtrudePolyline equals null. "
                    + "Setting attribute to default value.");
            value1 = 30.0;
        }

        List<Position> positionList = new ArrayList<Position>();
        List<Geometry> list = shpfile.getGeometryList(featureId);
        for (int i = 0; i < list.size(); i++) {
            Geometry g = list.get(i);
            for (int j = 0; j < g.GetPointCount(); j++) {
                double[] point = g.GetPoint_2D(j);
                // ... swap geo positions ???! why
                positionList.add(Position.fromDegrees(point[1], point[0], value1 / 150.0));
            }
        }

        //
        // Putting everything together ...
        //
        Path path = new Path(positionList);
        path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        path.setExtrude(true);
        path.setAttributes(sa);

        return path;
    }
}
