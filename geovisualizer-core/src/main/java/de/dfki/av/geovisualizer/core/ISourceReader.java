/*
 * ISourceReader.java
 *
 * Created by DFKI AV on 07.09.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface ISourceReader {

    /**
     * Returns {@code true} if the implementation is able to read the input
     * object. Otherwise {@code false}.
     *
     * @param input the input object to be read.
     * @return {@code true} if the reader is able to read the input.
     */
    boolean canRead(Object input);

    /**
     * Try to read the input object and converts the object into an
     * {@code ISource} object.
     *
     * @param input the input object.
     * @return the {@link ISource} object to return.
     */
    ISource read(Object input);
}
