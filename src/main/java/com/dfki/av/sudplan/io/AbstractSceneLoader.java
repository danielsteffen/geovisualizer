/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io;

import com.dfki.av.sudplan.io.dem.ElevationShape;
import com.dfki.av.sudplan.layer.FileBasedLayer;
import com.dfki.av.sudplan.util.TimeMeasurement;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.SceneBase;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.media.j3d.BranchGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public abstract class AbstractSceneLoader extends AbstractFileLoader implements SceneLoader {

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:credits for loaderbase
//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:why is basepath & url differently treated. Code from Java 3D ObjectFile    
    private final static Logger logger = LoggerFactory.getLogger(AbstractSceneLoader.class);
    protected Scene createdScene;

    public AbstractSceneLoader() {
        super();
        createdScene = createScene();
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:for what do I need scene Loading ?
    @Override
    public Scene load(Object source) throws LoadingNotPossibleException {
        return (Scene) super.load(source);
    }

    public abstract void fillScene() throws Exception;

    @Override
    protected Object loadImpl() throws Exception {
        fillScene();
        return createdScene;
    }

    protected Scene createScene() {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating scene...");
            TimeMeasurement.getInstance().startMeasurement(this);
        }
        final SceneBase tmpScene = new SceneBase();
        final BranchGroup tmpBranch = new BranchGroup();
        tmpBranch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        tmpBranch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        tmpBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        tmpBranch.setCapability(BranchGroup.ALLOW_DETACH);
        tmpScene.setSceneGroup(tmpBranch);
        if (logger.isDebugEnabled()) {
            logger.debug("Creating scene done. Time elapsed: "
                    + TimeMeasurement.getInstance().stopMeasurement(this).getDuration() + " ms.");
        }
        return tmpScene;
    }
}
