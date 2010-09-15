package com.dfki.av.sudplan.io;

import java.text.DecimalFormat;

/**
 *
 * @author Martin Weller
 */
public class Import {

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
