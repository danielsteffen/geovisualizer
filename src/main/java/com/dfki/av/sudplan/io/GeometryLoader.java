package com.dfki.av.sudplan.io;

import com.dfki.av.sudplan.util.NumberParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.geometry.GeometryInfo;

/**
 * 
 * @author Puhl,Sebastian Scholl,Martin
 * 
 * This class read with the assistance of the GeometryParser the geometric
 * informations and generates a IndexedTriangleArray, multiple sets of color and
 * the color indices
 * 
 */
public class GeometryLoader {

  private int columnCount;
  private LandscapeGeometryInfo geoInfo;
  private int rowCount;
  private double xOrigin;
  private double yOrigin;
  private int cellSize;
  private int coordinateCount;
  private int countQuads;
  private int countTriangles;
  private int indexCountTriangles;
  private int noData;
  // private Point3f[] quads;
  // private Point3f[][] realPoints;
  private Point3f[] pointList;
  private Color4f[] colorsElevation;
  private Color4f[] colorsSlope;
  private Color4f[] colorsGreen;
  private Color4f[] colorsPlain;
  private int[] indicesList;
  private static final float HIGH = 1800;
  private static final float MEDIUM = 1500;
  private static final float LOW = 1200;
  private static final float steeper = 45;
  private static final float steep = 40;
  private static final float flat = 35;
  private static final float flatest = 30;

  /**
   * Contructor requested an stream with coordinate informations
   *
   * @param reader -
   *            coordinate stream
   */
  public GeometryLoader(InputStreamReader reader) {
    readCoordinates(reader);
  }

