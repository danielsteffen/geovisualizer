package com.dfki.av.sudplan.j3d;

import com.dfki.av.sudplan.io.GeoData;
import com.dfki.av.sudplan.io.Import;
import com.sun.j3d.utils.image.TextureLoader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.PointArray;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 *
 * @author 
 */
public class Create {

public static int scalefactor = 1200;

    private void initComponents() throws FileNotFoundException, IOException {
        //Textur
        TextureLoader loader = new TextureLoader("map.jpg",null);
    
    }

    public static Shape3D Points(String filename) throws FileNotFoundException, IOException, URISyntaxException {
        ColoringAttributes ca2 = new ColoringAttributes(0.0f, 0.0f, 0.0f, ColoringAttributes.SHADE_FLAT);
        GeoData data = new GeoData(filename);
        double array[][] = data.getPoints();
//        double array[][] = Import.einlesen(filename);

        double koordinaten[][] = Import.arrayaufnull(array);
        System.out.println("array length : array[" + koordinaten.length + "][" + koordinaten[0].length + "]");
        int AnzahlKoords = koordinaten.length * koordinaten[0].length;
        System.out.println("Anzahl Koordinaten: " + AnzahlKoords);

        // MAP
        Point3f[] Kartenpunkte = new Point3f[AnzahlKoords];
        int zaehler = 0;
        for (int spalte = 0; spalte < koordinaten.length; spalte++) {
            for (int zeile = 0; zeile < koordinaten[0].length; zeile++) {
                Kartenpunkte[zaehler] = new Point3f(
                        (float) spalte / 300 - 1,
                        (float) zeile / 300 - 1,
                        (float) ((koordinaten[spalte][zeile]) / scalefactor));
//                System.out.println("Point3f["+zaehler+"]"+koordinaten[spalte][zeile] + Punkte[zaehler].toString());
                zaehler++;
            }
        }
        PointArray punkt2 = new PointArray(AnzahlKoords, PointArray.COORDINATES);
        punkt2.setCoordinates(0, Kartenpunkte);
        PointAttributes pat2 = new PointAttributes();
        //pat2.setPointAntialiasingEnable(true);
        //pat2.setPointSize(0.1f);
        Appearance punkt2App = new Appearance();
        punkt2App.setPointAttributes(pat2);
        punkt2App.setColoringAttributes(ca2);
        Shape3D punktShape = new Shape3D(punkt2, punkt2App);
        return punktShape;
    }   //end of Points

