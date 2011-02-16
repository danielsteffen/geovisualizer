

package com.dfki.av.sudplan.example;


public class BoundingBoxCheckExample {


    static double latOffSet = 0.000417;
    static double lonOffSet = 0.000855;

   //Bounding box Coords
   // Inputcoords -> 1 = left down corner ; 2 = right upper corner
    static double lat1;
    static double lon1;
    static double lat2;
    static double lon2;

    // Examplevalues -> 4 tiles
    // static double lat1 = 59.297637;
    // static double lon1 = 18.008895;
    // static double lat2 = lat1 + (2* latOffSet);
    // static double lon2 = lon1 + (1* lonOffSet);


    public static void main(String[] args) {

        lat2 = (Math.round(lat2 * Math.pow(10, 6))) / Math.pow(10, 6);
        lon2 = (Math.round(lon2 * Math.pow(10, 6))) / Math.pow(10, 6);


        System.out.println("Bounding Box : lat1 = "+lat1+", lon1 = "+lon1);
        System.out.println("Bounding Box : lat2 = "+lat2+", lon2 = "+lon2);

        double latDiff = Math.abs(lat1 - lat2);
        double lonDiff = Math.abs(lon1 - lon2);

        double numlat = Math.floor(latDiff / latOffSet);
        double numlon = Math.floor(lonDiff / lonOffSet);

        //System.out.println(numlat + " -- " + numlon + "->" + numlon*numlat +" Kacheln");

        if(numlat*numlon <= 1000){
            System.out.println("Boundingbox OK für einen Tag.");
        } else {
            System.out.println("Boundingbox zu groß für einen Tag -> Mehrere Subboxen werden erstellt.");
            if(numlat < numlon){
                int numRows = (int)Math.floor(1000/numlat);
                //System.out.println(numRows + " Lattitude");
                double templon = lon1;
                double oldlon = lon1;
                int counter = 1;
                for(int i = 0; i < numlon; i = i + numRows){
                    if(templon + (numRows * lonOffSet) > lon2){
                        while(templon < lon2){
                            templon += lonOffSet;
                            templon = (Math.round(templon * Math.pow(10, 6))) / Math.pow(10, 6);
                        }
                        System.out.println("Bounding Box "+counter+" : lat1 = "+lat1+", lon1 = "+oldlon);
                        System.out.println("Bounding Box "+counter+" : lat2 = "+lat2+", lon2 = "+templon);
                    } else {
                        templon += (numRows * lonOffSet);
                        System.out.println("Bounding Box "+counter+" : lat1 = "+lat1+", lon1 = "+oldlon);
                        System.out.println("Bounding Box "+counter+" : lat2 = "+lat2+", lon2 = "+templon);
                        templon += lonOffSet;
                        templon = (Math.round(templon * Math.pow(10, 6))) / Math.pow(10, 6);
                        oldlon = templon;
                        counter += 1;
                    }

                }


            } else {
                int numRows = (int)Math.floor(1000/numlon);
                //System.out.println(numRows + " Longitude");
                double templat = lat1;
                double oldlat = lat1;
                int counter = 1;
                for(int i = 0; i < numlat; i = i + numRows){
                    if(templat + (numRows * latOffSet) > lat2){
                        while(templat < lat2){
                            templat += latOffSet;
                            templat = (Math.round(templat * Math.pow(10, 6))) / Math.pow(10, 6);
                        }
                        System.out.println("Bounding Box "+counter+" : lat1 = "+oldlat+", lon1 = "+lon1);
                        System.out.println("Bounding Box "+counter+" : lat2 = "+templat+", lon2 = "+lon2);
                    } else {
                        templat += (numRows * lonOffSet);
                        System.out.println("Bounding Box "+counter+" : lat1 = "+oldlat+", lon1 = "+lon1);
                        System.out.println("Bounding Box "+counter+" : lat2 = "+templat+", lon2 = "+lon2);
                        templat += latOffSet;
                        templat = (Math.round(templat * Math.pow(10, 6))) / Math.pow(10, 6);
                        oldlat = templat;
                        counter += 1;
                    }

                }
            }
        }
    }

}
