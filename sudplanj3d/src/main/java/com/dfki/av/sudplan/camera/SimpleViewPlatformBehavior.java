/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.camera;

import com.sun.j3d.utils.behaviors.vp.ViewPlatformBehavior;
import java.util.Enumeration;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.media.j3d.WakeupOnTransformChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class SimpleViewPlatformBehavior extends ViewPlatformBehavior{
    private final static Logger logger = LoggerFactory.getLogger(SimpleViewPlatformBehavior.class);
    protected WakeupOnTransformChange transformWake;
    
    @Override
    public void initialize() {
        transformWake = new WakeupOnTransformChange(targetTG);        
    }

    @Override
    public void processStimulus(Enumeration criteria) {
        if (logger.isDebugEnabled()) {
            logger.debug("Transform wake: "+criteria);
        }
    }    
}
