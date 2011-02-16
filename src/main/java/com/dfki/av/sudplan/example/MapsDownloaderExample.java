

package com.dfki.av.sudplan.example;


import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;


public class MapsDownloaderExample {

    // Inputcoords -> 1 = left down corner ; 2 = right upper corner
    static double lat1 = 59.297637;
    static double lon1 = 18.008895;
    static double lat2 = 59.298471;
    static double lon2 = 18.00975;

    // Stallingtime between downloads ; 1000 = one second
    static int sleeptime = 7000;

    // Pathname where the downloaded images get stored
    public static final String path = "C:\\Users\\Brickwedde\\Downloads\\maps\\";

    public static final String GmapStaticURI = "http://maps.google.com/staticmap";
    public static final String ZoomKey = "zoom";
    public static final String SizeKey = "size";
    public static final String SizeSeparator = "x";
    public static final String CenterKey = "center";

    static double latOffSet = 0.000417;
    static double lonOffSet = 0.000855;

    public static final MapsDownloaderExample _map = new MapsDownloaderExample();

    public static void main(String[] args) throws IOException {




        int latlimit = (int)((lat2 - lat1)/latOffSet) + 1;
        int lonlimit = (int)((lon2 - lon1)/lonOffSet) + 1;

        int numTotal = (latlimit)*(lonlimit);

        double lat = lat1;
        double lon = lon1;

        int counter = 0;

        for(int i = 0; i < latlimit; i++){
            for(int j = 0; j < lonlimit; j ++){

                    File out = new File(path + "image"+lat+"_"+lon+".png");
                    if(out.exists()){
                        counter +=1;
                        lon += 0.000855;
                        lon = (Math.round(lon * Math.pow(10, 6))) / Math.pow(10, 6);
                        System.out.println("Tile "+counter+" of "+numTotal+" skippped because already existing");
                        continue;
                    } else {
                        String u2 = getMap(lat, lon, 640, 640, 20);

                        GetMethod get = new GetMethod(u2);

                        try {
                            new HttpClient().executeMethod(get);
                            //StringBuffer result = new StringBuffer(get.getResponseBodyAsString());
                            BufferedImage result2 = ImageIO.read(get.getResponseBodyAsStream());
                            Graphics gra1 = result2.getGraphics();
                            BufferedImage cuttedImage = new BufferedImage(640,610,BufferedImage.TYPE_INT_ARGB);
                            for(int k = 0; k < 640; k++){
                                for(int l = 0; l < 610; l++){
                                    cuttedImage.setRGB(k,l, result2.getRGB(k, l));
                                }
                            }

                            ImageIO.write(cuttedImage,"png",out);
                            //System.out.println("downloaded picture "+lat+"-"+lon);
                            counter += 1;
                            System.out.println("Downloaded "+counter+" of "+numTotal+" Tiles");

                            }
                        finally {
                            get.releaseConnection();
                        }


                        lon += 0.000855;
                        lon = (Math.round(lon * Math.pow(10, 6))) / Math.pow(10, 6);
                        try {
                            //System.out.println("sleeping");
                            Thread.sleep(sleeptime);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MapsDownloaderExample.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                lon = lon1;
                lat += 0.000417;
                lat = (Math.round(lat * Math.pow(10, 6))) / Math.pow(10, 6);
            }
    }



  public String getURI(double lat, double lon, int sizeW, int sizeH, int zoom) {

     // generate the URI
     StringBuilder sb = new StringBuilder();
    sb.append(GmapStaticURI);

    // center key
    sb.
      append("?").
      append(CenterKey).append("=").append(lat).append(",").append(lon);


    // zoom key
    sb.
          append("&").
          append(ZoomKey).append("=").append(zoom);

    // size key
     sb.
         append("&").
        append(SizeKey).append("=").append(sizeW).append(SizeSeparator).append(sizeH);



    sb.
        append("&").
            append("maptype").append("=").append("satellite");

    // Bildformat
    sb.
        append("&").
            append("format").append("=").append("png");


     return sb.toString();
    }

    public static String getMap(double lat, double lon, int sizeW, int sizeH, int zoom) {
        return _map.getURI(lat, lon, sizeW, sizeH, zoom);
    }

}
