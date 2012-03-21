/*
 *  ClassificationFactory.java 
 *
 *  Created by DFKI AV on 21.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.spi;

import com.dfki.av.sudplan.vis.core.IClassification;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class ClassificationFactory {
    
    /**
     *
     */
    private static final Logger log = LoggerFactory.getLogger(ClassificationFactory.class);
    /**
     *
     */
    private static final List<String> nameList = new ArrayList<String>();
    /**
     *
     */
    private static final List<IClassificationProvider> providerList = new ArrayList<IClassificationProvider>();

    /**
     *
     */
    static {
        ServiceLoader<IClassificationProvider> service = ServiceLoader.load(IClassificationProvider.class);
        for (Iterator<IClassificationProvider> providers = service.iterator(); providers.hasNext();) {
            IClassificationProvider provider = providers.next();
            log.info("Found plugin: {}", provider.getClass().getName());
            nameList.addAll(provider.getClassifications());
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
    public static IClassification newInstance(String name) {
        IClassification classificaiton = null;
        for (IClassificationProvider provider : providerList) {
            classificaiton = provider.get(name);
            if (classificaiton != null) {
                break;
            }
        }
        return classificaiton;
    }
}
