/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.util;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class TimeMeasurement {

  //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: does not work if used in a class to have multiple measurements in parallel --> done
  //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: would be possible to hide the variable count if the class could assume that measurements will be closed in the exact opposite order which they were started
  //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this is not thread safe.
  private static TimeMeasurement instance;
  private ConcurrentHashMap<Object, ArrayList<Measurement>> measurementsByObject = new ConcurrentHashMap<Object, ArrayList<Measurement>>();

  private TimeMeasurement() {
  }

  public static TimeMeasurement getInstance() {
    if (instance == null) {
      instance = new TimeMeasurement();
    }
    return instance;
  }

  public Measurement startMeasurement(Object id) {
    if (id == null) {
      return null;
    }
    final Measurement newMeasurement = new Measurement();
    ArrayList<Measurement> measurementsOfObject = measurementsByObject.get(id);
    if (measurementsOfObject == null) {
      measurementsOfObject = new ArrayList<Measurement>();
      measurementsByObject.put(id, measurementsOfObject);
    }
    newMeasurement.setId(id);
    newMeasurement.setStartTime(System.currentTimeMillis());
    measurementsOfObject.add(newMeasurement);
    return newMeasurement;
  }

  public Measurement stopMeasurement(Object id) {
    if (id == null) {
      return null;
    }
    final ArrayList<Measurement> measurementsOfObject = measurementsByObject.get(id);
    if (measurementsOfObject == null && measurementsOfObject.isEmpty()) {
      return null;
    }
    final Measurement lastMeasurement = measurementsOfObject.get(measurementsOfObject.size() - 1);
    lastMeasurement.setEndTime(System.currentTimeMillis());
    measurementsOfObject.remove(lastMeasurement);
    return lastMeasurement;
  }

}
