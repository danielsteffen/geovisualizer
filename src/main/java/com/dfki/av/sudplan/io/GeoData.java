/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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

    public double[][] getPoints()  {

            String komplett = "";
            String tmp = "";
            double[][] koordinaten = new double[301][601];
            int spalte = 0;
            URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);

           try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((tmp = br.readLine()) != null) {
                int zeile = 0;
                StringTokenizer st = new StringTokenizer(tmp);
                while (st.hasMoreTokens()) {
                    koordinaten[spalte][zeile] = Double.parseDouble(st.nextToken());
                    zeile++;
                }
                spalte++;
            }

            } catch (IOException ex) {
           // Logger.getLogger(GeoData.class.getName()).log(Level.SEVERE, null, ex);
            }
        return koordinaten;
    }
    
}
