package com.dfki.av.sudplan.vis.spi;

import com.dfki.av.sudplan.vis.core.ITransferFunction;
import com.dfki.av.sudplan.vis.core.TFPanel;
import java.util.List;

/**
 *
 * @author steffen
 */
public interface TFPanelProvider {

    /**
     * 
     * @return 
     */
    public List<String> getTFPanels();

    /**
     * 
     * @param function
     * @return 
     */
    public TFPanel get(ITransferFunction function);
}
