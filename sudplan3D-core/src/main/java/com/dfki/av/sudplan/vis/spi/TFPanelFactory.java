/*
 *  TFPanelFactory.java 
 *
 *  Created by DFKI AV on 26.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.spi;

import com.dfki.av.sudplan.vis.core.ITransferFunction;
import com.dfki.av.sudplan.vis.core.TFPanel;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class TFPanelFactory {

    /**
     *
     */
    private static final Logger log = LoggerFactory.getLogger(TFPanelFactory.class);
    /**
     *
     */
    private static final List<String> nameList = new ArrayList<String>();
    /**
     *
     */
    private static final List<TFPanelProvider> providerList = new ArrayList<TFPanelProvider>();

    /**
     *
     */
    static {
        ServiceLoader<TFPanelProvider> service = ServiceLoader.load(TFPanelProvider.class);
        for (Iterator<TFPanelProvider> providers = service.iterator(); providers.hasNext();) {
            TFPanelProvider provider = providers.next();
            log.info("Found plugin: {}", provider.getClass().getName());
            nameList.addAll(provider.getTFPanels());
            providerList.add(provider);
        }
    }

    /**
     *
     * @return
     */
    public static List<String> getNames() {
        return Collections.unmodifiableList(nameList);
    }

    /**
     *
     * @param name
     * @return
     */
    public static TFPanel newInstance(ITransferFunction function) {
        TFPanel panel = null;
        for (TFPanelProvider provider : providerList) {
            panel = provider.get(function);
            if (panel != null) {
                break;
            }
        }
        return panel;
    }

}
