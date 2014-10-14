/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.av.geovisualizer.sos.vis.ndw2012;

import de.dfki.av.geovisualizer.core.ISource;
import de.dfki.av.geovisualizer.core.functions.IdentityFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tzimmermann
 */
public class StressTransferFunction extends IdentityFunction {

    /*
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(StressTransferFunction.class);

    @Override
    public String getName() {
        return "Stress Transfer Function";
    }

    @Override
    public Object calc(Object o) {
        int result = 0;
        if (o == null) {
            log.error("Argument set to null.");
            return result;
        }

        if (o instanceof Number) {
            Number n = (Number) o;
            if (n.doubleValue() > 0) {
                result = 1;
            } else {
                result = 0;
            }
        } else {
            log.error("Data type {} not supported", o.getClass().getSimpleName());
            throw new IllegalArgumentException("Data type not supported.");
        }
        return result;
    }

    @Override
    public void preprocess(ISource is, String string) {
        log.debug("No pre-processing required.");
    }
}
