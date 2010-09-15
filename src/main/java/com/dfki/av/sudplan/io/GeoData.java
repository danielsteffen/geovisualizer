/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 *
 * @author steffen
 */
public class GeoData {

    private String filename;

    public GeoData() {
        this("test.txt");
    }

    public GeoData(String filename) {
        this.filename = filename;
    }

    public double[][] getPoints() throws FileNotFoundException, IOException {
        String komplett = "", tmp = "";
        double[][] koordinaten = new double[301][601];
        int spalte = 0;

//        ClassLoader cl = this.getClass().getClassLoader();
//        URL url = cl.getResource(filename);
        File f = new File(filename);
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);

        while ((tmp = br.readLine()) != null) {
            int zeile = 0;
            StringTokenizer st = new StringTokenizer(tmp);
            while (st.hasMoreTokens()) {
                koordinaten[spalte][zeile] = Double.parseDouble(st.nextToken());
                zeile++;
            }
            spalte++;
        }
        return koordinaten;
    }
    
}
