/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.conf;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class InitialisationException extends Exception {

  public InitialisationException(Throwable cause) {
    super(cause);
  }

  public InitialisationException(String message, Throwable cause) {
    super(message, cause);
  }

  public InitialisationException(String message) {
    super(message);
  }

  public InitialisationException() {
    super();
  }
}
