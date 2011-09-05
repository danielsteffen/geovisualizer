/*
 *  Created by DFKI AV on 05/09/2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.wfs.copy;

import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.cache.Cacheable;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.util.Logging;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.media.opengl.GL;

public class MultiRenderable
  implements Renderable, Cacheable, Disposable
{
  private final ArrayList<Part> parts;
  private final long estimatedMemorySize;
  private RenderData renderData;

  public MultiRenderable(ArrayList<Part> paramArrayList)
  {
    long l = 1L;
    this.parts = paramArrayList;
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      Part localPart = (Part)localIterator.next();
      l += localPart.getSizeInBytes();
    }
    this.estimatedMemorySize = l;
    this.renderData = null;
  }

  public void dispose()
  {
    deleteDisplayList();
  }

  private void deleteDisplayList()
  {
    if (this.renderData != null)
      this.renderData.dc.getGL().glDeleteLists(this.renderData.displayList, 1);
  }

  public long getSizeInBytes()
  {
    return this.estimatedMemorySize;
  }

  private void initializeGeometry(DrawContext paramDrawContext, double paramDouble)
  {
    this.renderData = new RenderData();
    this.renderData.verticalExaggeration = paramDrawContext.getVerticalExaggeration();
    this.renderData.opacity = paramDouble;
    this.renderData.dc = paramDrawContext;
    this.renderData.hasTransparentParts = false;
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    Iterator localIterator = this.parts.iterator();
    Part localPart;
    while (localIterator.hasNext())
    {
      localPart = (Part)localIterator.next();
      if (localPart.isTransparent())
        this.renderData.hasTransparentParts = true;
      Vec4 localVec4 = localPart.getRefPoint(paramDrawContext);
      d1 += localVec4.x;
      d2 += localVec4.y;
      d3 += localVec4.z;
    }
    if (this.parts.size() > 0)
    {
      d1 /= this.parts.size();
      d2 /= this.parts.size();
      d3 /= this.parts.size();
    }
    this.renderData.refPoint = new Vec4(d1, d2, d3);
    localIterator = this.parts.iterator();
    while (localIterator.hasNext())
    {
      localPart = (Part)localIterator.next();
      localPart.preDraw(paramDrawContext, this.renderData.refPoint, this.renderData.opacity);
    }
    this.renderData.displayList = paramDrawContext.getGL().glGenLists(1);
    if (this.renderData.displayList == 0)
    {
      Logging.logger().severe("Error creating display list for MultiRenderable");
      return;
    }
    paramDrawContext.getGL().glNewList(this.renderData.displayList, 4864);
    localIterator = this.parts.iterator();
    while (localIterator.hasNext())
    {
      localPart = (Part)localIterator.next();
      if (localPart.isAbsoluteAlt())
        localPart.draw(paramDrawContext, this.renderData.refPoint, this.renderData.opacity);
    }
    paramDrawContext.getGL().glEndList();
  }

  public void render(DrawContext paramDrawContext)
  {
    render(paramDrawContext, 1.0D);
  }

  public void render(DrawContext paramDrawContext, double paramDouble)
  {
    if (paramDrawContext == null)
    {
      String localObject1 = Logging.getMessage("nullValue.DrawContextIsNull");
      Logging.logger().severe((String)localObject1);
      throw new IllegalStateException((String)localObject1);
    }
    if ((this.renderData == null) || (this.renderData.dc != paramDrawContext) || (this.renderData.verticalExaggeration != paramDrawContext.getVerticalExaggeration()) || (this.renderData.opacity != paramDouble))
    {
      deleteDisplayList();
      initializeGeometry(paramDrawContext, paramDouble);
      if (this.renderData == null)
      {
        String localObject1 = "ComplexRenderable.initializeGeometry() failed";
        Logging.logger().severe((String)localObject1);
        throw new IllegalStateException((String)localObject1);
      }
    }
    Object localObject1 = paramDrawContext.getGL();
    int i = 32769;
    ((GL)localObject1).glPushAttrib(i);
    ((GL)localObject1).glPushClientAttrib(2);
    paramDrawContext.getView().pushReferenceCenter(paramDrawContext, this.renderData.refPoint);
    try
    {
      if ((!paramDrawContext.isPickingMode()) && ((paramDouble < 1.0D) || (this.renderData.hasTransparentParts)))
      {
        ((GL)localObject1).glEnable(3042);
        ((GL)localObject1).glBlendFunc(770, 771);
      }
      ((GL)localObject1).glCallList(this.renderData.displayList);
      Iterator localIterator = this.parts.iterator();
      while (localIterator.hasNext())
      {
        Part localPart = (Part)localIterator.next();
        if (!localPart.isAbsoluteAlt())
          localPart.draw(paramDrawContext, this.renderData.refPoint, paramDouble);
      }
    }
    finally
    {
      ((GL)localObject1).glPolygonMode(1032, 6914);
      ((GL)localObject1).glPopClientAttrib();
      ((GL)localObject1).glPopAttrib();
      paramDrawContext.getView().popReferenceCenter(paramDrawContext);
    }
  }

  private static class RenderData
  {
    double verticalExaggeration = (0.0D / 0.0D);
    double opacity = (0.0D / 0.0D);
    DrawContext dc;
    Vec4 refPoint;
    int displayList;
    boolean hasTransparentParts;
  }

  public static abstract class Part
    implements Cacheable
  {
    public abstract LatLon getCenter();

    public abstract Vec4 getRefPoint(DrawContext paramDrawContext);

    public abstract boolean isAbsoluteAlt();

    public abstract boolean isTransparent();

    public abstract void draw(DrawContext paramDrawContext, Vec4 paramVec4, double paramDouble);

    public void preDraw(DrawContext paramDrawContext, Vec4 paramVec4, double paramDouble)
    {
    }
  }
}

/* Location:           C:\develop\sourcen\gaea.jar
 * Qualified Name:     si.xlab.gaea.core.render.MultiRenderable
 * JD-Core Version:    0.6.0
 */