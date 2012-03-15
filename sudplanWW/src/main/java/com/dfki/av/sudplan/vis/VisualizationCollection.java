package com.dfki.av.sudplan.vis;

import com.dfki.av.sudplan.vis.core.IVisAlgorithm;
import com.dfki.av.sudplan.vis.spi.IVisAlgorithmProvider;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class VisualizationCollection {

    /**
     *
     */
    private static final Logger log = LoggerFactory.getLogger(VisualizationCollection.class);
    /**
     *
     */
    private static final List<String> nameList = new ArrayList<String>();
    /**
     *
     */
    private static final List<IVisAlgorithmProvider> providerList = new ArrayList<IVisAlgorithmProvider>();

    /**
     *
     */
    static {
        ServiceLoader<IVisAlgorithmProvider> service = ServiceLoader.load(IVisAlgorithmProvider.class);
        log.debug("Searching for plugings ...");
        for (Iterator<IVisAlgorithmProvider> providers = service.iterator(); providers.hasNext();) {
            IVisAlgorithmProvider provider = providers.next();
            log.debug("Found plugin: {}", provider.getClass().getName());
            nameList.addAll(provider.getVisualizationNames());
            providerList.add(provider);
        }
        log.debug("Finished.");
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
    public static IVisAlgorithm newInstance(String name) {
        IVisAlgorithm algo = null;
        for (IVisAlgorithmProvider provider : providerList) {
            algo = provider.get(name);
            if (algo != null) {
                break;
            }
        }
        return algo;
    }
}