  private void readCoordinates(InputStreamReader reader) {
    try {
      BufferedReader sr = new BufferedReader(reader);

      // READ PARAMETERFILES OF DGM
      //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:problem this must be variable --> no_data need not be there
      columnCount = Integer.parseInt(sr.readLine().split(" ")[1]);
      rowCount = Integer.parseInt(sr.readLine().split(" ")[1]);
      xOrigin = Double.parseDouble(sr.readLine().split(" ")[1]);
      yOrigin = Double.parseDouble(sr.readLine().split(" ")[1]);
      cellSize = Integer.parseInt(sr.readLine().split(" ")[1]);
      noData = Integer.parseInt(sr.readLine().split(" ")[1]);

//            coordinateCount = columnCount * rowCount;
      coordinateCount = 454536 * 3;
      countQuads = (columnCount - 1) * (rowCount - 1);
      countTriangles = countQuads * 2;
      indexCountTriangles = countTriangles * 3;

      // THE ARRAY WITH THE HEIGHT POINTS (Starts by 0,0)
      // points = new Point3f[rowCount][columCount];
      // REAL WORLD COORDINATES
      // realPoints = new Point3f[rowCount][columCount];
      // A list of all Points (the points only one time)
      pointList = new Point3f[coordinateCount];
      colorsElevation = new Color4f[coordinateCount];
      colorsSlope = new Color4f[coordinateCount];
      colorsGreen = new Color4f[coordinateCount];
      colorsPlain = null;
      NumberParser geoParser = new NumberParser(sr);
      int z = 0;
      int x = 0;
      int y = 0;
      float currentHeight = 0;
      int counter = 0;
      while (geoParser.getToken()) {
        counter++;
        currentHeight = (float) geoParser.nval;
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:evil hack only for looking
        if (currentHeight == noData) {
          currentHeight = 1;
        }
//        pointList[z] = new Point3f((x) * (cellSize / 1500f), (y)
//                * (cellSize / 1500f), currentHeight / 1500f);

        pointList[z] = new Point3f((x) * (cellSize/1.0f), (y)
                * (cellSize/1.0f), currentHeight/1.0f);

        if (currentHeight < LOW) {
          colorsElevation[z] = Colors.green;
        } else if (currentHeight < MEDIUM) {
          colorsElevation[z] = Colors.darkGreen;
        } else if (currentHeight < HIGH) {
          colorsElevation[z] = Colors.purple;
        } else {
          colorsElevation[z] = Colors.greyWhite;
        }
        colorsGreen[z] = Colors.darkGreen;

        z++;
        y++;
        if (y == 301) {
          y = 0;
          x++;
        }
      }
      System.out.println("counter: " + counter);
      sr.close();

      indicesList = new int[indexCountTriangles];
      int index = indexCountTriangles - 1;
      for (int i = 0; i < columnCount - 1; i++) {

        for (int j = 0; j < rowCount - 1; j++) {

          // under left triangle
          indicesList[index] = (i * rowCount) + j;
          indicesList[index - 1] = (i * rowCount) + j + 1;
          indicesList[index - 2] = (i * rowCount) + j + (rowCount);

          // upper right triangle
          indicesList[index - 3] = (i * rowCount) + j + 1;
          indicesList[index - 4] = (i * rowCount) + j + (rowCount)
                  + 1;
          indicesList[index - 5] = (i * rowCount) + j + (rowCount);

          for (int k = 0; k < 2; k++) {
            Point3f point1 = pointList[indicesList[index
                    - ((k * 3))]];
            Point3f point2 = pointList[indicesList[index
                    - ((k * 3) + 1)]];
            Point3f point3 = pointList[indicesList[index
                    - ((k * 3) + 2)]];
            float dist1 = point1.distance(point2);
            float dist2 = point1.distance(point3);
            float diff1 = Math.abs(point1.z - point2.z);
            float diff2 = Math.abs(point1.z - point3.z);
            float slope = ((diff1 / dist1) + (diff2 / dist2)) * 50;
            // slope = slope - 5;
            if (slope < flatest) {
              colorsSlope[indicesList[index - (k * 3)]] = colorsElevation[indicesList[index
                      - (k * 3)]];
              colorsSlope[indicesList[index - ((k * 3) + 1)]] = colorsElevation[indicesList[index
                      - ((k * 3) + 1)]];
              colorsSlope[indicesList[index - ((k * 3) + 2)]] = colorsElevation[indicesList[index
                      - ((k * 3) + 2)]];
            } else if (slope < flat) {
              colorsSlope[indicesList[index - ((k * 3))]] = Colors.lightOrange;
              colorsSlope[indicesList[index - ((k * 3) + 1)]] = Colors.lightOrange;
              colorsSlope[indicesList[index - ((k * 3) + 2)]] = Colors.lightOrange;
            } else if (slope < steep) {
              colorsSlope[indicesList[index - ((k * 3))]] = Colors.orange;
              colorsSlope[indicesList[index - ((k * 3) + 1)]] = Colors.orange;
              colorsSlope[indicesList[index - ((k * 3) + 2)]] = Colors.orange;
            } else if (slope < steeper) {
              colorsSlope[indicesList[index - (k * 3)]] = Colors.darkOrange;
              colorsSlope[indicesList[index - ((k * 3) + 1)]] = Colors.darkOrange;
              colorsSlope[indicesList[index - ((k * 3) + 2)]] = Colors.darkOrange;
            } else {
              // Highest
              colorsSlope[indicesList[index - (k * 3)]] = Colors.red;
              colorsSlope[indicesList[index - ((k * 3) + 1)]] = Colors.red;
              colorsSlope[indicesList[index - ((k * 3) + 2)]] = Colors.red;
            }
          }
          index -= 6;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    geoInfo = new LandscapeGeometryInfo(GeometryInfo.TRIANGLE_ARRAY,
            colorsGreen, colorsElevation, colorsSlope, indicesList);
    geoInfo.setCoordinates(pointList);
    geoInfo.setCoordinateIndices(indicesList);
  }

  /**
   * This method returns the complete Geometric/Color information for
   * generating the landscape
   *
   * @return
   */
  public LandscapeGeometryInfo getGeometryInfo() {
    return geoInfo;
  }
}
