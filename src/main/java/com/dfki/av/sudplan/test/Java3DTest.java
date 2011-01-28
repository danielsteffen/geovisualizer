/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.test;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.media.j3d.BranchGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class Java3DTest {
  private static final Logger logger = LoggerFactory.getLogger(Java3DTest.class);

  public static BranchGroup get3DScence() {
    final BranchGroup rootObj = new BranchGroup();
    final ObjectFile objFile = new ObjectFile(ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);  
    try {
      final Scene loadedScene = objFile.load(new BufferedReader(new InputStreamReader(Java3DTest.class.getResourceAsStream("/Grid 10m_1_RT90_25gV.obj"))));
      rootObj.addChild(loadedScene.getSceneGroup());
    } catch (Exception ex) {
      logger.error("Error during constructing sceneGraph", ex);
    }
    return rootObj;
  }
}
