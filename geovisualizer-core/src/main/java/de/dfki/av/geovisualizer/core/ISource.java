/*
 * ISource.java
 *
 * Created by DFKI AV on 20.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

import de.dfki.av.geovisualizer.core.io.GeometryType;
import java.util.List;
import java.util.Map;

/**
 * The interface for all data sources used by the geovisualizer application.
 * Especially, the {@link ITransferFunction} as well as the
 * {@link IVisAlgorithm} use objects of type {@link ISource}.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 * @author Tobias Zimmermann <tobias.zimmermann at dfki.de>
 */
public interface ISource {

    /**
     * Returns a {@link Map} of name and data type pairs.
     *
     * @return a {@link Map} of name and data type pairs.
     */
    Map<String, Object> getAttributes();

    /**
     * Returns name of the data source.
     *
     * @return the name of the data source.
     */
    String getName();

    /**
     * Returns the geometry type of the source geometry.
     *
     * @return {@link GeometryType}
     */
    GeometryType getGeometryType();

    /**
     * Returns the number of features contained in the {@link ISource}.
     *
     * @return the number of features (e.g. Points) to return.
     */
    int getFeatureCount();

    /**
     * Returns bounding box of the sources data in degrees.
     *
     * @return array of degrees as {@link Double}
     */
    double[] getBoundingBox();

    /**
     * Returns an {@link Object} of feature at index {@code featureId} of the
     * attribute {@code attributeName}. The return value may be {@code null}. In
     * most cases it is of type {@link Number} or {@link String}. In order to be
     * sure use {@code instanceof} to check the type.
     *
     * @param featureId the index of the feature.
     * @param attributeName the attribute name
     * @return the value to return.
     * @throws {@link IllegalArgumentException} or
     * {@link ArrayIndexOutOfBoundsException}.
     */
    Object getValue(int featureId, String attributeName);

    /**
     * Returns a {@link List} of {@link List} of {@link Double} arrays which
     * represents the points of the feature at index {@code featureId}.
     *
     * @param featureId the index of the feature.
     * @return {@link List} of {@link List} of points
     */
    List<List<double[]>> getPoints(int featureId);

    /**
     * Returns the minimum value for the {@code dataAttributeName}.
     *
     * @param attributeName the name of the attribute.
     * @return the minimum value of the attribute to return.
     */
    double min(String attributeName);

    /**
     * Returns the maximum value for the {@code dataAttributeName}.
     *
     * @param attributeName the name of the attribute.
     * @return the maximum value of the attribute to return.
     */
    double max(String attributeName);
}
