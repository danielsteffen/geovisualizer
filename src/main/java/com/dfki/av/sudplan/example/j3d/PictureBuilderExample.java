

package com.dfki.av.sudplan.example.j3d;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PictureBuilderExample {


    // Inputcoords -> 1 = left down corner ; 2 = right upper corner
    static double lat1 = 59.297637;
    static double lon1 = 18.008895;
    static double lat2 = 18.00975;
    static double lon2 = 18.017445;

    // Pathname where the downloaded images are stored
    public static final String path = "C:\\Users\\Brickwedde\\Downloads\\maps\\";


    static double latOffSet = 0.000417;
    static double lonOffSet = 0.000855;

    public static void main(String[] args) throws IOException {

    int numlat = (int)((lat2 - lat1)/latOffSet) + 1;
    int numlon = (int)((lon2 - lon1)/lonOffSet) + 1;

    BufferedImage bigImage = new BufferedImage(numlon*640,numlat*610,BufferedImage.TYPE_INT_ARGB);

    double lat = lat1;
    double lon = lon1;
    for(int i = 0; i < numlat ; i++){
        for(int j = 0; j < numlon; j ++){
            lat = lat1 + i*latOffSet;
            lon = lon1 + j*lonOffSet;
            lat = (Math.round(lat * Math.pow(10, 6))) / Math.pow(10, 6);
            lon = (Math.round(lon * Math.pow(10, 6))) / Math.pow(10, 6);

            File in = new File(path+"image"+lat+"_"+lon+".png");
            System.out.println(in);
            BufferedImage input = ImageIO.read(in);

            for(int k = 0; k < 640; k++){
                for(int l = 0; l < 610; l++){
                     bigImage.setRGB(k+j*640,((numlat-i-1)*610)+l, input.getRGB(k, l));
                }
            }
        }
    }
    File out = new File(path+"bigimage.png");
    ImageIO.write(bigImage,"png",out);
    }

}
