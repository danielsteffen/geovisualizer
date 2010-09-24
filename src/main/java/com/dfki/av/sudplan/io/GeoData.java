/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        try {

            URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            //        File f = new File(url.toURI());
            //        System.out.println(f.toString());
            //        ClassLoader cl = this.getClass().getClassLoader();
            //        URL url = cl.getResource(filename);
            //        File f = new File(filename);
            //        FileReader fr = new FileReader(f);
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
            Logger.getLogger(GeoData.class.getName()).log(Level.SEVERE, null, ex);
        }            
        return koordinaten;

    }
    
}
