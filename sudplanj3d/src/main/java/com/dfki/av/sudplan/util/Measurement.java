/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.util;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class Measurement {
    private Object id;
    private Long startTime;
    private Long endTime;
    private boolean isFinished=false;
    private boolean isStarted=false;

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(final long endTime) {
    isFinished = (startTime!=null);
    this.endTime = endTime;
  }

  public Object getId() {
    return id;
  }

  public void setId(final Object id) {
    this.id = id;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(final Long startTime) {
    isStarted = (startTime!=null);
    this.startTime = startTime;
  }

  public Long getDuration(){
    if(isFinished){
      return getEndTime()-getStartTime();
    }
    return null;
  }

  public Long stopMeasurement(){
    setEndTime(System.currentTimeMillis());
    return getDuration();
  }
}
