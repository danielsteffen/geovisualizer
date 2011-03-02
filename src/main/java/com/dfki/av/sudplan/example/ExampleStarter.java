/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.example;

import com.dfki.av.sudplan.example.j3d.CanvasDoubleBuffer;
import com.dfki.av.sudplan.example.j3d.ImagePlate;
import com.dfki.av.sudplan.example.j3d.TriangleArray;
import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JFrame;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ExampleStarter {
      public static void main(String[] args) {
      JFrame frame = new JFrame();
          frame.setSize(800,600);
          frame.setLayout(new BorderLayout());
          frame.getRootPane().setDoubleBuffered(false);
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.add(new ImagePlate().createUniverse(),BorderLayout.CENTER);
          frame.setVisible(true);
//          GeoTiff tiff = new GeoTiff();
//          tiff.readGeoTiff2();
      }
}
