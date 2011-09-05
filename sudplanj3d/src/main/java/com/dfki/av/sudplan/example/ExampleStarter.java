/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.example;

import com.dfki.av.sudplan.example.j3d.TriangleArrayPyramid;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ExampleStarter {
     private final static Logger logger = LoggerFactory.getLogger(ExampleStarter.class);
     private final static java.util.logging.Logger loggers =  java.util.logging.Logger.getLogger("org.wombat");
      public static void main(String[] args) {
      JFrame frame = new JFrame();
          frame.setSize(800,600);
          frame.setLayout(new BorderLayout());
          frame.getRootPane().setDoubleBuffered(false);
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.add(new LightTransformation(),BorderLayout.CENTER);
          frame.setVisible(true);
//          GeoTiff tiff = new GeoTiff();
//          tiff.readGeoTiff2();
//      logger.debug("testit");
//      SLF4JBridgeHandler.install();
//      loggers.fine("lala");
//      JavaLoggingRedirection test = new JavaLoggingRedirection();
//      test.testLogging();
      }
}
