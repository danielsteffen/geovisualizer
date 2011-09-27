/*
 *  Created by DFKI AV on 05/09/2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplanX.test;

import com.sun.opengl.util.BufferUtil;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.Logging;
import java.awt.Color;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;

public class Building extends MultiRenderable.Part
{
  static final double EXTEND_WALLS_BELOW_BASE = 10.0D;
  final String sid;
  final double elevBase;
//  final double elevGutter;
  final double elevRidge;
//  final int ko_sifko;
//  final int stev;
//  final int numFloors;
//  final double areaFundus;
//  final double areaTotal;
//  final double areaMin;
  final double areaCalculated;
//  final int yearBuilt;
//  final String note;
  final Position center;
  final LatLon[] corners;
  private final BuildingType type;
  private final boolean flatRoof;
  private final double elevRoof;
  private final Color wallColor;
  private final Color roofColor;
  private final long estimatedMemorySize;
  private ArrayList<GLElement> flatRoofGeom = null;
  static final String TOPP_SID = "topp:sid";
  static final String TOPP_ELEV_BASE = "topp:ztem";
  static final String TOPP_ELEV_RIDGE = "topp:zslem";
  static final String TOPP_ELEV_GUTTER = "topp:zkap";
  static final String TOPP_KO_SIFKO = "topp:ko_sifko";
  static final String TOPP_STEV = "topp:stev";
  static final String TOPP_NUM_FLOORS = "topp:stetaz";
  static final String TOPP_AREA_FUNDUS = "topp:povfun";
  static final String TOPP_AREA_TOTAL = "topp:skpov";
  static final String TOPP_AREA_MIN = "topp:minpov";
  static final String TOPP_AREA_CALCULATED = "topp:area";
  static final String TOPP_YEAR_BUILT = "topp:lizgr";
  static final String TOPP_NOTE = "topp:opomba";

  public static boolean implementsWFSType(String paramString)
  {
    return paramString.equals("dfki:building");
  }

  public Building(String paramString, double paramDouble1, double paramDouble2, LatLon paramLatLon, ArrayList<LatLon> paramArrayList)
  {
//     Logging.logger().log(Level.INFO, "Building constructor:{0},{1},{2},{3}", new Object[]{paramString, paramDouble1,paramLatLon,paramArrayList});
    this.sid = paramString;
    this.elevBase = paramDouble1;
//    this.elevGutter = paramDouble2;
    this.elevRidge = paramDouble2;
//    this.ko_sifko = paramInt2;
//    this.stev = paramInt3;
//    this.numFloors = paramInt4;
//    this.areaFundus = paramDouble4;
//    this.areaTotal = paramDouble5;
//    this.areaMin = paramDouble6;
    this.areaCalculated = 150;
//    this.yearBuilt = paramInt5;
//    this.note = paramString;
    this.center = new Position(paramLatLon, paramDouble1);
    this.corners = new LatLon[paramArrayList.size()];
    for (int i = 0; i < paramArrayList.size(); i++)
      this.corners[i] = ((LatLon)paramArrayList.get(i));
    this.type = BuildingType.guessType(this);
    Random localRandom = new Random();
    float f = localRandom.nextFloat();
    this.wallColor = new Color(0.3F + f * 0.6F + localRandom.nextFloat() * 0.1F, 0.3F + f * 0.5F + localRandom.nextFloat() * 0.2F, 0.25F + f * 0.6F + localRandom.nextFloat() * 0.1F, 1.0F);
    this.roofColor = new Color(0.1F + f * 0.7F, localRandom.nextFloat() * 0.2F, localRandom.nextFloat() * 0.1F, 1.0F);
//    this.flatRoof = (this.corners.length != 5);
    this.flatRoof = true;
    this.elevRoof = paramDouble1;
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: definetley wrong
    this.estimatedMemorySize = (344 + 160 * paramArrayList.size());
  }

  public Building(Hashtable<String, StringBuilder> paramHashtable, LatLon paramLatLon, ArrayList<LatLon> paramArrayList)
  {     
    this(
            "No ID",
            Double.parseDouble(new String((StringBuilder)paramHashtable.get("dfki:z_max"))),
            Double.parseDouble(new String((StringBuilder)paramHashtable.get("dfki:z_max"))),
            paramLatLon, paramArrayList);
  }

  public long getSizeInBytes()
  {
    return this.estimatedMemorySize;
  }

  public LatLon getCenter()
  {
    return this.center;
  }

  public Vec4 getRefPoint(DrawContext paramDrawContext)
  {
    return paramDrawContext.getGlobe().computePointFromPosition(new Position(this.center, this.center.getElevation() * paramDrawContext.getVerticalExaggeration()));
  }

  public boolean isTransparent()
  {
    return (this.wallColor.getAlpha() < 255) || (this.roofColor.getAlpha() < 255);
  }

  public boolean isAbsoluteAlt()
  {
    return true;
  }

  public void preDraw(DrawContext paramDrawContext, Vec4 paramVec4, double paramDouble)
  {
//      Logging.logger().log(Level.INFO, "preDraw()");
    if (this.flatRoof)
    {
      double[][] arrayOfDouble = new double[this.corners.length][3];
      for (int i = 0; i < this.corners.length; i++)
      {
        Position localObject = new Position(this.corners[i], this.elevRoof * paramDrawContext.getVerticalExaggeration());
        Vec4 localVec4 = paramDrawContext.getGlobe().computePointFromPosition((Position)localObject);
        arrayOfDouble[i][0] = (localVec4.x - paramVec4.x);
        arrayOfDouble[i][1] = (localVec4.y - paramVec4.y);
        arrayOfDouble[i][2] = (localVec4.z - paramVec4.z);
      }
      this.flatRoofGeom = new ArrayList();
      GLUtessellator localGLUtessellator = paramDrawContext.getGLU().gluNewTess();
      Object localObject = new TessCallbackConvert(paramDrawContext.getGLU(), this.flatRoofGeom);
      paramDrawContext.getGLU().gluTessCallback(localGLUtessellator, 100101, (GLUtessellatorCallback)localObject);
      paramDrawContext.getGLU().gluTessCallback(localGLUtessellator, 100100, (GLUtessellatorCallback)localObject);
      paramDrawContext.getGLU().gluTessCallback(localGLUtessellator, 100102, (GLUtessellatorCallback)localObject);
      paramDrawContext.getGLU().gluTessCallback(localGLUtessellator, 100103, (GLUtessellatorCallback)localObject);
      paramDrawContext.getGLU().gluTessBeginPolygon(localGLUtessellator, null);
      paramDrawContext.getGLU().gluTessBeginContour(localGLUtessellator);
      for (int j = 0; j < this.corners.length; j++)
        paramDrawContext.getGLU().gluTessVertex(localGLUtessellator, arrayOfDouble[j], 0, arrayOfDouble[j]);
      paramDrawContext.getGLU().gluTessEndContour(localGLUtessellator);
      paramDrawContext.getGLU().gluTessEndPolygon(localGLUtessellator);
      if (((TessCallbackConvert)localObject).hasFailed())
      {
        Logging.logger().warning("Error during tesselation of roof of building " + this.sid + "; roof will not be shown. Error message: " + ((TessCallbackConvert)localObject).getErrorMessage());
        this.flatRoofGeom.clear();
      }
      paramDrawContext.getGLU().gluDeleteTess(localGLUtessellator);
    }
  }

  public void draw(DrawContext paramDrawContext, Vec4 paramVec4, double paramDouble)
  {
//    Logging.logger().log(Level.INFO, "draw()");
    int i = 2 * (this.corners.length + (this.flatRoof ? 0 : 2));
    DoubleBuffer localDoubleBuffer = BufferUtil.newDoubleBuffer(3 * i);
    Vec4 localVec41 = paramDrawContext.getGlobe().computePointFromPosition(new Position(this.corners[0], this.elevRidge * paramDrawContext.getVerticalExaggeration()));
    Vec4 localVec42 = paramDrawContext.getGlobe().computePointFromPosition(new Position(this.corners[1], this.elevRidge * paramDrawContext.getVerticalExaggeration()));
    Vec4 localVec43 = paramDrawContext.getGlobe().computePointFromPosition(new Position(this.corners[3], this.elevRidge * paramDrawContext.getVerticalExaggeration()));
    int j = 0;
    if (localVec41.distanceTo3(localVec42) >= localVec41.distanceTo3(localVec43))
      j = 1;
    for (int k = 0; k < this.corners.length; k++)
    {
      int n = k + j;
      if (n == this.corners.length)
        n = j;
      putVec3(this.corners[n], this.elevBase - 10.0D, paramDrawContext, paramVec4, localDoubleBuffer);
      putVec3(this.corners[n], this.elevRoof, paramDrawContext, paramVec4, localDoubleBuffer);
//      if ((this.flatRoof) || ((k != 0) && (k != 2)))
//        continue;
//      putVec3Avg(this.corners[n], this.elevBase - 10.0D, this.corners[(n + 1)], this.elevBase - 10.0D, paramDrawContext, paramVec4, localDoubleBuffer);
//      putVec3Avg(this.corners[n], this.elevRidge, this.corners[(n + 1)], this.elevRidge, paramDrawContext, paramVec4, localDoubleBuffer);
    }
    paramDrawContext.getGL().glEnableClientState(32884);
    paramDrawContext.getGL().glVertexPointer(3, 5130, 0, localDoubleBuffer.rewind());
    paramDrawContext.getGL().glHint(3155, 4353);
    paramDrawContext.getGL().glHint(3154, 4354);
    paramDrawContext.getGL().glColor4ub((byte)this.wallColor.getRed(), (byte)this.wallColor.getGreen(), (byte)this.wallColor.getBlue(), (byte)(int)(this.wallColor.getAlpha() * paramDouble));
    paramDrawContext.getGL().glPolygonMode(1032, 6914);
    paramDrawContext.getGL().glDrawArrays(8, 0, i);
    paramDrawContext.getGL().glColor4ub((byte)0,(byte) 0,(byte) 0, (byte)(int)(255.0D * paramDouble));
    paramDrawContext.getGL().glPolygonMode(1032, 6913);
    paramDrawContext.getGL().glDrawArrays(8, 0, i);
    Object localObject;
//    if (this.flatRoof)
//    {
      paramDrawContext.getGL().glColor4ub((byte)this.roofColor.getRed(), (byte)this.roofColor.getGreen(), (byte)this.roofColor.getBlue(), (byte)(int)(this.roofColor.getAlpha() * paramDouble));
      paramDrawContext.getGL().glPolygonMode(1032, 6914);
      if (this.flatRoofGeom != null)
      {
        Iterator localIterator = this.flatRoofGeom.iterator();
        while (localIterator.hasNext())
        {
          localObject = (GLElement)localIterator.next();
          ((GLElement)localObject).GLDraw(paramDrawContext.getGL());
        }
      }
//    }
//    else
//    {
//      int m = 6;
//      localObject = BufferUtil.newDoubleBuffer(3 * m);
//      if (localVec41.distanceTo3(localVec42) < localVec41.distanceTo3(localVec43))
//      {
//        putVec3(this.corners[0], this.elevGutter, paramDrawContext, paramVec4, (DoubleBuffer)localObject);
//        putVec3(this.corners[3], this.elevGutter, paramDrawContext, paramVec4, (DoubleBuffer)localObject);
//        putVec3Avg(this.corners[0], this.elevRidge, this.corners[1], this.elevRidge, paramDrawContext, paramVec4, (DoubleBuffer)localObject);
//        putVec3Avg(this.corners[2], this.elevRidge, this.corners[3], this.elevRidge, paramDrawContext, paramVec4, (DoubleBuffer)localObject);
//        putVec3(this.corners[1], this.elevGutter, paramDrawContext, paramVec4, (DoubleBuffer)localObject);
//        putVec3(this.corners[2], this.elevGutter, paramDrawContext, paramVec4, (DoubleBuffer)localObject);
//      }
//      else
//      {
//        putVec3(this.corners[0], this.elevGutter, paramDrawContext, paramVec4, (DoubleBuffer)localObject);
//        putVec3(this.corners[1], this.elevGutter, paramDrawContext, paramVec4, (DoubleBuffer)localObject);
//        putVec3Avg(this.corners[0], this.elevRidge, this.corners[3], this.elevRidge, paramDrawContext, paramVec4, (DoubleBuffer)localObject);
//        putVec3Avg(this.corners[2], this.elevRidge, this.corners[1], this.elevRidge, paramDrawContext, paramVec4, (DoubleBuffer)localObject);
//        putVec3(this.corners[3], this.elevGutter, paramDrawContext, paramVec4, (DoubleBuffer)localObject);
//        putVec3(this.corners[2], this.elevGutter, paramDrawContext, paramVec4, (DoubleBuffer)localObject);
//      }
//      paramDrawContext.getGL().glEnableClientState(32884);
//      paramDrawContext.getGL().glVertexPointer(3, 5130, 0, ((DoubleBuffer)localObject).rewind());
//      paramDrawContext.getGL().glColor4ub((byte)this.roofColor.getRed(), (byte)this.roofColor.getGreen(), (byte)this.roofColor.getBlue(), (byte)(int)(this.roofColor.getAlpha() * paramDouble));
//      paramDrawContext.getGL().glPolygonMode(1032, 6914);
//      paramDrawContext.getGL().glDrawArrays(8, 0, m);
//      paramDrawContext.getGL().glColor4ub((byte)0,(byte) 0, (byte)0, (byte)(int)(255.0D * paramDouble));
//      paramDrawContext.getGL().glPolygonMode(1032, 6913);
//      paramDrawContext.getGL().glDrawArrays(8, 0, m);
//    }
  }

  private void putVec3(LatLon paramLatLon, double paramDouble, DrawContext paramDrawContext, Vec4 paramVec4, DoubleBuffer paramDoubleBuffer)
  {
    Vec4 localVec4 = paramDrawContext.getGlobe().computePointFromPosition(new Position(paramLatLon, paramDouble * paramDrawContext.getVerticalExaggeration()));
    paramDoubleBuffer.put(localVec4.x - paramVec4.x);
    paramDoubleBuffer.put(localVec4.y - paramVec4.y);
    paramDoubleBuffer.put(localVec4.z - paramVec4.z);
  }

  private void putVec3Avg(LatLon paramLatLon1, double paramDouble1, LatLon paramLatLon2, double paramDouble2, DrawContext paramDrawContext, Vec4 paramVec4, DoubleBuffer paramDoubleBuffer)
  {
    Vec4 localVec41 = paramDrawContext.getGlobe().computePointFromPosition(new Position(paramLatLon1, paramDouble1 * paramDrawContext.getVerticalExaggeration()));
    Vec4 localVec42 = paramDrawContext.getGlobe().computePointFromPosition(new Position(paramLatLon2, paramDouble2 * paramDrawContext.getVerticalExaggeration()));
    paramDoubleBuffer.put((localVec41.x + localVec42.x) / 2.0D - paramVec4.x);
    paramDoubleBuffer.put((localVec41.y + localVec42.y) / 2.0D - paramVec4.y);
    paramDoubleBuffer.put((localVec41.z + localVec42.z) / 2.0D - paramVec4.z);
  }

  private class TessCallbackConvert
    implements GLUtessellatorCallback
  {
    private GLU glu;
    private ArrayList<Building.GLElement> elements;
    private int currType;
    private ArrayList<Vec4> currVerts;
    private boolean failed;
    private String errorMessage;

    public TessCallbackConvert(GLU glu,ArrayList<Building.GLElement> arg2)
    {
//      Logging.logger().log(Level.INFO, "TessCallbackConvert(...)");
      this.glu = glu;
//      Object localObject2;
      this.elements = arg2;
      this.currVerts = new ArrayList();
      this.failed = false;
    }

    public String getErrorMessage()
    {
      return this.errorMessage;
    }

    public boolean hasFailed()
    {
      return this.failed;
    }

    public void begin(int paramInt)
    {
      this.currType = paramInt;
      this.currVerts.clear();
    }

    public void end()
    {
      this.elements.add(new Building.GLElement(this.currType, this.currVerts));
    }

    public void vertex(Object paramObject)
    {
      if ((paramObject instanceof double[]))
      {
        double[] arrayOfDouble = (double[])(double[])paramObject;
        this.currVerts.add(new Vec4(arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2]));
      }
    }

    public void error(int paramInt)
    {
      this.failed = true;
      switch (paramInt)
      {
      case 100151:
        this.errorMessage = "missing gluEndPolygon";
        break;
      case 100152:
        this.errorMessage = "missing gluBeginPolygon";
        break;
      case 100153:
        this.errorMessage = "isoriented contour";
        break;
      case 100154:
        this.errorMessage = "vertex/edge intersection";
        break;
      case 100155:
        this.errorMessage = "misoriented or self-intersecting loops";
        break;
      case 100156:
        this.errorMessage = "coincident vertices";
        break;
      case 100157:
        this.errorMessage = "all vertices collinear";
        break;
      case 100158:
        this.errorMessage = "intersecting edges";
        break;
      default:
        this.errorMessage = "unknown tesselation error";
      }
    }

    public void vertexData(Object paramObject1, Object paramObject2)
    {
    }

    public void combine(double[] paramArrayOfDouble, Object[] paramArrayOfObject1, float[] paramArrayOfFloat, Object[] paramArrayOfObject2)
    {
    }

    public void combineData(double[] paramArrayOfDouble, Object[] paramArrayOfObject1, float[] paramArrayOfFloat, Object[] paramArrayOfObject2, Object paramObject)
    {
    }

    public void beginData(int paramInt, Object paramObject)
    {
    }

    public void endData(Object paramObject)
    {
    }

    public void edgeFlag(boolean paramBoolean)
    {
    }

    public void edgeFlagData(boolean paramBoolean, Object paramObject)
    {
    }

    public void errorData(int paramInt, Object paramObject)
    {
    }
  }

  private static class GLElement
  {
    int type;
    Vec4[] verts;

    GLElement(int paramInt, ArrayList<Vec4> paramArrayList)
    {
      this.type = paramInt;
      this.verts = new Vec4[paramArrayList.size()];
      int i = 0;
      Iterator localIterator = paramArrayList.iterator();
      while (localIterator.hasNext())
      {
        Vec4 localVec4 = (Vec4)localIterator.next();
        this.verts[i] = localVec4;
        i++;
      }
    }

    void GLDraw(GL paramGL)
    {
      paramGL.glBegin(this.type);
      for (int i = 0; i < this.verts.length; i++)
        paramGL.glVertex3d(this.verts[i].x, this.verts[i].y, this.verts[i].z);
      paramGL.glEnd();
    }
  }

  public static enum BuildingType
  {
    FAMILY_HOUSE, BLOCK_OF_FLATS, BUSINESS, INDUSTRIAL, STORAGE, TRANSFORMATOR, TOWER;

    private static BuildingType guessType(Building paramBuilding)
    {
      double d = paramBuilding.elevRidge - paramBuilding.elevBase;
      if (paramBuilding.areaCalculated < 30.0D)
      {
        if (d < 5.0D)
          return STORAGE;
        if (d < 20.0D)
          return TRANSFORMATOR;
        return TOWER;
      }
      if (paramBuilding.areaCalculated < 200.0D)
        return FAMILY_HOUSE;
      if (d < 15.0D)
        return INDUSTRIAL;
      return BUSINESS;
    }

    private Color getColor()
    {
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:Attetion failure
      switch (1)
      {
      case 1:
        return Color.RED;
      case 2:
        return Color.MAGENTA;
      case 3:
        return Color.WHITE;
      case 4:
        return Color.BLACK;
      case 5:
        return Color.CYAN;
      case 6:
        return Color.BLUE;
      case 7:
        return Color.GREEN;
      }
      return null;
    }
  }
}

/* Location:           C:\develop\sourcen\gaea.jar
 * Qualified Name:     si.xlab.gaea.core.render.Building
 * JD-Core Version:    0.6.0
 */