    public static Shape3D Dreieck(String filename) throws FileNotFoundException, IOException, URISyntaxException {

        GeoData data = new GeoData(filename);
        double array[][] = data.getPoints();

//        double array[][] = Import.einlesen(filename);
        double koordinaten[][] = Import.arrayaufnull(array);
        int AnzahlKoords = koordinaten.length * koordinaten[0].length;

        int NUM_Triangles = 360000;
        int NUM_INDICES = NUM_Triangles * 3;    // f�r jedes Dreieck 3 Indices
        IndexedTriangleArray plane = new IndexedTriangleArray(AnzahlKoords, GeometryArray.COORDINATES | GeometryArray.COLOR_3, NUM_INDICES);
        System.out.println("AnzahlKoords: " + AnzahlKoords);

        Point3f[] pts = new Point3f[AnzahlKoords];
        System.out.println("pts.length: " + pts.length);
        int zaehler = 0;
        for (int spalte = 0; spalte < koordinaten.length; spalte++) {
            for (int zeile = 0; zeile < koordinaten[0].length; zeile++) {
                pts[zaehler] = new Point3f(
                        (float) spalte / 300 - 1,
                        (float) zeile / 300 - 1,
                        (float) ((koordinaten[spalte][zeile]) / scalefactor));
                zaehler++;
            }
        }
        int zeile = koordinaten[0].length;      // 301
        int spalte = koordinaten.length;        // 601
        int tmp = 0;
        int[] indices = new int[NUM_INDICES];
        System.out.println("indices.length: " + indices.length);
        
        for (int i = 0; i < (NUM_INDICES - 602); i = i + 6) {
            indices[i] = tmp;
            indices[i + 1] = tmp + 601;
            indices[i + 2] = tmp + 1;
            indices[i + 3] = tmp + 601;
            indices[i + 4] = tmp + 602;
            indices[i + 5] = tmp + 1;
            tmp = tmp + 1;
            if (((tmp + 1) % 601) == 0) {
                tmp = tmp + 1;
            }
        }
//        for (int i = 0; i < indices.length; i=i+3)
//            System.out.println("indices["+i+"] + 2 weitere: " + indices[i] +"  "+ indices[i+1] +"  "+ indices[i+2]);

        plane.setCoordinates(0, pts);
        plane.setCoordinateIndices(0, indices);

        // Color
        Color3f[] cols = new Color3f[AnzahlKoords];
        int color_counter = 0;
        float[] Farbwert = new float[AnzahlKoords];
        for (int spalte2 = 0; spalte2 < koordinaten.length; spalte2++) {
            for (int zeile2 = 0; zeile2 < koordinaten[0].length; zeile2++) {
                Farbwert[color_counter] = ((float) ((koordinaten[spalte2][zeile2]) / 300));
                color_counter++;
            }
        }
        float[] col_tmp2 = new float[3];
        int min = 0;
        int max = 111;
        // 240� because we want the area beetwen 0� and 240� (red to green)
        for (int i = 0; i < AnzahlKoords; i++) {
            col_tmp2 = Convert.HLStoRGB((240 - (240 * (((Farbwert[i] * 300 - min) / (max - min))))) / 360, 0.5f, 0.5f);
            cols[i] = (new Color3f(col_tmp2[0], col_tmp2[1], col_tmp2[2]));
        }
        plane.setColors(0, cols);
        plane.setColorIndices(0, indices);
        Shape3D killer = new Shape3D(plane);
        return killer;
    }

    public static Shape3D createKoordSystemY() {
        ColoringAttributes ca = new ColoringAttributes(1.0f, 1.0f, 1.0f, ColoringAttributes.SHADE_FLAT);
        Point3f[] dotPts2 = new Point3f[2];
        dotPts2[0] = new Point3f(0.0f, -0.9f, 0.0f);
        dotPts2[1] = new Point3f(0.0f, 0.9f, 0.0f);
        LineArray dot2 = new LineArray(2, LineArray.COORDINATES);
        dot2.setCoordinates(0, dotPts2);
        LineAttributes dotLa2 = new LineAttributes();
        dotLa2.setLineWidth(2.0f);
        dotLa2.setLinePattern(LineAttributes.PATTERN_DOT);
        Appearance dotApp2 = new Appearance();
        dotApp2.setLineAttributes(dotLa2);
        dotApp2.setColoringAttributes(ca);
        Shape3D dotShape2 = new Shape3D(dot2, dotApp2);
        return dotShape2;
        //objRoot.addChild(dotShape2);
    }

    public static Shape3D createKoordSystemX() {
        ColoringAttributes ca = new ColoringAttributes(1.0f, 1.0f, 1.0f, ColoringAttributes.SHADE_FLAT);
        Point3f[] dotPts = new Point3f[2];
        dotPts[0] = new Point3f(-0.9f, 0.0f, 0.0f);
        dotPts[1] = new Point3f(0.9f, 0.0f, 0.0f);
        LineArray dot = new LineArray(2, LineArray.COORDINATES);
        dot.setCoordinates(0, dotPts);
        LineAttributes dotLa = new LineAttributes();
        dotLa.setLineWidth(2.0f);
        dotLa.setLinePattern(LineAttributes.PATTERN_DOT);
        Appearance dotApp = new Appearance();
        dotApp.setLineAttributes(dotLa);
        dotApp.setColoringAttributes(ca);
        Shape3D dotShape = new Shape3D(dot, dotApp);
        return dotShape;
        //objRoot.addChild(dotShape);
    }
}
