/*
 *  TransferFunctionFactory.java 
 *
 *  Created by DFKI AV on 21.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.spi;

import com.dfki.av.sudplan.vis.core.ITransferFunction;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class TransferFunctionFactory {
    
    /**
     *
     */
    private static final Logger log = LoggerFactory.getLogger(TransferFunctionFactory.class);
    /**
     *
     */
    private static final List<String> nameList = new ArrayList<String>();
    /**
     *
     */
    private static final List<ITransferFunctionProvider> providerList = new ArrayList<ITransferFunctionProvider>();

    /**
     *
     */
    static {
        ServiceLoader<ITransferFunctionProvider> service = ServiceLoader.load(ITransferFunctionProvider.class);
        for (Iterator<ITransferFunctionProvider> providers = service.iterator(); providers.hasNext();) {
            ITransferFunctionProvider provider = providers.next();
            log.info("Found plugin: {}", provider.getClass().getName());
            nameList.addAll(provider.getTransferFunctions());
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
    public static ITransferFunction newInstance(String name) {
        ITransferFunction transferFunction = null;
        for (ITransferFunctionProvider provider : providerList) {
            transferFunction = provider.get(name);
            if (transferFunction != null) {
                break;
            }
        }
        return transferFunction;
    }
}
