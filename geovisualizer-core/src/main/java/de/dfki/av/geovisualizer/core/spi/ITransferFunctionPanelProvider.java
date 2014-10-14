/*
 * ITransferFunctionPanelProvider.java
 *
 * Created by DFKI AV on 07.09.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.spi;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.ITransferFunction;
import java.util.List;

/**
 * An implementation of the {@link ITransferFunctionPanelProvider} collects a
 * list of available {@link AbstractTransferFunctionPanel}.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public interface ITransferFunctionPanelProvider {

    /**
     * Returns a {@link List} of full qualified class names that implement the
     * interface {@link AbstractTransferFunctionPanel}.
     *
     * @return the {@link List} of names of available
     * {@link AbstractTransferFunctionPanel} implementations.
     */
    List<String> getTFPanels();

    /**
     * Returns an instance for the full qualified {@code function} of an
     * {@link AbstractTransferFunctionPanel} implementation.
     *
     * @param function the function of the {@link AbstractTransferFunctionPanel}
     * implementation
     * @return an instance of the {@link AbstractTransferFunctionPanel}
     * implementation
     */
    AbstractTransferFunctionPanel get(ITransferFunction function);
}
