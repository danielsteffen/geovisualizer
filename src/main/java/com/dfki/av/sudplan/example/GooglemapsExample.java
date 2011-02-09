/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.example;

import java.io.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

/**
 *
 * @author brickwedde
 */
public class GooglemapsExample {

    public static final String GmapStaticURI = "http://maps.google.com/staticmap";
    public static String GmapLicense = "";
    public static final String ZoomKey = "zoom";
    public static final String SizeKey = "size";
    public static final String SizeSeparator = "x";
    public static final String GmapLicenseKey = "key";
    public static final String CenterKey = "center";

    public static final GooglemapsExample _map = new GooglemapsExample();

    public static void main(String[] args) throws IOException {
        setLicenseKey("ABQIAAAACvML0PKw1j1MFHgap8v4MxSjvzzuOuCjdbPbssUHfGpUHk16_hTppBa_nu1mduDTaDTd_ZDscCjsCg");

        double lat = 38.931099;
        double lon = -77.3489;

        String u2 = getMap(lat, lon, 640, 640, 21);
        System.out.println(u2);



    }

    public static void setLicenseKey(String lic) {
        GmapLicense = lic;
    }

    public static StringBuffer getDataFromURI(String uri) throws IOException {

        GetMethod get = new GetMethod(uri);

        try {
            new HttpClient().executeMethod(get);
            StringBuffer result = new StringBuffer(get.getResponseBodyAsString());
            return result;

        }
        finally {
            get.releaseConnection();
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

    // maps key
    //sb.
      //  append("&").
        //  append(GmapLicenseKey).append("=").append(GmapLicense);

    // maptype

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
