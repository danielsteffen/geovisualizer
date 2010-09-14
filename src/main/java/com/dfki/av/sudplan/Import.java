package com.dfki.av.sudplan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

/**
 *
 * @author Martin Weller
 */
public class Import {

    public static double[][] einlesen() throws FileNotFoundException, IOException {
        String komplett = "", tmp = "";
        double[][] koordinaten = new double[301][601];
        int spalte = 0;
        FileReader fileReader = new FileReader(new File("test.txt"));
        BufferedReader in = new BufferedReader(fileReader);

        while ((tmp = in.readLine()) != null) {
            int zeile = 0;
            StringTokenizer st = new StringTokenizer(tmp);
            while (st.hasMoreTokens()) {
                koordinaten[spalte][zeile] = Double.parseDouble(st.nextToken());
                zeile++;
            }
            spalte++;
        }
        return koordinaten;
    } // end of einlesen()

    public static double[][] arrayaufnull(double[][] koordinaten) {
        double max = koordinaten[0][0];
        double min = koordinaten[0][0];

        for (int i = 0; i < koordinaten.length; i++) {
            for (int j = 0; j < koordinaten[0].length; j++) {
                if (koordinaten[i][j] > max) {
                    max = koordinaten[i][j];
                } else if (koordinaten[i][j] < min) {
                    min = koordinaten[i][j];
                }
            }
        }

        System.out.println("minimaler Wert " + min + "    neuer min Wert: " + (min - min));
        System.out.println("maximlaer Wert " + max + "    neuer max Wert: " + (max - min));


        double[][] ziel = new double[koordinaten.length][koordinaten[0].length];
        // mindestens 1 Vorkommastelle, genau 2 Nachkommastellen
        DecimalFormat f = new DecimalFormat("#0.00");

        for (int i = 0; i < koordinaten.length; i++) {
            for (int j = 0; j < koordinaten[0].length; j++) {
                ziel[i][j] = (double) (int) ((koordinaten[i][j] - min) * 100) / 100;
            }
        }

        return ziel;
    }
}
