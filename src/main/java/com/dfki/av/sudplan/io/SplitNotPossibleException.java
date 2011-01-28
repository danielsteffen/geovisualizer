/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.io;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
class SplitNotPossibleException extends Exception {
 public SplitNotPossibleException(Throwable cause) {
    super(cause);
  }

  public SplitNotPossibleException(String message, Throwable cause) {
    super(message,cause);
  }

  public SplitNotPossibleException(String message) {
    super(message);
  }

  public SplitNotPossibleException() {
    super();
  } 
}
